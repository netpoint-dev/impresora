package pos.com.demo.utils;

import android.util.Log;

public class LogUtil {

    private static final String TAG = "POSDemoLog";
    private static final boolean PRINT = true;

    public static void info(String msg){
        if (PRINT)
        Log.i(TAG,"----------"+msg);
    }

    public static void error(String msg){
        if (PRINT)
        Log.e(TAG,"----------"+msg);
    }

    public static void error(int msg){
        error(msg+"");
    }

    public static void log(String content) {
        if (PRINT) {
            Log.i(TAG, content);
        }
    }
    public static void loge(String content) {
        if (PRINT) {
            Log.e(TAG, content);
        }
    }
}
