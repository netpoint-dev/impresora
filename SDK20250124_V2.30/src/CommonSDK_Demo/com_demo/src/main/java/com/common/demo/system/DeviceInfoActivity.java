package com.common.demo.system;

import android.app.Activity;
import android.os.Bundle;

import com.common.apiutil.system.DeviceInfo;
import com.common.demo.databinding.ActivityDeviceInfoBinding;

public class DeviceInfoActivity extends Activity {

    private ActivityDeviceInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textSerialNumber.setText(DeviceInfo.getSerialNumber() + "");
        binding.textPartNumber.setText(DeviceInfo.getPartNumber() + "");
        binding.textHardwareVersion.setText(DeviceInfo.getHardwareVersion() + "");
        binding.textCpuSerialNumber.setText(DeviceInfo.getCPUSerialNumber() + "");
    }
}
