package pos.com.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pos.com.demo.activitys.ActivityDrawer;
import pos.com.demo.activitys.ActivityLcd;
import pos.com.demo.activitys.ActivityMagcard;
import pos.com.demo.activitys.ActivityPrinter;
import pos.com.demo.activitys.ActivitySerialPort;
import pos.com.demo.utils.LogUtil;
import pos.com.demo.utils.UtilPermission;
import pos.com.demo.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String[]    permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UtilPermission.getPermission(permissions,this.getApplicationContext(),MainActivity.this);
       // getPermission();
    }

    public void onClick(View v){
        int id = v.getId();

        Class cls = null;
        switch (id){
            case R.id.btn_prn:
                cls = ActivityPrinter.class;
                break;
            case R.id.btn_lcd:
                cls = ActivityLcd.class;
                break;
            case R.id.btn_drawer:
                cls = ActivityDrawer.class;
                break;
            case R.id.btn_uart:
                cls = ActivitySerialPort.class;
                break;
            case R.id.btn_magcard:
                cls = ActivityMagcard.class;
                break;
        }
        showPage(cls);
    }


    private void showPage(Class cls){

        if (cls!=null){
            Intent intent = new Intent(this, cls);
            // intent.putExtra("index",iIndex);
            startActivity(intent);
        }
    }
}
