package com.common.demo.pos;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.common.CommonConstants;
import com.common.apiutil.pos.CommonUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class GpioActivity extends BaseActivity {

    //UI
    private Spinner spinner_gpio;
    private TextView tv_gpio_statu;
    private Button btn_gpio_low;
    private Button btn_gpio_high;

    private ArrayAdapter<String> mGpioAdapter;
    private final HashMap<String, String> mTypeHashMap = new HashMap<>();
    private int mGpioType = CommonConstants.GPIOType.GPIO41;

    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gpio);

        mCommonUtil = new CommonUtil(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        spinner_gpio = findViewById(R.id.spinner_gpio);
        tv_gpio_statu = findViewById(R.id.tv_gpio_statu);

        mGpioAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mGpioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGpioAdapter = generateAdapterFromClass("com.common.CommonConstants$GPIOType", mTypeHashMap);
        spinner_gpio.setAdapter(mGpioAdapter);

        mGpioAdapter.notifyDataSetChanged();
        spinner_gpio.setSelection(0);
        spinner_gpio.setOnItemSelectedListener(spinnerSelectedListener);

        btn_gpio_low = findViewById(R.id.btn_gpio_low);
        btn_gpio_high = findViewById(R.id.btn_gpio_high);

        if(mCommonUtil.getGPIOStatus(mGpioType)[0] == CommonConstants.GPIODirection.INPUT){
            btn_gpio_low.setEnabled(false);
            btn_gpio_high.setEnabled(false);
        }
    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_gpio_in:
                btn_gpio_low.setEnabled(false);
                btn_gpio_high.setEnabled(false);
                Toast.makeText(this, "" + mCommonUtil.setGPIOControl(mGpioType, CommonConstants.GPIODirection.INPUT), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_gpio_out:
                btn_gpio_low.setEnabled(true);
                btn_gpio_high.setEnabled(true);
                Toast.makeText(this, "" + mCommonUtil.setGPIOControl(mGpioType, CommonConstants.GPIODirection.OUTPUT), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_gpio_low:
                Toast.makeText(this, "" + mCommonUtil.setGPIOLevel(mGpioType, CommonConstants.GPIOLevel.LOW_LEVEL), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_gpio_high:
                Toast.makeText(this, "" + mCommonUtil.setGPIOLevel(mGpioType, CommonConstants.GPIOLevel.HIGH_LEVEL), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_gpio_check:
                int[] arr = mCommonUtil.getGPIOStatus(mGpioType);
                @StringRes int directionId = R.string.pos_gpio_error;
                if (arr[0] == CommonConstants.GPIODirection.INPUT) {
                    directionId = R.string.pos_gpio_in;
                } else if (arr[0] == CommonConstants.GPIODirection.OUTPUT) {
                    directionId = R.string.pos_gpio_out;
                }
                @StringRes int levelId = R.string.pos_gpio_error;
                if (arr[1] == CommonConstants.GPIOLevel.HIGH_LEVEL) {
                    levelId = R.string.pos_gpio_high;
                } else if (arr[1] == CommonConstants.GPIOLevel.LOW_LEVEL) {
                    levelId = R.string.pos_gpio_low;
                }
                String msg = getString(R.string.pos_gpio_direction) + getString(directionId) + "\n" +
                        getString(R.string.pos_gpio_level) + getString(levelId) + "\n";
                tv_gpio_statu.setText(msg);
                break;
        }
    }

    private AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = mGpioAdapter.getItem(position);
            String value = mTypeHashMap.get(name);
            mGpioType = Integer.parseInt(value == null ? String.valueOf(CommonConstants.GPIOType.GPIO41) : value);
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
}
