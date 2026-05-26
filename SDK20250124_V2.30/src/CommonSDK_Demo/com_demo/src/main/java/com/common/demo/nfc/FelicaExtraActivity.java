package com.common.demo.nfc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.Toast;

import com.common.apiutil.CommonException;
import com.common.apiutil.ErrorCode;
import com.common.apiutil.nfc.Nfc;

import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.FelicaTestMainBinding;

import java.util.Arrays;

public class FelicaExtraActivity extends BaseActivity {
    private final String TAG = "FelicaExtraActivity";

    Nfc nfc = new Nfc(this);
    OnClickListener listener;
    private FelicaTestMainBinding binding;
    int successCount = 0;
    ProgressDialog dialog;

    @SuppressLint("NonConstantResourceId")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = FelicaTestMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listener = v -> {
            switch (v.getId()) {
                case R.id.nfc_open_btn:
                    boolean openFlag = true;
                    try {
                        nfc.open();
                    } catch (CommonException e) {
                        openFlag = false;
                        Log.d(TAG, "open() CommonException: " + e);
                    }
                    if (openFlag) {
                        binding.nfcOpenBtn.setEnabled(false);
                        binding.nfcCloseBtn.setEnabled(true);
                        binding.nfcFelicaSetModeBtn.setEnabled(true);
                        successCount = 0;
                        Log.d(TAG, "open() success.");
                    } else {
                        runOnUiThread(() -> Toast.makeText(FelicaExtraActivity.this, R.string.nfc_not_support_tips, Toast.LENGTH_LONG).show());
                        Log.e(TAG, "open() failed.");
                    }
                    break;

                case R.id.nfc_FelicaSetMode_btn:
                    try {
                        int iRet = nfc.FelicaSetMode();
                        String msg = "FelicaSetMode iRet=" + iRet;
                        Log.d(TAG, msg);
                        binding.nfcFelicaExchangeBtn.setEnabled(true);
                        showData(msg);
                    } catch (CommonException e) {
                        Log.e(TAG, "FelicaSetMode() CommonException: " + e);
//                        throw new RuntimeException(e);
                    }
                    break;

                case R.id.nfc_FelicaExchange_btn:
                    try {
                        String str = "06 00 FF FF 00 03";
                        byte[] tmp = hexStringToBytes(str);
                        Log.d(TAG, "FelicaExchange tmp=" + Arrays.toString(tmp));
                        byte[] data = nfc.FelicaExchange(tmp, tmp.length);
                        String msg = "FelicaExchange data=" + Arrays.toString(data);
                        Log.d(TAG, msg);
                        showData("[" + successCount++ + "]\t" + Arrays.toString(data));
                    } catch (CommonException e) {
//                        throw new RuntimeException(e);
                        Log.e(TAG, "FelicaExchange() CommonException: " + e);
                    }
                    break;

                case R.id.nfc_FelicaExchange_test_btn:
                    new Thread(() -> {
                        try {
                            nfc.close();
                            nfc.open();
                            runOnUiThread(() -> {
                                binding.nfcOpenBtn.setEnabled(true);
                                binding.nfcFelicaSetModeBtn.setEnabled(false);
                                binding.nfcFelicaExchangeBtn.setEnabled(false);
                                binding.nfcCloseBtn.setEnabled(false);
                                binding.tvShowData.setText("");
                                dialog = new ProgressDialog(FelicaExtraActivity.this);
                                dialog.setMessage("please wait ...");
                                dialog.setCancelable(false);
                                dialog.show();
                                Toast.makeText(FelicaExtraActivity.this, "请把felica卡置于感应区内不要移动直至压测结束。", Toast.LENGTH_SHORT).show();
                            });

                            int times = 10;
                            String str = "06 00 FF FF 00 03";
                            byte[] tmp = hexStringToBytes(str);
                            StringBuilder result = new StringBuilder();
                            for (int i = 0; i < times; i++) {
                                Thread.sleep(233);
                                int iRet = nfc.FelicaSetMode();
                                if (iRet != ErrorCode.OK) {
                                    int finalI = i;
                                    runOnUiThread(() -> Toast.makeText(FelicaExtraActivity.this, "[" + finalI + "]\tfailed at nfc.FelicaSetMode() iRet: " + iRet, Toast.LENGTH_SHORT).show());
                                    break;
                                }
                                try {
                                    byte[] data = nfc.FelicaExchange(tmp, tmp.length);
                                    result.append("[").append(i).append("]\t").append(Arrays.toString(data)).append("\n");
                                } catch (CommonException e) {
                                    Log.d(TAG, "nfc_FelicaExchange_test_btn CommonException: " + e);
                                    break;
                                }
                            }
                            runOnUiThread(() -> {
                                showData(result.toString());
                                dialog.dismiss();
                                try {
                                    nfc.close();
                                } catch (CommonException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    break;

                case R.id.nfc_close_btn:
                    try {
                        nfc.close();
                    } catch (CommonException e) {
                        e.printStackTrace();
                    }
                    binding.nfcOpenBtn.setEnabled(true);
                    binding.nfcFelicaSetModeBtn.setEnabled(false);
                    binding.nfcFelicaExchangeBtn.setEnabled(false);
                    binding.nfcCloseBtn.setEnabled(false);
                    break;

                default:
                    break;
            }
        };

        initView();
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        try {
            nfc.close();
        } catch (CommonException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void initView() {
        binding.nfcOpenBtn.setOnClickListener(listener);
        binding.nfcCloseBtn.setOnClickListener(listener);
        binding.nfcFelicaSetModeBtn.setOnClickListener(listener);
        binding.nfcFelicaExchangeBtn.setOnClickListener(listener);
        binding.nfcFelicaExchangeTestBtn.setOnClickListener(listener);

        LinearLayout layout = findViewById(R.id.felica_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if (!((Button) v).getText().equals(getString(R.string.nfc_open_text))) {
                    v.setEnabled(false);
                }
            } else if (v instanceof TableRow) {
                for (int j = 0; j < ((TableRow) v).getChildCount(); j++) {
                    View b = ((TableRow) v).getChildAt(j);
                    if (b instanceof Button) {
                        b.setEnabled(false);
                    }
                }
            }
        }
        binding.nfcOpenBtn.setEnabled(true);
        binding.nfcFelicaExchangeTestBtn.setEnabled(true);
    }

    public static byte[] toByteArray(String hexString) {
        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {
                    byteArray[len] = (byte) (value << 4);
                } else {
                    byteArray[len] |= value;
                    len++;
                }
                first = !first;
            }
        }
        return byteArray;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String[] hex = hexString.split(" ");

        if (hex.length > 0) {
            String hexStr = hex[0];
            for (int i = 1; i < hex.length; i++) {
                hexStr = hexStr.concat(hex[i]);
            }
            hexStr = hexStr.toUpperCase();
            int length = hexStr.length() / 2;
            char[] hexChars = hexStr.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        }
        return null;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public void showData(String str) {
//        binding.tvShowData.setText(str);
        binding.tvShowData.append(str + "\n");

        if (successCount % 2 == 0) {
            binding.tvShowData.setTextColor(Color.RED);
        } else {
            binding.tvShowData.setTextColor(Color.BLUE);
        }
        new Thread(() -> {
            try {
                Thread.sleep(100);
                binding.scrollAndroidNfcSaveData.fullScroll(ScrollView.FOCUS_DOWN);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
