package com.common.demo.power;

import androidx.annotation.StringRes;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.apiutil.ResultCode;
import com.common.apiutil.powercontrol.PowerControl;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityPowerControlBinding;


import java.util.Locale;

public class PowerControlActivity extends BaseActivity {

    private static final String TAG = "PowerControlActivity";
    private ActivityPowerControlBinding binding;
    private boolean mRedTips = false;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityPowerControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final PowerControl powerControl = new PowerControl(this);

        {
            // printer
            @StringRes final int item = R.string.power_control_printer;
            binding.btnPrinterPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.printerPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnPrinterPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.printerPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // decode
            @StringRes final int item = R.string.power_control_decode;
            binding.btnDecodePowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.decodePower(1);
                    showTips(ret, item);
                }
            });

            binding.btnDecodePowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.decodePower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // ethernet
            @StringRes final int item = R.string.power_control_ethernet;
            binding.btnEthernetPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.ethernetPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnEthernetPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.ethernetPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // fingerprint
            @StringRes final int item = R.string.power_control_fingerprint;
            binding.btnFpPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.fingerPrintPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnFpPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.fingerPrintPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // iris
            @StringRes final int item = R.string.power_control_iris;
            binding.btnIrisPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.irisPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnIrisPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.irisPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // passport
            @StringRes final int item = R.string.power_control_passport;
            binding.btnPspPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.pspPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnPspPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.pspPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // usb/otg
            @StringRes final int item = R.string.power_control_usb_otg;
            binding.btnUsbOtgPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.usbPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnUsbOtgPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.usbPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // iccard
            @StringRes final int item = R.string.power_control_iccard;
            binding.btnIccardPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.iccardPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnIccardPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.iccardPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // idcard
            @StringRes final int item = R.string.power_control_idcard;
            binding.btnIdcardPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.idcardPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnIdcardPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.idcardPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // psam
            @StringRes final int item = R.string.power_control_psamcard;
            binding.btnPsamcardPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.psamcardPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnPsamcardPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.psamcardPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // uhf
            @StringRes final int item = R.string.power_control_uhf;
            binding.btnUhfPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.uhfPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnUhfPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.uhfPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // usb camera
            @StringRes final int item = R.string.power_control_usb_camera;
            binding.btnUsbCameraPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.usbCameraPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnUsbCameraPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.usbCameraPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // zink
            @StringRes final int item = R.string.power_control_zink;
            binding.btnZinkPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.zinkPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnZinkPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.zinkPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // nfc
            @StringRes final int item = R.string.power_control_nfc;
            binding.btnNfcPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.nfcPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnNfcPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.nfcPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // beep
            @StringRes final int item = R.string.power_control_beep;
            binding.btnBeepPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.beepPower(1);
                    showTips(ret, item);
                }
            });

            binding.btnBeepPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.beepPower(0);
                    showTips(ret, item);
                }
            });
        }

        {
            // dip switch
            @StringRes final int item = R.string.power_control_dial_switch;
            binding.btnGetDialStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.getDialSwitchStatus();
//                    showTips(ret, item);
                    binding.btnDialStatusResult.setText("Result:" + (ret==1?getString(R.string.power_control_open):getString(R.string.power_control_close)));
                }
            });

        }

        {
            // case switch
            @StringRes final int item = R.string.power_control_case_switch;
            binding.btnGetCaseStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.getCaseSwitchStatus(1);
//                    showTips(ret, item);
                    binding.btnCaseStatusResult.setText("Result:" + (ret==1?getString(R.string.power_control_open):getString(R.string.power_control_close)));
                }
            });
        }

        {
            // lan
            @StringRes final int item = R.string.power_control_lan_power;
            binding.btnLanPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.lanPower(1,1);
                    showTips(ret, item);
                }
            });

            binding.btnLanPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.lanPower(1,0);
                    showTips(ret, item);
                }
            });
        }

        {
            // hdmi
            @StringRes final int item = R.string.power_control_hdmi_power;
            binding.btnHdmiPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.hdmiPower(1,1);
                    showTips(ret, item);
                }
            });

            binding.btnHdmiPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = powerControl.hdmiPower(1,0);
                    showTips(ret, item);
                }
            });
        }

        {
            // usb
            @StringRes final int item = R.string.power_control_usb_power;
            binding.btnUsbPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String which = binding.edtUsbNum.getText().toString();
                    if(which.isEmpty()){
                        showTips(-1, item);
                    }
                    int ret = powerControl.usbPower(Integer.valueOf(which),1);
                    showTips(ret, item);
                }
            });

            binding.btnUsbPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String which = binding.edtUsbNum.getText().toString();
                    if(which.isEmpty()){
                        showTips(-1, item);
                    }
                    int ret = powerControl.usbPower(Integer.valueOf(which),0);
                    showTips(ret, item);
                }
            });
        }

        {
            // mpos
            @StringRes final int item = R.string.power_control_mpos_power;
            binding.btnMposPowerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String which = binding.edtMposNum.getText().toString();
                    if(which.isEmpty()){
                        showTips(-1, item);
                    }
                    int ret = powerControl.mposPower(Integer.valueOf(which),1);
                    showTips(ret, item);
                }
            });

            binding.btnMposPowerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String which = binding.edtMposNum.getText().toString();
                    if(which.isEmpty()){
                        showTips(-1, item);
                    }
                    int ret = powerControl.mposPower(Integer.valueOf(which),0);
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

        @StringRes int resultId;
        switch (code) {
            case ResultCode.SUCCESS:
                resultId = R.string.success_test;
                break;
            case ResultCode.ERR_SYS_NOT_SUPPORT:
                resultId = R.string.not_support_test;
                break;
            case ResultCode.ERR_INVALID_PARAM:
                resultId = R.string.invalid_test;
                break;
            default:
                resultId = R.string.fail_test;
                break;
        }

        String msg = String.format(Locale.getDefault(),
                "[%d] %s %s: %s", ++mIndex, getString(itemId), getString(R.string.operation_result), getString(resultId));
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        binding.tvPowerControlLog.setText(msg);
    }

}