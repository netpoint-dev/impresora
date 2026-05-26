@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.common.apiutil.printer.UsbThermalPrinter
import com.common.apiutil.printer.ThermalPrinter
import com.common.apiutil.util.SystemUtil
import com.common.apiutil.nfc.NfcUtil
import com.common.apiutil.powercontrol.PowerControl
import com.common.apiutil.CommonException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.app.PendingIntent
import android.view.KeyEvent

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import com.common.apiutil.decode.DecodeReader
import com.common.callback.IInputListener
import com.common.apiutil.pos.CommonUtil

import com.netpoint.impresora.ui.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {

    // ZXing cámara: launcher para scanear con cámara si módulo HW no responde
    internal var zxingResultCallback: ((String) -> Unit)? = null
    internal val zxingLauncher = registerForActivityResult(ScanContract()) { result ->
        val txt = result?.contents
        if (txt != null) zxingResultCallback?.invoke(txt)
        else zxingResultCallback?.invoke("")
    }

    // NFC Android nativo: callback para notificar al composable cuando llega un tag
    internal var onAndroidNfcTag: ((uid: String, techs: String) -> Unit)? = null

    // Scanner Keyboard Mode (emulación de teclado física)
    internal var onKeyboardScanDetected: ((String) -> Unit)? = null
    private val scanBuffer = StringBuilder()
    private var lastKeyTime = 0L

    // Background Camera Scanner (sin Preview UI)
    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    internal var isScanningBackground = false
    internal var isNfcListening = false

    private var nfcPendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme { HardwareScreen() } }

        // Setup PendingIntent for NFC Foreground Dispatch
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, flags)
    }

    override fun onResume() {
        super.onResume()
        if (isNfcListening) {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            if (nfcAdapter != null && nfcAdapter.isEnabled) {
                val intentFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                val techLists = arrayOf(
                    arrayOf("android.nfc.tech.NfcA"),
                    arrayOf("android.nfc.tech.NfcB"),
                    arrayOf("android.nfc.tech.NfcF"),
                    arrayOf("android.nfc.tech.NfcV"),
                    arrayOf("android.nfc.tech.IsoDep"),
                    arrayOf("android.nfc.tech.MifareClassic"),
                    arrayOf("android.nfc.tech.MifareUltralight")
                )
                nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, arrayOf(intentFilter), techLists)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isNfcListening) {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            nfcAdapter?.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isNfcListening && (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action || 
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action)) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                val uid = tag.id.joinToString("") { String.format("%02X", it) }
                val techs = tag.techList.joinToString(", ") { it.substringAfterLast(".") }
                onAndroidNfcTag?.invoke(uid, techs)
            }
        }
    }

    fun startNfcListening(
        onResult: (uid: String, techs: String) -> Unit,
        onError: (String) -> Unit,
        onStateChange: (Boolean) -> Unit
    ) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            onError("NFC no soportado")
            return
        }
        if (!nfcAdapter.isEnabled) {
            onError("NFC desactivado")
            return
        }

        isNfcListening = true
        onStateChange(true)
        onAndroidNfcTag = { uid, techs ->
            onResult(uid, techs)
            stopNfcListening(onStateChange)
        }

        val intentFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val techLists = arrayOf(
            arrayOf("android.nfc.tech.NfcA"),
            arrayOf("android.nfc.tech.NfcB"),
            arrayOf("android.nfc.tech.NfcF"),
            arrayOf("android.nfc.tech.NfcV"),
            arrayOf("android.nfc.tech.IsoDep"),
            arrayOf("android.nfc.tech.MifareClassic"),
            arrayOf("android.nfc.tech.MifareUltralight")
        )
        try {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, arrayOf(intentFilter), techLists)

            // Timeout de 15 segundos
            lifecycleScope.launch {
                delay(15000L)
                if (isNfcListening) {
                    runOnUiThread {
                        onError("Timeout NFC (15s sin lectura)")
                        stopNfcListening(onStateChange)
                    }
                }
            }
        } catch (e: Exception) {
            onError("Error al iniciar NFC: ${e.message}")
            stopNfcListening(onStateChange)
        }
    }

    fun stopNfcListening(onStateChange: (Boolean) -> Unit) {
        isNfcListening = false
        onAndroidNfcTag = null
        onStateChange(false)
        try {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (_: Exception) {}
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Intercepta caracteres veloces de la emulación de teclado del escáner
        if (event.action == KeyEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            if (now - lastKeyTime > 120L) {
                scanBuffer.clear()
            }
            lastKeyTime = now

            val keyCode = event.keyCode
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val scanned = scanBuffer.toString().trim()
                if (scanned.length >= 3) {
                    runOnUiThread {
                        onKeyboardScanDetected?.invoke(scanned)
                    }
                    scanBuffer.clear()
                    return true // Consumir el Enter para que no navegue en la UI
                }
                scanBuffer.clear()
            } else {
                val unicodeChar = event.unicodeChar
                if (unicodeChar != 0) {
                    val char = unicodeChar.toChar()
                    if (char.isLetterOrDigit() || char in "-_./:+=*&?@#") {
                        scanBuffer.append(char)
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    // Escáner de Cámara en Segundo Plano (sin Preview, silencioso)
    fun startBackgroundCameraScan(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onStateChange: (Boolean) -> Unit
    ) {
        if (isScanningBackground) return
        isScanningBackground = true
        onStateChange(true)

        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                cameraProvider = provider

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor ?: Executors.newSingleThreadExecutor().also { cameraExecutor = it }) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        BarcodeScanning.getClient().process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val value = barcode.rawValue
                                    if (!value.isNullOrEmpty()) {
                                        runOnUiThread {
                                            onResult(value)
                                            stopBackgroundCameraScan(onStateChange)
                                        }
                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    imageAnalysis
                )

                // Timeout de 15 segundos
                lifecycleScope.launch {
                    delay(15000L)
                    if (isScanningBackground) {
                        runOnUiThread {
                            onError("Timeout de 15s sin lectura de cámara")
                            stopBackgroundCameraScan(onStateChange)
                        }
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    onError("Error de cámara: ${e.message}")
                    stopBackgroundCameraScan(onStateChange)
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    fun stopBackgroundCameraScan(onStateChange: (Boolean) -> Unit) {
        isScanningBackground = false
        onStateChange(false)
        try {
            cameraProvider?.unbindAll()
        } catch (_: Exception) {}
        cameraProvider = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
    }
}

@SuppressLint("MissingPermission")
@Composable
fun HardwareScreen() {
    val context = LocalContext.current as android.app.Activity
    val scope = rememberCoroutineScope()
    val logScroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA ---\n") }

    // SDK Instances
    val usbPrinter = remember { UsbThermalPrinter(context) }
    val nfcUtil = remember { NfcUtil(context) }
    val powerControl = remember { PowerControl(context) }
    val decodeReader = remember { DecodeReader(context) }
    val commonUtil = remember { CommonUtil(context) }

    // Printer state
    var printerType by remember { mutableIntStateOf(-1) }
    var printerTypeName by remember { mutableStateOf("No detectada") }
    var printerStatus by remember { mutableStateOf("No verificado") }
    var useBluetoothPrinter by remember { mutableStateOf(false) }
    var lastBluetoothName by remember { mutableStateOf("No detectada") }
    var lastBluetoothMac by remember { mutableStateOf("No detectada") }
    var usbPrintSpeedMode by remember { mutableIntStateOf(0) }  // 0=Mín,1=Med,2=Máx
    var usbGrayMax by remember { mutableIntStateOf(200) }
    val is58mm = !useBluetoothPrinter && printerType == SystemUtil.PRINTER_PRT_COMMON
    val is80mm = !useBluetoothPrinter && (printerType == SystemUtil.PRINTER_80MM_USB_COMMON ||
            printerType == SystemUtil.PRINTER_SY581 ||
            printerType == SystemUtil.PRINTER_PT486F08401MB)
    val isBluetooth80mm = useBluetoothPrinter && lastBluetoothMac != "No detectada"
    val isPrinterReady = is58mm || is80mm || isBluetooth80mm

    // Format state
    var alignment by remember { mutableIntStateOf(0) } // 0=left,1=center,2=right
    var grayLevel by remember { mutableIntStateOf(3) }
    var fontSize by remember { mutableIntStateOf(24) }
    var fontWidthMul by remember { mutableIntStateOf(1) }
    var fontHeightMul by remember { mutableIntStateOf(1) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isInverse by remember { mutableStateOf(false) }
    var leftMargin by remember { mutableIntStateOf(0) }
    var lineSpacing by remember { mutableIntStateOf(6) }
    var paperFeedLines by remember { mutableIntStateOf(20) }
    var protectTemp by remember { mutableIntStateOf(80) }
    var printText by remember { mutableStateOf("=== PRUEBA ===\nHola desde NetPoint\nBanco de pruebas v2.0\n") }

    // Scanner state
    var lastScanned by remember { mutableStateOf("Nada") }
    var isScanning by remember { mutableStateOf(false) }
    var isScanningBackground by remember { mutableStateOf(false) }
    var isNfcListeningAndroid by remember { mutableStateOf(false) }

    val grayMax = if (is58mm) usbGrayMax else 12
    val grayDefault = if (is58mm) minOf(usbGrayMax, 1) else 3

    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $msg\n"
    }

    fun tap(label: String) = log("Tap -> $label")

    fun logError(msg: String) { log("❌ $msg") }
    fun logWarn(msg: String) { log("⚠️ $msg") }
    fun logOk(msg: String) { log("✅ $msg") }

    fun statusToString(status: Int): String = when (status) {
        ThermalPrinter.STATUS_OK -> "✅ Normal"
        ThermalPrinter.STATUS_NO_PAPER -> "🔴 Sin papel"
        ThermalPrinter.STATUS_OVER_HEAT -> "🟠 Sobrecalentamiento"
        ThermalPrinter.STATUS_OVER_FLOW -> "🟡 Cache lleno"
        ThermalPrinter.STATUS_BOX_OPEN -> "🟣 Tapa abierta"
        ThermalPrinter.STATUS_CUT_WRONG -> "🔵 Error de cuchilla"
        else -> "⚪ Desconocido ($status)"
    }

    fun alignConst(mode: Int) = when (mode) {
        0 -> UsbThermalPrinter.ALGIN_LEFT
        1 -> UsbThermalPrinter.ALGIN_MIDDLE
        2 -> UsbThermalPrinter.ALGIN_RIGHT
        else -> UsbThermalPrinter.ALGIN_LEFT
    }

    @Composable
    fun TracedButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.primary,
        icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
        enabled: Boolean = true
    ) {
        CompactButton(
            text = text,
            onClick = { tap(text); onClick() },
            modifier = modifier,
            color = color,
            icon = icon,
            enabled = enabled
        )
    }

    fun escPosAlign(mode: Int) = byteArrayOf(0x1B, 0x61, mode.coerceIn(0, 2).toByte())

    suspend fun logOnMain(msg: String) {
        withContext(Dispatchers.Main) { log(msg) }
    }

    suspend fun run58mmStep(label: String, block: () -> Unit) {
        logOnMain("58mm step -> $label")
        try {
            block()
        } catch (e: Exception) {
            throw IllegalStateException("$label: ${e.javaClass.simpleName}: ${e.message}", e)
        }
    }

    suspend fun try58mmOptionalStep(label: String, block: () -> Unit) {
        logOnMain("58mm step -> $label")
        try {
            block()
        } catch (e: Exception) {
            logOnMain("⚠️ 58mm optional fail -> $label: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    fun get58mmStatus(): Int {
        val method = usbPrinter.javaClass.getMethod("checkStatus")
        val receiver = if (java.lang.reflect.Modifier.isStatic(method.modifiers)) null else usbPrinter
        return method.invoke(receiver) as Int
    }

    fun withBluetoothSocket(block: (BluetoothSocket) -> Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: error("Bluetooth no disponible")
        if (!adapter.isEnabled) error("Bluetooth apagado")
        if (lastBluetoothMac == "No detectada") error("MAC Bluetooth no disponible")
        val device = adapter.getRemoteDevice(lastBluetoothMac)
        var socket: BluetoothSocket? = null
        try {
            adapter.cancelDiscovery()
            log("BT socket -> ${device.name ?: "Sin nombre"} | MAC=$lastBluetoothMac")
            socket = try {
                device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                    .invoke(device, 1) as BluetoothSocket
            } catch (_: Exception) {
                device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            }
            socket.connect()
            logOk("BT socket conectado -> ${device.name ?: "Sin nombre"} | MAC=$lastBluetoothMac")
            block(socket)
        } finally {
            try { socket?.close() } catch (_: IOException) {}
        }
    }

    // --- Printer operations ---

    // --- Detect: solo informa, no conecta ---
    fun doDetect() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    log("Detect start -> MAC=$lastBluetoothMac")
                }
                val detected = SystemUtil.checkPrinter581(context)
                val rawType = if (detected == -1 || detected == 0) SystemUtil.getPrinterType() else detected
                val printerInterface = SystemUtil.getProperty("persist.printer.interface", "")
                withContext(Dispatchers.Main) {
                    log("SDK detect -> type=$rawType interface=${printerInterface.ifBlank { "n/a" }}")
                    val name = when (rawType) {
                        SystemUtil.PRINTER_PRT_COMMON -> "58mm USB (PRINTER_PRT_COMMON=$rawType)"
                        SystemUtil.PRINTER_80MM_USB_COMMON -> "80mm USB (PRINTER_80MM_USB_COMMON=$rawType)"
                        SystemUtil.PRINTER_SY581 -> "80mm Serial (PRINTER_SY581=$rawType)"
                        SystemUtil.PRINTER_PT486F08401MB -> "PT486 58mm/80mm ($rawType)"
                        else -> "Desconocida (type=$rawType)"
                    }
                    printerTypeName = name
                    printerStatus = "Detectada - sin conectar"
                    log("ℹ️ Impresora detectada: $name")
                    log("ℹ️ Usá los botones de Forzar para conectar")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error detectando: ${e.message}") }
            }
        }
    }

    // --- Conexión forzada 58mm USB ---
    fun doConnect58mmUsb() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("Conectando 58mm USB | speed=$usbPrintSpeedMode") }
                usbPrinter.start(usbPrintSpeedMode)
                usbPrinter.reset()
                printerType = SystemUtil.PRINTER_PRT_COMMON
                printerStatus = "Conectada 58mm USB"
                useBluetoothPrinter = false
                grayLevel = 100
                withContext(Dispatchers.Main) { logOk("Conectada 58mm USB speed=$usbPrintSpeedMode") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error 58mm USB: ${e.javaClass.simpleName}: ${e.message}") }
                printerStatus = "Error"
            }
        }
    }

    // --- Conexión forzada 80mm USB ---
    fun doConnect80mmUsb() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("Conectando 80mm USB") }
                powerControl.printerPower(1)
                SystemUtil.setProperty("persist.printer.interface", "usb")
                ThermalPrinter.setPaperWidth(ThermalPrinter.PAPER_80mm)
                ThermalPrinter.init80mmUsbPrinter(context)
                ThermalPrinter.start(context); ThermalPrinter.reset()
                val s = ThermalPrinter.checkStatus()
                printerType = SystemUtil.PRINTER_80MM_USB_COMMON
                printerStatus = statusToString(s)
                useBluetoothPrinter = false
                grayLevel = 3
                withContext(Dispatchers.Main) { logOk("Conectada 80mm USB (Status: $s)") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error 80mm USB: ${e.javaClass.simpleName}: ${e.message}") }
                printerStatus = "Error"
            }
        }
    }

    // --- Conexión forzada 80mm Serial ---
    fun doConnect80mmSerial() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("Conectando 80mm Serial") }
                powerControl.printerPower(1)
                SystemUtil.setProperty("persist.printer.interface", "serial")
                ThermalPrinter.setPaperWidth(ThermalPrinter.PAPER_80mm)
                ThermalPrinter.init80mmSerialPrinter()
                ThermalPrinter.start(context); ThermalPrinter.reset()
                val s = ThermalPrinter.checkStatus()
                printerType = SystemUtil.PRINTER_SY581
                printerStatus = statusToString(s)
                useBluetoothPrinter = false
                grayLevel = 3
                withContext(Dispatchers.Main) { logOk("Conectada 80mm Serial (Status: $s)") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error 80mm Serial: ${e.javaClass.simpleName}: ${e.message}") }
                printerStatus = "Error"
            }
        }
    }

    // doForce80mm removido - reemplazado por doConnect80mmUsb/doConnect80mmSerial

    fun doListBluetoothPrinters() {
        scope.launch(Dispatchers.IO) {
            try {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                if (adapter == null) {
                    withContext(Dispatchers.Main) { logWarn("Bluetooth no disponible") }
                    return@launch
                }
                withContext(Dispatchers.Main) {
                    log("Bluetooth -> enabled=${adapter.isEnabled} name=${adapter.name ?: "n/a"}")
                }
                val devices = adapter.bondedDevices.orEmpty().sortedBy { it.name ?: it.address }
                if (devices.isEmpty()) {
                    withContext(Dispatchers.Main) { logWarn("Sin dispositivos Bluetooth vinculados") }
                    return@launch
                }
                var telpoCount = 0
                devices.forEach { device ->
                    val name = device.name ?: "Sin nombre"
                    val classCode = device.bluetoothClass?.deviceClass ?: -1
                    val majorClass = device.bluetoothClass?.majorDeviceClass ?: -1
                    val hint = if (
                        name.contains("telpo", ignoreCase = true) ||
                        name.contains("printer", ignoreCase = true) ||
                        majorClass == BluetoothClass.Device.Major.IMAGING
                    ) {
                        telpoCount += 1
                        lastBluetoothName = name
                        lastBluetoothMac = device.address
                        " <- posible impresora/Telpo"
                    } else ""
                    withContext(Dispatchers.Main) {
                        log("BT bonded -> $name | ${device.address} | type=${device.type} | class=$classCode/$majorClass$hint")
                    }
                }
                withContext(Dispatchers.Main) {
                    if (telpoCount > 0) log("MAC candidata BT: $lastBluetoothMac")
                    logOk("Bluetooth revisado: ${devices.size} vinculados, $telpoCount candidatos")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error Bluetooth: ${e.message}") }
            }
        }
    }

    fun doProbeBluetoothPrinter() {
        scope.launch(Dispatchers.IO) {
            fun isCandidate(device: BluetoothDevice): Boolean {
                val name = device.name ?: ""
                val majorClass = device.bluetoothClass?.majorDeviceClass ?: -1
                return name.contains("telpo", ignoreCase = true) ||
                        name.contains("printer", ignoreCase = true) ||
                        majorClass == BluetoothClass.Device.Major.IMAGING
            }

            fun probeRfcomm(device: BluetoothDevice): Boolean {
                var socket: BluetoothSocket? = null
                return try {
                    socket = try {
                        device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket
                    } catch (_: Exception) {
                        device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    }
                    socket.connect()
                    true
                } finally {
                    try { socket?.close() } catch (_: IOException) {}
                }
            }

            try {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                if (adapter == null) {
                    withContext(Dispatchers.Main) { logWarn("Bluetooth no disponible") }
                    return@launch
                }
                if (!adapter.isEnabled) {
                    withContext(Dispatchers.Main) { logWarn("Bluetooth apagado") }
                    return@launch
                }
                adapter.cancelDiscovery()
                val candidates = adapter.bondedDevices.orEmpty()
                    .filter(::isCandidate)
                    .sortedBy { it.name ?: it.address }
                if (candidates.isEmpty()) {
                    withContext(Dispatchers.Main) { logWarn("Sin candidatos BT para RFCOMM") }
                    return@launch
                }
                withContext(Dispatchers.Main) { log("Probe RFCOMM -> ${candidates.size} candidatos") }
                var connected = false
                candidates.forEach { device ->
                    if (connected) return@forEach
                    val name = device.name ?: "Sin nombre"
                    lastBluetoothName = name
                    lastBluetoothMac = device.address
                    withContext(Dispatchers.Main) { log("Probando BT -> $name | MAC=${device.address}") }
                    runCatching { probeRfcomm(device) }
                        .onSuccess { ok ->
                            if (ok) {
                                connected = true
                                withContext(Dispatchers.Main) {
                                    logOk("RFCOMM OK -> $name | MAC=${device.address}")
                                    useBluetoothPrinter = true
                                    printerTypeName = "80mm BT [$name]"
                                    printerStatus = "BT RFCOMM OK"
                                }
                            }
                        }
                        .onFailure { e ->
                            withContext(Dispatchers.Main) {
                                logWarn("RFCOMM FAIL -> $name | MAC=${device.address} | ${e.message}")
                            }
                        }
                }
                if (!connected) {
                    withContext(Dispatchers.Main) { logWarn("RFCOMM sin conexión. Última MAC=$lastBluetoothMac") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error probe BT: ${e.message}") }
            }
        }
    }

    fun doCheckStatus() {
        scope.launch(Dispatchers.IO) {
            try {
                if (isBluetooth80mm) {
                    withBluetoothSocket { }
                    withContext(Dispatchers.Main) {
                        printerStatus = "BT RFCOMM OK"
                        logOk("Estado BT OK -> $lastBluetoothName | MAC=$lastBluetoothMac")
                    }
                    return@launch
                }
                val s = if (is58mm) get58mmStatus() else ThermalPrinter.checkStatus()
                printerStatus = statusToString(s)
                withContext(Dispatchers.Main) { log("Estado: ${statusToString(s)}") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error estado: ${e.javaClass.simpleName}: ${e.message}") }
            }
        }
    }

    fun doGetVersion() {
        scope.launch(Dispatchers.IO) {
            try {
                if (isBluetooth80mm) {
                    withContext(Dispatchers.Main) { log("Versión BT no disponible en SDK | $lastBluetoothName | MAC=$lastBluetoothMac") }
                    return@launch
                }
                val v = if (is58mm) usbPrinter.version else ThermalPrinter.getVersion()
                withContext(Dispatchers.Main) { logOk("Versión: $v") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error versión: ${e.message}") }
            }
        }
    }

    fun doGetPrintCount() {
        scope.launch(Dispatchers.Main) {
            try {
                val count = ThermalPrinter.getPrintCount()
                logOk("Impresiones: $count")
            } catch (e: Exception) { logError("Error contador: ${e.message}") }
        }
    }

    fun doSetProtectTemp() {
        scope.launch(Dispatchers.IO) {
            try {
                ThermalPrinter.setTem(protectTemp)
                withContext(Dispatchers.Main) { logOk("Temp. protección: ${protectTemp}°C") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error temp: ${e.message}") }
            }
        }
    }

    fun doPrint() {
        scope.launch(Dispatchers.IO) {
            try {
                val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                withContext(Dispatchers.Main) {
                    log("Print start -> ready=$isPrinterReady | 58=$is58mm | 80=$is80mm | bt=$isBluetooth80mm | gray=$grayLevel | feed=$paperFeedLines")
                }
                if (isBluetooth80mm) {
                    val size = (((fontWidthMul - 1).coerceIn(0, 7) shl 4) or (fontHeightMul - 1).coerceIn(0, 7)).toByte()
                    val payload = buildList<Byte> {
                        addAll(byteArrayOf(0x1B, 0x40).toList())
                        addAll(escPosAlign(alignment).toList())
                        addAll(byteArrayOf(0x1B, 0x45, if (isBold) 1 else 0).toList())
                        addAll(byteArrayOf(0x1D, 0x42, if (isInverse) 1 else 0).toList())
                        addAll(byteArrayOf(0x1D, 0x21, size).toList())
                        addAll(("$printText\nHora: $now\n").toByteArray().toList())
                        addAll(byteArrayOf(0x0A, 0x0A, 0x1B, 0x64, paperFeedLines.coerceIn(0, 255).toByte()).toList())
                    }.toByteArray()
                    withContext(Dispatchers.Main) {
                        log("Print branch -> Bluetooth RFCOMM | bytes=${payload.size} | MAC=$lastBluetoothMac")
                        logWarn("BT raw ESC/POS -> gray/speed SDK no aplican; requieren comandos propietarios")
                    }
                    withBluetoothSocket { socket ->
                        socket.outputStream.write(payload)
                        socket.outputStream.flush()
                    }
                } else if (is58mm) {
                    val clampedLeft = leftMargin.coerceIn(0, 255)
                    val clampedLine = lineSpacing.coerceIn(0, 255)
                    val clampedFont = fontSize.coerceIn(8, 64)
                    val clampedGray = grayLevel.coerceIn(0, 200)
                    withContext(Dispatchers.Main) {
                        log("Print branch -> UsbThermalPrinter 58mm | size=$clampedFont gray=$clampedGray left=$clampedLeft line=$clampedLine")
                    }
                    run58mmStep("start($usbPrintSpeedMode)") { usbPrinter.start(usbPrintSpeedMode) }
                    run58mmStep("reset") { usbPrinter.reset() }
                    run58mmStep("setAlgin($alignment)") { usbPrinter.setAlgin(alignConst(alignment)) }
                    run58mmStep("setLeftIndent($clampedLeft)") { usbPrinter.setLeftIndent(clampedLeft) }
                    run58mmStep("setLineSpace($clampedLine)") { usbPrinter.setLineSpace(clampedLine) }
                    run58mmStep("setBold($isBold)") { usbPrinter.setBold(isBold) }
                    run58mmStep("setTextSize($clampedFont)") { usbPrinter.setTextSize(clampedFont) }
                    run58mmStep("setMonoSpace(false)") { usbPrinter.setMonoSpace(false) }
                    // SDK doc says gray support depends on hardware; demo even notes setGray can error.
                    try {
                        run58mmStep("setGray($clampedGray)") { usbPrinter.setGray(clampedGray) }
                    } catch (e: Exception) {
                        if (clampedGray > 1) {
                            usbGrayMax = 1
                            // También clampear el nivel actual para que no falle de nuevo
                            if (grayLevel > 1) grayLevel = 1
                            withContext(Dispatchers.Main) { logWarn("58mm gray soporte real = 0 o 1. Ajusto UI y valor.") }
                        }
                        withContext(Dispatchers.Main) { logWarn("58mm optional fail -> setGray($clampedGray): ${e.cause?.javaClass?.simpleName ?: e.javaClass.simpleName}: ${e.cause?.message ?: e.message}") }
                    }
                    if (isItalic) run58mmStep("setItalic(true)") { usbPrinter.setItalic(true) }
                    // 58mm solo acepta enlarge hasta x2 (x3+ lanza IllegalArgumentException)
                    val safeWidth = fontWidthMul.coerceIn(1, 2)
                    val safeHeight = fontHeightMul.coerceIn(1, 2)
                    if (safeWidth > 1 || safeHeight > 1) {
                        run58mmStep("enlargeFontSize($safeWidth,$safeHeight)") {
                            usbPrinter.enlargeFontSize(safeWidth, safeHeight)
                        }
                    }
                    run58mmStep("addString(len=${printText.length})") { usbPrinter.addString("$printText\nHora: $now\n") }
                    run58mmStep("printString") { usbPrinter.printString() }
                    run58mmStep("walkPaper($paperFeedLines)") { usbPrinter.walkPaper(paperFeedLines) }
                } else if (is80mm) {
                    withContext(Dispatchers.Main) { log("Print branch -> ThermalPrinter 80mm") }
                    ThermalPrinter.reset()
                    ThermalPrinter.setAlgin(alignConst(alignment))
                    ThermalPrinter.setGray(grayLevel)
                    if (isInverse) ThermalPrinter.setInverse(true)
                    if (fontWidthMul > 1 || fontHeightMul > 1)
                        ThermalPrinter.enlargeFontSize(fontWidthMul, fontHeightMul)
                    ThermalPrinter.addString("$printText\nHora: $now\n")
                    ThermalPrinter.printString()
                    ThermalPrinter.walkPaper(paperFeedLines)
                } else {
                    withContext(Dispatchers.Main) { logWarn("Impresora no conectada") }
                    return@launch
                }
                withContext(Dispatchers.Main) { logOk("Impresión enviada") }
            } catch (e: Exception) {
                val msg = e.toString()
                withContext(Dispatchers.Main) {
                    when {
                        msg.contains("NoPaper") -> logError("SIN PAPEL")
                        msg.contains("OverHeat") -> logError("🔥 SOBRECALENTAMIENTO")
                        else -> logError("Error: ${e.javaClass.simpleName}: ${e.message}")
                    }
                }
            }
        }
    }

    fun doPaperFeed() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    log("Feed start -> lines=$paperFeedLines | 58=$is58mm | 80=$is80mm | bt=$isBluetooth80mm")
                }
                if (isBluetooth80mm) {
                    withBluetoothSocket { socket ->
                        socket.outputStream.write(byteArrayOf(0x1B, 0x64, paperFeedLines.coerceIn(0, 255).toByte()))
                        socket.outputStream.flush()
                    }
                } else if (is58mm) usbPrinter.walkPaper(paperFeedLines)
                else ThermalPrinter.walkPaper(paperFeedLines)
                withContext(Dispatchers.Main) { logOk("Avanzar papel: $paperFeedLines líneas") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error avance: ${e.javaClass.simpleName}: ${e.message}") }
            }
        }
    }

    fun doPaperCut() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("Cut start -> 58=$is58mm | 80=$is80mm | bt=$isBluetooth80mm") }
                if (isBluetooth80mm) {
                    withBluetoothSocket { socket ->
                        socket.outputStream.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00))
                        socket.outputStream.flush()
                    }
                } else if (is58mm) {
                    // 58mm: intento via método directo, fallback ESC/POS
                    try {
                        val cutMethod = usbPrinter.javaClass.getMethod("paperCut")
                        cutMethod.invoke(usbPrinter)
                        withContext(Dispatchers.Main) { log("58mm cut -> paperCut() OK") }
                    } catch (ex: NoSuchMethodException) {
                        withContext(Dispatchers.Main) { logWarn("58mm cut -> paperCut() no existe, enviando ESC/POS GS V") }
                        val cmdMethod = usbPrinter.javaClass.getMethod("sendCommand", ByteArray::class.java, Int::class.java)
                        val cmd = byteArrayOf(0x1D, 0x56, 0x42, 0x00)
                        cmdMethod.invoke(usbPrinter, cmd, cmd.size)
                    }
                } else {
                    ThermalPrinter.paperCut()
                }
                withContext(Dispatchers.Main) { logOk("Papel cortado") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error corte: ${e.javaClass.simpleName}: ${e.message}") }
            }
        }
    }

    fun doStop() {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("Stop start -> 58=$is58mm | 80=$is80mm | bt=$isBluetooth80mm") }
                if (isBluetooth80mm) {
                    useBluetoothPrinter = false
                } else if (is58mm) usbPrinter.stop()
                else { ThermalPrinter.stop(context); powerControl.printerPower(0) }
                withContext(Dispatchers.Main) { logOk("Impresora apagada"); printerStatus = "Apagada" }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error apagando: ${e.message}") }
            }
        }
    }

    fun doSystemInfo() {
        scope.launch(Dispatchers.IO) {
            try {
                val cpu = SystemUtil.getCpuRate()
                val temp = SystemUtil.getCpuTem()
                val mem = SystemUtil.getMemRate()
                withContext(Dispatchers.Main) {
                    log("📊 CPU: ${cpu}% | Temp: ${temp}°C | RAM: ${mem}%")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Error info: ${e.message}") }
            }
            try {
                val supported = SystemUtil.getDeviceSupport()
                if (supported != null && supported.isNotEmpty()) {
                    withContext(Dispatchers.Main) { log("Funciones: ${supported.joinToString(", ")}") }
                }
            } catch (_: Exception) {}
        }
    }

    fun startBarcodeScan() {
        if (isScanning) return
        scope.launch(Dispatchers.IO) {
            isScanning = true
            withContext(Dispatchers.Main) { log("Scanner HW | decodePower(1)...") }
            try {
                val pwrResult = powerControl.decodePower(1)
                withContext(Dispatchers.Main) { log("decodePower(1) -> $pwrResult") }
                Thread.sleep(800)

                // Estrategia 1: baud 115200, escucha auto-scan (sin trigger)
                var scannedData = ""
                val open1 = decodeReader.open(115200)
                withContext(Dispatchers.Main) { log("open(115200) -> $open1 | escuchando 2s (auto-scan)...") }
                decodeReader.setDecodeReaderListener { data ->
                    if (data != null && data.isNotEmpty()) scannedData = String(data).trim()
                }
                val t1 = System.currentTimeMillis() + 2000
                while (System.currentTimeMillis() < t1 && scannedData.isEmpty()) Thread.sleep(80)

                if (scannedData.isEmpty()) {
                    // Estrategia 2: comando trigger Newland (~\x010000@SCNTRG1;\x03)
                    val newlandCmd = byteArrayOf(0x7E,0x01,0x30,0x30,0x30,0x30,0x40,0x53,0x43,0x4E,0x54,0x52,0x47,0x31,0x3B,0x03)
                    decodeReader.cmdSend(newlandCmd)
                    withContext(Dispatchers.Main) { log("Trigger Newland enviado, escuchando 2s...") }
                    val t2 = System.currentTimeMillis() + 2000
                    while (System.currentTimeMillis() < t2 && scannedData.isEmpty()) Thread.sleep(80)
                }

                try { decodeReader.close() } catch (_: Exception) {}

                if (scannedData.isEmpty()) {
                    // Estrategia 3: baud 9600, escucha + trigger
                    val open2 = decodeReader.open(9600)
                    withContext(Dispatchers.Main) { log("open(9600) -> $open2 | escuchando 1s...") }
                    decodeReader.setDecodeReaderListener { data ->
                        if (data != null && data.isNotEmpty()) scannedData = String(data).trim()
                    }
                    val t3 = System.currentTimeMillis() + 1000
                    while (System.currentTimeMillis() < t3 && scannedData.isEmpty()) Thread.sleep(80)
                    if (scannedData.isEmpty()) {
                        decodeReader.cmdSend(byteArrayOf(0x7E,0x01,0x30,0x30,0x30,0x30,0x40,0x53,0x43,0x4E,0x54,0x52,0x47,0x31,0x3B,0x03))
                        val t4 = System.currentTimeMillis() + 1000
                        while (System.currentTimeMillis() < t4 && scannedData.isEmpty()) Thread.sleep(80)
                    }
                    try { decodeReader.close() } catch (_: Exception) {}
                }

                withContext(Dispatchers.Main) {
                    if (scannedData.isNotEmpty()) {
                        lastScanned = scannedData
                        logOk("Scanner HW: \"$scannedData\"")
                    } else {
                        logWarn("Scanner HW: sin respuesta (3 estrategias). El módulo puede no estar presente.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("Scanner HW: ${e.javaClass.simpleName}: ${e.message}") }
            } finally {
                try { powerControl.decodePower(0) } catch (_: Exception) {}
                isScanning = false
            }
        }
    }

    fun startNfcScan(timeoutMs: Long = 8000L) {
        scope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { log("NFC start | nfcPower(1) -> ...") }
                val pwrResult = powerControl.nfcPower(1)
                withContext(Dispatchers.Main) { log("nfcPower(1) -> $pwrResult") }
                Thread.sleep(500)
                withContext(Dispatchers.Main) { log("NFC initSerial...") }
                nfcUtil.initSerial()
                Thread.sleep(300)
                // Verificar versión del MCU NFC
                try {
                    val ver = nfcUtil.checkVersion()
                    withContext(Dispatchers.Main) { log("NFC MCU version: $ver") }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { logWarn("NFC MCU version error: ${e.javaClass.simpleName}: ${e.message}") }
                }
                withContext(Dispatchers.Main) { log("NFC esperando tarjeta (${timeoutMs/1000}s)... Acerá el tag al lector.") }
                val endTime = System.currentTimeMillis() + timeoutMs
                var found = false
                var attempts = 0
                while (System.currentTimeMillis() < endTime && !found) {
                    attempts++
                    try {
                        val result = nfcUtil.selectCard()
                        if (result != null) {
                            val hex = result.cardNum?.joinToString("") { String.format("%02X", it) } ?: "(sin UID)"
                            withContext(Dispatchers.Main) {
                                logOk("NFC: tipo=${result.cardType?.name ?: "?"} | UID=$hex | intentos=$attempts")
                            }
                            found = true
                        }
                    } catch (e: Exception) {
                        // ignorar errores de polling, solo logear cada 10 intentos
                        if (attempts % 10 == 0) {
                            withContext(Dispatchers.Main) { logWarn("NFC poll #$attempts: ${e.javaClass.simpleName}") }
                        }
                    }
                    if (!found) Thread.sleep(150)
                }
                if (!found) withContext(Dispatchers.Main) { logWarn("NFC timeout ($attempts intentos). Verificá que el tag sea ISO14443-A/B, M1 o CPU.") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { logError("NFC: ${e.javaClass.simpleName}: ${e.message}") }
            } finally {
                try { nfcUtil.destroySerial() } catch (_: Exception) {}
                try { powerControl.nfcPower(0) } catch (_: Exception) {}
                withContext(Dispatchers.Main) { log("NFC: serial cerrado, power off") }
            }
        }
    }

    // Physical button listener
    DisposableEffect(Unit) {
        commonUtil.registerInputBroadcast(context)
        commonUtil.setInputListener(object : IInputListener {
            override fun wiegandInput(inputData: ByteArray?) {}
            override fun input(sw: Int, status: Int) {
                if (sw == 2 && status == 0) {
                    scope.launch(Dispatchers.Main) {
                        val act = context as? MainActivity
                        if (act != null) {
                            if (!isScanningBackground) {
                                log("Botón FUNC → Iniciando escáner cámara 2do plano")
                                act.startBackgroundCameraScan(
                                    onResult = { result ->
                                        lastScanned = result
                                        logOk("Escáner 2do plano (Botón FUNC): \"$result\"")
                                    },
                                    onError = { err ->
                                        logWarn("Escáner 2do plano (Botón FUNC): $err")
                                    },
                                    onStateChange = { isScanningBackground = it }
                                )
                            } else {
                                log("Botón FUNC → Cancelando escáner cámara 2do plano")
                                act.stopBackgroundCameraScan { isScanningBackground = it }
                            }
                        }
                    }
                }
            }
        })
        onDispose { commonUtil.unRegisterInputBroadcast() }
    }

    LaunchedEffect(logs) { logScroll.animateScrollTo(logScroll.maxValue) }

    // ========= UI =========
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).padding(top = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            // LEFT: Console
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF1A1A2E))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    val dir = File(context.cacheDir, "shared_logs").apply { mkdirs() }
                                    val file = File(dir, "impresora_logs_$ts.txt")
                                    file.writeText(logs)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, file.name)
                                        putExtra(Intent.EXTRA_TEXT, "Logs app impresora")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Compartir logs"))
                                    logOk("Logs listos en TXT")
                                } catch (e: Exception) {
                                    logError("Error compartiendo logs: ${e.message}")
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir logs")
                            Spacer(Modifier.width(6.dp))
                            Text("COMPARTIR TXT", fontSize = 11.sp)
                        }
                    }
                    Text(
                        text = logs,
                        color = Color(0xFF00FF41),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.fillMaxSize().verticalScroll(logScroll).padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            // RIGHT: Controls
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Text("Impresora: $printerTypeName", style = MaterialTheme.typography.bodySmall)
                    Text("Estado: $printerStatus", style = MaterialTheme.typography.bodySmall)
                    Text("MAC BT: $lastBluetoothMac", style = MaterialTheme.typography.bodySmall)
                    val speedLabel = when (usbPrintSpeedMode) { 0 -> "Mín (0)" ; 1 -> "Med (1)" ; else -> "Máx (2)" }
                    Text("SDK speed 58mm: $speedLabel", style = MaterialTheme.typography.bodySmall)
                }

                item {
                    CollapsibleSection("Estado y Diagnóstico", Icons.Default.MonitorHeart, Color(0xFF4CAF50), true) {
                    // -- Solo detecta, no conecta --
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("DETECTAR", ::doDetect, Modifier.weight(1f), icon = Icons.Default.Search)
                        TracedButton("ESTADO", ::doCheckStatus, Modifier.weight(1f), Color(0xFF2196F3), Icons.Default.Info, isPrinterReady)
                    }
                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                    // -- Forzar conexión --
                    Text("Forzar conexión:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("58mm USB", ::doConnect58mmUsb, Modifier.weight(1f), Color(0xFF00838F), Icons.Default.Usb)
                        TracedButton("58mm BT", ::doProbeBluetoothPrinter, Modifier.weight(1f), Color(0xFF1565C0), Icons.Default.Bluetooth)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("80mm USB", ::doConnect80mmUsb, Modifier.weight(1f), Color(0xFF00897B), Icons.Default.Usb)
                        TracedButton("80mm Serial", ::doConnect80mmSerial, Modifier.weight(1f), Color(0xFF6A1B9A), Icons.Default.SettingsEthernet)
                    }
                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("VERSIÓN", ::doGetVersion, Modifier.weight(1f), Color(0xFF607D8B), Icons.Default.Code, isPrinterReady)
                        TracedButton("SISTEMA", ::doSystemInfo, Modifier.weight(1f), Color(0xFF795548), Icons.Default.Memory)
                    }
                    TracedButton("BLUETOOTH INTERNOS", ::doListBluetoothPrinters, Modifier.fillMaxWidth(), Color(0xFF1565C0), Icons.Default.Bluetooth)
                    if (is80mm) {
                        TracedButton("CONTADOR", ::doGetPrintCount, Modifier.fillMaxWidth(), Color(0xFF9C27B0), Icons.Default.Numbers)
                        NumericCounter("Temp protección (°C):", protectTemp, { protectTemp = it }, 60, 127, 5)
                        TracedButton("APLICAR TEMP", ::doSetProtectTemp, Modifier.fillMaxWidth(), Color(0xFFFF5722))
                    }
                }
                }

                item {
                    CollapsibleSection("Densidad / Gris", Icons.Default.Contrast, Color(0xFF9C27B0)) {
                    if (is58mm) {
                        // 58mm: hardware confirmado acepta solo 0 o 1 (int, sin decimales)
                        Text("⚠️ SDK 58mm: setGray acepta solo enteros 0 o 1", fontSize = 10.sp, color = Color(0xFFE65100))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TracedButton(
                                text = "Gris 0 (claro)",
                                onClick = { grayLevel = 0; log("Gray 58mm -> 0") },
                                modifier = Modifier.weight(1f),
                                color = if (grayLevel == 0) Color(0xFF1B5E20) else Color(0xFF607D8B)
                            )
                            TracedButton(
                                text = "Gris 1 (oscuro)",
                                onClick = { grayLevel = 1; log("Gray 58mm -> 1") },
                                modifier = Modifier.weight(1f),
                                color = if (grayLevel == 1) Color(0xFFB71C1C) else Color(0xFF607D8B)
                            )
                        }
                        Text("Actual: $grayLevel | SDK int (0=tenue, 1=más denso)", fontSize = 10.sp, color = Color.Gray)
                    } else {
                        // 80mm: rango 0-12
                        NumericCounter("Nivel de gris:", grayLevel, { grayLevel = it }, 0, grayMax)
                        GrayLevelBar(grayLevel, grayMax)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TracedButton("Reset ($grayDefault)", { grayLevel = grayDefault }, Modifier.weight(1f), Color(0xFF607D8B))
                            TracedButton("Mínimo", { grayLevel = 0 }, Modifier.weight(1f), Color(0xFF607D8B))
                            TracedButton("Máximo", { grayLevel = grayMax }, Modifier.weight(1f), Color(0xFFE53935))
                        }
                        Text("Rango: 0-$grayMax | Actual: $grayLevel", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                }

                // --- Velocidad 58mm (solo visible si 58mm conectada) ---
                if (is58mm) item {
                    CollapsibleSection("Velocidad 58mm", Icons.Default.Speed, Color(0xFF00838F)) {
                    val speedNames = listOf("Mínima (0)", "Media (1)", "Máxima (2)")
                    NumericCounter(
                        label = "Velocidad: ${speedNames.getOrElse(usbPrintSpeedMode) { "$usbPrintSpeedMode" }}",
                        value = usbPrintSpeedMode,
                        onValueChange = { v ->
                            usbPrintSpeedMode = v
                            log("ℹ️ Velocidad 58mm -> ${speedNames.getOrElse(v) { "$v" }}")
                        },
                        min = 0, max = 2
                    )
                    SpeedLevelBar(usbPrintSpeedMode, 2)
                    Text(
                        "Se aplica al conectar o en cada start(). Rango: 0-2",
                        fontSize = 10.sp, color = Color.Gray
                    )
                }
                }

                item {
                    CollapsibleSection("Formato de Texto", Icons.Default.TextFormat, Color(0xFF2196F3)) {
                    AlignmentSelector(alignment) { alignment = it }
                    if (is58mm) {
                        NumericCounter("Tamaño fuente:", fontSize, { fontSize = it }, 8, 64, 4)
                    } else if (is80mm) {
                        NumericCounter("Tamaño fuente:", fontSize, { fontSize = it }, 1, 2)
                    }
                    // 58mm: enlargeFontSize acepta solo 1-2 (x3+ lanza IllegalArgumentException)
                    val maxMul = if (is58mm) 2 else 4
                    NumericCounter("Ancho x:", fontWidthMul, { fontWidthMul = it }, 1, maxMul)
                    NumericCounter("Alto x:", fontHeightMul, { fontHeightMul = it }, 1, maxMul)
                    if (is58mm) Text("⚠️ 58mm: máx x2 (x3+ no soportado)", fontSize = 10.sp, color = Color(0xFFE65100))
                    if (is58mm) {
                        LabeledSwitch("Negrita", isBold, { isBold = it })
                        LabeledSwitch("Cursiva", isItalic, { isItalic = it })
                        NumericCounter("Margen izq:", leftMargin, { leftMargin = it }, 0, 255, 8)
                        NumericCounter("Interlineado:", lineSpacing, { lineSpacing = it }, 0, 255)
                    }
                    if (is80mm) {
                        LabeledSwitch("Invertir colores", isInverse, { isInverse = it })
                    }
                }
                }

                item {
                    CollapsibleSection("Imprimir", Icons.Default.Print, Color(0xFF4CAF50)) {
                    OutlinedTextField(
                        value = printText,
                        onValueChange = { printText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                        label = { Text("Texto a imprimir", fontSize = 10.sp) }
                    )
                    TracedButton("🖨 IMPRIMIR TEXTO", ::doPrint, Modifier.fillMaxWidth(), Color(0xFF2E7D32), Icons.Default.Print, isPrinterReady)
                    Spacer(Modifier.height(4.dp))
                    // Impresión larga: genera 60 líneas, mide tiempo real antes/después del print
                    TracedButton(
                        text = "⏱ IMPRIMIR LARGO (test velocidad)",
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val tStart = System.currentTimeMillis()
                                    val fmtHora = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                                    val horaInicio = fmtHora.format(Date(tStart))
                                    withContext(Dispatchers.Main) { log("Print largo start -> speed=$usbPrintSpeedMode | inicio=$horaInicio") }
                                    val linea = "ABCDEFGHIJ 0123456789 abcdefghij !@#$%&*()\n"
                                    // El bloque NO incluye hora fin — se imprime en un 2do pass real
                                    val bloque = buildString {
                                        append("=== TEST VELOCIDAD ===\n")
                                        append("Speed: $usbPrintSpeedMode | Gray: $grayLevel\n")
                                        append("Inicio: $horaInicio\n")
                                        append("--- inicio ---\n")
                                        repeat(60) { i -> append("${(i+1).toString().padStart(2)}: $linea") }
                                        append("--- fin ---\n")
                                    }
                                    if (is58mm) {
                                        usbPrinter.start(usbPrintSpeedMode); usbPrinter.reset()
                                        usbPrinter.setAlgin(alignConst(alignment))
                                        usbPrinter.setTextSize(fontSize.coerceIn(8, 64))
                                        try { usbPrinter.setGray(grayLevel.coerceIn(0, usbGrayMax)) } catch (_: Exception) {}
                                        usbPrinter.addString(bloque)
                                        usbPrinter.printString()
                                        // Capturar hora DESPUÉS de que printString() retorna = tiempo real
                                        val horaFin = fmtHora.format(Date())
                                        usbPrinter.addString("Fin: $horaFin\n")
                                        usbPrinter.printString()
                                        usbPrinter.walkPaper(paperFeedLines)
                                    } else if (is80mm) {
                                        ThermalPrinter.reset()
                                        ThermalPrinter.setGray(grayLevel)
                                        ThermalPrinter.addString(bloque)
                                        ThermalPrinter.printString(ThermalPrinter.SPANISH)
                                        val horaFin = fmtHora.format(Date())
                                        ThermalPrinter.addString("Fin: $horaFin\n")
                                        ThermalPrinter.printString(ThermalPrinter.SPANISH)
                                        ThermalPrinter.walkPaper(paperFeedLines)
                                    } else {
                                        withContext(Dispatchers.Main) { logWarn("Impresora no conectada") }
                                        return@launch
                                    }
                                    val tEnd = System.currentTimeMillis()
                                    val elapsed = tEnd - tStart
                                    val horaFinLog = fmtHora.format(Date(tEnd))
                                    withContext(Dispatchers.Main) {
                                        logOk("Print largo OK | inicio=$horaInicio | fin=$horaFinLog | duración=${elapsed}ms (~${elapsed/1000}s)")
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) { logError("Print largo: ${e.javaClass.simpleName}: ${e.message}") }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF4527A0),
                        icon = Icons.Default.Timer,
                        enabled = isPrinterReady
                    )
                }
                }

                item {
                    CollapsibleSection("Control de Papel", Icons.Default.Receipt, Color(0xFFFF9800)) {
                    NumericCounter("Avanzar líneas:", paperFeedLines, { paperFeedLines = it }, 1, 255, 5)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("AVANZAR", ::doPaperFeed, Modifier.weight(1f), Color(0xFFFF9800), Icons.Default.ArrowDownward, isPrinterReady)
                        // Cortar disponible para 80mm, BT, y 58mm (fallback ESC/POS)
                        if (isPrinterReady) {
                            TracedButton("CORTAR", ::doPaperCut, Modifier.weight(1f), Color(0xFFF44336), Icons.Default.ContentCut)
                        }
                    }
                }
                }

                item {
                    CollapsibleSection("NFC y Scanner", Icons.Default.Nfc, Color(0xFF1976D2)) {
                        // --- NFC Status ---
                        val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }
                        val nfcStatusText = when {
                            nfcAdapter == null -> "❌ NFC no soportado en este dispositivo"
                            !nfcAdapter.isEnabled -> "⚠️ NFC desactivado (activalo en Ajustes)"
                            isNfcListeningAndroid -> "📶 NFC Activo - Esperando tarjeta..."
                            else -> "✅ NFC Listo (Presioná para leer)"
                        }
                        Text(nfcStatusText, style = MaterialTheme.typography.labelSmall, color = if (isNfcListeningAndroid) Color(0xFF4FC3F7) else if (nfcAdapter?.isEnabled == true) Color(0xFF81C784) else Color(0xFFFFB74D))
                        
                        if (nfcAdapter != null && !nfcAdapter.isEnabled) {
                            TracedButton(
                                text = "Abrir Ajustes NFC",
                                onClick = {
                                    try {
                                        context.startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                                    } catch (_: Exception) {
                                        try {
                                            context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                                        } catch (_: Exception) {}
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                color = Color(0xFFE65100),
                                icon = Icons.Default.Settings
                            )
                        }

                        // Registrar callbacks para Input de Teclado del Escáner
                        DisposableEffect(Unit) {
                            val act = context as? MainActivity
                            act?.onKeyboardScanDetected = { scanned ->
                                scope.launch(Dispatchers.Main) {
                                    lastScanned = scanned
                                    logOk("🔌 Escáner (Emul. Teclado): \"$scanned\"")
                                }
                            }
                            onDispose {
                                act?.onKeyboardScanDetected = null
                            }
                        }
                        
                        // --- NFC Android Button ---
                        TracedButton(
                            text = if (isNfcListeningAndroid) "Detener Detección NFC" else "LEER TARJETA NFC",
                            onClick = {
                                val act = context as? MainActivity
                                if (act != null) {
                                    if (isNfcListeningAndroid) {
                                        act.stopNfcListening { isNfcListeningAndroid = it }
                                        log("Detección NFC cancelada")
                                    } else {
                                        log("NFC → Esperando tarjeta... Acercala al lector.")
                                        act.startNfcListening(
                                            onResult = { uid, techs ->
                                                lastScanned = uid
                                                logOk("📶 NFC Android: UID=$uid | techs=$techs")
                                            },
                                            onError = { err ->
                                                logWarn("NFC Android: $err")
                                            },
                                            onStateChange = { isNfcListeningAndroid = it }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isNfcListeningAndroid) Color(0xFFD32F2F) else Color(0xFF1565C0),
                            icon = Icons.Default.Nfc,
                            enabled = nfcAdapter?.isEnabled == true
                        )

                        Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 6.dp))
                        
                        // --- Cámara en 2do Plano (Background sin UI) ---
                        Text("Cámara (2do plano - silencioso, sin preview)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        TracedButton(
                            text = if (isScanningBackground) "Detener Escaneo Cámara" else "ESCANEAR CÁMARA (2do plano)",
                            onClick = {
                                val act = context as? MainActivity
                                if (act != null) {
                                    if (isScanningBackground) {
                                        act.stopBackgroundCameraScan { isScanningBackground = it }
                                        log("Escáner 2do plano cancelado")
                                    } else {
                                        act.startBackgroundCameraScan(
                                            onResult = { result ->
                                                lastScanned = result
                                                logOk("Escáner 2do plano: \"$result\"")
                                            },
                                            onError = { err ->
                                                logWarn("Escáner 2do plano: $err")
                                            },
                                            onStateChange = { isScanningBackground = it }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isScanningBackground) Color(0xFFD32F2F) else Color(0xFF00796B),
                            icon = if (isScanningBackground) Icons.Default.Stop else Icons.Default.VisibilityOff
                        )
                        
                        Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                        Text("Último scan: $lastScanned", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                item {
                    CollapsibleSection("Utilidades", Icons.Default.Settings, Color(0xFF607D8B)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TracedButton("APAGAR", ::doStop, Modifier.weight(1f), Color(0xFFC62828), Icons.Default.PowerOff, isPrinterReady)
                        TracedButton("LIMPIAR", { logs = "--- CONSOLA ---\n" }, Modifier.weight(1f), Color(0xFF607D8B), Icons.Default.Delete)
                    }
                }
                }
            }
        }
    }
}
