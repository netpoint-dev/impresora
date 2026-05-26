package com.common.demo.can;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.common.apiutil.can.CanOperationListener;
import com.common.apiutil.can.CanRecvInfo;
import com.common.apiutil.can.CanUtil;
import com.common.apiutil.util.StringUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

public class CanActivity extends BaseActivity {

    private EditText edt_can_bitrate;
    private TextView tv_candump;
    private Spinner spinner_frame_format;
    private Spinner spinner_frame_type;
    private EditText edt_cansend_data;
    private EditText edt_cansend_id;

    private ArrayAdapter<String> mFormatAdapter;
    private ArrayAdapter<String> mTypeAdapter;
    private int recvCount = 0;

    private CanUtil mCanUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_can);

        mCanUtil = new CanUtil(this);

        edt_can_bitrate = findViewById(R.id.edt_can_bitrate);
        tv_candump = findViewById(R.id.tv_candump);
        spinner_frame_format = findViewById(R.id.spinner_frame_format);
        spinner_frame_type = findViewById(R.id.spinner_frame_type);
        edt_cansend_data = findViewById(R.id.edt_cansend_data);
        edt_cansend_id = findViewById(R.id.edt_cansend_id);

        mFormatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFormatAdapter.add(getResources().getString(R.string.can_standard_frame));
        mFormatAdapter.add(getResources().getString(R.string.can_extended_frame));
        spinner_frame_format.setAdapter(mFormatAdapter);

        mFormatAdapter.notifyDataSetChanged();
        spinner_frame_format.setSelection(0);
        spinner_frame_format.setOnItemSelectedListener(spinnerSelectedListener);

        mTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeAdapter.add(getResources().getString(R.string.can_data_frame));
        mTypeAdapter.add(getResources().getString(R.string.can_remote_frame));
        spinner_frame_type.setAdapter(mTypeAdapter);

        mTypeAdapter.notifyDataSetChanged();
        spinner_frame_type.setSelection(0);
        spinner_frame_type.setOnItemSelectedListener(spinnerSelectedListener);
    }

    @Override
    protected void onStop() {
        mCanUtil.close();
        super.onStop();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_can_open:
                mCanUtil.close();
                String str = edt_can_bitrate.getText().toString();
                if(!str.isEmpty()) {
                    Toast.makeText(this, mCanUtil.open(Integer.valueOf(str)) + "", Toast.LENGTH_SHORT).show();
                }
                mCanUtil.canRecv(new CanOperationListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void canInput(CanRecvInfo canRecvInfo) {
//                        Log.d("tagg", " can id: " + canRecvInfo.getRecvId()+" Data: " + StringUtil.toHexString(canRecvInfo.getRecvData()));
                        String frameFormat = "";
                        String frameType = "";
                        String frameData = "";
                        if(canRecvInfo.getFrameFormat() == CanUtil.STANDARD_FRAME_FORMAT){
                            frameFormat = getResources().getString(R.string.can_standard_frame);
                        }else if(canRecvInfo.getFrameFormat() == CanUtil.EXTEND_FRAME_FORMAT){
                            frameFormat = getResources().getString(R.string.can_extended_frame);
                        }

                        if(canRecvInfo.getFrameType() == CanUtil.DATA_FRAME_TYPE){
                            frameType = getResources().getString(R.string.can_data_frame);
                        }else if(canRecvInfo.getFrameType() == CanUtil.REMOTE_FRAME_TYPE){
                            frameType = getResources().getString(R.string.can_remote_frame);
                        }
                        if(canRecvInfo.getRecvData()!=null){
                            frameData = StringUtil.toHexString(canRecvInfo.getRecvData());
                        }
                        recvCount++;
                        tv_candump.setText(getResources().getString(R.string.can_frame_format) + frameFormat + "\r\n" +
                                getResources().getString(R.string.can_frame_type) + frameType + "\r\n" +
                                getResources().getString(R.string.can_id) + canRecvInfo.getRecvId() + "\r\n" +
                                getResources().getString(R.string.can_data_len) + canRecvInfo.getRecvDataLen() + "\r\n" +
                                getResources().getString(R.string.can_data) + frameData + "\r\n\r\n" +
                                getResources().getString(R.string.can_recv_num) + "【" + recvCount + "】");
                    }
                });
                break;
            case R.id.btn_can_close:
                Toast.makeText(this, mCanUtil.close()+"", Toast.LENGTH_SHORT).show();
                tv_candump.setText("");
                break;
            case R.id.btn_get_can_status:
                int status = mCanUtil.getStatus();
                str = getResources().getString(R.string.can_status);
                if(status == 1){
                    str += getResources().getString(R.string.can_open);
                }else if(status == 0){
                    str += getResources().getString(R.string.can_close);
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cansend_data:
                byte[] data = {0x00};
                int formatPosition = spinner_frame_format.getSelectedItemPosition();
                int typePosition = spinner_frame_type.getSelectedItemPosition();
                String idStr = edt_cansend_id.getText().toString();
                String dataStr = edt_cansend_data.getText().toString();
                if(idStr.isEmpty() || dataStr.isEmpty()){
                    Toast.makeText(this, getResources().getString(R.string.can_send_parameter_null_tips), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!dataStr.isEmpty()){
                    data = StringUtil.toBytes(dataStr);
                }

                if(formatPosition == 0){
                    formatPosition = CanUtil.STANDARD_FRAME_FORMAT;
                }else if(formatPosition == 1){
                    formatPosition = CanUtil.EXTEND_FRAME_FORMAT;
                }

                if(typePosition == 0){
                    typePosition = CanUtil.DATA_FRAME_TYPE;
                }else if(typePosition == 1){
                    typePosition = CanUtil.REMOTE_FRAME_TYPE;
                }

                int ret = mCanUtil.canSend(formatPosition, typePosition, idStr, data);
                Toast.makeText(this, ret+"", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, getResources().getString(R.string.can_send_parameter_error_tips), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
