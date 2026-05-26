package com.common.demo.pos;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.ResultCode;
import com.common.apiutil.pos.CommonUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class LedActivity extends BaseActivity {

    // ledColor
    private ArrayAdapter<String> mColorAdapter;
    private final HashMap<String, String> mColorHashMap = new HashMap<>();
    private int mLedColor = CommonConstants.LedColor.WHITE_LED;

    // ledType
    private ArrayAdapter<String> mTypeAdapter;
    private final HashMap<String, String> mTypeHashMap = new HashMap<>();
    private int mLedType = CommonConstants.LedType.FILL_LIGHT_1;

    //UI
    private TextView mTvBrightness;
    private SeekBar mSeekbarColor;
    private Spinner mSpinnerColor;
    private Spinner mSpLedType;
    private Button ledOpenBtn;
    private Button ledCloseBtn;

    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leds);

        mCommonUtil = new CommonUtil(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mSpinnerColor = findViewById(R.id.spinner_led);
        mSpLedType = findViewById(R.id.spinner_led_type);
        ledOpenBtn = findViewById(R.id.ledOpenBtn);
        ledCloseBtn = findViewById(R.id.ledCloseBtn);

        mTvBrightness = findViewById(R.id.seekbar_led_num);
        mSeekbarColor = findViewById(R.id.seekbar_led);
        mSeekbarColor.setOnSeekBarChangeListener(onSeekBarChangeListener);

        mColorAdapter = generateAdapterFromClass("com.common.CommonConstants$LedColor", mColorHashMap);
        mSpinnerColor.setAdapter(mColorAdapter);
        mSpinnerColor.setSelection(4);
        mColorAdapter.notifyDataSetChanged();
        mSpinnerColor.setOnItemSelectedListener(spinnerSelectedListener);

        mTypeAdapter = generateAdapterFromClass("com.common.CommonConstants$LedType", mTypeHashMap);
        mSpLedType.setAdapter(mTypeAdapter);
        mSpLedType.setSelection(0);
        mTypeAdapter.notifyDataSetChanged();
        mSpLedType.setOnItemSelectedListener(mIndexSelectListener);

        if(SystemUtil.getInternalModel().equals("C1B")){
            mSeekbarColor.setVisibility(View.GONE);
            mSpinnerColor.setEnabled(false);
            mSpLedType.setEnabled(false);

            mSpinnerColor.setSelection(4);
            mSpLedType.setSelection(4);
//            mColorAdapter.clear();
//            mColorAdapter.add("WHITE_LED");
//            mColorAdapter.notifyDataSetChanged();
//
//            mTypeAdapter.clear();
//            mTypeAdapter.add("FILL_LIGHT_1");
//            mTypeAdapter.notifyDataSetChanged();
//
//            mLedType = CommonConstants.LedType.FILL_LIGHT_1;
//            mLedColor = CommonConstants.LedColor.WHITE_LED;
        }/*else{
            ledOpenBtn.setVisibility(View.GONE);
            ledCloseBtn.setVisibility(View.GONE);
        }*/

//        mSeekbarColor.setVisibility(View.GONE);

    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekbar_led:
                    mCommonUtil.setColorLed(mLedType, mLedColor, progress);
                    mTvBrightness.setText(progress + "");
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = mColorAdapter.getItem(position);
            String value = mColorHashMap.get(name);
            mLedColor = Integer.parseInt(value == null ? String.valueOf(CommonConstants.LedColor.WHITE_LED) : value);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener mIndexSelectListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = mTypeAdapter.getItem(position);
            String value = mTypeHashMap.get(name);
            mLedType = Integer.parseInt(value == null ? String.valueOf(CommonConstants.LedType.COLOR_LED_1) : value);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * 根据包名获取常量名字并装包
     *
     * @param packageName 需要装包的常量类包名
     * @param map         以<常量名，常量值>存储的HashMap
     * @return 返回装包后的数组适配器
     */
    private ArrayAdapter<String> generateAdapterFromClass(String packageName, HashMap<String, String> map) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        try {
            Class<?> c = Class.forName(packageName);
            Field[] f = c.getDeclaredFields();
            for (Field field : f) {
                String name = field.getName();
                String value = String.valueOf(field.get(name));
                adapter.add(name);
                map.put(name, value);
            }
        } catch (ClassNotFoundException ignore) {
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return adapter;
    }

    public void onClick(View view) {
        int result = ResultCode.ERR_SYS_UNEXPECT;
        switch (view.getId()){
            case R.id.ledOpenBtn:
                result = mCommonUtil.setColorLed(mLedType, mLedColor, 255);
                break;
            case R.id.ledCloseBtn:
                result = mCommonUtil.setColorLed(mLedType, mLedColor, 0);
                break;
        }
        if(result == ResultCode.SUCCESS){
            Toast.makeText(this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
        }else if(result == ResultCode.ERR_SYS_NOT_SUPPORT){
            Toast.makeText(this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.fail_test), Toast.LENGTH_SHORT).show();
        }
    }
}
