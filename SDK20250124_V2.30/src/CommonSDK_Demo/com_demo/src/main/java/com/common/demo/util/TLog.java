package com.common.demo.util;

import android.util.Log;

public class TLog {
    private static boolean enableLogging = true;
    private static String tag = "tagg"; // 默认的 TAG

    public static void setEnableLogging(boolean enable) {
        enableLogging = enable;
    }

    public static void setTag(String tag) {
        TLog.tag = tag;
    }

    public static void d(String message) {
        if (enableLogging) {
            Log.d(tag, message);
        }
    }

    public static void i(String message) {
        if (enableLogging) {
            Log.i(tag, message);
        }
    }

    public static void w(String message) {
        if (enableLogging) {
            Log.w(tag, message);
        }
    }

    public static void e(String message) {
        if (enableLogging) {
            Log.e(tag, message);
        }
    }

    public static void e(String message, Throwable throwable) {
        if (enableLogging) {
            Log.e(tag, message, throwable);
        }
    }

    // 其他日志级别的方法类似地实现
}
