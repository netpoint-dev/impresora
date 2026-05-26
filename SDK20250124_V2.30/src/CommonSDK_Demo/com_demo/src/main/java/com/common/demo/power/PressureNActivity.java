package com.common.demo.power;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.apiutil.ResultCode;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityPowerManagerAutoN2Binding;
import com.common.demo.databinding.ActivityPowerManagerNPressureBinding;
import com.common.demo.decode.KeyEventResolver;
import com.common.demo.util.FileUtil;
import com.common.demo.util.SharedPreferencesUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PressureNActivity extends BaseActivity implements View.OnClickListener, KeyEventResolver.OnKeyEventListener{
    private ActivityPowerManagerNPressureBinding binding;

    private PowerManager_N2 powerManager;

    private static final int SHOW_MESSAGE = 1;
    private static int tofCustomAllCount = 0;
    private static int tof3cmAllCount = 0;
    private static int tof30cmAllCount = 0;
    private static int tofNetAllCount = 0;
    private static int tofCustomfailCount = 0;
    private static int tof3cmfailCount = 0;
    private static int tof30cmfailCount = 0;
    private static int tofNetfailCount = 0;
    private static int hidReadCount = 0;
    private static int hidAllCount = 0;
    private static int nfcReaderAllCount = 0;
    private static int nfcReaderSuccessCount = 0;
    private static int audioAllCount = 0;
    private boolean isHidPressing;
    private boolean isNfcReaderPressing;
    private boolean isAudioPressing;
    private boolean isCheckN;

    private String hidRecord = "HID[sucess/all]";
    private String nfcReaderRecord = "NfcReader[sucess/all]";
    private String tof3cmRecord = "TOF3cm[fail/all]";
    private String tof30cmRecord = "TOF30cm[fail/all]";
    private String tofNetRecord = "TOFNet[fail/all]";
    private int tofCustomErr;
    private int tofCustomMm;
    private final Object lock = new Object();  // 用于加锁
    private String nfcUid;
    private ProgressDialog checkNProgressDialog;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private KeyEventResolver mKeyEventResolver;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SHOW_MESSAGE:
                    if (getLineCount() > 100) {
                        binding.testTextView.setText("");
                    }
                    binding.testTextView.append(DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ": " + (String) msg.obj + "\n");
                    binding.scrollView.smoothScrollTo(0, binding.testTextView.getBottom());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerManagerNPressureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        powerManager = PowerManager_N2.getInstance(this);
        powerManager.monitorUSB();
        int ret = powerManager.open(115200, serialListener);
        if(ret == ResultCode.SUCCESS || ret == ResultCode.ERR_SYS_ALREADY_OPEN){
            showMessage("open n success");
        }else {
            showMessage("not find n");
        }

        DataStorage.setFilePath(Environment.getExternalStorageDirectory() + "/npressure_record.txt");

        String tof3cmRecordStr = DataStorage.loadData(tof3cmRecord);
        String tof30cmRecordStr = DataStorage.loadData(tof30cmRecord);
        String tofNetRecordStr = DataStorage.loadData(tofNetRecord);
        String hidRecordStr = DataStorage.loadData(hidRecord);
        String nfcReaderRecordStr = DataStorage.loadData(nfcReaderRecord);
        if(tof3cmRecordStr!= null && tof3cmRecordStr.contains("/")){
            String[] tof3cmRecordArr = tof3cmRecordStr.split("/");
            tof3cmfailCount = Integer.parseInt(tof3cmRecordArr[0]);
            tof3cmAllCount = Integer.parseInt(tof3cmRecordArr[1]);
            binding.tof3cmCountTv.setText("fail/all[" + tof3cmfailCount + "/" + tof3cmAllCount + "]");
        }
        if(tof30cmRecordStr!= null && tof30cmRecordStr.contains("/")){
            String[] tof30cmRecordArr = tof30cmRecordStr.split("/");
            tof30cmfailCount = Integer.parseInt(tof30cmRecordArr[0]);
            tof30cmAllCount = Integer.parseInt(tof30cmRecordArr[1]);
            binding.tof30cmCountTv.setText("fail/all[" + tof30cmfailCount + "/" + tof30cmAllCount + "]");
        }
        if(tofNetRecordStr!= null && tofNetRecordStr.contains("/")){
            String[] tofNetRecordArr = tofNetRecordStr.split("/");
            tofNetfailCount = Integer.parseInt(tofNetRecordArr[0]);
            tofNetAllCount = Integer.parseInt(tofNetRecordArr[1]);
            binding.tofNetCountTv.setText("fail/all[" + tofNetfailCount + "/" + tofNetAllCount + "]");
        }
        if(hidRecordStr!= null && hidRecordStr.contains("/")){
            String[] hidRecordArr = hidRecordStr.split("/");
            hidReadCount = Integer.parseInt(hidRecordArr[0]);
            hidAllCount = Integer.parseInt(hidRecordArr[1]);
            binding.hidCountTv.setText("success/all[" + hidReadCount + "/" + hidAllCount + "]");
        }
        if(nfcReaderRecordStr!= null && nfcReaderRecordStr.contains("/")){
            String[] nfcReaderRecordArr = nfcReaderRecordStr.split("/");
            nfcReaderSuccessCount = Integer.parseInt(nfcReaderRecordArr[0]);
            nfcReaderAllCount = Integer.parseInt(nfcReaderRecordArr[1]);
            binding.nfcReaderCountTv.setText("success/all[" + nfcReaderSuccessCount + "/" + nfcReaderAllCount + "]");
        }

        binding.saveLocalCb.setChecked(SharedPreferencesUtil.getBoolean(this, "save_local", false));
        binding.saveLocalCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.putBoolean(this, "save_local", isChecked);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKeyEventResolver = new KeyEventResolver(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        powerManager.unmonitorUSB();
        mKeyEventResolver.onDestroy();

        binding.tofStopBtn.performClick();
        binding.hidStopBtn.performClick();
        binding.nfcReaderStopBtn.performClick();
        binding.audioStopBtn.performClick();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tofCustomStartBtn:
                tofCustomfailCount = 0;
                tofCustomAllCount = 0;
                String tofCustomMmStr = binding.tofCustomMmEt.getText().toString();
                String tofCustomErrStr = binding.tofCustomErrEt.getText().toString();
                if(tofCustomMmStr.isEmpty() || tofCustomErrStr.isEmpty()){
                    showMessage("请输入自定义测距距离和误差值");
                    return;
                }
                powerManager.tofStopGetDistance();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tofCustomErr = Integer.valueOf(tofCustomErrStr);
                tofCustomMm = Integer.valueOf(tofCustomMmStr);
                powerManager.tofStartGetDistance(tofCustomMm, new TOFResultCallback(3));
                break;
            case R.id.tof3cmStartBtn:
                powerManager.tofStopGetDistance();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                powerManager.tofStartGetDistance(30, new TOFResultCallback(0));
                break;
            case R.id.tof30cmStartBtn:
                powerManager.tofStopGetDistance();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                powerManager.tofStartGetDistance(300, new TOFResultCallback(1));
                break;
            case R.id.tofNetStartBtn:
                powerManager.tofStopGetDistance();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                powerManager.tofStartGetDistance(500, new TOFResultCallback(2));
                break;
            case R.id.tofStopBtn:
                powerManager.tofStopGetDistance();
                DataStorage.saveData(tof3cmRecord, tof3cmfailCount + "/" + tof3cmAllCount);
                DataStorage.saveData(tof30cmRecord, tof30cmfailCount + "/" + tof30cmAllCount);
                DataStorage.saveData(tofNetRecord, tofNetfailCount + "/" + tofNetAllCount);
                break;
            case R.id.hidStartBtn:
                isHidPressing = true;
                executorService.submit(hidPressRunnable);
                break;
            case R.id.hidStopBtn:
                isHidPressing = false;
                DataStorage.saveData(hidRecord, hidReadCount + "/" + hidAllCount);
                break;
            case R.id.nfcReaderStartBtn:
                isNfcReaderPressing = true;
                executorService.submit(nfcReaderPressRunnable);
                break;
            case R.id.nfcReaderStopBtn:
                isNfcReaderPressing = false;
                DataStorage.saveData(nfcReaderRecord, nfcReaderSuccessCount + "/" + nfcReaderAllCount);
                break;
            case R.id.audioStartBtn:
                isAudioPressing = true;
                audioAllCount = 0;
                executorService.submit(audioPressRunnable);
                break;
            case R.id.audioStopBtn:
                isAudioPressing = false;
                break;
            case R.id.clearBtn:
                FileUtil.deleteFile("npressure_hid.txt", Environment.getExternalStorageDirectory().getAbsolutePath());
                FileUtil.deleteFile("npressure_record.txt", Environment.getExternalStorageDirectory().getAbsolutePath());
                FileUtil.deleteFile("npressure_all.txt", Environment.getExternalStorageDirectory().getAbsolutePath());
                tof3cmfailCount = 0;
                tof3cmAllCount = 0;
                tof30cmfailCount = 0;
                tof30cmAllCount = 0;
                tofNetfailCount = 0;
                tofNetAllCount = 0;
                tofCustomfailCount = 0;
                tofCustomAllCount = 0;
                hidReadCount = 0;
                hidAllCount = 0;
                nfcReaderSuccessCount = 0;
                nfcReaderAllCount = 0;
                audioAllCount = 0;
                binding.tof3cmCountTv.setText("fail/all[0/0]");
                binding.tof30cmCountTv.setText("fail/all[0/0]");
                binding.tofNetCountTv.setText("fail/all[0/0]");
                binding.tofCustomCountTv.setText("fail/all[0/0]");
                binding.hidCountTv.setText("success/all[0/0]");
                binding.nfcReaderCountTv.setText("success/all[0/0]");
                binding.audioCountTv.setText("all[0]");
                showMessage("清除成功");
                break;
        }
    }

    @Override
    public void onScanSuccess(String barcode) {
        if(!isHidPressing){
            mKeyEventResolver.onDestroy();
            return;
        }
        boolean isPass = false;
        FileUtil.saveFile("npressure_hid.txt", Environment.getExternalStorageDirectory().getAbsolutePath(), DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ": " + barcode);
        if(barcode.equals("0123456789-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ[]~!@#$%^&*()_+`{}|\\;':\",./<>?")){
            hidReadCount++;
            isPass = true;
        }
        handler.post(new Runnable() {

            @Override
            public void run() {
                binding.hidCountTv.setText("success/all[" + hidReadCount + "/" + hidAllCount + "]");
            }
        });
        showMessage("hid[" + (isPass?"PASS":"FAIL") + "]:"  + barcode + ", success/all: " + hidReadCount + "/" + hidAllCount);

    }

    @Override
    public void onBackPress() {

    }

    private class TOFResultCallback implements PowerManager_N2.TofResultCallback {

        private int mType;
        public TOFResultCallback(int type) {
            mType = type;
        }

        @Override
        public void onCheckDistance(int status, int distance) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    boolean isPass = false;
                    if(mType == 0){
                        if(Math.abs(distance - 30) > 10 || status != 0){
                            tof3cmfailCount++;
                            isPass = false;
                        }else{
                            isPass = true;
                        }
                        tof3cmAllCount++;
                        if(tof3cmAllCount % 50 == 0){
                            DataStorage.saveData(tof3cmRecord, tof3cmfailCount + "/" + tof3cmAllCount);
                        }
                        showMessage("3cm tof[" + (isPass?"PASS":"FAIL") + "]: " + distance + ", fail/all: " + tof3cmfailCount + "/" + tof3cmAllCount);
                        binding.tof3cmCountTv.setText("fail/all[" + tof3cmfailCount + "/" + tof3cmAllCount + "]");
                    }else if(mType == 1){
                        if(Math.abs(distance - 300) > 15 || status != 0){
                            tof30cmfailCount++;
                            isPass = false;
                        }else {
                            isPass = true;
                        }
                        tof30cmAllCount++;
                        if(tof30cmAllCount % 50 == 0){
                            DataStorage.saveData(tof30cmRecord, tof30cmfailCount + "/" + tof30cmAllCount);
                        }
                        showMessage("30cm tof[" + (isPass?"PASS":"FAIL") + "]: " + distance + ", fail/all: " + tof30cmfailCount + "/" + tof30cmAllCount);
                        binding.tof30cmCountTv.setText("fail/all[" + tof30cmfailCount + "/" + tof30cmAllCount + "]");
                    }else if(mType == 2){
                        if(status == 0){
                            tofNetfailCount++;
                            isPass = false;
                        }else {
                            isPass = true;
                        }
                        tofNetAllCount++;
                        if(tofNetAllCount % 50 == 0){
                            DataStorage.saveData(tofNetRecord, tofNetfailCount + "/" + tofNetAllCount);
                        }
                        showMessage("净空测试 tof[" + (isPass?"PASS":"FAIL") + "]: " + distance + ", fail/all: " + tofNetfailCount + "/" + tofNetAllCount);
                        binding.tofNetCountTv.setText("fail/all[" + tofNetfailCount + "/" + tofNetAllCount + "]");
                    }else if(mType == 3){
                        if(Math.abs(distance - tofCustomMm) > tofCustomErr || status != 0){
                            tofCustomfailCount++;
                            isPass = false;
                        }else {
                            isPass = true;
                        }
                        tofCustomAllCount++;
                        showMessage(tofCustomMm + "mm tof[" + (isPass?"PASS":"FAIL") + "]: " + distance + ", fail/all: " + tofCustomfailCount + "/" + tofCustomAllCount);
                        binding.tofNetCountTv.setText("fail/all[" + tofCustomfailCount + "/" + tofCustomAllCount + "]");
                    }
                }
            });

        }

        @Override
        public void onCalibrationResult(int mode, boolean isSuccess) {

        }

        @Override
        public void onCheckError(String error) {
            showMessage(error);
        }
    }

    private Runnable hidPressRunnable = new Runnable() {

        @Override
        public void run() {
            while (isHidPressing) {
                powerManager.checkHid();
                hidAllCount++;
                if (hidAllCount % 50 == 0){
                    DataStorage.saveData(hidRecord, hidReadCount + "/" + hidAllCount);
                }
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        binding.hidCountTv.setText("success/all[" + hidReadCount + "/" + hidAllCount + "]");
                    }
                });
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable nfcReaderPressRunnable = new Runnable() {

        @Override
        public void run() {
            while (isNfcReaderPressing) {
                boolean isPass = false;
                synchronized (lock) {
                    nfcUid = null;
                }
                powerManager.setNfcReader(nfcReaderCallback);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nfcReaderAllCount++;
                if(nfcUid!= null && nfcUid.length() > 0){
                    isPass = true;
                    nfcReaderSuccessCount++;
                }

                showMessage("nfc[" + (isPass?"PASS":"FAIL") + "]: uid = " + nfcUid + ", success/all: " + nfcReaderSuccessCount + "/" + nfcReaderAllCount);

                if(nfcReaderAllCount % 50 == 0){
                    DataStorage.saveData(nfcReaderRecord, nfcReaderSuccessCount + "/" + nfcReaderAllCount);
                }
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        binding.nfcReaderCountTv.setText("success/all[" + nfcReaderSuccessCount + "/" + nfcReaderAllCount + "]");
                    }
                });
            }
        }
    };

    private Runnable audioPressRunnable = new Runnable() {

        @Override
        public void run() {
            while (isAudioPressing) {
                audioAllCount++;
                powerManager.audioPlay(7);
                showMessage("audioPlay volume: 7, all: " + audioAllCount);

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        binding.audioCountTv.setText("all[" + audioAllCount + "]");
                    }
                });
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    };

    private PowerManager_N2.NfcResultCallback nfcReaderCallback = new PowerManager_N2.NfcResultCallback() {

        @Override
        public void onCheckError(String error) {
            showMessage(error);
        }

        @Override
        public void onCheckCard(String key, String value) {
            if(key.equals("typea_uid") && !value.isEmpty()){
                synchronized (lock) {
                    nfcUid = value;
                }
            }
        }
    };

    private PowerManager_N2.SerialListener serialListener = new PowerManager_N2.SerialListener() {

        @Override
        public void onConnected(String message) {
            if(checkNProgressDialog.isShowing()){
                checkNProgressDialog.dismiss();
            }
            showMessage(message);
        }

        @Override
        public void onDisconnected(String message) {
            showCheckNLoadingDialog();
            showMessage(message);
        }
    };

    private void showCheckNLoadingDialog() {
        checkNProgressDialog = new ProgressDialog(PressureNActivity.this);
        checkNProgressDialog.setMessage("正在检测N模块，请稍等...");
        checkNProgressDialog.setCancelable(true); // 设置点击弹窗外部区域或按“返回”按钮时关闭弹窗
        checkNProgressDialog.setCanceledOnTouchOutside(false); // 禁止触摸外部区域关闭弹窗
        checkNProgressDialog.show();
    }

    private void showMessage(String message) {
        Log.d("tagg", message);
        Message msg = new Message();
        msg.obj = message;
        msg.what = SHOW_MESSAGE;
        handler.sendMessage(msg);

        if(binding.saveLocalCb.isChecked()){
            FileUtil.saveFile("npressure_all.txt", Environment.getExternalStorageDirectory().getAbsolutePath(), DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ": " + message);
        }
    }

    // 用于计算 TextView 当前的行数
    private int getLineCount() {
        Layout layout = binding.testTextView.getLayout();
        if (layout != null) {
            return layout.getLineCount();  // 获取行数
        }
        return 0;
    }
}
