@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.app.PendingIntent
import android.content.*
import android.hardware.usb.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

private const val USB_DISCONNECT_TIMEOUT_MS = 2_000L
private const val TARGET_USB_PATH = "/dev/bus/usb/001/003"

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
}

@Composable
fun HardwareScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val logScroll = rememberScrollState()
    val uiScroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA LIMPIA ---\n") }

    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    var usbDevice by remember { mutableStateOf<UsbDevice?>(null) }
    var usbConnection by remember { mutableStateOf<UsbDeviceConnection?>(null) }
    var usbInterface by remember { mutableStateOf<UsbInterface?>(null) }
    var usbEndpointOut by remember { mutableStateOf<UsbEndpoint?>(null) }

    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $msg\n"
    }

    fun dumpUsbDevice(device: UsbDevice) {
        log("🔍 USB DEVICE DUMP")
        log("deviceName=${device.deviceName}")
        log("vendorId=${device.vendorId}")
        log("productId=${device.productId}")
        log("deviceClass=${device.deviceClass}")
        log("deviceSubclass=${device.deviceSubclass}")
        log("protocol=${device.deviceProtocol}")
        log("interfaces=${device.interfaceCount}")
        log("manufacturer=${device.manufacturerName ?: "N/A"}")
        log("product=${device.productName ?: "N/A"}")
        log("serial=${device.serialNumber ?: "N/A"}")

        for (i in 0 until device.interfaceCount) {
            val intf = device.getInterface(i)
            log(" ├─ Interface[$i]")
            log(" │  class=${intf.interfaceClass}")
            log(" │  subclass=${intf.interfaceSubclass}")
            log(" │  protocol=${intf.interfaceProtocol}")
            log(" │  endpoints=${intf.endpointCount}")

            for (e in 0 until intf.endpointCount) {
                val ep = intf.getEndpoint(e)
                log(
                    " │   └─ EP[$e] " +
                            "type=${ep.type} " +
                            "dir=${if (ep.direction == UsbConstants.USB_DIR_OUT) "OUT" else "IN"} " +
                            "maxPacket=${ep.maxPacketSize}"
                )
            }
        }
    }

    LaunchedEffect(logs) {
        logScroll.animateScrollTo(logScroll.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(uiScroll)
            .padding(16.dp)
    ) {

        /* ================= CONSOLA ================= */

        Box(
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Text(
                text = logs,
                color = Color(0xFF00FF41),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(logScroll)
                    .padding(12.dp)
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
                onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("logs", logs)
                    )
                    log("Logs copiados.")
                }
            ) {
                Icon(Icons.Default.ContentCopy, null, tint = Color.Cyan)
            }
        }

        Spacer(Modifier.height(12.dp))

        /* ================= LISTAR USB ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                log("Listando dispositivos USB...")
                usbManager.deviceList.values.forEach { d ->
                    log(
                        "USB → path=${d.deviceName} | VID=${d.vendorId} | PID=${d.productId} | class=${d.deviceClass}"
                    )
                }
            }
        ) {
            Icon(Icons.Default.List, null)
            Spacer(Modifier.width(8.dp))
            Text("LISTAR USB")
        }

        Spacer(Modifier.height(6.dp))

        /* ================= DETECTAR USB ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            onClick = {
                usbDevice = usbManager.deviceList.values.firstOrNull {
                    it.deviceName == TARGET_USB_PATH
                }

                if (usbDevice == null) {
                    log("❌ No se encontró USB en $TARGET_USB_PATH")
                    return@Button
                }

                log("✅ USB encontrado en ${usbDevice!!.deviceName}")
                log("VID=${usbDevice!!.vendorId} PID=${usbDevice!!.productId}")

                val pi = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent("USB_PERMISSION"),
                    PendingIntent.FLAG_MUTABLE
                )
                usbManager.requestPermission(usbDevice, pi)
            }
        ) {
            Icon(Icons.Default.Usb, null)
            Spacer(Modifier.width(8.dp))
            Text("DETECTAR USB")
        }

        Spacer(Modifier.height(6.dp))

        /* ================= IMPRIMIR ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val device = usbDevice ?: run {
                            withContext(Dispatchers.Main) {
                                log("No hay USB seleccionado.")
                            }
                            return@launch
                        }

                        usbInterface = device.getInterface(0)
                        usbConnection = usbManager.openDevice(device)

                        if (usbConnection == null) {
                            withContext(Dispatchers.Main) {
                                log("❌ No se pudo abrir el USB.")
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            dumpUsbDevice(device)
                        }

                        usbConnection!!.claimInterface(usbInterface, true)

                        usbEndpointOut = (0 until usbInterface!!.endpointCount)
                            .map { usbInterface!!.getEndpoint(it) }
                            .first { it.direction == UsbConstants.USB_DIR_OUT }

                        val payload =
                            byteArrayOf(0x1B, 0x40) +
                                    "IMPRESION USB OK\n\n".toByteArray() +
                                    byteArrayOf(0x1D, 0x56, 0x42, 0x00)

                        usbConnection!!.bulkTransfer(
                            usbEndpointOut,
                            payload,
                            payload.size,
                            2000
                        )

                        withContext(Dispatchers.Main) {
                            log("🖨 Impresión enviada.")
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            log("Error USB: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.Print, null)
            Spacer(Modifier.width(8.dp))
            Text("IMPRIMIR USB")
        }

        Spacer(Modifier.height(6.dp))

        /* ================= DESCONECTAR ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        withTimeout(USB_DISCONNECT_TIMEOUT_MS) {
                            usbInterface?.let {
                                usbConnection?.releaseInterface(it)
                            }
                            usbConnection?.close()
                        }

                        withContext(Dispatchers.Main) {
                            log("🔌 USB desconectado.")
                        }

                    } catch (_: TimeoutCancellationException) {
                        withContext(Dispatchers.Main) {
                            log("⚠ Timeout al cerrar USB.")
                        }
                    } finally {
                        usbConnection = null
                        usbInterface = null
                        usbEndpointOut = null
                        usbDevice = null
                    }
                }
            }
        ) {
            Icon(Icons.Default.Close, null)
            Spacer(Modifier.width(8.dp))
            Text("DESCONECTAR USB")
        }

        Spacer(Modifier.height(24.dp))
    }
}
