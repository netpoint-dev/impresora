package com.common.demo.can;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.common.apiutil.ResultCode;
import com.common.apiutil.can.CanUtil2;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

public class Can2DeptActivity extends BaseActivity {

    private TextView tv_log;
    private EditText edt_id;
    private EditText edt_data;
    private TextView tv_data_hex, tv_id_hex;
    private CheckBox cb_mode_self;
    private Spinner spinner_frame_format;
    private Spinner spinner_frame_type;
    private boolean mTextColorRed = false;

    private TableLayout mTlMode, mTlData;

    private EditText edtSjw;
    private EditText edtBs1;
    private EditText edtBs2;
    private EditText edtPrescaler;
    private TextView tv_baud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_can2_dept);

        initView();
    }

    private void initView() {

        final Button btnSetModeLayout = findViewById(R.id.btn_can_mode_layout);
        final Button btnSetDataLayout = findViewById(R.id.btn_can_data_layout);
        final Button btnSetMode = findViewById(R.id.btn_can_set_mode);
        final Button btnSetFilter = findViewById(R.id.btn_can_set_filter);

        tv_log = findViewById(R.id.tv_can_log);
        edt_id = findViewById(R.id.edt_can_send_id);
        edt_data = findViewById(R.id.edt_can_send_data_string);
        tv_data_hex = findViewById(R.id.tv_can_send_data_hex);
        tv_id_hex = findViewById(R.id.tv_can_send_id_hex);
        cb_mode_self = findViewById(R.id.cb_can_mode_self);
        mTlMode = findViewById(R.id.table_can_mode);
        mTlData = findViewById(R.id.table_can_send_data);

        tv_baud = findViewById(R.id.tv_can_mode_baud);
        edtSjw = findViewById(R.id.edt_can_mode_sjw);
        edtBs1 = findViewById(R.id.edt_can_mode_bs1);
        edtBs2 = findViewById(R.id.edt_can_mode_bs2);
        edtPrescaler = findViewById(R.id.edt_can_mode_prescaler);

        {
            edtSjw.setText("0");
            edtBs1.setText("0");
            edtBs2.setText("0");
            edtPrescaler.setText("36");
            calcBaud();

            edtSjw.addTextChangedListener(mModeWatcher);
            edtBs1.addTextChangedListener(mModeWatcher);
            edtBs2.addTextChangedListener(mModeWatcher);
            edtPrescaler.addTextChangedListener(mModeWatcher);

            cb_mode_self.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(Can2DeptActivity.this, R.string.can_set_mode_loopback_tips, Toast.LENGTH_SHORT).show();
                    setLog(getString(R.string.can_set_mode_loopback_tips));
                }
            });
        }

        {
            spinner_frame_format = findViewById(R.id.sp_can_frame_format);
            ArrayAdapter<String> mFormatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            mFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFormatAdapter.add(getString(R.string.can_standard_frame));
            mFormatAdapter.add(getString(R.string.can_extended_frame));
            spinner_frame_format.setAdapter(mFormatAdapter);

            mFormatAdapter.notifyDataSetChanged();
            spinner_frame_format.setSelection(0);
        }

        {
            spinner_frame_type = findViewById(R.id.sp_can_frame_type);
            ArrayAdapter<String> mTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mTypeAdapter.add(getString(R.string.can_data_frame));
            mTypeAdapter.add(getString(R.string.can_remote_frame));
            spinner_frame_type.setAdapter(mTypeAdapter);

            mTypeAdapter.notifyDataSetChanged();
            spinner_frame_type.setSelection(0);
        }

        {
            final CanUtil2 can = new CanUtil2(this);
            btnSetMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!verifyLegitimacyOfModeParameters()) {
                        Toast.makeText(Can2DeptActivity.this, R.string.can_send_parameter_error_tips, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int sjw = Integer.parseInt(edtSjw.getText().toString());
                    int bs1 = Integer.parseInt(edtBs1.getText().toString());
                    int bs2 = Integer.parseInt(edtBs2.getText().toString());
                    int preScaler = Integer.parseInt(edtPrescaler.getText().toString());
                    int mode = 0;
                    if (cb_mode_self.isChecked()) {
                        mode = 1;
                    }

                    int ret = can.configMode(sjw, bs1, bs2, preScaler, mode);
                    String log = String.format(Locale.getDefault(),
                            "%s: %s", getString(R.string.can_set_mode_result), getString(resultDeal(ret)));
                    setLog(log);
                    btnSetFilter.setEnabled(ret == ResultCode.SUCCESS);
                }
            });

            btnSetFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ret = can.configFilter(0, 0, 1, 0, 0, 0, 0, 1);
                    String log = String.format(Locale.getDefault(),
                            "%s: %s", getString(R.string.can_set_filter_result), getString(resultDeal(ret)));
                    setLog(log);
                    btnSetDataLayout.setEnabled(ret == ResultCode.SUCCESS);
                }
            });

            findViewById(R.id.btn_can_send_data).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String msg = edt_data.getText().toString();
                    if (TextUtils.isEmpty(msg)) {
                        Toast.makeText(Can2DeptActivity.this, R.string.can_send_parameter_error_tips, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String id = edt_id.getText().toString();
                    if (TextUtils.isEmpty(id)) {
                        Toast.makeText(Can2DeptActivity.this, R.string.can_send_parameter_null_tips, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int msgId = 0;
                    try {
                        msgId = Integer.parseInt(id);
                    } catch (NumberFormatException ignore) {
                    }

                    int format = CanUtil2.CAN_ID_STANDARD;
                    if (spinner_frame_format.getSelectedItemPosition() == 1) {
                        format = CanUtil2.CAN_ID_EXTENDED;
                    }

                    int type = CanUtil2.CAN_RTR_DATA;
                    if (spinner_frame_type.getSelectedItemPosition() == 1) {
                        type = CanUtil2.CAN_RTR_REMOTE;
                    }

                    byte[] data = msg.getBytes(StandardCharsets.UTF_8);
                    int ret = can.write(msgId, format, type, data, data.length);
                    String log = String.format(Locale.getDefault(), "%s: %s", getString(R.string.can_send_result), getString(resultDeal(ret)));
                    setLog(log);
                }
            });

            findViewById(R.id.btn_can_receive_data).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_log.setText("");
                    byte[] ret = can.read(20, 5);
                    if (ret.length < 20) {
                        return;
                    }

                    String log = "";
                    if (ret[8] == CanUtil2.CAN_ID_STANDARD) {
                        int id = (((int) ret[3] & 0xff) << 24) + (((int) ret[2] & 0xff) << 16) + (((int) ret[1] & 0xff) << 8) + (((int) ret[0] & 0xff));
                        log += String.format(Locale.getDefault(), "%s 0x%04x\n%s %d\r\n%s %s\r\n",
                                getString(R.string.can_id_hex), id,
                                getString(R.string.can_id), id,
                                getString(R.string.can_frame_format), getString(R.string.can_standard_frame));
                    } else if (ret[8] == CanUtil2.CAN_ID_EXTENDED) {
                        int id = (((int) ret[7] & 0xff) << 24) + (((int) ret[6] & 0xff) << 16) + (((int) ret[5] & 0xff) << 8) + (((int) ret[4] & 0xff));
                        log += String.format(Locale.getDefault(), "%s 0x%04x\n%s %d\r\n%s %s\r\n",
                                getString(R.string.can_id_hex), id,
                                getString(R.string.can_id), id,
                                getString(R.string.can_frame_format), getString(R.string.can_extended_frame));
                    }
                    if (ret[9] == CanUtil2.CAN_RTR_DATA) {
                        log += String.format(Locale.getDefault(), "%s %s\r\n", getString(R.string.can_frame_type), getString(R.string.can_data_frame));
                    } else if (ret[9] == CanUtil2.CAN_RTR_REMOTE) {
                        log += String.format(Locale.getDefault(), "%s %s\r\n", getString(R.string.can_frame_type), getString(R.string.can_remote_frame));
                    }
                    String dataString = new String(ret, 11, 8, StandardCharsets.UTF_8);
                    String dataHexString = toHexString(ret, 11, 8);
                    log += String.format(Locale.getDefault(), "%s %d\r\n%s %s\r\n%s %s\r\n%s %d",
                            getString(R.string.can_data_len), ret[10],
                            getString(R.string.can_data_hex), dataHexString,
                            getString(R.string.can_data_string), dataString,
                            "FMI:", ret[19]);
                    setLog(log);
                }
            });
        }

        {
            btnSetModeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTlMode.setVisibility(View.VISIBLE);
                    mTlData.setVisibility(View.GONE);
                    btnSetModeLayout.setEnabled(false);
                    btnSetDataLayout.setEnabled(false);
                    btnSetFilter.setEnabled(false);
                }
            });

            btnSetDataLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTlMode.setVisibility(View.GONE);
                    mTlData.setVisibility(View.VISIBLE);
                    btnSetModeLayout.setEnabled(true);
                    btnSetDataLayout.setEnabled(false);
                }
            });

        }
        mTlData.setVisibility(View.GONE);
        edt_data.addTextChangedListener(mDataWatcher);
        edt_id.addTextChangedListener(mDataWatcher);
        edt_data.setText("12345678");
        edt_id.setText("123");
    }

    private void setLog(String msg) {

        tv_log.setText("");
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (mTextColorRed) {
            tv_log.setTextColor(Color.RED);
        } else {
            tv_log.setTextColor(Color.BLUE);
        }
        mTextColorRed = !mTextColorRed;
        tv_log.setText(msg);
    }

    private boolean verifyLegitimacyOfModeParameters() {

        String sSjw = edtSjw.getText().toString();
        if (TextUtils.isEmpty(sSjw) || !isInteger(sSjw)) {
            return false;
        }
        int sjw = Integer.parseInt(sSjw);
        if (sjw < 0 || sjw > 3) {
            return false;
        }

        String sBs1 = edtBs1.getText().toString();
        if (TextUtils.isEmpty(sBs1) || !isInteger(sBs1)) {
            return false;
        }
        int bs1 = Integer.parseInt(sBs1);
        if (bs1 < 0 || bs1 > 15) {
            return false;
        }

        String sBs2 = edtBs2.getText().toString();
        if (TextUtils.isEmpty(sBs2) || !isInteger(sBs2)) {
            return false;
        }
        int bs2 = Integer.parseInt(sBs2);
        if (bs2 < 0 || bs2 > 7) {
            return false;
        }

        String sPrescaler = edtPrescaler.getText().toString();
        if (TextUtils.isEmpty(sPrescaler) || !isInteger(sPrescaler)) {
            return false;
        }
        int prescaler = Integer.parseInt(sPrescaler);
        if (prescaler < 1 || prescaler > 1024) {
            return false;
        }

        return true;
    }

    private void calcBaud() {

        if (!verifyLegitimacyOfModeParameters()) {
            tv_baud.setText("");
            return;
        }
        int sjw = Integer.parseInt(edtSjw.getText().toString());
        int bs1 = Integer.parseInt(edtBs1.getText().toString());
        int bs2 = Integer.parseInt(edtBs2.getText().toString());
        int preScaler = Integer.parseInt(edtPrescaler.getText().toString());
        int baud = 108 * 1000000 / preScaler / (sjw + bs1 + bs2 + 3);
        String template = getString(R.string.baud_rate) + ": %d";
        if (baud > 1000) {
            baud /= 1000;
            template += " Kbps";
        }
        String msg = String.format(Locale.getDefault(), template, baud);
        tv_baud.setText(msg);
    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private static String toHexString(byte[] data, int offset, int length) {

        if (data == null || offset > data.length || offset + length > data.length) {
            return "";
        }

        String string;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = offset; i < offset + length; i++) {
            string = Integer.toHexString(data[i] & 0xFF);
            if (string.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(string.toUpperCase()).append(" ");
        }

        return stringBuilder.toString().trim();
    }

    private final TextWatcher mModeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            calcBaud();
        }
    };

    private final TextWatcher mDataWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String msg1 = edt_data.getText().toString();
            String msg2 = edt_id.getText().toString();
            if (!TextUtils.isEmpty(msg1)) {
                byte[] data = msg1.getBytes(StandardCharsets.UTF_8);
                tv_data_hex.setText(toHexString(data, 0, data.length));
            }

            if (!TextUtils.isEmpty(msg2) && isInteger(msg2)) {

                tv_id_hex.setText(String.format("0x%04x", Integer.parseInt(msg2)));
            }

        }
    };

    @StringRes
    private int resultDeal(int code) {

        if (code == ResultCode.SUCCESS) {
            return R.string.success_test;
        } else if (code == ResultCode.ERR_SYS_NOT_SUPPORT) {
            return R.string.not_support;
        } else {
            return R.string.fail_test;
        }
    }

}