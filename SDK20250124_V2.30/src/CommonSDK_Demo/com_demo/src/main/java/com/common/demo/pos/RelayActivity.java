package com.common.demo.pos;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.common.CommonConstants;
import com.common.apiutil.pos.CommonUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.lang.reflect.Field;
import java.util.HashMap;


public class RelayActivity extends BaseActivity {

    // UI
    private Spinner relaytypeSpn;

    // RS485Type
    private ArrayAdapter<String> mRelayTypeAdapter;
    private final HashMap<String, String> mRelayTypeHashMap = new HashMap<>();
    private int mRelayType;

    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_relay);

        mCommonUtil = new CommonUtil(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        relaytypeSpn = findViewById(R.id.spinner_relaytype);
        relaytypeSpn.setSelection(0);
        mRelayTypeAdapter = generateAdapterFromClass("com.common.CommonConstants$RelayType", mRelayTypeHashMap);
        relaytypeSpn.setAdapter(mRelayTypeAdapter);
        mRelayTypeAdapter.notifyDataSetChanged();
        relaytypeSpn.setOnItemSelectedListener(spinnerRelayTypeListener);
    }

    private AdapterView.OnItemSelectedListener spinnerRelayTypeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = mRelayTypeAdapter.getItem(position);
            String value = mRelayTypeHashMap.get(name);
            mRelayType = Integer.parseInt(value == null ? String.valueOf(CommonConstants.RelayType.RELAY_1) : value);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_relay_on:
                Toast.makeText(this, mCommonUtil.setRelayPower(mRelayType,1)+"", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_relay_off:
                Toast.makeText(this, mCommonUtil.setRelayPower(mRelayType,0)+"", Toast.LENGTH_SHORT).show();
                break;
        }
    }

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
