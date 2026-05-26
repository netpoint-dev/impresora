package pos.com.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class UtilPermission {
    static private final int MY_REQUEST_CODE   = 1000;

    static public void getPermission(String[]    permissions, Context context, Activity activity){
        PackageManager packageManager  = context.getPackageManager();
        PermissionInfo permissionInfo  = null;
        for(int i=0;i<permissions.length;i++){
            try {
                permissionInfo  = packageManager.getPermissionInfo(permissions[i],0);
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
            CharSequence permissionName = permissionInfo.loadLabel(packageManager);
            if(ContextCompat.checkSelfPermission(context,permissions[i])!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissions[i])){
                    LogUtil.info("不再提示【"+permissionName+"】权限申请");
                }else{
                    ActivityCompat.requestPermissions(activity,permissions,MY_REQUEST_CODE);
                }
            }else{
                LogUtil.info("已经获取权限:"+permissionName);
            }
        }
    }
}
