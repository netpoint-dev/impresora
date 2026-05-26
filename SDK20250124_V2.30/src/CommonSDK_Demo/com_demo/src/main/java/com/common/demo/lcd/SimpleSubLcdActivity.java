package com.common.demo.lcd;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.common.apiutil.ResultCode;
import com.common.apiutil.lcd.SimpleSubLcd;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.io.InputStream;
import java.util.Locale;

public class SimpleSubLcdActivity extends BaseActivity {

    //UI
    private TextView versionTv;
    private TextView resolutionTv;
    private EditText picPathEdt;
    private EditText updatePathEdt;
    private Button updateBtn;

    private SimpleSubLcd mSmileLCDUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_simple_sublcd);

        initView();

        mSmileLCDUtil = new SimpleSubLcd(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSmileLCDUtil.init();
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmileLCDUtil.release();
    }

    private void initView() {
        versionTv = findViewById(R.id.versionTv);
        resolutionTv = findViewById(R.id.resolutionTv);
        picPathEdt = findViewById(R.id.picPathEdt);
        updatePathEdt = findViewById(R.id.updatePathEdt);
        updateBtn = findViewById(R.id.updateBtn);

        picPathEdt.setText("/data/data/" + getPackageName() + "/cache/1.bin");
        updatePathEdt.setText("/data/data/" + getPackageName() + "/cache/project.bin");
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getVersionBtn:
                String version = mSmileLCDUtil.getVersion();
                if(version!=null){
                    versionTv.setText(version);
                }
                break;
            case R.id.getResolutionBtn:
                String resolution = String.format(
                        Locale.getDefault(), "%d x %d", mSmileLCDUtil.getScreenWidth(), mSmileLCDUtil.getScreenHeight());
                resolutionTv.setText(resolution);
                break;
            case R.id.updateBtn:
                updateBtn.setEnabled(false);
                String updatePath = updatePathEdt.getText().toString();
                if(!updatePath.isEmpty()){
                    int result = mSmileLCDUtil.update(updatePath);
                    if(result == ResultCode.SUCCESS){
                        Toast.makeText(this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
                    }else if(result == ResultCode.ERR_SYS_NOT_SUPPORT){
                        Toast.makeText(this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                    }
                }
                updateBtn.setEnabled(true);
                break;
            case R.id.showBtn:
                String picPath = picPathEdt.getText().toString();
                if(!picPath.isEmpty()){
                    int result = mSmileLCDUtil.show(picPath);
                    if(result == ResultCode.SUCCESS){
                        Toast.makeText(this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
                    }else if(result == ResultCode.ERR_SYS_INVALID){
                        Toast.makeText(this, getString(R.string.invalid_test), Toast.LENGTH_SHORT).show();
                    }else if(result == ResultCode.ERR_SYS_TIMEOUT){
                        Toast.makeText(this, getString(R.string.timeout_text), Toast.LENGTH_SHORT).show();
                    }else if(result == ResultCode.ERR_SYS_OVER_FLOW){
                        Toast.makeText(this, getString(R.string.over_flow_text), Toast.LENGTH_SHORT).show();
                    }else if(result == ResultCode.ERR_SYS_NOT_SUPPORT){
                        Toast.makeText(this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.showBitmapBtn:
            case R.id.showOffsetBitmapBtn:
                @SuppressLint("ResourceType")
                InputStream ips = getResources().openRawResource(R.drawable.sub_screen_test);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeStream(ips, null, options);
                int result;
                if (view.getId() == R.id.showOffsetBitmapBtn) {
                    result = mSmileLCDUtil.show(bitmap, 50, 50);
                } else {
                    result = mSmileLCDUtil.show(bitmap);
                }
                if (result == ResultCode.SUCCESS) {
                    Toast.makeText(this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
                } else if (result == ResultCode.ERR_SYS_NOT_SUPPORT) {
                    Toast.makeText(this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
