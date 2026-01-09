@file:Suppress("DEPRECATION")

package com.netpoint.impresora

import android.annotation.SuppressLint
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

@SuppressLint("MissingPermission")
@Composable
fun HardwareScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var logs by remember { mutableStateOf("--- CONSOLA LIMPIA ---\n") }
    var btSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var selectedMac by remember { mutableStateOf<String?>(null) }

    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs += "[$time] $msg\n"
    }

    fun isConnected(): Boolean =
        btSocket != null && btSocket!!.isConnected

    LaunchedEffect(logs) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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

        Spacer(Modifier.height(8.dp))


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val manager =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    val adapter = manager.adapter

                    withContext(Dispatchers.Main) {
                        selectedMac = null
                        log("Buscando dispositivos vinculados…")
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


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {

                    if (selectedMac == null) {
                        withContext(Dispatchers.Main) {
                            log("Primero ejecutá VER SOCKETS.")
                        }
                        return@launch
                    }

                    if (isConnected()) {
                        withContext(Dispatchers.Main) {
                            log("El socket ya está conectado.")
                        }
                        return@launch
                    }

                    val manager =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    val adapter = manager.adapter

                    try {
                        val device = adapter.getRemoteDevice(selectedMac!!)
                        adapter.cancelDiscovery()

                        btSocket = device.javaClass
                            .getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket

                        btSocket!!.connect()

                        withContext(Dispatchers.Main) {
                            log("Conectado a ${device.address}")
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            log("Error conectando socket: ${e.message}")
                        }
                        btSocket = null
                    }
                }
            }
        ) {
            Icon(Icons.Default.Link, null)
            Spacer(Modifier.width(8.dp))
            Text("CONECTAR SOCKET")
        }

        Spacer(Modifier.height(6.dp))


        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            onClick = {
                scope.launch(Dispatchers.IO) {

                    if (!isConnected()) {
                        withContext(Dispatchers.Main) {
                            log("No hay socket conectado.")
                        }
                        return@launch
                    }
                    try {
                        val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(Date())
                        val text = """
                        ====================
                        MAC: $selectedMac
                        Hora: $now
                        Hola mundo
                        ====================
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
            Text("IMPRIMIR")
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
    }
}
