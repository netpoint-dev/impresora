package com.common.demo.power;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.apiutil.ResultCode;
import com.common.apiutil.util.ShellUtils;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityPowerManagerAutoN2Binding;
import com.common.demo.decode.KeyEventResolver;
import com.common.demo.util.FileUtil;

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
import java.util.concurrent.Future;
import android.view.ViewGroup;

public class PowerManageKN2AutoActivity extends BaseActivity implements View.OnClickListener, KeyEventResolver.OnKeyEventListener {

    private PowerManager_N2 powerManager;
    private ActivityPowerManagerAutoN2Binding binding;
    private ProgressDialog loadingDialog;

    private static final int TOF_DETECT_SHOW = 1;
    private static final int LED_DETECT_COMPLETE = 2;
    private static final int TELEPHONY_DETECT_COMPLETE = 3;
    private static final int TOF_CALIBRATION_COMPLETE = 4;
    private static final int MCU_DETECT_COMPLETE = 5;
    private static final int BLUETOOTH_SCAN_SHOW = 6;
    private static final int NFCREADER_DETECT_COMPLETE = 7;
    private static final int INIT_SIM_COMPLETE = 8;
    private boolean isTofCheck = false;
    private boolean ishidOnScan = false;
    private boolean isAudioPlay = false;
    private Future<?> ledDetectionFuture;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private List<String> bluetoothList = new ArrayList<>();  // 用于存储扫描到的数据
    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView recyclerView;
    private KeyEventResolver mKeyEventResolver;
    private ProgressDialog checkNProgressDialog;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case NFCREADER_DETECT_COMPLETE:
                    Bundle bundle = msg.getData();
                    String typea_uid = bundle.getString("typea_uid", "");
                    String cpu_ats = bundle.getString("cpu_ats", "");
                    String cpu_apdu = bundle.getString("cpu_apdu", "");
                    String typeb_atqb = bundle.getString("typeb_atqb", "");
                    String mifare_block0 = bundle.getString("mifare_block0", "");
                    String mifare_block1 = bundle.getString("mifare_block1", "");
                    if(!typea_uid.isEmpty()){
                        binding.nfcTypeAUIDTv.setText(typea_uid);
                    }else if (!cpu_ats.isEmpty()){
                        binding.nfcCpuAtsTv.setText(cpu_ats);
                    }else if (!cpu_apdu.isEmpty()){
                        binding.nfcCpuApduTv.setText(cpu_apdu);
                    }else if(!typeb_atqb.isEmpty()){
                        binding.nfcTypeBAtqBTv.setText(typeb_atqb);
                    }else if(!mifare_block0.isEmpty()){
                        binding.nfcMifareBlock0Tv.setText(mifare_block0);
                    } else if (!mifare_block1.isEmpty()) {
                        binding.nfcMifareBlock1Tv.setText(mifare_block1);
                    }
                    break;
                case BLUETOOTH_SCAN_SHOW:
                    PowerManager_N2.BluetoothInfo bluetoothInfo = (PowerManager_N2.BluetoothInfo) msg.obj;
                    if(bluetoothInfo != null) {
                        String addr = bluetoothInfo.getAddress();  // 获取蓝牙设备地址
                        int rssi = bluetoothInfo.getRssi();        // 获取设备信号强度

                        // 更新数据到List并刷新RecyclerView
                        String displayText = "Addr: " + addr + ", RSSI: " + rssi;
                        bluetoothList.add(displayText);
                        bluetoothAdapter.notifyItemInserted(bluetoothList.size() - 1);

                        // 自动滚动到RecyclerView底部
                        recyclerView.scrollToPosition(bluetoothList.size() - 1);
                    }

                    break;
                case TOF_CALIBRATION_COMPLETE:
                    if(loadingDialog != null){
                        loadingDialog.dismiss();
                    }
                    boolean tofCalibrationResult = (boolean) msg.obj;
                    int arg = msg.arg1;
                    if(arg == PowerManager_N2.TOP_CALIBRATION_10CM){
                        if(tofCalibrationResult){
                            binding.tofCalibration10cmResultTv.setTextColor(Color.GREEN);
                        }else {
                            binding.tofCalibration10cmResultTv.setTextColor(Color.RED);
                        }
                        binding.tofCalibration10cmResultTv.setText(tofCalibrationResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                        binding.tofCalibration10cmBtn.setEnabled(true);
                    }else if(arg == PowerManager_N2.TOP_CALIBRATION_60CM){
                        if(tofCalibrationResult){
                            binding.tofCalibration60cmResultTv.setTextColor(Color.GREEN);
                        }else {
                            binding.tofCalibration60cmResultTv.setTextColor(Color.RED);
                        }
                        binding.tofCalibration60cmResultTv.setText(tofCalibrationResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                        binding.tofCalibration60cmBtn.setEnabled(true);
                    }
                case TOF_DETECT_SHOW:
                    int status = msg.arg1;
                    int tofDistance = msg.arg2;
                    if(status == 0){
                        binding.tofDistanceTv.setText(tofDistance + "");
                    }else {
                        binding.tofDistanceTv.setText("invalid");
                    }
                    break;
                case LED_DETECT_COMPLETE:
                    if(loadingDialog != null){
                        loadingDialog.dismiss();
                    }
                    break;
                case TELEPHONY_DETECT_COMPLETE:
                    if(loadingDialog != null){
                        loadingDialog.dismiss();
                    }
                    int result = msg.arg1;
                    if(result == ResultCode.SUCCESS){
                        PowerManager_N2.TelephonyPingResult telephonyPingResult = (PowerManager_N2.TelephonyPingResult) msg.obj;
                        if(telephonyPingResult != null) {
                            binding.telephonyRssiTv.setText(telephonyPingResult.getRssi() + "");
                            int pingStatus = telephonyPingResult.getPingStatus();
                            if(pingStatus == 0){
                                binding.telephonyPingStatusTv.setTextColor(Color.GREEN);
                                binding.telephonyPingStatusTv.setText(getString(R.string.powerManage_n_telephony_ping_status_complete_text));
                            }else if(pingStatus == 1){
                                binding.telephonyPingStatusTv.setTextColor(Color.RED);
                                binding.telephonyPingStatusTv.setText(getString(R.string.powerManage_n_telephony_ping_status_timeout_text));
                            }else if(pingStatus == 2){
                                binding.telephonyPingStatusTv.setTextColor(Color.RED);
                                binding.telephonyPingStatusTv.setText(getString(R.string.powerManage_n_telephony_ping_status_terminated_text));
                            }else if(pingStatus == 3){
                                binding.telephonyPingStatusTv.setTextColor(Color.RED);
                                binding.telephonyPingStatusTv.setText(getString(R.string.powerManage_n_telephony_ping_status_flow_control_close_text));
                            }else if(pingStatus == 4){
                                binding.telephonyPingStatusTv.setTextColor(Color.RED);
                                binding.telephonyPingStatusTv.setText(getString(R.string.powerManage_n_telephony_ping_status_flow_control_open_text));
                            }
                            binding.telephonyPingReceiveCountTv.setText(telephonyPingResult.getReceiveCount() + "");
                            binding.telephonyPingSingleTimeTv.setText(telephonyPingResult.getPingTime() + "");
                        }
                    }else {
                        String error = (String) msg.obj;
                        Toast.makeText(PowerManageKN2AutoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                        binding.telephonyFailBtn.performClick();
                    }
                    break;
                case MCU_DETECT_COMPLETE:
                    if(loadingDialog != null){
                        loadingDialog.dismiss();
                    }
                    boolean mcuResult = (boolean) msg.obj;
                    if(mcuResult){
                        binding.mcuResultTv.setTextColor(Color.GREEN);
                        binding.mcuResultTv.setText(getString(R.string.powerManage_n_pass_text));
                        powerManager.setCheckResult(PowerManager_N2.set_mcu_result, true);
                    }else {
                        binding.mcuResultTv.setTextColor(Color.RED);
                        binding.mcuResultTv.setText(getString(R.string.powerManage_n_fail_text));
                        powerManager.setCheckResult(PowerManager_N2.set_mcu_result, false);
                    }
                    binding.mcuCheckBtn.setEnabled(true);
                    break;
                case INIT_SIM_COMPLETE:
                    if(loadingDialog != null){
                        loadingDialog.dismiss();
                    }
                    int ret = (int) msg.arg1;
                    Toast.makeText(PowerManageKN2AutoActivity.this, getString(R.string.powerManage_n_init_sim_text) + (ret==0?getString(R.string.success_test):getString(R.string.fail_test)), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityPowerManagerAutoN2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.redLedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(PowerManager_N2.RED_LED));
        binding.greenLedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(PowerManager_N2.GREEN_LED));
        binding.blueLedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(PowerManager_N2.BLUE_LED));
        binding.logoLedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(PowerManager_N2.LOGO_LED));
        binding.audioVolumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(PowerManager_N2.AUDIO_VOLUME));
        binding.allLedPassBtn.setVisibility(View.GONE);
        binding.allLedFailBtn.setVisibility(View.GONE);
        binding.audioPassBtn.setVisibility(View.GONE);
        binding.audioFailBtn.setVisibility(View.GONE);
        binding.tofPassBtn.setVisibility(View.GONE);
        binding.tofFailBtn.setVisibility(View.GONE);
        binding.telephonyPassBtn.setVisibility(View.GONE);
        binding.telephonyFailBtn.setVisibility(View.GONE);
        binding.nfcCardPassBtn.setVisibility(View.GONE);
        binding.nfcCardFailBtn.setVisibility(View.GONE);
        binding.nfcCardDeactivateBtn.setEnabled(false);
        binding.nfcReaderPassBtn.setVisibility(View.GONE);
        binding.nfcReaderFailBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKeyEventResolver = new KeyEventResolver(this);
    }

    private PowerManager_N2.LedResultCallback ledResultCallback = new PowerManager_N2.LedResultCallback() {

        @Override
        public void onCheckError(String error) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(ledDetectionFuture != null){
                        ledDetectionFuture.cancel(true);
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    binding.allLedFailBtn.performClick();
                    binding.allLedResultTv.append("\n" + error + "\n");
                }
            });
        }
    };

    private PowerManager_N2.BluetoothResultCallback bluetoothResultCallback = new PowerManager_N2.BluetoothResultCallback() {

        @Override
        public void onScan(PowerManager_N2.BluetoothInfo bluetoothInfo) {
            Message message = new Message();
            message.what = BLUETOOTH_SCAN_SHOW;
            message.obj = bluetoothInfo;
            handler.sendMessage(message);
        }
    };

    private class TofCallback implements PowerManager_N2.TofResultCallback {

        private int mMode;
        public TofCallback(int mode) {
            mMode = mode;
        }

        @Override
        public void onCheckDistance(int status, int distance) {
            Message message = new Message();
            message.what = TOF_DETECT_SHOW;
            message.arg1 = status;
            message.arg2 = distance;
            handler.sendMessage(message);
        }

        @Override
        public void onCalibrationResult(int mode, boolean isSuccess) {
            if(mode == PowerManager_N2.TOP_CALIBRATION_60CM){
                Message message = new Message();
                message.what = TOF_CALIBRATION_COMPLETE;
                message.arg1 = mode;
                message.obj = isSuccess;
                handler.sendMessage(message);
            }else if (mode == PowerManager_N2.TOP_CALIBRATION_10CM){
                Message message = new Message();
                message.what = TOF_CALIBRATION_COMPLETE;
                message.arg1 = mode;
                message.obj = isSuccess;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onCheckError(String error) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMode == PowerManager_N2.TOP_CALIBRATION_60CM){
                        binding.tofCalibration60cmTv.setText(error);
                    }else if (mMode == PowerManager_N2.TOP_CALIBRATION_10CM){
                        binding.tofCalibration10cmTv.setText(error);
                    }else {
                        binding.tofDistanceTv.setText(error);
                    }

                }
            });
        }
    };

    private PowerManager_N2.NfcResultCallback nfcResultCallback = new PowerManager_N2.NfcResultCallback() {


        @Override
        public void onCheckError(String error) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PowerManageKN2AutoActivity.this, error + "", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCheckCard(String key, String value) {
            Message message = new Message();
            message.what = NFCREADER_DETECT_COMPLETE;
            Bundle bundle = new Bundle();
            bundle.putString(key, value);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    };

    @Override
    public void onScanSuccess(String barcode) {
        if(ishidOnScan){
            return;
        }

        ishidOnScan = true;
        binding.hidCheckBtn.setEnabled(true);
        binding.hidReadTv.setText(barcode);
        Log.d("tagg", "onScanSuccess: " + barcode);
    }

    @Override
    public void onBackPress() {

    }

    private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        private int type;
        public OnSeekBarChangeListener(int type) {
            this.type = type;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(type == PowerManager_N2.RED_LED){
                powerManager.ledControl(type, progress, ledResultCallback);
                binding.redLedBrightnessTv.setText(progress + "");
            }else if(type == PowerManager_N2.GREEN_LED){
                powerManager.ledControl(type, progress, ledResultCallback);
                binding.greenLedBrightnessTv.setText(progress + "");
            }else if(type == PowerManager_N2.BLUE_LED){
                powerManager.ledControl(type, progress, ledResultCallback);
                binding.blueLedBrightnessTv.setText(progress + "");
            }else if(type == PowerManager_N2.LOGO_LED){
                powerManager.ledControl(type, progress, ledResultCallback);
                binding.logoLedBrightnessTv.setText(progress + "");
            }else if(type == PowerManager_N2.AUDIO_VOLUME){
                powerManager.setAudioVolume(progress);
                binding.audioVolumeTv.setText(progress + "");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        powerManager = PowerManager_N2.getInstance(this);
        powerManager.monitorUSB();
        int ret = powerManager.open(115200, serialListener);
        if (ret != ResultCode.SUCCESS && ret != ResultCode.ERR_SYS_ALREADY_OPEN){
            showNErrorDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        powerManager.ledControl(PowerManager_N2.LED_OFF, 0, null);
        powerManager.unmonitorUSB();
        powerManager.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyEventResolver.onDestroy();
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

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.basicInfoCheckBtn:
                binding.basicInfoPassBtn.setVisibility(View.VISIBLE);
                binding.basicInfoFailBtn.setVisibility(View.VISIBLE);
                binding.basicInfoCheckBtn.setEnabled(false);
                binding.snTv.setText("");
                binding.iemiTv.setText("");
                binding.imsiTv.setText("");
                binding.ccidTv.setText("");
                binding.factoryVerTv.setText("");
                binding.tofCalibrateFlagTv.setText("");
                binding.tofCalibration10cmTv.setText("");
                binding.tofCalibration60cmTv.setText("");
                binding.bleVersionTv.setText("");
                binding.bleAtVersionTv.setText("");
                binding.bleSdkVersionTv.setText("");
                binding.bleCompileTimeTv.setText("");
                binding.bleMacAddressTv.setText("");
                binding.basicInfoAllResultTv.setText("");
                binding.efuseTv.setText("");
                // 基础信息检测
                PowerManager_N2.NInfo info = powerManager.infoCheck();
                PowerManager_N2.BluetoothVersion versions = powerManager.getBluetoothVersion();
                boolean isfuse = powerManager.getResult(PowerManager_N2.get_efuse);
                if(info != null) {
                    binding.snTv.setText(info.getSn() + "");
                    binding.iemiTv.setText(info.getImei() + "");
                    binding.imsiTv.setText(info.getImsi() + "");
                    binding.ccidTv.setText(info.getCcid() + "");
                    binding.factoryVerTv.setText(info.getFactoryver() + "");
                    binding.tofCalibrateFlagTv.setText(info.isTofFlag()?getString(R.string.powerManage_n_info_tof_calibrated_text):getString(R.string.powerManage_n_info_tof_not_calibrated_text));
                    binding.tofCalibration10cmTv.setText(info.getTofOffset() + "");
                    binding.tofCalibration60cmTv.setText(info.getTofXtalk() + "");
                }
                if(versions != null){
                    binding.bleAtVersionTv.setText(versions.getAtVersion() + "");
                    binding.bleSdkVersionTv.setText(versions.getSdkVersion() + "");
                    binding.bleCompileTimeTv.setText(versions.getCompileTime() + "");
                    binding.bleVersionTv.setText(versions.getBleVersion() + "");
                    binding.bleMacAddressTv.setText(versions.getMacAddress() + "");
                }
                binding.efuseTv.setText(isfuse?getString(R.string.powerManage_n_efused_text):getString(R.string.powerManage_n_enotfuse_read_text));
                break;
            case R.id.ledCheckBtn:
                binding.allLedResultTv.setText("");
                binding.allLedPassBtn.setVisibility(View.VISIBLE);
                binding.allLedFailBtn.setVisibility(View.VISIBLE);
                binding.ledCheckBtn.setEnabled(false);
                showLoadingDialog();
                ledDetectionFuture = executorService.submit(new ledDetectionRunable());
                break;
            case R.id.audioCheckBtn:
                binding.audioResultTv.setText("");
                binding.audioPassBtn.setVisibility(View.VISIBLE);
                binding.audioFailBtn.setVisibility(View.VISIBLE);
                isAudioPlay = !isAudioPlay;
                if(isAudioPlay){
                    binding.audioCheckBtn.setText(getString(R.string.powerManage_n_cancel_text));
                    executorService.submit(audioPressRunnable);
                }else {
                    binding.audioCheckBtn.setText(getString(R.string.powerManage_n_check_text));
                }
                break;
            case R.id.telephonyCheckBtn:
                binding.telephonyResultTv.setText("");
                binding.telephonyPassBtn.setVisibility(View.VISIBLE);
                binding.telephonyFailBtn.setVisibility(View.VISIBLE);
                binding.telephonyCheckBtn.setEnabled(false);
                showLoadingDialog();
                String pingIp = binding.telephonyPingEdt.getText().toString();
                String pingCount = binding.telephonyPingCountEdt.getText().toString();
                String pingPackageSize = binding.telephonyPingPackageEdt.getText().toString();
                String pingTimeout = binding.telephonyPingTimeoutEdt.getText().toString();
                if(!pingCount.isEmpty() && !pingPackageSize.isEmpty() && !pingTimeout.isEmpty() && !pingIp.isEmpty()){
                    powerManager.telephonyPingCheck(pingIp, Integer.valueOf(pingCount), Integer.valueOf(pingPackageSize), Integer.valueOf(pingTimeout), new PowerManager_N2.TelephonyResultCallback() {
                        @Override
                        public void onCheckError(String error) {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = TELEPHONY_DETECT_COMPLETE;
                                    message.arg1 = ResultCode.ERR_SYS_UNEXPECT;
                                    message.obj = error;
                                    handler.sendMessage(message);
                                }
                            });
                        }

                        @Override
                        public void onCheckComplete(PowerManager_N2.TelephonyPingResult result) {
                            Message message = new Message();
                            message.what = TELEPHONY_DETECT_COMPLETE;
                            message.arg1 = ResultCode.SUCCESS;
                            message.obj = result;
                            handler.sendMessage(message);
                        }
                    });
                }
                break;
            case R.id.mcuCheckBtn:
                binding.mcuCheckBtn.setEnabled(false);
                showLoadingDialog();
                powerManager.checkMcu(new PowerManager_N2.McuResultCallback() {

                    @Override
                    public void onCheckComplete(boolean isSuccess) {
                        Message message = new Message();
                        message.what = MCU_DETECT_COMPLETE;
                        message.obj = isSuccess;
                        handler.sendMessage(message);
                    }
                });
                break;
            case R.id.bluetoothCheckBtn:
                createBluetoothCheckDialog();
                powerManager.scanBluetooth(bluetoothResultCallback);
                break;
            case R.id.tofCalibration10cmBtn:
                binding.tofCalibration10cmResultTv.setText("");
                binding.tofCalibration10cmBtn.setEnabled(false);
                showLoadingDialog();
                powerManager.tofCalibration(PowerManager_N2.TOP_CALIBRATION_10CM, new TofCallback(PowerManager_N2.TOP_CALIBRATION_10CM));
                break;
            case R.id.tofCalibration60cmBtn:
                binding.tofCalibration60cmResultTv.setText("");
                binding.tofCalibration60cmBtn.setEnabled(false);
                showLoadingDialog();
                powerManager.tofCalibration(PowerManager_N2.TOP_CALIBRATION_60CM, new TofCallback(PowerManager_N2.TOP_CALIBRATION_60CM));
                break;
            case R.id.tofCheckBtn:
                binding.tofResultTv.setText("");
                binding.tofPassBtn.setVisibility(View.VISIBLE);
                binding.tofFailBtn.setVisibility(View.VISIBLE);
                isTofCheck = !isTofCheck;
                if(isTofCheck){
                    binding.tofCheckBtn.setText(getString(R.string.powerManage_n_cancel_text));
                    String threshold = binding.tofDistanceThresholdEdt.getText().toString();
                    if(!threshold.isEmpty()){
                        powerManager.tofStartGetDistance(Integer.valueOf(threshold), new TofCallback(-1));
                    }
                }else {
                    binding.tofCheckBtn.setText(getString(R.string.powerManage_n_check_text));
                    powerManager.tofStopGetDistance();
                }
                break;
            case R.id.hidCheckBtn:
                ishidOnScan = false;
                binding.hidReadTv.setText(getString(R.string.powerManage_n_detecting_text));
                binding.hidCheckBtn.setEnabled(false);
                binding.hidPassBtn.setVisibility(View.VISIBLE);
                binding.hidFailBtn.setVisibility(View.VISIBLE);
                powerManager.checkHid();

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(!ishidOnScan){
                            binding.hidCheckBtn.setEnabled(true);
                        }
                    }
                }, 5000);
                break;
            case R.id.nfcCardActivateBtn:
                binding.nfcCardResultTv.setText("");
                binding.nfcCardPassBtn.setVisibility(View.VISIBLE);
                binding.nfcCardFailBtn.setVisibility(View.VISIBLE);
                powerManager.setNfcCard(true, nfcResultCallback);
                binding.nfcCardActivateBtn.setEnabled(false);
                binding.nfcReaderActivateBtn.setEnabled(false);
                binding.nfcCardDeactivateBtn.setEnabled(true);
                break;
            case R.id.nfcCardDeactivateBtn:
                binding.nfcCardResultTv.setText("");
                binding.nfcCardPassBtn.setVisibility(View.VISIBLE);
                binding.nfcCardFailBtn.setVisibility(View.VISIBLE);
                powerManager.setNfcCard(false, nfcResultCallback);
                binding.nfcCardActivateBtn.setEnabled(true);
                binding.nfcReaderActivateBtn.setEnabled(true);
                binding.nfcCardDeactivateBtn.setEnabled(false);
                break;
            case R.id.nfcReaderActivateBtn:
                binding.nfcReaderResultTv.setText("");
                binding.nfcTypeAUIDTv.setText("");
                binding.nfcCpuAtsTv.setText("");
                binding.nfcCpuApduTv.setText("");
                binding.nfcTypeBAtqBTv.setText("");
                binding.nfcMifareBlock0Tv.setText("");
                binding.nfcMifareBlock1Tv.setText("");
                binding.nfcReaderPassBtn.setVisibility(View.VISIBLE);
                binding.nfcReaderFailBtn.setVisibility(View.VISIBLE);
                powerManager.setNfcReader(nfcResultCallback);
                break;
            case R.id.basicInfoPassBtn:
                binding.basicInfoCheckBtn.setEnabled(true);
                binding.basicInfoPassBtn.setVisibility(View.GONE);
                binding.basicInfoFailBtn.setVisibility(View.GONE);
                binding.basicInfoAllResultTv.setTextColor(Color.GREEN);
                binding.basicInfoAllResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_info_resulte, true);
                break;
            case R.id.basicInfoFailBtn:
                binding.basicInfoCheckBtn.setEnabled(true);
                binding.basicInfoPassBtn.setVisibility(View.GONE);
                binding.basicInfoFailBtn.setVisibility(View.GONE);
                binding.basicInfoAllResultTv.setTextColor(Color.RED);
                binding.basicInfoAllResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_info_resulte, false);
                break;
            case R.id.allLedPassBtn:
                binding.ledCheckBtn.setEnabled(true);
                binding.allLedPassBtn.setVisibility(View.GONE);
                binding.allLedFailBtn.setVisibility(View.GONE);
                binding.allLedResultTv.setTextColor(Color.GREEN);
                binding.allLedResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.ledControl(PowerManager_N2.LED_OFF, 0, null);
                powerManager.setCheckResult(PowerManager_N2.set_led_result, true);
                break;
            case R.id.allLedFailBtn:
                binding.ledCheckBtn.setEnabled(true);
                binding.allLedPassBtn.setVisibility(View.GONE);
                binding.allLedFailBtn.setVisibility(View.GONE);
                binding.allLedResultTv.setTextColor(Color.RED);
                binding.allLedResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.ledControl(PowerManager_N2.LED_OFF, 0, null);
                powerManager.setCheckResult(PowerManager_N2.set_led_result, false);
                break;
            case R.id.audioPassBtn:
                isAudioPlay = false;
                binding.audioPassBtn.setVisibility(View.GONE);
                binding.audioFailBtn.setVisibility(View.GONE);
                binding.audioResultTv.setTextColor(Color.GREEN);
                binding.audioResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_audio_result, true);
                break;
            case R.id.audioFailBtn:
                isAudioPlay = false;
                binding.audioPassBtn.setVisibility(View.GONE);
                binding.audioFailBtn.setVisibility(View.GONE);
                binding.audioResultTv.setTextColor(Color.RED);
                binding.audioResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_audio_result, false);
                break;
            case R.id.tofPassBtn:
                binding.tofPassBtn.setVisibility(View.GONE);
                binding.tofFailBtn.setVisibility(View.GONE);
                binding.tofResultTv.setTextColor(Color.GREEN);
                binding.tofResultTv.setText(getString(R.string.powerManage_n_pass_text));

                binding.tofCheckBtn.setText(getString(R.string.powerManage_n_check_text));
                powerManager.tofStopGetDistance();
                powerManager.setCheckResult(PowerManager_N2.set_tof_result, true);
                isTofCheck = false;
                break;
            case R.id.tofFailBtn:
                binding.tofPassBtn.setVisibility(View.GONE);
                binding.tofFailBtn.setVisibility(View.GONE);
                binding.tofResultTv.setTextColor(Color.RED);
                binding.tofResultTv.setText(getString(R.string.powerManage_n_fail_text));

                binding.tofCheckBtn.setText(getString(R.string.powerManage_n_check_text));
                powerManager.tofStopGetDistance();
                powerManager.setCheckResult(PowerManager_N2.set_tof_result, false);
                isTofCheck = false;
                break;
            case R.id.telephonyPassBtn:
                binding.telephonyCheckBtn.setEnabled(true);
                binding.telephonyPassBtn.setVisibility(View.GONE);
                binding.telephonyFailBtn.setVisibility(View.GONE);
                binding.telephonyResultTv.setTextColor(Color.GREEN);
                binding.telephonyResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_4G_result, true);
                break;
            case R.id.telephonyFailBtn:
                binding.telephonyCheckBtn.setEnabled(true);
                binding.telephonyPassBtn.setVisibility(View.GONE);
                binding.telephonyFailBtn.setVisibility(View.GONE);
                binding.telephonyResultTv.setTextColor(Color.RED);
                binding.telephonyResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_4G_result, false);
                break;
            case R.id.nfcCardPassBtn:
                powerManager.setNfcCard(false, null);
                binding.nfcCardActivateBtn.setEnabled(true);
                binding.nfcReaderActivateBtn.setEnabled(true);
                binding.nfcCardDeactivateBtn.setEnabled(false);
                binding.nfcCardPassBtn.setVisibility(View.GONE);
                binding.nfcCardFailBtn.setVisibility(View.GONE);
                binding.nfcCardResultTv.setTextColor(Color.GREEN);
                binding.nfcCardResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_nfccard_result, true);
                break;
            case R.id.nfcCardFailBtn:
                powerManager.setNfcCard(false, null);
                binding.nfcCardActivateBtn.setEnabled(true);
                binding.nfcReaderActivateBtn.setEnabled(true);
                binding.nfcCardDeactivateBtn.setEnabled(false);
                binding.nfcCardPassBtn.setVisibility(View.GONE);
                binding.nfcCardFailBtn.setVisibility(View.GONE);
                binding.nfcCardResultTv.setTextColor(Color.RED);
                binding.nfcCardResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_nfccard_result, false);
                break;
            case R.id.nfcReaderPassBtn:
                binding.nfcReaderPassBtn.setVisibility(View.GONE);
                binding.nfcReaderFailBtn.setVisibility(View.GONE);
                binding.nfcReaderResultTv.setTextColor(Color.GREEN);
                binding.nfcReaderResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_nfcreader_result, true);
                break;
            case R.id.nfcReaderFailBtn:
                binding.nfcReaderPassBtn.setVisibility(View.GONE);
                binding.nfcReaderFailBtn.setVisibility(View.GONE);
                binding.nfcReaderResultTv.setTextColor(Color.RED);
                binding.nfcReaderResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_nfcreader_result, false);
                break;
            case R.id.hidPassBtn:
                binding.hidPassBtn.setVisibility(View.GONE);
                binding.hidFailBtn.setVisibility(View.GONE);
                binding.hidResultTv.setTextColor(Color.GREEN);
                binding.hidResultTv.setText(getString(R.string.powerManage_n_pass_text));
                powerManager.setCheckResult(PowerManager_N2.set_hid_result, true);
                break;
            case R.id.hidFailBtn:
                binding.hidPassBtn.setVisibility(View.GONE);
                binding.hidFailBtn.setVisibility(View.GONE);
                binding.hidResultTv.setTextColor(Color.RED);
                binding.hidResultTv.setText(getString(R.string.powerManage_n_fail_text));
                powerManager.setCheckResult(PowerManager_N2.set_hid_result, false);
                break;
            case R.id.getAllResultBtn:
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean infoResult = powerManager.getResult(PowerManager_N2.get_info_result);
                        boolean ledResult = powerManager.getResult(PowerManager_N2.get_led_result);
                        boolean audioResult = powerManager.getResult(PowerManager_N2.get_audio_result);
                        boolean mcuResult = powerManager.getResult(PowerManager_N2.get_mcu_result);
                        boolean tofResult = powerManager.getResult(PowerManager_N2.get_tof_result);
                        boolean p4gResult = powerManager.getResult(PowerManager_N2.get_4G_result);
                        boolean nfccardResult = powerManager.getResult(PowerManager_N2.get_nfccard_result);
                        boolean nfcreaderResult = powerManager.getResult(PowerManager_N2.get_nfcreader_result);
                        boolean bluetoothResult = powerManager.getResult(PowerManager_N2.get_bluetooth_result);
                        boolean hidResult = powerManager.getResult(PowerManager_N2.get_hid_result);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.basicInfoAllResultTv.setTextColor(infoResult?Color.GREEN:Color.RED);
                                binding.basicInfoAllResultTv.setText(infoResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.allLedResultTv.setTextColor(ledResult?Color.GREEN:Color.RED);
                                binding.allLedResultTv.setText(ledResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.audioResultTv.setTextColor(audioResult?Color.GREEN:Color.RED);
                                binding.audioResultTv.setText(audioResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.mcuResultTv.setTextColor(mcuResult?Color.GREEN:Color.RED);
                                binding.mcuResultTv.setText(mcuResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.tofResultTv.setTextColor(tofResult?Color.GREEN:Color.RED);
                                binding.tofResultTv.setText(tofResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.telephonyResultTv.setTextColor(p4gResult?Color.GREEN:Color.RED);
                                binding.telephonyResultTv.setText(p4gResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.nfcCardResultTv.setTextColor(nfccardResult?Color.GREEN:Color.RED);
                                binding.nfcCardResultTv.setText(nfccardResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.nfcReaderResultTv.setTextColor(nfcreaderResult?Color.GREEN:Color.RED);
                                binding.nfcReaderResultTv.setText(nfcreaderResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.bluetoothResultTv.setTextColor(bluetoothResult?Color.GREEN:Color.RED);
                                binding.bluetoothResultTv.setText(bluetoothResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                                binding.hidResultTv.setTextColor(hidResult?Color.GREEN:Color.RED);
                                binding.hidResultTv.setText(hidResult?getString(R.string.powerManage_n_pass_text):getString(R.string.powerManage_n_fail_text));
                            }
                        });
                    }
                });
                break;
            case R.id.switchToWindowBtn:
                int ret = powerManager.switchTo(PowerManager_N2.switch_to_windows);
                Toast.makeText(PowerManageKN2AutoActivity.this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT);
                break;
            case R.id.switchToAndroidBtn:
                ret = powerManager.switchTo(PowerManager_N2.switch_to_android);
                Toast.makeText(PowerManageKN2AutoActivity.this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT);
                break;
            case R.id.switchToHidBtn:
                ret = powerManager.switchTo(PowerManager_N2.switch_to_hid);
                Toast.makeText(PowerManageKN2AutoActivity.this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT);
                break;
            case R.id.switchToLoaderBtn:
                ret = powerManager.switchTo(PowerManager_N2.switch_to_loader);
                Toast.makeText(PowerManageKN2AutoActivity.this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT);
                break;
            case R.id.snSetBtn:
                String hostsn = getSN();
                ret = powerManager.setSN(hostsn);
                if(ret != ResultCode.SUCCESS) {
                    binding.snTv.setText(getString(R.string.powerManage_n_sn_error_text));
                }
                break;
            case R.id.pressureBtn:
//                powerManager.close();
                Intent pressureIntent = new Intent(PowerManageKN2AutoActivity.this, PressureNActivity.class);
                startActivity(pressureIntent);
                break;
            case R.id.initSimBtn:
                showLoadingDialog();
                powerManager.findSim(new PowerManager_N2.NResultCallback() {

                    @Override
                    public void onFindSim(int ret) {
                        Message msg = new Message();
                        msg.what = INIT_SIM_COMPLETE;
                        msg.arg1 = ret;
                        handler.sendMessage(msg);
                    }
                });
                break;

        }
    }

    private void createBluetoothCheckDialog() {
        bluetoothList = new ArrayList<>();
        // 创建RecyclerView适配器
        bluetoothAdapter = new BluetoothAdapter(bluetoothList);
        // 创建对话框并设置布局
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.powerManage_n_bluetooth_text));
        builder.setMessage(getString(R.string.powerManage_n_detecting_text));
        builder.setCancelable(false);
        // 这里我们使用RecyclerView来展示数据
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bluetoothAdapter);

        // 设置通过和失败按钮
        builder.setView(recyclerView)
                .setPositiveButton(getString(R.string.powerManage_n_pass_text), (dialog, id) -> {
                    binding.bluetoothResultTv.setTextColor(Color.GREEN);
                    binding.bluetoothResultTv.setText(getString(R.string.powerManage_n_pass_text));
                    powerManager.setCheckResult(PowerManager_N2.set_bluetooth_result, true);
                })
                .setNegativeButton(getString(R.string.powerManage_n_fail_text), (dialog, id) -> {
                    binding.bluetoothResultTv.setTextColor(Color.RED);
                    binding.bluetoothResultTv.setText(getString(R.string.powerManage_n_fail_text));
                    powerManager.setCheckResult(PowerManager_N2.set_bluetooth_result, false);
                });

        AlertDialog dataDialog = builder.create();
        dataDialog.show();
    }

    private void showNErrorDialog() {
        bluetoothList = new ArrayList<>();
        // 创建RecyclerView适配器
        bluetoothAdapter = new BluetoothAdapter(bluetoothList);
        // 创建对话框并设置布局
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.powerManage_n_test));
        builder.setMessage(getString(R.string.powerManage_n_check_error_text));
        builder.setCancelable(false);

        // 设置通过和失败按钮
        builder.setPositiveButton("OK", (dialog, id) -> {
            finish();
        });


        AlertDialog dataDialog = builder.create();
        dataDialog.show();
    }

    private void showLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(getString(R.string.powerManage_n_start_test_text));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private Runnable audioPressRunnable = new Runnable() {

        @Override
        public void run() {
            int volume = binding.audioVolumeSeekBar.getProgress();
            while (isAudioPlay) {
                powerManager.audioPlay(volume);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private class ledDetectionRunable implements Runnable{

        @Override
        public void run() {
            powerManager.ledControl(PowerManager_N2.LED_OFF, 0, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*powerManager.ledControl(PowerManager_N2.RED_LED, 255, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.RED_LED, 0, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.GREEN_LED, 255, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.GREEN_LED, 0, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.BLUE_LED, 255, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.BLUE_LED, 0, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            powerManager.ledControl(PowerManager_N2.LOGO_LED, 255, ledResultCallback);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.HORSE_RACE, 255, ledResultCallback);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerManager.ledControl(PowerManager_N2.LED_OFF, 0, ledResultCallback);
            handler.sendEmptyMessage(LED_DETECT_COMPLETE);
        }
    }

    private PowerManager_N2.SerialListener serialListener = new PowerManager_N2.SerialListener() {

        @Override
        public void onConnected(String message) {
            if(checkNProgressDialog!=null && checkNProgressDialog.isShowing()){
                checkNProgressDialog.dismiss();
            }
            Toast.makeText(PowerManageKN2AutoActivity.this, "USB设备已插入", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(String message) {
            showCheckNLoadingDialog();
            Toast.makeText(PowerManageKN2AutoActivity.this, "USB设备已拔出", Toast.LENGTH_SHORT).show();
        }
    };

    private void showCheckNLoadingDialog() {
        checkNProgressDialog = new ProgressDialog(PowerManageKN2AutoActivity.this);
        checkNProgressDialog.setMessage("正在检测N模块，请稍等...");
        checkNProgressDialog.setCancelable(true); // 设置点击弹窗外部区域或按“返回”按钮时关闭弹窗
        checkNProgressDialog.setCanceledOnTouchOutside(false); // 禁止触摸外部区域关闭弹窗
        checkNProgressDialog.show();
    }

    private String getSN() {
        String sn = Build.SERIAL;

        if (sn.equals("unknown")) {
            sn = ShellUtils.execCommand("getprop ro.serialno", false).successMsg;
        }
        return sn;
    }

}
