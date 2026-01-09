@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.app.PendingIntent
import android.bluetooth.*
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

/* ================= UI ================= */

@Composable
fun HardwareScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val logScroll = rememberScrollState()
    val uiScroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA LIMPIA ---\n") }

    var btSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var selectedMac by remember { mutableStateOf<String?>(null) }

    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    var usbDevice by remember { mutableStateOf<UsbDevice?>(null) }
    var usbConnection by remember { mutableStateOf<UsbDeviceConnection?>(null) }
    var usbInterface by remember { mutableStateOf<UsbInterface?>(null) }
    var usbEndpointOut by remember { mutableStateOf<UsbEndpoint?>(null) }


    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $msg\n"
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


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val manager =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    val adapter = manager.adapter

                    withContext(Dispatchers.Main) {
                        log("Escaneando dispositivos Bluetooth vinculados...")
                        selectedMac = null
                    }

                    adapter?.bondedDevices?.forEach {
                        withContext(Dispatchers.Main) {
                            log("${it.name ?: "Sin nombre"} | ${it.address}")
                            selectedMac = it.address
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.BluetoothSearching, null)
            Spacer(Modifier.width(8.dp))
            Text("VER SOCKETS BT")
        }

        Spacer(Modifier.height(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val manager =
                            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                        val adapter = manager.adapter
                        val device = adapter.getRemoteDevice(selectedMac!!)
                        adapter.cancelDiscovery()

                        btSocket = device.javaClass
                            .getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket

                        btSocket!!.connect()

                        withContext(Dispatchers.Main) {
                            log("Bluetooth conectado: ${device.address}")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            log("Error BT: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.Link, null)
            Spacer(Modifier.width(8.dp))
            Text("CONECTAR BT")
        }

        Spacer(Modifier.height(12.dp))


        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            onClick = {
                scope.launch {
                    usbDevice = usbManager.deviceList.values.firstOrNull {
                        it.deviceClass == UsbConstants.USB_CLASS_PRINTER ||
                                it.getInterface(0).interfaceClass == UsbConstants.USB_CLASS_PRINTER
                    }

                    if (usbDevice == null) {
                        log("No se detectó impresora USB.")
                        return@launch
                    }

                    log("USB detectado VID=${usbDevice!!.vendorId} PID=${usbDevice!!.productId}")

                    val pi = PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent("USB_PERMISSION"),
                        PendingIntent.FLAG_MUTABLE
                    )
                    usbManager.requestPermission(usbDevice, pi)
                }
            }
        ) {
            Icon(Icons.Default.Usb, null)
            Spacer(Modifier.width(8.dp))
            Text("DETECTAR USB")
        }

        Spacer(Modifier.height(6.dp))

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
                            log("Impresión USB enviada.")
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


        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        withTimeout(USB_DISCONNECT_TIMEOUT_MS) {
                            usbConnection?.releaseInterface(usbInterface)
                            usbConnection?.close()
                        }

                        withContext(Dispatchers.Main) {
                            log("USB desconectado correctamente.")
                        }

                    } catch (e: TimeoutCancellationException) {
                        withContext(Dispatchers.Main) {
                            log("⚠ Timeout al desconectar USB, estado limpiado.")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            log("Error al desconectar USB: ${e.message}")
                        }
                    } finally {
                        usbConnection = null
                        usbInterface = null
                        usbEndpointOut = null
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
