# Integracion SDK y Scanner 2D

## Objetivo

Este proyecto unifica distintos backends de impresora y lectores 2D segun familia de terminal POS.

## SDK de impresora por familia

### PTA0130 / PTA0140

Usa SDK propietario `demoSDK_v2.30.20250124.aar`.

Clases principales:

- `com.common.apiutil.printer.UsbThermalPrinter`
- `com.common.apiutil.printer.ThermalPrinter`
- `com.common.apiutil.util.SystemUtil`
- `com.common.apiutil.powercontrol.PowerControl`
- `com.common.apiutil.serial.Serial`

Reglas practicas:

- `UsbThermalPrinter` se usa como camino 58mm/USB.
- `ThermalPrinter` se usa para caminos seriales/80mm y algunos equipos que reportan tipos extraños.
- `SystemUtil.checkPrinter581(context)` y `SystemUtil.getPrinterType()` solo sirven como heuristica. En varios equipos devuelven `-1`.
- `UsbThermalPrinter.setGray()` depende del hardware. En algunos PTA0140 solo acepta `0` o `1`.
- `UsbThermalPrinter.enlargeFontSize()` puede fallar con multiplicadores mayores a `2`.

Flujo 58mm SDK:

1. `usbPrinter.start(speedMode)`
2. `usbPrinter.reset()`
3. `usbPrinter.setAlgin(...)`
4. `usbPrinter.setLeftIndent(...)`
5. `usbPrinter.setLineSpace(...)`
6. `usbPrinter.setBold(...)`
7. `usbPrinter.setTextSize(...)`
8. `usbPrinter.setMonoSpace(false)`
9. `usbPrinter.setGray(...)` solo si hardware lo soporta
10. `usbPrinter.addString(...)`
11. `usbPrinter.printString()`
12. `usbPrinter.walkPaper(...)`

Flujo serial/80mm SDK:

1. `powerControl.printerPower(1)`
2. `SystemUtil.setProperty("persist.printer.interface", "serial" | "usb")`
3. `ThermalPrinter.setPaperWidth(...)`
4. `ThermalPrinter.init80mmSerialPrinter()` o `ThermalPrinter.init80mmUsbPrinter(context)`
5. `ThermalPrinter.start(context)`
6. `ThermalPrinter.reset()`

### PTA0010

Usa SDK oficial `possdk.aar` basado en UnifiedPOS.

Clases principales:

- `upos.POSPrinter`
- `upos.POSPrinterConst`
- `upos.events.StatusUpdateListener`

Reglas practicas:

- No usa `com.common.apiutil.*` como camino principal.
- No abre por `/dev/ttyS*`, ni por MAC, ni por VID/PID.
- Abre por nombre logico:
  - `POSPrinterConst.PTR_CP_2INCH`
  - `POSPrinterConst.PTR_CP_3INCH`
- El proyecto actual usa `PTR_CP_2INCH` como boton principal `PTA0010 SDK`.
- El SDK oficial no expone API de densidad/gris.
- El formato se manda con ESC/POS via `printNormal(0, byte[])`.
- En este proyecto el cutter se deja deshabilitado para PTA0010 cuando equipo no tiene guillotina fisica.

Flujo PTA0010:

1. `val dev = POSPrinter()`
2. `dev.addStatusUpdateListener(...)`
3. `dev.open(POSPrinterConst.PTR_CP_2INCH)`
4. para imprimir: `dev.printNormal(0, payloadEscPos)`
5. para avanzar: `dev.printNormal(0, byteArrayOf(0x1B, 0x64, n))`
6. para cerrar: `dev.close()`

Formato ESC/POS que se usa en PTA0010:

- `ESC @` init
- `ESC a n` alineacion
- `ESC 3 n` interlineado
- `GS L nL nH` margen izquierdo
- `ESC E n` negrita
- `ESC 4` cursiva on solo si aplica
- `GS B 1` invertido solo si aplica
- `GS ! n` ancho/alto
- texto
- `ESC d n` feed final

Nota:

- No mandar `ESC 5` en PTA0010 si firmware responde `"(1b 35) this command is not supported"`.

## Backends directos ESC/POS

Se mantienen como fallback para equipos donde el SDK falla pero existe acceso bruto.

### USB directo

Ubicacion: `DirectPrinterDriver.connectUsb(...)`

Uso:

1. detectar `UsbManager.deviceList`
2. pedir permiso USB si falta
3. reclamar interfaz clase impresora (`interfaceClass == 7`) o primera interfaz
4. buscar endpoint `BULK OUT`
5. mandar comandos ESC/POS por `bulkTransfer`

### Serial directo

Ubicacion: `DirectPrinterDriver.connectSerial(...)`

Uso:

1. abrir `com.common.apiutil.serial.Serial(port, baudrate, 0)`
2. escribir bytes por `outputStream`

## Scanner 2D usados en este programa

### 1. Scanner hardware integrado

SDK:

- `com.common.apiutil.decode.DecodeReader`

Uso:

1. energizar modulo con `PowerControl.decodePower(1)`
2. `decodeReader.open(115200)`
3. registrar listener con `setDecodeReaderListener`
4. disparar trigger con `cmdSend(...)`
5. leer respuesta y cerrar

Ventaja:

- usa lector fisico del equipo, mas rapido y estable cuando modulo existe.

### 2. Fallback camara con ZXing UI

Librerias:

- `com.journeyapps:zxing-android-embedded:4.3.0`
- `com.google.zxing:core:3.5.3`

Uso:

1. `registerForActivityResult(ScanContract())`
2. lanzar `ScanOptions`
3. recibir `result.contents`

Ventaja:

- simple, rapido para fallback con pantalla de escaneo lista.

### 3. Camara en segundo plano con CameraX + ML Kit

Librerias:

- `androidx.camera:camera-core`
- `androidx.camera:camera-camera2`
- `androidx.camera:camera-lifecycle`
- `com.google.mlkit:barcode-scanning:17.3.0`

Uso:

1. `ProcessCameraProvider.getInstance(...)`
2. `ImageAnalysis.Builder()`
3. convertir frame a `InputImage`
4. procesar con `BarcodeScanning.getClient().process(image)`

Ventaja:

- escaneo silencioso sin UI dedicada.

### 4. Emulacion teclado del scanner

Uso:

1. interceptar `dispatchKeyEvent`
2. acumular caracteres rapidos en buffer
3. cerrar lectura con `KEYCODE_ENTER`

Ventaja:

- cubre equipos donde lector 2D se presenta como teclado HID.

## Recomendacion para otra app

1. detectar perfil de terminal primero
2. PTA0010 -> usar `possdk.aar` y `POSPrinter`
3. PTA0130/PTA0140 -> usar `demoSDK_v2.30.20250124.aar`
4. si SDK falla -> fallback USB directo ESC/POS
5. para scanner 2D:
   - primero hardware `DecodeReader`
   - luego camara fondo `CameraX + ML Kit`
   - ultimo fallback `ZXing UI`

## Archivos clave de esta app

- `app/src/main/java/com/netpoint/impresora/MainActivity.kt`
- `app/build.gradle.kts`
- `app/libs/demoSDK_v2.30.20250124.aar`
- `app/libs/possdk.aar`
