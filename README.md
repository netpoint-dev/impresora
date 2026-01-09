# 📘 Prueba de impresión PTA-0140  
## Conexión Bluetooth RFCOMM por MAC

**Dispositivo:** 3nStar PTA-0140  
**Plataforma:** Android 10+ (API 29–36)  
**Lenguaje:** Kotlin + Jetpack Compose

---

## 1. Objetivo del módulo

Este módulo implementa una **conexión Bluetooth (RFCOMM)** hacia la impresora térmica incluida en la **3nStar PTA-0140**, utilizando la **dirección MAC** en lugar de UUIDs SDP.

El objetivo es:

- Identificar dispositivos que actúan como **impresoras Bluetooth (SPP/RFCOMM)**.
- Seleccionar automáticamente **la última dirección MAC** encontrada durante la enumeración.
- Crear una conexión RFCOMM **directa por MAC** hacia la impresora seleccionada.
- Enviar un payload ESC/POS con un texto de prueba a la impresora.
- **Cerrar la conexión Bluetooth** una vez finalizada la impresión.

---

## 2. Modelo de conexión Bluetooth utilizado

La aplicación utiliza **Bluetooth Clásico** mediante el protocolo **RFCOMM (Serial Port Profile – SPP)**, estableciendo la conexión a través de un **canal fijo RFCOMM 1**.

No se utiliza Bluetooth Low Energy (BLE) ni resolución de servicios vía UUID / SDP.

### Características técnicas

- Tipo de Bluetooth: **Clásico**
- Protocolo: **RFCOMM**
- Perfil: **SPP (Serial Port Profile)**
- Canal RFCOMM: **1 (fijo)**
- Método de conexión: **Directo por MAC**
- Resolución SDP / UUID: **No utilizada**

### Implementación

```kotlin
val device = adapter.getRemoteDevice(selectedMac!!)
adapter.cancelDiscovery()

btSocket = device.javaClass
    .getMethod("createRfcommSocket", Int::class.java)
    .invoke(device, 1) as BluetoothSocket

btSocket!!.connect()
```

Se utiliza **reflexión** para acceder a `createRfcommSocket`, evitando el uso de UUIDs.

Una vez conectado, la comunicación se realiza mediante streams estándar:

```kotlin
btSocket!!.outputStream.write(payload)
btSocket!!.outputStream.flush()
```

Cierre del socket:

```kotlin
btSocket?.close()
btSocket = null
```

---

## 3. Permisos requeridos

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

---

## 4. Gestión del socket

```kotlin
fun isConnected(): Boolean =
    btSocket != null && btSocket!!.isConnected
```

Reglas de operación:

- No imprimir sin conexión.
- No reconectar si el socket ya está conectado.
- Cierre obligatorio del socket.

---

## 5. Búsqueda de dispositivos

Se listan únicamente los dispositivos previamente vinculados (`bondedDevices`) para evitar:

- Descubrimientos lentos y consumo de batería.
- Necesidad de permisos de ubicación en Android 12+.
- Interacciones con dispositivos no relacionados.

La aplicación selecciona automáticamente la **última MAC encontrada**, que suele corresponder a la impresora que se conectó previamente.

```kotlin
adapter?.bondedDevices?.forEach { device ->
    log("${device.name ?: "Sin nombre"} | ${device.address}")
    selectedMac = device.address // última MAC asignada automáticamente
}
```

---

## 6. Conexión RFCOMM

La conexión Bluetooth se realiza mediante RFCOMM (SPP) utilizando un canal fijo.

### Flujo de conexión

```text
[ Usuario ]
    ↓
VER SOCKETS
    ↓
List bondedDevices
    ↓
Seleccionar MAC
    ↓
Validar MAC
    ↓
cancelDiscovery()
    ↓
getRemoteDevice(MAC)
    ↓
createRfcommSocket(channel = 1)
    ↓
connect()
```

### Implementación

```kotlin
val device = adapter.getRemoteDevice(selectedMac!!)
adapter.cancelDiscovery()

btSocket = device.javaClass
    .getMethod("createRfcommSocket", Int::class.java)
    .invoke(device, 1) as BluetoothSocket

btSocket!!.connect()
```

---

## 7. Flujo completo de usuario

```text
[ Usuario ]
    ↓
VER SOCKETS
    ↓
Listar bondedDevices
    ↓
Seleccionar MAC (última encontrada)
    ↓
Validar MAC
    ↓
cancelDiscovery()
    ↓
getRemoteDevice(MAC)
    ↓
createRfcommSocket(channel = 1)
    ↓
connect()
    ↓
Socket conectado
    ↓
IMPRIMIR (payload ESC/POS)
    ↓
OutputStream.write(payload)
    ↓
(espera acción del usuario o impresión múltiple)
    ↓
CERRAR SOCKET (manual)
```

---

## 8. Impresión ESC/POS con MAC y hora

La aplicación envía un **payload binario ESC/POS** a través del `OutputStream` del `BluetoothSocket` RFCOMM.

### Formato de salida impresa

```text
====================
MAC: XX:XX:XX:XX:XX:XX
Hora: HH:mm:ss
Hola mundo
====================
```

### Payload utilizado

```kotlin
val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
val text = """
====================
MAC: $selectedMac
Hora: $now
Hola mundo
====================
""".trimIndent()

val payload =
    byteArrayOf(0x1B, 0x40) +          // ESC @  → Reset / inicialización
    text.toByteArray() +               // Texto imprimible
    byteArrayOf(0x1D, 0x56, 0x42, 0x00) // GS V B 0 → Corte total de papel
```

- **Reset / inicialización (ESC @)**: asegura que la impresora comience en un estado limpio.
- **Corte total de papel (GS V B 0)**: indica a la impresora que corte el papel al finalizar la impresión.
