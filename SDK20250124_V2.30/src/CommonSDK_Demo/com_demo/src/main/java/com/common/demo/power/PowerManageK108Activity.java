package com.common.demo.power;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.powercontrol.PowerManager_K108;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityPowerManagerK108Binding;

public class PowerManageK108Activity extends BaseActivity implements View.OnClickListener {

    private PowerManager_K108 powerManager;
    private ActivityPowerManagerK108Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityPowerManagerK108Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        powerManager = new PowerManager_K108(this);
        powerManager.open();
        String version = powerManager.getVersion();
        if (version != null) {
            binding.versionTv.setText(version + "");
        }

        binding.stateGetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PowerManager_K108.PeripheralsState state = powerManager.getPeripheralsState();
                if(state != null){
                    binding.hdmiStateTv.setText(state.getHdmiState()==1?"Access":"No Access");
                    binding.smallBatteryStateTv.setText(state.getSmallBatteryState()==1?"Access":"No Access");
                    binding.largeBatteryStateTv.setText(state.getBigBatteryState()==1?"Access":"No Access");
                }
            }
        });

        binding.lightBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(version != null) {
                    powerManager.setFillLightBrightness(progress > 0 ? true : false, progress);
                    binding.lightBrightnessEdt.setText(progress + "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.lcdBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(version != null) {
                    powerManager.setLcdBrightness(progress);
                    binding.lcdBrightnessEdt.setText(progress + "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        powerManager.close();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lightSetBtn:
                int result = ResultCode.ERR_SYS_UNEXPECT;
                String brightness = binding.lightBrightnessEdt.getText().toString();
                if (brightness.isEmpty()) {
                    return;
                }
                int level = Integer.parseInt(brightness);
                if (level < 0 || level > 255)
                    return;

                if(level == 0){
                    result = powerManager.setFillLightBrightness(false, level);
                }else{
                    result = powerManager.setFillLightBrightness(true, level);
                }
                if(result == ResultCode.SUCCESS){
                    Toast.makeText(this, R.string.powerManage_k108_success, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, R.string.powerManage_k108_fail, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lcdSetBtn:
                brightness = binding.lcdBrightnessEdt.getText().toString();
                if (brightness.isEmpty()) {
                    return;
                }
                level = Integer.parseInt(brightness);
                if (level < 0 || level > 100)
                    return;

                result = powerManager.setLcdBrightness(level);
                if(result == ResultCode.SUCCESS){
                    Toast.makeText(this, R.string.powerManage_k108_success, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, R.string.powerManage_k108_fail, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.powerGetBtn:
                int[] batteryPower = powerManager.getBatteryPower();
                if(batteryPower != null && batteryPower.length == 2){
                    Toast.makeText(this, R.string.powerManage_k108_success, Toast.LENGTH_SHORT).show();
                    binding.largeBatteryPowerEdt.setText(batteryPower[0] + "");
                    binding.smallBatteryPowerEdt.setText(batteryPower[1] + "");
                }else {
                    Toast.makeText(this, R.string.powerManage_k108_fail, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
