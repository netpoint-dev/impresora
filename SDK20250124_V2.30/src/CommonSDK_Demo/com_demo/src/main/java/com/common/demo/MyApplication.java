package com.common.demo;

import android.app.Application;
import android.util.Log;

import com.common.apiutil.util.SDKUtil;
import com.common.apiutil.util.SystemUtil;


public class MyApplication extends Application {
//    private static RefWatcher refWatcher;
//    public static SystemUtil systemUtil;
//    public static ThermalPrinter thermalPrinter;
//    public static MoneyBox moneyBox;
//    public static PowerUtil powerUtil;
//    public static FingerPrint fingerPrint;
//    public static MagneticCard magneticCard;
//    public static ReaderMonitor readerMonitor;
//    public IdCard idCard;
//    public static PosUtil posUtil;
//    public static PN512 pn512;
    private static int[] config = null;
    private String internal_Model;

    @Override
    public void onCreate() {
        super.onCreate();

        SDKUtil.getInstance(this).initSDK();

        if (!SystemUtil.isInstallServiceApk()) {
            Log.d("tagg", "API 调用 >> 系统反射");
        }else {
            Log.d("tagg", "API 调用 >> 服务APK");
        }

//        SystemUtil.getProperty("ro.serial.port.hardreader", "")
        internal_Model = SystemUtil.getInternalModel();
        setConfig(ConfigureUtil.COMMON);
//        initConfig();
//        moneyBox = MoneyBox.getInstance(this);
//        powerUtil = PowerUtil.getInstance(this);
//        fingerPrint = FingerPrint.getInstance(this);
//        magneticCard = MagneticCard.getInstance(this);
//        readerMonitor = ReaderMonitor.getInstance(this);
//        pn512 = PN512.getInstance(this);

//        refWatcher = LeakCanary.install(this);
//        SystemClock.sleep(3000);
    }
//    //提供给外部调用的方法
//    public static RefWatcher getRefWatcher() {
//
//        return refWatcher;
//    }

    public static void setConfig(int[] config) {
        MyApplication.config = config;
    }

    public static int[] getConfig() {
        return config;
    }

    private void initConfig() {
        /*if(internal_Model.equals("S1")){
            setConfig(ConfigureUtil.S1);
        }else */

        String[] testSupports = SystemUtil.getDeviceSupport();
        if(testSupports != null){
            setConfig(StringArrayToIntArray(testSupports));
        }else{

        }

    }

    public int[] StringArrayToIntArray(String[] arr){
        int[] array = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            array[i] = Integer.parseInt(arr[i]);
        }
        return array;
    }
}
