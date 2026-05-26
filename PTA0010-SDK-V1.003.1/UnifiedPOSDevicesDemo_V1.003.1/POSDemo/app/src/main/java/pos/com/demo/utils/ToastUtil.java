package pos.com.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(Activity activity, int resource){
        Toast.makeText(activity,resource,Toast.LENGTH_LONG).show();
    }
    public static void showToast(Activity activity,String value){
        Toast.makeText(activity,value,Toast.LENGTH_LONG).show();
    }
    public static void showToast(Context context, String value){
        Toast.makeText(context,value,Toast.LENGTH_LONG).show();
    }
}
