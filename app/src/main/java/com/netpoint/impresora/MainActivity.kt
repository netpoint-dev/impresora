@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.bluetooth.*
import android.content.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
    val scroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA LIMPIA ---\n") }
    var btSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var selectedMac by remember { mutableStateOf<String?>(null) }

    fun log(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $message\n"
    }

    fun isSocketConnected(): Boolean =
        btSocket != null && btSocket!!.isConnected

    LaunchedEffect(logs) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ================= CONSOLA ================= */

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Text(
                text = logs,
                color = Color(0xFF00FF41),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(12.dp)
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("logs", logs)
                    )
                    log("Logs copiados al portapapeles.")
                }
            ) {
                Icon(Icons.Default.ContentCopy, null, tint = Color.Cyan)
            }
        }

        Spacer(Modifier.height(8.dp))

        /* ================= VER SOCKETS ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val manager =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    val adapter = manager.adapter

                    withContext(Dispatchers.Main) {
                        log("Escaneando dispositivos vinculados...")
                        selectedMac = null
                    }

                    adapter?.bondedDevices?.forEach { device ->
                        withContext(Dispatchers.Main) {
                            log("${device.name ?: "Sin nombre"} | ${device.address}")
                            selectedMac = device.address
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (selectedMac != null) {
                            log("MAC seleccionada: $selectedMac")
                        } else {
                            log("No se encontraron dispositivos.")
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.BluetoothSearching, null)
            Spacer(Modifier.width(8.dp))
            Text("VER SOCKETS")
        }

        Spacer(Modifier.height(6.dp))

        /* ================= CONECTAR SOCKET ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {

                    if (selectedMac == null) {
                        withContext(Dispatchers.Main) {
                            log("No hay MAC seleccionada. Ejecutá VER SOCKETS primero.")
                        }
                        return@launch
                    }

                    val manager =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    val adapter = manager.adapter

                    if (adapter == null || !adapter.isEnabled) {
                        withContext(Dispatchers.Main) {
                            log("Bluetooth apagado.")
                        }
                        return@launch
                    }

                    val device = try {
                        adapter.getRemoteDevice(selectedMac!!)
                    } catch (_: Exception) {
                        null
                    }

                    if (device == null) {
                        withContext(Dispatchers.Main) {
                            log("No se pudo obtener el dispositivo por MAC.")
                        }
                        return@launch
                    }

                    try {
                        adapter.cancelDiscovery()

                        btSocket = device.javaClass
                            .getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket

                        btSocket!!.connect()

                        withContext(Dispatchers.Main) {
                            log("Socket conectado a ${device.address}")
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            log("Error conectando socket: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.Link, null)
            Spacer(Modifier.width(8.dp))
            Text("CONECTAR SOCKET")
        }

        Spacer(Modifier.height(6.dp))

        /* ================= IMPRIMIR TEXTO ================= */

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            onClick = {
                scope.launch(Dispatchers.IO) {

                    if (!isSocketConnected()) {
                        withContext(Dispatchers.Main) {
                            log("Socket no conectado.")
                        }
                        return@launch
                    }

                    try {
                        val text = """
                            Poema de la impresora

                            En la mesa zumba baja, concentrada,
                            la impresora despierta su latido,
                            un motor que murmura madrugada
                            y escupe palabras con sonido.

                            Traga silencio, bytes y pensamientos,
                            los mastica en calor y en tinta oscura,
                            y pariendo recibos y argumentos
                            deja huellas exactas de cordura.

                            Cada línea cae recta, obediente,
                            como soldados blancos en papel,
                            pero adentro hay un pulso diferente:
                            un poema mecánico y fiel.

                            Cuando corta, suspira, satisfecha,
                            queda el texto, la marca y la razón:
                            la máquina también sueña —aunque estrecha—
                            con dejar su mensaje en impresión.
                        """.trimIndent()

                        val payload =
                            byteArrayOf(0x1B, 0x40) +
                                    text.toByteArray() +
                                    byteArrayOf(0x1D, 0x56, 0x42, 0x00)

                        btSocket!!.outputStream.write(payload)
                        btSocket!!.outputStream.flush()

                        withContext(Dispatchers.Main) {
                            log("Impresión enviada.")
                        }

                    } catch (e: IOException) {
                        withContext(Dispatchers.Main) {
                            log("Error imprimiendo: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Icon(Icons.Default.Print, null)
            Spacer(Modifier.width(8.dp))
            Text("IMPRIMIR TEXTO")
        }

        Spacer(Modifier.height(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            onClick = {
                scope.launch {
                    try {
                        btSocket?.close()
                        btSocket = null
                        log("Socket cerrado.")
                    } catch (_: Exception) {
                        log("Error cerrando socket.")
                    }
                }
            }
        ) {
            Icon(Icons.Default.Close, null)
            Spacer(Modifier.width(8.dp))
            Text("CERRAR SOCKET")
        }

        Spacer(Modifier.height(6.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            onClick = {
                logs = "--- CONSOLA LIMPIA ---\n"
            }
        ) {
            Icon(Icons.Default.Delete, null)
            Spacer(Modifier.width(8.dp))
            Text("LIMPIAR LOGS")
        }
    }
}
