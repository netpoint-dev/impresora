package com.common.demo.serial;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.pos.RSTTLReader;
import com.common.callback.IRSReaderListener;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityTtlTestBinding;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TTLTestActivity extends BaseActivity implements IRSReaderListener {

    private static final String TAG = "TTLTestActivity";
    private static final Integer[] TTL_BAUD_ARRAY = {9600, 19200, 38400, 57600, 115200, 230400, 460800, 500000, 576000, 921600, 1000000};

    private ActivityTtlTestBinding binding;
    private ArrayAdapter<String> mCharsetAdapter;
    private RSTTLReader mReader;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTtlTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mReader = new RSTTLReader(getApplicationContext());

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 状态栏与导航栏设置为透明色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        // 全屏操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            this.getWindow().setAttributes(lp);
        }
        int fullScreenUiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().getDecorView().setSystemUiVisibility(fullScreenUiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReader != null) {
            mReader.rsDestroy();
        }
    }

    private void initView() {
        binding.edtTtlData.setText("123abc321");
        mCharsetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mCharsetAdapter.add("GB2312");
        mCharsetAdapter.add("GBK");
        mCharsetAdapter.add(StandardCharsets.US_ASCII.name());
        mCharsetAdapter.add(StandardCharsets.UTF_8.name());
        mCharsetAdapter.add(StandardCharsets.UTF_16.name());
        mCharsetAdapter.add(StandardCharsets.ISO_8859_1.name());
        binding.spnTtlCharset.setAdapter(mCharsetAdapter);
        binding.spnTtlCharset.setSelection(3);

        final ArrayAdapter<Integer> mBaudAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, TTL_BAUD_ARRAY);
        mBaudAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnTtlBaud.setAdapter(mBaudAdapter);

        binding.btnTtlOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.btnTtlOpen.setEnabled(false);
                binding.spnTtlBaud.setEnabled(false);
                binding.btnTtlClose.setEnabled(false);
                binding.btnTtlSendData.setEnabled(false);

                int position = binding.spnTtlBaud.getSelectedItemPosition();
                int ret = mReader.rsOpen(CommonConstants.RSTTLType.TTL_1, TTL_BAUD_ARRAY[position]);
                if (ret == ResultCode.SUCCESS) {
                    Toast.makeText(TTLTestActivity.this, R.string.success_test, Toast.LENGTH_SHORT).show();
                    mReader.setRSReaderListener(TTLTestActivity.this);
                } else if (ret == ResultCode.ERR_SYS_NOT_SUPPORT) {
                    Toast.makeText(TTLTestActivity.this, R.string.not_support_test, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TTLTestActivity.this, R.string.fail_test, Toast.LENGTH_SHORT).show();
                }

                binding.btnTtlOpen.setEnabled(ret != ResultCode.SUCCESS);
                binding.spnTtlBaud.setEnabled(ret != ResultCode.SUCCESS);
                binding.btnTtlClose.setEnabled(ret == ResultCode.SUCCESS);
                binding.btnTtlSendData.setEnabled(ret == ResultCode.SUCCESS);

            }
        });

        binding.btnTtlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndex = 0;
                binding.tvTtlShowData.setText("");

                binding.btnTtlOpen.setEnabled(false);
                binding.spnTtlBaud.setEnabled(false);
                binding.btnTtlClose.setEnabled(false);
                binding.btnTtlSendData.setEnabled(false);

                int ret = mReader.rsDestroy();
                if (ret == ResultCode.SUCCESS) {
                    Toast.makeText(TTLTestActivity.this, R.string.success_test, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TTLTestActivity.this, R.string.fail_test, Toast.LENGTH_SHORT).show();
                }

                binding.btnTtlOpen.setEnabled(ret == ResultCode.SUCCESS);
                binding.spnTtlBaud.setEnabled(ret == ResultCode.SUCCESS);
                binding.btnTtlClose.setEnabled(ret != ResultCode.SUCCESS);
                binding.btnTtlSendData.setEnabled(ret != ResultCode.SUCCESS);
            }
        });

        binding.btnTtlSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable editable = binding.edtTtlData.getText();
                if (editable == null) {
                    Toast.makeText(TTLTestActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                String msg = editable.toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(TTLTestActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = binding.spnTtlCharset.getSelectedItemPosition();
                Charset charset = Charset.forName(mCharsetAdapter.getItem(position));
                int ret = mReader.rsSend(msg.getBytes(charset));
                if (ret == ResultCode.SUCCESS) {
                    Toast.makeText(TTLTestActivity.this, R.string.success_test, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TTLTestActivity.this, R.string.fail_test, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRecvData(byte[] data) {

        int position = binding.spnTtlCharset.getSelectedItemPosition();
        Charset charset = Charset.forName(mCharsetAdapter.getItem(position));
        String msg = "[" + ++mIndex + "] " + new String(data, charset);
        binding.tvTtlShowData.setText(msg);
        if (mIndex % 2 == 0) {
            binding.tvTtlShowData.setTextColor(Color.RED);
        } else {
            binding.tvTtlShowData.setTextColor(Color.BLUE);
        }
    }
}