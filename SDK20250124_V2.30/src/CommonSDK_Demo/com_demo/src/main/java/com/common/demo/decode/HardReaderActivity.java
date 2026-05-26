package com.common.demo.decode;


import java.io.UnsupportedEncodingException;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.apiutil.CommonException;
import com.common.apiutil.decode.HardReader;
import com.common.callback.IDecodeReaderListener;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;


public class HardReaderActivity extends BaseActivity implements KeyEventResolver.OnKeyEventListener{
    final private static String TAG = "HardReaderActivity";
    final private static int DISCOVERY_DATA = 1;
    private HardReader mHardReader;
    private TextView tvDataShow, circleCountShow;
    private RadioGroup radioGroup_type;
    private EditText etCircleCount;
    private Button open_hardreader;
    private Button close_hardreader;
    private boolean circleTest = false;
    private int successCount = 0;
    private Thread circleThread;
    private Button scan_hardreader;
    private Button cycle_test;
    private KeyEventResolver mKeyEventResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hardreader);

        initView();
    }

    private void initView(){

        scan_hardreader = (Button)findViewById(R.id.scan_hardreader);
        cycle_test = (Button)findViewById(R.id.cycle_test);
        scan_hardreader.setEnabled(false);
        cycle_test.setEnabled(false);

        tvDataShow =  (TextView) findViewById(R.id.data_show);
        tvDataShow.setInputType(0);
        tvDataShow.setSingleLine(false);
        tvDataShow.setHorizontallyScrolling(false);
        etCircleCount = (EditText)findViewById(R.id.circle_num);
        circleCountShow = (TextView) findViewById(R.id.circleCountShow);

        open_hardreader = findViewById(R.id.open_hardreader);
        close_hardreader = findViewById(R.id.close_hardreader);
        radioGroup_type = (RadioGroup) findViewById(R.id.radioGroup_type);

        radioGroup_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton_usb){
                    open_hardreader.setVisibility(View.GONE);
                    close_hardreader.setVisibility(View.GONE);
                    tvDataShow.setText("");
                    circleCountShow.setText("");
                    try {
                        mHardReader.close();
                        circleTest = false;
                        successCount = 0;
                    } catch (CommonException e) {
                        e.printStackTrace();
                    }

                }else{
                    open_hardreader.setVisibility(View.VISIBLE);
                    close_hardreader.setVisibility(View.VISIBLE);
                    tvDataShow.setText("");
                    circleCountShow.setText("");
                }
            }
        });

    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case DISCOVERY_DATA:
                    tvDataShow.setText("");
                    byte[] buf = new byte[1024];
                    int count = 0;
                    try {
                        count = mHardReader.get(buf);
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if(count!=0){
                        String str = null;
                        try {
                            str = new String(buf,"GB2312");
                            Log.d("hyt", str);
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d(TAG, "String == null!");
                        }
                        successCount++;
                        circleCountShow.setText("扫描成功["+successCount+"]");

                        tvDataShow.setText(str);
                    }
                    break;
            }
        }
    };

    public void openHardreader(View view){
        try {
            mHardReader.open(115200);
            Toast.makeText(HardReaderActivity.this, "打开成功", Toast.LENGTH_SHORT).show();
            scan_hardreader.setEnabled(true);
            cycle_test.setEnabled(true);
        } catch (CommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void closeHardreader(View view){
        try {
            if(circleThread!=null){
                circleThread.interrupt();
                circleThread = null;
            }

            mHardReader.close();

            circleTest = false;
            successCount = 0;
            tvDataShow.setText("");
            circleCountShow.setText("");
            scan_hardreader.setEnabled(false);
            cycle_test.setEnabled(false);
            Toast.makeText(HardReaderActivity.this, "关闭成功", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void scanHardreader(View view){
        tvDataShow.setText("");
    }


    public void circleTest(View view){
        findViewById(R.id.scan_hardreader).setEnabled(false);
        circleTest = true;
        final int count = Integer.valueOf(etCircleCount.getText().toString());

        circleThread = new Thread(new Runnable() {
            int testCount = 0;
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(circleTest && testCount < count){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                            tvDataShow.setText("");
                        }
                    });

                    testCount++;

                    Log.d(TAG, "循环执行[" + testCount + "]");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        scan_hardreader.setEnabled(true);
                    }
                });
            }
        });
        circleThread.start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG,"onResume ===");
        if(mHardReader == null){
            Log.i(TAG,"mHardReader ===");
            mHardReader = new HardReader(this);//初始化
        }
        mKeyEventResolver = new KeyEventResolver(this);

        mHardReader.setHardReaderListener(new IDecodeReaderListener(){

            @Override
            public void onRecvData(byte[] data) {
                // TODO Auto-generated method stub

                try {
                    final String str = new String(data,"GB2312");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            successCount++;
                            circleCountShow.setText("扫描成功["+successCount+"]");

                            tvDataShow.setText(str);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d(TAG, "String == null!");
                }
            }});
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG,"onStop ===");
        try {
            if(mHardReader != null){
                mHardReader.close();
            }
        } catch (CommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 截获按键事件.发给ScanGunKeyEventHelper
     */
    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        //要是重虚拟键盘输入怎不拦截
        if ("Virtual".equals(event.getDevice().getName())) {
            return super.dispatchKeyEvent(event);
        }
        mKeyEventResolver.analysisKeyEvent(event);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyEventResolver.onDestroy();
    }

    @Override
    public void onScanSuccess(String barcode) {
        tvDataShow.setText(barcode);
        successCount++;
        circleCountShow.setText("扫描成功["+successCount+"]");
    }

    @Override
    public void onBackPress() {

    }
}
