package com.common.demo.deliverylocker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.deliverylocker.DeliveryLocker;
import com.common.callback.IUpdateCallBack;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

public class DeliverylockerActivity extends BaseActivity {

    //UI
    private TextView versionTv;
    private TextView statusTv;
    private Button openBtn;
    private Button statusBtn;
    private EditText coordinateXEdt;
    private EditText coordinateYEdt;
    private EditText binPathEdt;

    private DeliveryLocker mDeliveryLocker;
    private int coordinateX = 0;
    private int coordinateY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_deliverylocker);

        mDeliveryLocker = new DeliveryLocker(this);
        mDeliveryLocker.init(CommonConstants.ConnectType.SERIAL);
        initView();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeliveryLocker.destroy();
    }

    private void initView() {
        versionTv = findViewById(R.id.versionTv);
        statusTv = findViewById(R.id.statusTv);
        coordinateXEdt = findViewById(R.id.coordinateXEdt);
        coordinateYEdt = findViewById(R.id.coordinateYEdt);
        openBtn = findViewById(R.id.openBtn);
        statusBtn = findViewById(R.id.statusBtn);
        binPathEdt = findViewById(R.id.binPathEdt);

        binPathEdt.setText("/data/data/" + getPackageName() + "/cache/update.bin");

        coordinateXEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String xStr = editable.toString();
                if(!xStr.isEmpty()){
                    coordinateX = Integer.valueOf(xStr);
                    Log.d("hyt", "coordinateX " + coordinateX);
                }

            }
        });

        coordinateYEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String yStr = editable.toString();
                if(!yStr.isEmpty()){
                    coordinateY = Integer.valueOf(yStr);
                }

            }
        });
    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.versionBtn:
                String version = mDeliveryLocker.getVersion(coordinateX,coordinateY);
                if(version.isEmpty()){
                    versionTv.setText(getString(R.string.unexpect_text));
                }else{
                    versionTv.setText(version);
                }

                break;
            case R.id.openBtn:
                int ret = mDeliveryLocker.open(coordinateX, coordinateY);
                Toast.makeText(this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                break;
            case R.id.statusBtn:
                ret = mDeliveryLocker.getStatus(coordinateX, coordinateY);
                if(ret != 0 && ret != 1){
                    statusTv.setText(getString(R.string.unexpect_text));
                }else{
                    statusTv.setText(ret==0?getString(R.string.deliverylocker_status_close):getString(R.string.deliverylocker_status_open));
                }
                break;
            case R.id.updateBtn:
                statusTv.setText(getString(R.string.deliverylocker_updating));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDeliveryLocker.moduleUpdate(coordinateX, coordinateY, binPathEdt.getText().toString(), new IUpdateCallBack() {
                            @Override
                            public void updateComplete(final boolean isUpdateSuccess) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        statusTv.setText(isUpdateSuccess?getString(R.string.deliverylocker_update_succeed):getString(R.string.deliverylocker_update_succeed));
                                    }
                                });

                            }
                        });
                    }
                }).start();
                break;
        }
    }

}
