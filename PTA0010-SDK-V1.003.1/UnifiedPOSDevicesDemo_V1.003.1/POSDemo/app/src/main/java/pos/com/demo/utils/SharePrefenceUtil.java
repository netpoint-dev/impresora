package pos.com.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import pos.com.demo.application.BaseApplication;


public class SharePrefenceUtil {

    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;

    private static final String KEY_DEVICE_CONNECTED             = "KEY_DEVICE_CONNECTED";//判断设备是否连接
    private static final String KEY_DEVICE_STRING                = "KEY_DEVICE_STRING";
    private static final String KEY_DEVICE_ORIENTATION          = "KEY_DEVICE_ORIENTATION";

    private static SharedPreferences getSp() {
        if (sharedPreferences == null) {
            sharedPreferences = BaseApplication.getInstance().getSharedPreferences("pos", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    private static SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSp().edit();
        }
        return editor;
    }

    private static void setStr(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    private static String getStr(String key){
        return getSp().getString(key,"");
    }

    private static void setBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    private static boolean getBoolean(String key){
        return getSp().getBoolean(key,false);
    }

    private static void setInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    private static int getInt(String key){
        return getSp().getInt(key,-1);
    }

    public static boolean getKeyDeviceConnected() {
        return getBoolean(KEY_DEVICE_CONNECTED);
    }

    public static void setKeyDeviceConnected(boolean connected) {
        setBoolean(KEY_DEVICE_CONNECTED,connected);
    }

    public static void setConnectDeivceString(String str){
        setStr(KEY_DEVICE_STRING,str);
    }

    public static String getConnectDeviceString(){
        return getStr(KEY_DEVICE_STRING);
    }

    public static void setKeyDeviceOrientation(boolean bLandScape){
        setBoolean(KEY_DEVICE_ORIENTATION,bLandScape);
    }

    public static boolean getKeyDeviceOrientation(){
        return getBoolean(KEY_DEVICE_ORIENTATION);
    }
}



