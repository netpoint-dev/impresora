package com.common.demo.power;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.idcard.IdCard;
import com.common.apiutil.idcard.ReadCallBack;
import com.common.apiutil.serial.Serial;
import com.common.apiutil.util.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PowerManager_N2 {

    private static final String TAG = "PowerManager_N";

    private UsbDevice usbReader = null;
    private UsbManager mUsbManager = null;

    private Context mContext;

    private static final String check_info = "CHECK_INFO";
    private static final String check_led = "CHECK_LED";
    private static final String check_tof = "CHECK_TOF";
    private static final String check_audio = "CHECK_AUDIO";
    private static final String check_4G = "CHECK_4G";
    private static final String check_mcu = "CHECK_MCU";
    private static final String check_nfccard = "CHECK_NFCCARD";
    private static final String check_nfcreader = "CHECK_NFCREADER";
    private static final String check_bluetooth = "CHECK_BLE";
    private static final String check_hid = "CHECK_HID";
    private static final String find_sim = "FIND_SIM";


    public static final String set_info_resulte = "SET_INFO_RESULT";
    public static final String set_led_result = "SET_LED_RESULT";
    public static final String set_tof_result = "SET_TOF_RESULT";
    public static final String set_audio_result = "SET_AUDIO_RESULT";
    public static final String set_4G_result = "SET_4G_RESULT";
    public static final String set_mcu_result = "SET_MCU_RESULT";
    public static final String set_nfccard_result = "SET_NFCCARD_RESULT";
    public static final String set_nfcreader_result = "SET_NFCREADER_RESULT";
    public static final String set_bluetooth_result = "SET_BLE_RESULT";
    public static final String set_hid_result = "SET_HID_RESULT";
    public static final String set_sn = "SET_SN";
    public static final String set_efuse = "SET_EFUSE";

    public static final String get_info_result = "GET_INFO_RESULT";
    public static final String get_led_result = "GET_LED_RESULT";
    public static final String get_tof_result = "GET_TOF_RESULT";
    public static final String get_audio_result = "GET_AUDIO_RESULT";
    public static final String get_4G_result = "GET_4G_RESULT";
    public static final String get_mcu_result = "GET_MCU_RESULT";
    public static final String get_nfccard_result = "GET_NFCCARD_RESULT";
    public static final String get_nfcreader_result = "GET_NFCREADER_RESULT";
    public static final String get_bluetooth_result = "GET_BLE_RESULT";
    public static final String get_hid_result = "GET_HID_RESULT";
    public static final String get_efuse = "GET_EFUSE";

    public static final String switch_to_windows = "SWITCH_TO_WINDOWS";
    public static final String switch_to_android = "SWITCH_TO_ANDROID";
    public static final String switch_to_hid = "SWITCH_TO_HID";
    public static final String switch_to_loader = "SWITCH_TO_LOADER";


    public static final int RED_LED = 1;
    public static final int GREEN_LED = 2;
    public static final int BLUE_LED = 3;
    public static final int LOGO_LED = 4;
    public static final int LED_OFF = 5;
    public static final int HORSE_RACE = 6;
    public static final int BLE_VERSION = 1;
    public static final int BLE_MAC = 2;
    public static final int BLE_SCAN = 3;
    public static final int AUDIO_VOLUME = 100;

    public static final int TOP_CALIBRATION_10CM = 1;
    public static final int TOP_CALIBRATION_60CM = 2;

    private Lock serialLock = new ReentrantLock();//锁串口
    private Serial serial;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private byte[] receiveData = {};
    private boolean initFlag = false;
    private boolean receiveComplete = false;
    private int mConnectType = CommonConstants.ConnectType.USB;
    private int baudrate;
    private static PowerManager_N2 instance;
    private TofResultCallback mTofResultCallback;
    private TelephonyResultCallback mTelephonyResultCallback;
    private LedResultCallback mLedResultCallback;
    private McuResultCallback mMcuResultCallback;
    private BluetoothResultCallback mBluetoothResultCallback;
    private NfcResultCallback mNfcResultCallback;
    private NResultCallback mNResultCallback;
    private StringBuilder stringBuilder;  // 用来存储拼接的数据
    private String cmd;
    private SerialListener mSerialListener;
    private static boolean isRegister = false;
    private Handler mHandler = new Handler();

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "action:" + action);
            if (action != null) {
                if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    // USB 设备插入
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        if(device.getVendorId() == 0x1782 && device.getProductId() == 0x89d1){
                            while (getSerialPath().size() <= 0){
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            int ret = open(mConnectType);
                            if(ret == ResultCode.SUCCESS || ret == ResultCode.ERR_SYS_ALREADY_OPEN) {
                                if (mSerialListener != null) {
                                    mSerialListener.onConnected("USB Device has been inserted");
                                }
                            }
                        }
                    }
                } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    // USB 设备拔出
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        if(device.getVendorId() == 0x1782 && device.getProductId() == 0x89d1){
                            close();
                            Log.i(TAG, "USB设备已拔出");
                            if(mSerialListener != null){
                                mSerialListener.onDisconnected("USB Device has been removed");
                            }
                        }
                    }
                }
            }
        }
    };

    public class NInfo {
        private String sn;
        private String imei;
        private String imsi;
        private String ccid;
        private String factoryver;
        private boolean tofFlag; // tof是否已校准，"1953457763"表示已校准
        private int tofOffset; // tof 10cm校准值
        private int tofXtalk;  // tof 60cm校准值

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getCcid() {
            return ccid;
        }

        public void setCcid(String ccid) {
            this.ccid = ccid;
        }

        public String getFactoryver() {
            return factoryver;
        }

        public void setFactoryver(String factoryver) {
            this.factoryver = factoryver;
        }

        public boolean isTofFlag() {
            return tofFlag;
        }

        public void setTofFlag(boolean tofFlag) {
            this.tofFlag = tofFlag;
        }

        public int getTofOffset() {
            return tofOffset;
        }

        public void setTofOffset(int tofOffset) {
            this.tofOffset = tofOffset;
        }

        public int getTofXtalk() {
            return tofXtalk;
        }

        public void setTofXtalk(int tofXtalk) {
            this.tofXtalk = tofXtalk;
        }
    }

    public class TelephonyPingResult {

        private int pingStatus;
        private String pingIp;
        private int sendCount;
        private int receiveCount;
        private int pingTime;
        private int rssi;

        public int getPingStatus() {
            return pingStatus;
        }

        public void setPingStatus(int pingStatus) {
            this.pingStatus = pingStatus;
        }

        public String getPingIp() {
            return pingIp;
        }

        public void setPingIp(String pingIp) {
            this.pingIp = pingIp;
        }

        public int getSendCount() {
            return sendCount;
        }

        public void setSendCount(int sendCount) {
            this.sendCount = sendCount;
        }

        public int getReceiveCount() {
            return receiveCount;
        }

        public void setReceiveCount(int receiveCount) {
            this.receiveCount = receiveCount;
        }

        public int getPingTime() {
            return pingTime;
        }

        public void setPingTime(int pingTime) {
            this.pingTime = pingTime;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }
    }

    private PowerManager_N2(Context context) {
        mContext = context;
    }

    public static PowerManager_N2 getInstance(Context context) {
        if (instance == null) {
            instance = new PowerManager_N2(context);
        }
        return instance;
    }

    public interface SerialListener {
        void onConnected(String message);
        void onDisconnected(String message);
    }

    public void monitorUSB() {
        if (!isRegister) {
            Log.i(TAG, "USB广播接收器注册成功");
            // 注册 USB 插拔广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mContext.registerReceiver(mUsbReceiver, filter);
            isRegister = true;
        }
    }

    public void unmonitorUSB() {
        if (isRegister) {
            Log.i(TAG, "USB广播接收器注销成功");
            mContext.unregisterReceiver(mUsbReceiver);
            isRegister = false;
        }
    }

    public boolean isCheckOpen() {
        Log.i(TAG, "isCheckN:" + initFlag);
        return initFlag;
    }

    public int open(int baudrate, SerialListener serialListener) {
        mConnectType = CommonConstants.ConnectType.SERIAL;
        this.baudrate = baudrate;
        mSerialListener = serialListener;
        return open(mConnectType);
    }

    public int openAgain() {
        return open(mConnectType);
    }

    private int open(int connectType) {
        mConnectType = connectType;
        if (mConnectType == CommonConstants.ConnectType.USB) {
            try {
                boolean isUsbReader = false;
                if (usbReader == null) {
                    PendingIntent mPermissionIntent = null;
                    if (Build.VERSION.SDK_INT > 30)
                        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.android.example.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE);
                    else
                        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                    mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
                    HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
                    Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();

                    while (iterator.hasNext()) {
                        UsbDevice usbDevice = iterator.next();
                        int pid = usbDevice.getProductId();
                        int vid = usbDevice.getVendorId();
//                    setLog("openReader(context) >>> usb device pid:" + pid + "/vid:" + vid);
                        if (pid == IdCard.READER_PID_SMALL && vid == IdCard.READER_VID_SMALL) {
                            usbReader = usbDevice;
                            isUsbReader = true;
                            if (mUsbManager.hasPermission(usbDevice)) {
                                break;
                            } else {
                                mUsbManager.requestPermission(usbDevice, mPermissionIntent);//requestPermission,
                            }
                        }
                    }
                }
                setLog("isUsbReader[" + isUsbReader + "]");
                return isUsbReader ? ResultCode.SUCCESS : ResultCode.ERR_SYS_NO_DEV;

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return ResultCode.ERR_SYS_UNEXPECT;
            }
        } else {
            if (initFlag) {
                return ResultCode.ERR_SYS_ALREADY_OPEN;
            }
            try {
                if (serial == null) {
                    serial = new Serial(getMaxTtyUSB(), baudrate, 0);
                }
                if (mOutputStream == null)
                    mOutputStream = serial.getOutputStream();
                if (mInputStream == null)
                    mInputStream = serial.getInputStream();
                if (mReadThread == null) {
                    mReadThread = new ReadThread();
                    mReadThread.start();
                }
                initFlag = true;
                return ResultCode.SUCCESS;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return ResultCode.ERR_SYS_UNEXPECT;
            }
        }
    }

    public void close() {
        if (mConnectType == CommonConstants.ConnectType.USB) {
            try {
                if (usbReader != null || mUsbManager != null) {
                    usbReader = null;
                    mUsbManager = null;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            try {
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                if (mOutputStream != null) {
                    mOutputStream.close();
                    mOutputStream = null;
                }
                if (mInputStream != null) {
                    mInputStream.close();
                    mInputStream = null;
                }
                if (serial != null) {
                    serial.close();
                    serial = null;
                }
                if(stringBuilder != null){
                    stringBuilder = null;
                }
                initFlag = false;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

    }

    public NInfo infoCheck() {
        cmd = check_info;
        byte[] readData = sendCommand(cmd.getBytes(), true);

        if (readData != null) {
            String result = new String(readData);
            // {"sn": "BJ7CQT024N","imei": "865062063588507"}
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                // 获取 sn 和 imei
                String sn = jsonObject.optString("sn");
                String imei = jsonObject.optString("imei");
                String imsi = jsonObject.optString("imsi");
                String ccid = jsonObject.optString("ccid");
                String factoryver = jsonObject.optString("factoryver");
                String tof_flag = jsonObject.optString("tof_flag");
                int tof_offset = jsonObject.optInt("tof_offset");
                int tof_xtalk = jsonObject.optInt("tof_xtalk");
                NInfo nInfo = new NInfo();
                nInfo.setSn(sn);
                nInfo.setImei(imei);
                nInfo.setImsi(imsi);
                nInfo.setCcid(ccid);
                nInfo.setFactoryver(factoryver);
                nInfo.setTofFlag(tof_flag.equals("1953457763"));
                nInfo.setTofOffset(tof_offset);
                nInfo.setTofXtalk(tof_xtalk);
                return nInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public interface LedResultCallback {
        void onCheckError(String error);
    }
    public void ledControl(int ledType, int brightness, LedResultCallback callback) {
        if(brightness < 0 || brightness > 255){
            return;
        }

        if(ledType != RED_LED && ledType != GREEN_LED && ledType != BLUE_LED && ledType != LOGO_LED && ledType != LED_OFF && ledType != HORSE_RACE){
            return;
        }
        mLedResultCallback = callback;
        cmd = check_led + "[" + ledType + "," + brightness + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public void tofCalibration(int mode, TofResultCallback callback) {
        if(mode != TOP_CALIBRATION_10CM && mode != TOP_CALIBRATION_60CM){
            return;
        }

        cmd = check_tof + "[" + mode + "," + 50 + "]";
        mTofResultCallback = callback;
        sendCommand(cmd.getBytes(), false);
    }

    public interface TofResultCallback {
        void onCheckDistance(int status, int distance);
        void onCalibrationResult(int mode, boolean isSuccess);
        void onCheckError(String error);
    }
    public void tofStartGetDistance(int threshold, TofResultCallback callback) {
        mTofResultCallback = callback;
        cmd = check_tof + "[" + 3 + "," + threshold + "," + 1 + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public boolean getResult(String item) {
        cmd = item;
        byte[] readData = sendCommand(cmd.getBytes(), true);
        if (readData != null) {
            String result = new String(readData);
            // SET_INFO_RESULT[1] 解析
            // 正则表达式：匹配接收到的数据中的前缀和数字部分
            String regex = "\\{(\\w+)(\\[(\\d+)\\])?\\}";// 第一个分组匹配前缀，第二个分组匹配数字（如果有）

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(result);

            if (matcher.matches()) {
                // 提取前缀部分
                String extractedPrefix = matcher.group(1);
                // 提取数字部分（如果有）
                String extractedNumber = matcher.group(3);  // 如果没有方括号，group(3) 会是 null

                if (item.equals(extractedPrefix)) {
                    if (extractedNumber != null && extractedNumber.equals("1")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void tofStopGetDistance() {
        mTofResultCallback = null;
        cmd = check_tof + "[" + 3 + "," + 0 + "," + 0 + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public boolean tofPCBATest() {
        cmd = check_tof + "[" + 4 + "," + 0 + "," + 0 + "]";
        byte[] readData = sendCommand(cmd.getBytes(), true);
        if (readData != null){
            String result = new String(readData);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                String item = jsonObject.optString("item");
                if(item.equals(check_tof)){
                    int pcba = jsonObject.optInt("pcba");
                    if (pcba == 1){
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 播放音频
     */
    public void audioPlay(int volume) {
        cmd = check_audio + "[" + 1 + "," + volume + "]";
        sendCommand(cmd.getBytes(), false);
    }

    /**
     * 设置播放音频音量
     * @param volume
     */
    public void setAudioVolume(int volume) {
        cmd = check_audio + "[" + 0 + "," + volume + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public interface TelephonyResultCallback {
        void onCheckError(String error);
        void onCheckComplete(TelephonyPingResult result);
    }
    public void telephonyPingCheck(String ip, int count, int packetSize, int timeout, TelephonyResultCallback callback) {
        if (ip.isEmpty()){
            return;
        }

        if (count < 1 || count > 255){
            return;
        }

        if (packetSize < 1 || packetSize > 1372){
            return;
        }

        if (timeout < 1 || timeout > 65535){
            return;
        }

        mTelephonyResultCallback = callback;
        cmd = check_4G + "[" + ip + "," + count + "," + packetSize + "," + timeout + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public interface McuResultCallback {
        void onCheckComplete(boolean isSuccess);
    }

    public void checkMcu(McuResultCallback callback){
        mMcuResultCallback = callback;
        cmd = check_mcu;
        sendCommand(cmd.getBytes(), false);
    }

    public class NFCCardInfo {

        private String typeaUid;
        private String cpuAts;
        private String cpuApdu;
        private String typebAtqb;
        private String mifareBlock0;
        private String mifareBlock1;

        public NFCCardInfo(String typeaUid, String cpuAts, String cpuApdu, String typebAtqb, String mifareBlock0, String mifareBlock1) {
            this.typeaUid = typeaUid;
            this.cpuAts = cpuAts;
            this.cpuApdu = cpuApdu;
            this.typebAtqb = typebAtqb;
            this.mifareBlock0 = mifareBlock0;
            this.mifareBlock1 = mifareBlock1;
        }

        public String getTypeaUid() {
            return typeaUid;
        }

        public String getCpuAts() {
            return cpuAts;
        }

        public String getCpuApdu() {
            return cpuApdu;
        }

        public String getTypebAtqb() {
            return typebAtqb;
        }

        public String getMifareBlock0() {
            return mifareBlock0;
        }

        public String getMifareBlock1() {
            return mifareBlock1;
        }
    }
    public interface NfcResultCallback {
        void onCheckError(String error);
        void onCheckCard(String key, String value);
    }
    public void setNfcCard(boolean isEnable, NfcResultCallback callback) {
        mNfcResultCallback = callback;
        cmd = check_nfccard + "[" + (isEnable?1:0) + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public void setNfcReader(NfcResultCallback callback) {
        mNfcResultCallback = callback;
        cmd = check_nfcreader + "[" + 1 + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public class BluetoothInfo{
        private String address;
        private int rssi;

        public BluetoothInfo(String address, int rssi) {
            this.address = address;
            this.rssi = rssi;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }
    }
    public class BluetoothVersion{
        private String macAddress;
        private String atVersion;
        private String sdkVersion;
        private String compileTime;
        private String bleVersion;

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public void setAtVersion(String atVersion) {
            this.atVersion = atVersion;
        }

        public void setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
        }

        public void setCompileTime(String compileTime) {
            this.compileTime = compileTime;
        }

        public void setBleVersion(String bleVersion) {
            this.bleVersion = bleVersion;
        }

        public String getAtVersion() {
            return atVersion;
        }

        public String getSdkVersion() {
            return sdkVersion;
        }

        public String getCompileTime() {
            return compileTime;
        }

        public String getBleVersion() {
            return bleVersion;
        }

        public String getMacAddress() {
            return macAddress;
        }
    }

    public interface BluetoothResultCallback {
        void onScan(BluetoothInfo bluetoothInfo);
    }

    public void scanBluetooth(BluetoothResultCallback callback) {
        mBluetoothResultCallback = callback;
        cmd = check_bluetooth + "[" + BLE_SCAN + "]";
        uniqueAddrs.clear();
        sendCommand(cmd.getBytes(), false);
    }

    public BluetoothVersion getBluetoothVersion() {
        cmd = check_bluetooth + "[" + BLE_VERSION + "]";
        byte[] readData = sendCommand(cmd.getBytes(), true);
        BluetoothVersion versions = new BluetoothVersion();
        if(readData != null) {
            String result = new String(readData);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String item = jsonObject.optString("item");
                if (item.equals(check_bluetooth)) {
                    String atVersion = jsonObject.optString("at_version", null);
                    String sdkVersion = jsonObject.optString("sdk_version", null);
                    String compileTime = jsonObject.optString("compile_time", null);
                    String bleVersion = jsonObject.optString("ble_version", null);
                    versions.setAtVersion(atVersion);
                    versions.setSdkVersion(sdkVersion);
                    versions.setCompileTime(compileTime);
                    versions.setBleVersion(bleVersion);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cmd = check_bluetooth + "[" + BLE_MAC + "]";
        byte[] readData2 = sendCommand(cmd.getBytes(), true);
        if(readData2 != null) {
            String result = new String(readData2);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String item = jsonObject.optString("item");
                if (item.equals(check_bluetooth)) {
                    String mac = jsonObject.optString("mac", null);
                    versions.setMacAddress(mac);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return versions;
    }

    public void checkHid() {
        cmd = check_hid;
        sendCommand(cmd.getBytes(), false);
    }

    public interface NResultCallback {
        void onFindSim(int ret);
    }
    public void findSim(NResultCallback callback){
        mNResultCallback = callback;
        cmd = find_sim;
        sendCommand(cmd.getBytes(), false);
    }

    public int setSN(String hostSN) {
        boolean isSNValid = false;
        // 判断第五位是否为 'F' 和第八位是否为 '0'
        if (hostSN.length() >= 8) {
            char fifthChar = hostSN.charAt(4);  // 获取第五位字符
            char eighthChar = hostSN.charAt(7); // 获取第八位字符

            // 输出检查结果
            if (fifthChar == 'F' && eighthChar == '0') {
                isSNValid = true;
            }
        }

        if(isSNValid) {
            // 替换第五位 'F' 为 'M'（索引为 4）
            String nsn = hostSN.substring(0, 4) + 'M' + hostSN.substring(5);
            // 替换第八位 '0' 为 '1'（索引为 7）
            nsn = nsn.substring(0, 7) + '1' + nsn.substring(8);
            cmd = set_sn + "[" + nsn + "]";
            sendCommand(cmd.getBytes(), false);
            return ResultCode.SUCCESS;
        }
        return ResultCode.ERR_SYS_UNEXPECT;
    }

    public void setEfuse() {
        cmd = set_efuse;
        sendCommand(cmd.getBytes(), false);
    }


    public void setCheckResult(String item, boolean isSuccess) {
        cmd = item + "[" + (isSuccess?1:0) + "]";
        sendCommand(cmd.getBytes(), false);
    }

    public int switchTo(String mode) {
        if(!mode.equals(switch_to_windows) && !mode.equals(switch_to_android) && !mode.equals(switch_to_hid) && !mode.equals(switch_to_loader)){
            return ResultCode.ERR_INVALID_PARAM;
        }
        cmd = mode;
        byte[] readData = sendCommand(cmd.getBytes(), true);
        if(readData != null){
            String result = new String(readData);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                String item = jsonObject.optString("item");
                int ret = jsonObject.optInt("ret");
                if(item.equals(switch_to_windows) || item.equals(switch_to_android)){
                    if (ret == 17){
                        return ResultCode.SUCCESS;
                    }
                }else if(item.equals(switch_to_hid)){
                    if (ret == 13){
                        return ResultCode.SUCCESS;
                    }
                }else if(item.equals(switch_to_loader)){
                    if (ret == 24){
                        return ResultCode.SUCCESS;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ResultCode.ERR_SYS_UNEXPECT;
    }

    private void reopen() {

        sleepThread(2000);

        PendingIntent mPermissionIntent = null;
        if (Build.VERSION.SDK_INT > 30)
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.android.example.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE);
        else
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();

        while (iterator.hasNext()) {
            UsbDevice usbDevice = iterator.next();
            int pid = usbDevice.getProductId();
            int vid = usbDevice.getVendorId();
            setLog("reopen() >>> usb device pid:" + pid + "/vid:" + vid);
            if (pid == IdCard.READER_PID_SMALL && vid == IdCard.READER_VID_SMALL) {
                usbReader = usbDevice;
                setLog("reopen get usbReader complete");
                if (mUsbManager.hasPermission(usbDevice)) {
                    break;
                } else {
                    mUsbManager.requestPermission(usbDevice, mPermissionIntent);
                }
            }
        }
    }

    private byte[] sendCommand(byte[] dataBytes, boolean isNeedWaitReceive) {

        int timeout = 2000;

        if (mConnectType == CommonConstants.ConnectType.USB) {
            return sendCommandUSB(dataBytes, timeout);
        } else {
            return sendCommandSerial(dataBytes, timeout, isNeedWaitReceive);
        }

    }

    private byte[] sendCommandUSB(byte[] cmd, int timeout) {
        if (usbReader == null) {
            setLog("sendCommandUSB usbReader is null");
            return null;
        }
        byte[] result = null;

        UsbInterface usbInterface = null;
        UsbEndpoint inEndpoint = null;
        UsbEndpoint outEndpoint = null;
        UsbDeviceConnection connection = null;
        boolean hasconnection = false;

        try {
            usbInterface = usbReader.getInterface(0);// USBEndpoint为读写数据所需的节点
            inEndpoint = usbInterface.getEndpoint(0); // 读数据节点
            outEndpoint = usbInterface.getEndpoint(1);
            connection = mUsbManager.openDevice(usbReader);
            if (connection != null) {
                hasconnection = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            connection = null;
        }

        try {
            if (!hasconnection) {
                reopen();
                usbInterface = usbReader.getInterface(0);// USBEndpoint为读写数据所需的节点
                inEndpoint = usbInterface.getEndpoint(0); // 读数据节点
                outEndpoint = usbInterface.getEndpoint(1);
                connection = mUsbManager.openDevice(usbReader);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            connection = null;
        }

        try {

            if (connection == null) {
                setLog("usb connection is null");
                reopen();
                return null;
            }
            connection.claimInterface(usbInterface, true);

            int out = connection.bulkTransfer(outEndpoint, cmd, cmd.length, 3000);
            setLog("send[" + StringUtil.toHexString(cmd) + "] >>> out[" + out + "]");
            byte[] byte2 = new byte[5 * 1024];
            int ret = connection.bulkTransfer(inEndpoint, byte2, byte2.length, timeout);
            setLog("receive ret[" + ret + "]");

            if (ret > 0) {
                result = Arrays.copyOfRange(byte2, 0, ret);
                int count = 0;
                while ((ret - count) > 512) {
                    setLog("usb receive[" + StringUtil.toHexString(Arrays.copyOfRange(result, count, count + 512)) + "]");
                    count += 512;
                }
                setLog("usb receive[" + StringUtil.toHexString(Arrays.copyOfRange(result, count, ret)) + "]");
            } else {
                setLog("usb no receive");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }

            }
        }

        return result;
    }

    private byte[] sendCommandSerial(byte[] cmd, int timeout, boolean isNeedWaitReceive) {
        serialLock.lock();
        try {
            // 清空接收数据
            receiveData = null;
            receiveComplete = false;

            // 发送命令
            try {
                if (mOutputStream != null) {
                    mOutputStream.write(cmd);
                    setLog("send[" + new String(cmd) + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 如果需要接收数据，等待返回
            if (isNeedWaitReceive) {
                long startTime = System.currentTimeMillis();

                // 等待直到接收到数据或者超时
                while (!receiveComplete && (System.currentTimeMillis() - startTime < timeout)) {
                    try {
                        Thread.sleep(10);  // 等待间隔时间
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // 如果超时没有接收到数据，返回 null
            if (!receiveComplete) {
                receiveData = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            serialLock.unlock();
        }

        return receiveData;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            stringBuilder = new StringBuilder();  // 初始化 StringBuilder
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null) {
                        continue;  // 如果输入流为空，继续等待
                    }

                    // 判断是否有数据可读
                    if (mInputStream.available() > 0) {
                        byte[] buffer = new byte[1024];
                        int size = mInputStream.read(buffer);

                        if (size > 0) {
                            // 将接收到的数据拼接到 stringBuilder 中
                            String receivedData = new String(buffer, 0, size, StandardCharsets.UTF_8);
                            stringBuilder.append(receivedData);
                        }

                        // 检查是否有完整的数据包
                        while (stringBuilder.indexOf("}") != -1) {
                            // 判断是否是以 '{' 开头，并且以 '}' 结尾
                            int startIndex = stringBuilder.indexOf("{");
                            int endIndex = stringBuilder.indexOf("}") + 1;  // 包括 '}'

                            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                                String completeData = stringBuilder.substring(startIndex, endIndex);  // 获取完整的一条数据
                                setLog("receive[" + completeData + "]");
                                synchronized (serialLock) {  // 确保线程安全
                                    receiveData = completeData.getBytes(StandardCharsets.UTF_8);
                                    stringBuilder.delete(0, endIndex);  // 删除已处理的数据
                                    // 处理接收到的完整 JSON 数据
                                    processReceivedData(completeData);

                                    receiveComplete = true;
                                }
                            }else {
                                // 如果不是有效的完整数据包，删除已接收到的数据并继续拼接
                                stringBuilder.delete(0, 1);  // 删除第一个字符，避免处理无效数据
                            }
                        }

                    }

                    // 适当休眠，避免过高的 CPU 占用
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // 线程被中断，优雅退出
                    interrupt();
                } catch (Exception e) {
                    // 捕获其他未知异常
                    e.printStackTrace();
                    interrupt();  // 出现异常时停止线程
                }
            }
        }
    }


    private Set<String> uniqueAddrs = new HashSet<>();
    /**
     * 处理接收到的数据
     * @param receivedDataStr 接收到的字符串数据
     */
    private void processReceivedData(String receivedDataStr) {
        if (receivedDataStr != null && !receivedDataStr.isEmpty()) {
            try {
                // 解析 JSON 对象
                JSONObject jsonObject = new JSONObject(receivedDataStr);
                String item = jsonObject.optString("item");

                if (check_tof.equals(item)) {
                    String error = jsonObject.optString("error", "");
                    if(!error.isEmpty()){
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCheckError(error);
                            mTofResultCallback = null;
                        }
                        return;
                    }
                    int status = jsonObject.optInt("status", -1);
                    int distance = jsonObject.optInt("distance");
                    int calibrate_10cm =jsonObject.optInt("10cmcalibrate", -1);
                    int calibrate_60cm =jsonObject.optInt("60cmcalibrate", -1);
                    if (status != -1) {
                        setLog("tof distance[" + distance + "]");
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCheckDistance(status, distance);
                        }
                    }

                    if(calibrate_10cm == 1){
                        setLog("tof 10cm calibration success");
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCalibrationResult(TOP_CALIBRATION_10CM, true);
                        }
                    }else if(calibrate_10cm == 0){
                        setLog("tof 10cm calibration failed");
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCalibrationResult(TOP_CALIBRATION_10CM, false);
                        }
                    }

                    if(calibrate_60cm == 1){
                        setLog("tof 60cm calibration success");
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCalibrationResult(TOP_CALIBRATION_60CM, true);
                            mTofResultCallback = null;
                        }
                    }else if(calibrate_60cm == 0){
                        setLog("tof 60cm calibration failed");
                        if(mTofResultCallback != null){
                            mTofResultCallback.onCalibrationResult(TOP_CALIBRATION_60CM, false);
                            mTofResultCallback = null;
                        }
                    }
                }else if (check_4G.equals(item)) {
                    String error = jsonObject.optString("error", null);
                    if (error != null) {
                        if (mTelephonyResultCallback != null) {
                            mTelephonyResultCallback.onCheckError(error);
                            mTelephonyResultCallback = null;
                        }
                    }else {
                        TelephonyPingResult telephonyPingResult = new TelephonyPingResult();
                        telephonyPingResult.setPingStatus(jsonObject.optInt("pingStatus"));
                        telephonyPingResult.setPingIp(jsonObject.optString("ip"));
                        telephonyPingResult.setRssi(jsonObject.optInt("rssi"));
                        telephonyPingResult.setSendCount(jsonObject.optInt("sendCount"));
                        telephonyPingResult.setReceiveCount(jsonObject.optInt("receiveCount"));
                        telephonyPingResult.setPingTime(jsonObject.optInt("time"));
                        if (mTelephonyResultCallback != null) {
                            mTelephonyResultCallback.onCheckComplete(telephonyPingResult);
                            mTelephonyResultCallback = null;
                        }
                    }
                }else if (item.equals(check_led)) {
                    String error = jsonObject.optString("error", null);
                    if (error != null) {
                        if(mLedResultCallback != null){
                            mLedResultCallback.onCheckError(error);
                            mLedResultCallback = null;
                        }
                    }
                }else if (item.equals(check_mcu)) {
                    boolean isSuccess = jsonObject.optInt("update") == 0;
                    if (mMcuResultCallback != null) {
                        mMcuResultCallback.onCheckComplete(isSuccess);
                        mMcuResultCallback = null;
                    }
                }else if (item.equals(check_bluetooth)){
                    String addr = jsonObject.optString("addr", null);
                    int rssi = jsonObject.optInt("rssi", -1);
                    if (rssi != -1){
                        rssi = -113 + rssi * 2;
                    }
//                    Log.d("TAGG", "uniqueAddrs: " + uniqueAddrs.contains(addr));
                    if (addr!= null && !uniqueAddrs.contains(addr)) {
                        uniqueAddrs.add(addr);  // 添加 addr 到 Set
                        if(mBluetoothResultCallback != null){
                            mBluetoothResultCallback.onScan(new BluetoothInfo(addr, rssi));
                        }
                    }
                }else if (item.equals(check_nfccard)) {
                    String error = jsonObject.optString("error","");
                    setLog("setNfcCard error[" + error + "]");
                    if(!error.isEmpty()){
                        if(mNfcResultCallback != null){
                            mNfcResultCallback.onCheckError(error);
                            mNfcResultCallback = null;
                        }
                    }
                }else if (item.equals(check_nfcreader)) {
                    String error = jsonObject.optString("error","");
                    setLog("checkNfcReader error[" + error + "]");
                    if(!error.isEmpty()){
                        if(mNfcResultCallback != null){
                            mNfcResultCallback.onCheckError(error);
                            mNfcResultCallback = null;
                        }
                        return;
                    }
                }else if (item.equals(find_sim)) {
                    int ret = jsonObject.optInt("ret",-1);
                    setLog("sim ret[" + ret + "]");
                    if(mNResultCallback != null){
                        mNResultCallback.onFindSim(ret);
                        mNResultCallback = null;
                    }
                }

                if (cmd != null && cmd.contains(check_nfcreader)) {
                    String typea_uid = jsonObject.optString("typea_uid", "");
                    String cpu_ats = jsonObject.optString("cpu_ats", "");
                    String cpu_apdu = jsonObject.optString("cpu_apdu", "");
                    String typeb_atqb = jsonObject.optString("typeb_atqb", "");
                    String mifare_block0 = jsonObject.optString("mifare_block0", "");
                    String mifare_block1 = jsonObject.optString("mifare_block1", "");
                    if(mNfcResultCallback != null){
                        if(!typea_uid.isEmpty()){
                            mNfcResultCallback.onCheckCard("typea_uid", typea_uid);
                        }else if(!cpu_ats.isEmpty()){
                            mNfcResultCallback.onCheckCard("cpu_ats", cpu_ats);
                        }else if(!cpu_apdu.isEmpty()){
                            mNfcResultCallback.onCheckCard("cpu_apdu", cpu_apdu);
                        }else if(!typeb_atqb.isEmpty()){
                            mNfcResultCallback.onCheckCard("typeb_atqb", typeb_atqb);
                        }else if(!mifare_block0.isEmpty()){
                            mNfcResultCallback.onCheckCard("mifare_block0", mifare_block0);
                        }else if(!mifare_block1.isEmpty()){
                            mNfcResultCallback.onCheckCard("mifare_block1", mifare_block1);
                        }
                    }
                }
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }


    private byte[] addBytes(byte[] data1, byte[] data2) {
        if (data1 == null) {
            if (data2 == null) {
                return null;
            }
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        byte[] data3 = new byte[data1.length + data2.length];

        System.arraycopy(data1, 0, data3, 0, data1.length);

        System.arraycopy(data2, 0, data3, data1.length, data2.length);

        return data3;

    }

    private void setLog(String content) {
        Log.d("TAGG-" + TAG, content);
    }

    private void sleepThread(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static List<String> getSerialPath() {
        List<String> ttysList = new ArrayList<String>();
        String cmd = "ls /dev";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                if (lineStr.contains("ttyUSBN")) {
                    ttysList.add("/dev/" + lineStr.trim());
                }
            }
            // 检查命令是否执行失败。
            if (p.waitFor() != 0 && p.exitValue() == 1) {
                // p.exitValue()==0表示正常结束，1：非正常结束
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inBr != null) {
                    inBr.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ttysList;

    }

    private String getMaxTtyUSB() {
        int maxNumber = -1;
        String maxTty = "/dev/ttyUSBN0";
        List<String> ttysList = getSerialPath();
        for (String tty : ttysList) {
            // 从 "/dev/ttyUSB" 后面提取数字部分
            if (tty.contains("ttyUSBN")) {
                String numberStr = tty.substring(tty.lastIndexOf("ttyUSBN") + 7); // 提取数字部分
                try {
                    int number = Integer.parseInt(numberStr);
                    if (number > maxNumber) {
                        maxNumber = number;
                        maxTty = tty;
                    }
                } catch (NumberFormatException e) {
                    // 如果解析失败，忽略该项
                }
            }
        }
        Log.d("tagg", "getMaxTtyUSB: " + maxTty);
        return maxTty;
    }
}
