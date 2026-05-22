@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

import com.common.apiutil.printer.UsbThermalPrinter
import com.common.apiutil.printer.ThermalPrinter
import com.common.apiutil.util.SystemUtil
import com.common.apiutil.nfc.NfcUtil
import com.common.apiutil.powercontrol.PowerControl
import com.common.apiutil.CommonException
import android.nfc.NfcAdapter

import com.common.apiutil.decode.DecodeReader
import com.common.callback.IDecodeReaderListener
import com.common.apiutil.pos.CommonUtil
import com.common.callback.IInputListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                HardwareScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Prevent default OS NFC apps (like the demo) from popping up
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.enableReaderMode(
            this,
            { _ -> /* handled manually by SDK */ },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
            NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.disableReaderMode(this)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun HardwareScreen() {
    val context = LocalContext.current as android.app.Activity
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA LIMPIA ---\n") }
    
    // SDK Instances
    val usbPrinter = remember { UsbThermalPrinter(context) }
    val nfcUtil = remember { NfcUtil(context) }
    val powerControl = remember { PowerControl(context) }
    val decodeReader = remember { DecodeReader(context) }
    val commonUtil = remember { CommonUtil(context) }

    // State
    var printerType by remember { mutableStateOf(-1) }
    var printerTypeName by remember { mutableStateOf("Desconocida") }
    var printerStatus by remember { mutableStateOf("Estado: No verificado") }
    
    var lastScanned by remember { mutableStateOf("Nada") }
    var isScanning by remember { mutableStateOf(false) }

    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $msg\n"
    }

    fun startBarcodeScan() {
        if (isScanning) return
        scope.launch(Dispatchers.IO) {
            isScanning = true
            withContext(Dispatchers.Main) { log("Activando Scanner (5s timeout)...") }
            try {
                powerControl.decodePower(1)
                Thread.sleep(500)
                decodeReader.open(115200) // Default baud
                
                var scannedData = ""
                decodeReader.setDecodeReaderListener { data ->
                    if (data != null) {
                        scannedData = String(data)
                    }
                }
                
                // Send trigger command
                decodeReader.cmdSend(com.common.apiutil.util.StringUtil.hexStringToBytes("7E01303030304053434E545247313B03"))
                
                // Wait for up to 5 seconds for a scan
                val endTime = System.currentTimeMillis() + 5000
                while (System.currentTimeMillis() < endTime && scannedData.isEmpty()) {
                    Thread.sleep(100)
                }
                
                if (scannedData.isNotEmpty()) {
                    withContext(Dispatchers.Main) { 
                        lastScanned = scannedData
                        log("Código Escaneado: $scannedData") 
                    }
                } else {
                    withContext(Dispatchers.Main) { log("Scanner: Timeout (No se leyó nada)") }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { log("Error Scanner: ${e.message}") }
            } finally {
                decodeReader.close()
                powerControl.decodePower(0)
                isScanning = false
            }
        }
    }

    DisposableEffect(Unit) {
        commonUtil.registerInputBroadcast(context)
        commonUtil.setInputListener(object : IInputListener {
            override fun wiegandInput(inputData: ByteArray?) {}
            override fun input(sw: Int, status: Int) {
                // sw == 2 usually corresponds to the side FUNC/Scan button
                if (sw == 2 && status == 0) { // status 0 = released
                    scope.launch(Dispatchers.Main) {
                        log("Botón físico FUNC presionado. Iniciando scan...")
                        startBarcodeScan()
                    }
                }
            }
        })
        
        onDispose {
            commonUtil.unRegisterInputBroadcast()
        }
    }

    LaunchedEffect(logs) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CONSOLA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black)
            ) {
                Text(
                    text = logs,
                    color = Color(0xFF00FF41),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(12.dp)
                )
            }

            // COMANDOS
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Impresora: $printerTypeName", style = MaterialTheme.typography.bodyMedium)
                Text(text = printerStatus, style = MaterialTheme.typography.bodyMedium)
                
                Divider(color = Color.Gray, thickness = 1.dp)

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                printerType = SystemUtil.checkPrinter581(context)
                                // Fallback detect
                                if (printerType == -1 || printerType == 0) {
                                    printerType = SystemUtil.getPrinterType()
                                }
                                
                                when (printerType) {
                                    SystemUtil.PRINTER_PRT_COMMON, SystemUtil.PRINTER_PT486F08401MB -> {
                                        printerTypeName = "Impresora 58mm (USB) [$printerType]"
                                        usbPrinter.start(0)
                                        usbPrinter.reset()
                                        printerStatus = "Estado: 58mm Conectada"
                                        withContext(Dispatchers.Main) { log("Conectada 58mm") }
                                    }
                                    SystemUtil.PRINTER_80MM_USB_COMMON, SystemUtil.PRINTER_SY581 -> {
                                        printerTypeName = "Impresora 80mm/Serial [$printerType]"
                                        powerControl.printerPower(1)
                                        ThermalPrinter.start(context)
                                        ThermalPrinter.reset()
                                        val status = ThermalPrinter.checkStatus()
                                        printerStatus = "Estado: 80mm Conectada (Cod: $status)"
                                        withContext(Dispatchers.Main) { log("Conectada 80mm (Status: $status)") }
                                    }
                                    else -> {
                                        printerTypeName = "No detectada (Cod: $printerType)"
                                        printerStatus = "Intentando forzar ThermalPrinter 80mm..."
                                        try {
                                            powerControl.printerPower(1)
                                            ThermalPrinter.start(context)
                                            ThermalPrinter.reset()
                                            val status = ThermalPrinter.checkStatus()
                                            printerStatus = "Forzada 80mm OK (Status: $status)"
                                            printerType = SystemUtil.PRINTER_80MM_USB_COMMON
                                            withContext(Dispatchers.Main) { log("Forzada conexión 80mm") }
                                        } catch (e: Exception) {
                                            try {
                                                usbPrinter.start(0)
                                                usbPrinter.reset()
                                                printerStatus = "Forzada 58mm OK"
                                                printerType = SystemUtil.PRINTER_PRT_COMMON
                                                withContext(Dispatchers.Main) { log("Forzada conexión 58mm") }
                                            } catch (e2: Exception) {
                                                printerStatus = "Error total de conexión"
                                                withContext(Dispatchers.Main) { log("Fallo en forzar conexion. Error 1: ${e.message}, Error 2: ${e2.message}") }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { 
                                    log("Error detectando: ${e.message}")
                                    printerStatus = "Estado: Error"
                                }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Search, null)
                    Spacer(Modifier.width(8.dp))
                    Text("DETECTAR")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                if (printerType == SystemUtil.PRINTER_PRT_COMMON || printerType == SystemUtil.PRINTER_PT486F08401MB) {
                                    usbPrinter.reset()
                                    usbPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                                    usbPrinter.addString("=== PRUEBA 58MM ===\n")
                                    usbPrinter.addString("Hora: $now\n")
                                    usbPrinter.printString()
                                    usbPrinter.walkPaper(20)
                                    withContext(Dispatchers.Main) { log("Impresión 58mm enviada") }
                                } else if (printerType == SystemUtil.PRINTER_80MM_USB_COMMON || printerType == SystemUtil.PRINTER_SY581) {
                                    ThermalPrinter.reset()
                                    ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT)
                                    ThermalPrinter.addString("=== PRUEBA 80MM ===\n")
                                    ThermalPrinter.addString("Hora: $now\n")
                                    ThermalPrinter.printString()
                                    ThermalPrinter.walkPaper(20)
                                    withContext(Dispatchers.Main) { log("Impresión 80mm enviada") }
                                } else {
                                    withContext(Dispatchers.Main) { log("Impresora no lista para imprimir") }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { log("Error imprimiendo: ${e.message}") }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(Modifier.width(8.dp))
                    Text("IMPRIMIR")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                if (printerType == SystemUtil.PRINTER_PRT_COMMON || printerType == SystemUtil.PRINTER_PT486F08401MB) {
                                    usbPrinter.stop()
                                } else {
                                    ThermalPrinter.stop(context)
                                    powerControl.printerPower(0)
                                }
                                withContext(Dispatchers.Main) { 
                                    log("Impresora detenida / apagada") 
                                    printerStatus = "Estado: Apagada"
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { log("Error apagando: ${e.message}") }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.PowerOff, null)
                    Spacer(Modifier.width(8.dp))
                    Text("APAGAR")
                }

                Divider(color = Color.Gray, thickness = 1.dp)

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                withContext(Dispatchers.Main) { log("NFC: Buscando por 5 segundos...") }
                                powerControl.nfcPower(1)
                                nfcUtil.initSerial()
                                Thread.sleep(500)
                                
                                val endTime = System.currentTimeMillis() + 5000
                                var found = false
                                
                                while (System.currentTimeMillis() < endTime && !found) {
                                    val result = nfcUtil.selectCard()
                                    if (result != null) {
                                        val hexData = result.cardNum.joinToString("") { String.format("%02X", it) }
                                        withContext(Dispatchers.Main) { log("NFC Leído!\nTipo: ${result.cardType.name}\nUID: $hexData") }
                                        found = true
                                    } else {
                                        Thread.sleep(200)
                                    }
                                }
                                
                                if (!found) {
                                    withContext(Dispatchers.Main) { log("NFC: Timeout (No se detectó tarjeta)") }
                                }
                                
                                // Clean up serial to avoid locking the port
                                nfcUtil.destroySerial()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { log("Error NFC: ${e.message}") }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Nfc, null)
                    Spacer(Modifier.width(8.dp))
                    Text("LEER NFC (5s)")
                }

                Divider(color = Color.Gray, thickness = 1.dp)

                Text(text = "Último Scan: $lastScanned", style = MaterialTheme.typography.bodyMedium)

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                    onClick = {
                        startBarcodeScan()
                    }
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(Modifier.width(8.dp))
                    Text("ESCANEAR (5s)")
                }
            }
        }
    }
}
