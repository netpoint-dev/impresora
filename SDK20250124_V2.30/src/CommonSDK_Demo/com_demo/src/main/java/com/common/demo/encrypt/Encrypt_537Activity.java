package com.common.demo.encrypt;

/*
 * add by liyk on 2021/03/22
 *
 * tps537 加密芯片测试
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.common.apiutil.encrypt.State;
import com.common.apiutil.util.StringUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import static android.view.View.GONE;

public class Encrypt_537Activity extends BaseActivity {

    final static private String TAG = "encrypt_537";

    State getState = new State(this);

    private byte[] write_key = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
            (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};

    private byte[] plain_data = {(byte) 0x09, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x00, (byte) 0x01, (byte) 0x0A, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
            (byte) 0x09, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x00, (byte) 0x01, (byte) 0x0A, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08};

    TextView tv_message;
    private static final int UPDATE_CHIP = 0;
    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_FAIL = 2;
    private static final int SAVE_KEY_SUCCESS = 3;
    private static final int SAVE_KEY_FAIL = 4;
    private static final int ENCRYPT_SUCCESS = 5;
    private static final int ENCRYPT_FAIL = 6;
    private static final int CHIP_MODE = 7;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_CHIP) {
                tv_message.setText("正在升级单片机，请勿操作！");
            } else if (msg.what == UPDATE_SUCCESS) {
                tv_message.setText("升级成功！");
            } else if (msg.what == UPDATE_FAIL) {
                tv_message.setText("升级失败！");
            } else if (msg.what == SAVE_KEY_SUCCESS) {
                tv_message.setText("保存密钥成功！");
            } else if (msg.what == SAVE_KEY_FAIL) {
                tv_message.setText("保存密钥失败！");
            } else if (msg.what == ENCRYPT_SUCCESS) {
                String type = msg.getData().getString("type");
                String plain = msg.getData().getString("plain");
                String cipher = msg.getData().getString("cipher");
                String decrypt = msg.getData().getString("decrypt");
                String txt = type + "\n\n"
                             + "原文：" + plain + "\n\n"
                             + "加密成功!\n\n"
                             + "密文：" + cipher + "\n\n"
                             + "解密成功!\n\n"
                             + "解密后的明文：" + decrypt;
                tv_message.setText(txt);
            } else if (msg.what == ENCRYPT_FAIL) {
                tv_message.setText("加密失败");
            } else if (msg.what == CHIP_MODE) {
                String mode = msg.getData().getString("appMode");
                tv_message.setText("芯片模式：" + mode);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_encrypt_537);
        initView();
    }

    private void initView() {

        tv_message = (TextView) findViewById(R.id.tv_message);

        final Button btn_serial = (Button) findViewById(R.id.btn_serial);
        btn_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_serial.getText().equals("打开串口")) {
                    getState.open();
                    btn_serial.setText("关闭串口");
                } else {
                    getState.close();
                    btn_serial.setText("打开串口");
                }
            }
        });

        Button btn_setkey = (Button) findViewById(R.id.btn_setkey);
        btn_setkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int write_state = getState.setAcessKey(write_key);

                Message msg = new Message();
                if (write_state == 0) {
                    msg.what = SAVE_KEY_SUCCESS;
                    mHandler.sendMessage(msg);
                } else if (write_state == 1) {
                    msg.what = SAVE_KEY_FAIL;
                    mHandler.sendMessage(msg);
                }
            }
        });

        /**
         * 将 i360 芯片升级为 App 模式
         */
        Button btn_upgrade = (Button) findViewById(R.id.btn_upgrade);
        btn_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg1 = new Message();
                msg1.what = UPDATE_CHIP;
                mHandler.sendMessage(msg1);

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        boolean ret = getState.setAppMode();
                        Log.d(TAG, "upgrade result : " + ret);
                        Message msg2 = new Message();
                        if (ret) {
                            msg2.what = UPDATE_SUCCESS;
                            mHandler.sendMessage(msg2);
                        } else {
                            msg2.what = UPDATE_FAIL;
                            mHandler.sendMessage(msg2);
                        }
                    }
                }.start();
            }
        });

        Button btn_encrypt = (Button) findViewById(R.id.btn_encrypt);
        btn_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] cipher = getState.SM4ECBEncrypt(plain_data);
                Log.d(TAG, "the cipher is : " + StringUtil.toHexString(cipher));

                byte[] decryptPlain = getState.SM4ECBDecrypt(cipher);

                Message msg = new Message();
                Bundle bundle = new Bundle();
                if (cipher != null && decryptPlain != null) {
                    msg.what = ENCRYPT_SUCCESS;

                    String EncryptTypeStr = "SM4 加解密";
                    bundle.putString("type", EncryptTypeStr);
                    String str_plain = StringUtil.toHexString(plain_data);  // 明文
                    bundle.putString("plain", str_plain);
                    String str_cipher = StringUtil.toHexString(cipher);  // 密文
                    bundle.putString("cipher", str_cipher);
                    String str_decrypt = StringUtil.toHexString(decryptPlain);  // 解密后的明文
                    bundle.putString("decrypt", str_decrypt);
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);
                } else {
                    msg.what = ENCRYPT_FAIL;

                    mHandler.sendMessage(msg);
                }
            }
        });

        Button btn_get_mode = (Button) findViewById(R.id.btn_get_mode);
        btn_get_mode.setEnabled(false);
        btn_get_mode.setVisibility(GONE);
        /*
        btn_get_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] mode = new byte[4];
                // mode = getState.getMode();
                // Log.d(TAG, "mode is : " + SerialUtil.toHexString(mode));
            }
        });
        */

        /**
         * 获取 i360 芯片状态
         */
        Button btn_getState = (Button) findViewById(R.id.btn_getState);
        // btn_getState.setEnabled(false);
        // btn_getState.setVisibility(GONE);
        btn_getState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chipMode = getState.getChipMode();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                if (chipMode == 0) {
                    bundle.putString("appMode", "App 模式!");
                    msg.setData(bundle);
                    msg.what = CHIP_MODE;
                    mHandler.sendMessage(msg);
                } else if (chipMode == 1) {
                    bundle.putString("appMode", "非 App 模式!");
                    msg.setData(bundle);
                    msg.what = CHIP_MODE;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }
}