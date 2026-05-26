package com.common.demo.pos;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.StringRes;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.pos.CommonUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivitySensorBinding;
import java.util.Locale;

public class SensorActivity extends BaseActivity {

    private ActivitySensorBinding binding;
    private boolean mRedTips = false;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySensorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final CommonUtil mCommonUtil = new CommonUtil(this);

        mCommonUtil.switchInput(CommonConstants.InputType.WIEGAND_INPUT, CommonConstants.InputType.DOOR_INPUT, CommonConstants.InputStatus.SWITCH_OPEN_INPUT2);

        {
            @StringRes final int item = R.string.sensor_physical_key;
            binding.getPhysicalKeyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.PHYSICALKEY_SENSOR);
                    showTips(ret, item);
                }
            });
        }

        {
            @StringRes final int item = R.string.sensor_magnetic_sensitive_circuit;
            binding.getMagneticSensitiveCircuitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.MAGNETICSENSITIVECIRCUIT_SENSOR);
                    showTips(ret, item);
                }
            });
        }

        {
            @StringRes final int item = R.string.sensor_rgbcamer_tempsensor;
            binding.getRgbcamerTempsensorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.RGBCAMERA_TEMPERATURESENSOR);
                    showTips(ret, item);
                }
            });
        }

        {
            @StringRes final int item = R.string.sensor_rightcamer_tempsensor;
            binding.getRightcamerTempsensorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.RIGHTCAMERA_TEMPERATURESENSOR);
                    showTips(ret, item);
                }
            });
        }

        {
            @StringRes final int item = R.string.sensor_leftcamer_tempsensor;
            binding.getLeftcamerTempsensorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.LEFTCAMERA_TEMPERATURESENSOR);
                    showTips(ret, item);
                }
            });
        }

        {
            @StringRes final int item = R.string.sensor_infrared;
            binding.getInfraredSensorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = mCommonUtil.getPriximitySensorStatus(CommonConstants.SensorType.INFRARED_SENSOR);
                    showTips(ret, item);
                }
            });
        }
    }

    private void showTips(int code, @StringRes int itemId) {

        if (mRedTips) {
            binding.tvPowerControlLog.setTextColor(Color.RED);
        } else {
            binding.tvPowerControlLog.setTextColor(Color.BLUE);
        }
        mRedTips = !mRedTips;

        String result;
        switch (code) {
            case ResultCode.ERR_SYS_NOT_SUPPORT:
                result = getString(R.string.not_support_test);
                break;
            case ResultCode.ERR_INVALID_PARAM:
                result = getString(R.string.invalid_test);
                break;
            default:
                if(itemId==R.string.sensor_rgbcamer_tempsensor ||
                        itemId==R.string.sensor_rightcamer_tempsensor ||
                        itemId==R.string.sensor_leftcamer_tempsensor){
                    result = (double)((code/100.0)) + "";
                }else{
                    result = code + "";
                }
                break;
        }

        String msg = String.format(Locale.getDefault(),
                "[%d] %s %s: %s", ++mIndex, getString(itemId), getString(R.string.operation_result), result);
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        binding.tvPowerControlLog.setText(msg);
    }

}