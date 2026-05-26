package com.common.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;

import com.common.apiutil.serial.Serial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class BootReceiver extends BroadcastReceiver {

    int three_cams_status = 0;
    int sanec_card_status = 0;
    int n_module_status = 0;
    int rgb_status = 0;
    int logo_status = 0;
    int tof_status = 0;
    int nfccard_status = 0;
    int nfcreader_status = 0;
    int ble_status = 0;

    private InputStream mInputStream =  null;
    private OutputStream mOutputStream = null;
    private Serial serial = null;
    private ReadThread mReadThread = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("taxx", "----------------------boot");
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkCamera();
                sanec_card_status = hasUsbModule(context, 0x04bc, 0x4612) ? 1 : 0;
                Log.d("taxx", String.format("three_cams_status[%d] sanec_card_status[%d]", three_cams_status, sanec_card_status));
                if(hasUsbModule(context, 0x89d1, 0x1782) && new File("/dev/ttyUSB1").exists()){
                    n_module_status = 1;
                    if(open_serial()){
                        try {
                            sendCMD("CHECK_LED[6,255]");
                            Thread.sleep(1000);
                            sendCMD("CHECK_TOF[4]");
                            Thread.sleep(1000);
                            sendCMD("CHECK_NFCCARD[2]");
                            Thread.sleep(1000);
                            sendCMD("CHECK_NFCREADER[2]");
                            Thread.sleep(1000);
                            sendCMD("CHECK_BLE[4]");
                            Thread.sleep(1000);
                            close_serial();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String n_module_info = String.format("n_module_status[%d] rgb_status[%d] logo_status[%d] tof_status[%d] nfccard_status[%d] nfcreader_status[%d] ble_status[%d]", n_module_status, rgb_status, logo_status, tof_status, nfccard_status, nfcreader_status, ble_status);
                        Log.d("taxx", n_module_info);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        writeToFile(Environment.getExternalStorageDirectory() + "npressure_power.txt", n_module_info + "\n");
                    }
                }else{
                    Log.d("taxx", "n module not fount");
                }
            }
        }).start();
    }

    void sendCMD(String cmd){
        try {
            if(mOutputStream != null) {
                mOutputStream.write(cmd.getBytes());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean open_serial(){
        try {
            if(serial==null){
                serial = new Serial("/dev/ttyUSB1", 115200, 0);
                if(mInputStream==null){
                    mInputStream = serial.getInputStream();
                    mOutputStream = serial.getOutputStream();
                }
                if(mReadThread==null){
                    mReadThread = new ReadThread();
                    mReadThread.start();
                }
                return true;
            }
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    void close_serial(){
        if(mReadThread!=null){
            mReadThread.interrupt();
            mReadThread = null;
        }
        try {
            if(mInputStream!=null){
                mInputStream.close();
                mInputStream = null;
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(serial!=null){
            serial.close();
            serial = null;
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    if(mInputStream != null && mInputStream.available() > 0){
                        byte[] buffer = new byte[256];
                        size = mInputStream.read(buffer);
                        buffer = Arrays.copyOfRange(buffer, 0, size);
                        String tmp = new String(buffer);
                        Log.d("tauu", "receive:"+tmp);
                        parseJson(tmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    void parseJson(String jsonStr){
        try {
            JSONObject jObject = new JSONObject(jsonStr);
            String item = jObject.getString("item");
            Log.d("tauu", String.format("item[%s]", item));

            if("CHECK_LED".equals(item)){
                rgb_status = Integer.valueOf(jObject.getString("rgb_pcba"));
                logo_status = Integer.valueOf(jObject.getString("logo_pcba"));
                Log.d("tauu", String.format("rgb_status[%d] logo_status[%d]", rgb_status, logo_status));
            }else{
                int pcbaStatus = Integer.valueOf(jObject.getString("pcba"));
                if("CHECK_TOF".equals(item)){
                    tof_status = pcbaStatus;
                    Log.d("tauu", String.format("tof_status[%d]", tof_status));
                }else if("CHECK_NFCCARD".equals(item)){
                    nfccard_status = pcbaStatus;
                    Log.d("tauu", String.format("nfccard_status[%d]", nfccard_status));
                }else if("CHECK_NFCREADER".equals(item)){
                    nfcreader_status = pcbaStatus;
                    Log.d("tauu", String.format("nfcreader_status[%d]", nfcreader_status));
                }else if("CHECK_BLE".equals(item)){
                    ble_status = pcbaStatus;
                    Log.d("tauu", String.format("ble_status[%d]", ble_status));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("tauu", "error---json");
        }

    }

    public static String toHexString(byte[] data)
    {
        if (data == null)
        {
            return "";
        }

        String string;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < data.length; i++)
        {
            string = Integer.toHexString(data[i] & 0xFF);
            if (string.length() == 1)
            {
                stringBuilder.append("0");
            }

            stringBuilder.append(string.toUpperCase());
        }

        return stringBuilder.toString();
    }

    //检查H10T主机 --- 3摄/三未加密卡
    private void checkCamera(){
        String path = "/proc/driver/camera_info";
        try {
            FileInputStream fos = new FileInputStream(new File(path));
            byte[] info = new byte[4096];
            int readRet = fos.read(info);
            fos.close();
            if(readRet > 0){
                String infoStr = new String(Arrays.copyOf(info, readRet));
                Log.d("tauu", String.format("getCameraInfo readRet[%d]\n[%s]", readRet, infoStr));
                if(infoStr.contains("Sensor ID = 6010") && infoStr.contains("Sensor ID = 885a") && infoStr.contains("Sensor ID = 2e0")){
                    three_cams_status = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //N模块:Vendor=0x1782 ProdID=0x89d1
    //三未加密卡:Vendor=0x4612 ProdID=0x04bc
    public boolean hasUsbModule(Context context, int pid, int vid){
        boolean hasUsbModule = false;
        try {
            UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
            Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
            while (iterator.hasNext()) {
                UsbDevice usbDevice = iterator.next();
                if(usbDevice.getVendorId() == vid && usbDevice.getProductId() == pid){
                    hasUsbModule = true;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return hasUsbModule;
    }

    /**
     * 将数据追加到内部存储文件中
     * @param context 上下文对象
     * @param fileName 文件名
     * @param data 要写入的数据
     */
    public static void appendToInternalFile(Context context, String fileName, String data) {
        FileOutputStream fos = null;
        try {
            // 使用内部存储，追加模式（如果文件不存在，会自动创建）
            fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            Log.d("taxx", "appendToInternalFile fileName:" + fileName + " data:" + data);
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 将字符串写入到指定路径的文件中
    public static void writeToFile(String filePath, String content) {
        Path path = Paths.get(filePath);
        try {
            // 如果文件不存在，则创建文件
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());  // 创建父目录（如果不存在）
                Files.createFile(path);  // 创建文件
            }

            // 写入字符串内容到文件，使用APPEND模式追加内容
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Log.d("taxx", "writeToFile filePath:" + filePath + " content:" + content);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
