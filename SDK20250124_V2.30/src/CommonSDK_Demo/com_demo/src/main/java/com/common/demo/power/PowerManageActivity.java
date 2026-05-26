package com.common.demo.power;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.apiutil.ResultCode;
import com.common.apiutil.powercontrol.PowerManager;
import com.common.callback.IUpdateCallBack;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityPowerManagerBinding;

public class PowerManageActivity extends BaseActivity implements View.OnClickListener {

    private PowerManager powerManager;
    private ActivityPowerManagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityPowerManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        powerManager = new PowerManager(this);
        powerManager.open();

        String version = powerManager.getVersion();
        if(version.isEmpty()){
            binding.versionTv.setText(getString(R.string.unexpect_text));
        }else{
            binding.versionTv.setText(version);
        }

        int moneyBoxInLevel = powerManager.getPinLevel(PowerManager.Peripheral.MONEYBOX_IN);
        if(moneyBoxInLevel == 0 || moneyBoxInLevel == 1) {
            binding.moneyBoxInPinLevelEdt.setText(moneyBoxInLevel + "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        powerManager.close();
    }

    private void initView() {
        binding.binPathEdt.setText("/data/data/" + getPackageName() + "/cache/update.bin");
    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getPinLevelBtn:
                int moneyBoxInLevel = powerManager.getPinLevel(PowerManager.Peripheral.MONEYBOX_IN);
                if(moneyBoxInLevel == 0 || moneyBoxInLevel == 1){
                    Toast.makeText(this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
                    binding.moneyBoxInPinLevelEdt.setText(moneyBoxInLevel + "");
                }else if(moneyBoxInLevel == ResultCode.ERR_SYS_NOT_SUPPORT){
                    Toast.makeText(this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.setPinLevelBtn:
                int moneyBoxOutPin = binding.moneyBoxOutPinLevelChebox.isChecked()?1:0;
                int mpos1Pin = binding.mpos1PinLevelChebox.isChecked()?1:0;
                int mpos2Pin = binding.mpos2PinLevelChebox.isChecked()?1:0;
                int lanPin = binding.lanPinLevelChebox.isChecked()?1:0;
                int usb1Pin = binding.usb1PinLevelChebox.isChecked()?1:0;
                int usb2Pin = binding.usb2PinLevelChebox.isChecked()?1:0;
                int usb3Pin = binding.usb3PinLevelChebox.isChecked()?1:0;
                int usb4Pin = binding.usb4PinLevelChebox.isChecked()?1:0;
                int usb5Pin = binding.usb5PinLevelChebox.isChecked()?1:0;
                int usb6Pin = binding.usb6PinLevelChebox.isChecked()?1:0;

                PowerManager.Peripheral[] peripherals = {PowerManager.Peripheral.USB1, PowerManager.Peripheral.USB2, PowerManager.Peripheral.USB3,
                        PowerManager.Peripheral.USB4, PowerManager.Peripheral.USB5, PowerManager.Peripheral.USB6, PowerManager.Peripheral.MPOS1, PowerManager.Peripheral.MPOS2,
                        PowerManager.Peripheral.MONEYBOX_OUT, PowerManager.Peripheral.LAN};

                int[] levels = {usb1Pin, usb2Pin, usb3Pin, usb4Pin, usb5Pin, usb6Pin, mpos1Pin, mpos2Pin, moneyBoxOutPin, lanPin};
                int ret = powerManager.setPinLevel(peripherals, levels);
                Toast.makeText(this, ret==ResultCode.SUCCESS?getString(R.string.success_test):getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
                break;
            case R.id.serial1SendBtn:
                binding.serialRecvTv.setText("");
                String content = binding.serialSendEdt.getText().toString();
                if(content.isEmpty()){
                    return;
                }
                byte[] serialCommand = powerManager.serialCommand(1, content.getBytes());
                if(serialCommand != null){
                    binding.serialRecvTv.setText(new String(serialCommand));
                }
                break;
            case R.id.serial2SendBtn:
                binding.serialRecvTv.setText("");
                content = binding.serialSendEdt.getText().toString();
                if(content.isEmpty()){
                    return;
                }
                serialCommand = powerManager.serialCommand(2, content.getBytes());
                if(serialCommand != null){
                    binding.serialRecvTv.setText(new String(serialCommand));
                }
                break;
            case R.id.updateBtn:
                if(binding.binPathEdt.getText().toString().isEmpty()){
                    return;
                }
                binding.updateStatusTv.setText(getString(R.string.deliverylocker_updating));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        powerManager.moduleUpdate(binding.binPathEdt.getText().toString(), new IUpdateCallBack() {
                            @Override
                            public void updateComplete(final boolean isUpdateSuccess) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.updateStatusTv.setText(isUpdateSuccess?getString(R.string.deliverylocker_update_succeed):getString(R.string.deliverylocker_update_fail));
                                        if(isUpdateSuccess){
                                            String version = powerManager.getVersion();
                                            if(version.isEmpty()){
                                                binding.versionTv.setText(getString(R.string.unexpect_text));
                                            }else{
                                                binding.versionTv.setText(version);
                                            }
                                        }
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
