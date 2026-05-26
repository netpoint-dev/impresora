package com.common.demo.moneybox;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.common.CommonConstants;
import com.common.apiutil.ErrorCode;
import com.common.apiutil.ResultCode;
import com.common.demo.R;
import com.common.apiutil.moneybox.MoneyBox;
import com.common.demo.bean.BaseActivity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MoneyBoxActivity extends BaseActivity {

	private Button moneybox;
	private Spinner spMoneyBoxType;

	// MoneyBoxType
	private ArrayAdapter<String> moneyBoxTypeAdapter;
	private final HashMap<String, String> moneyBoxHashMap = new HashMap<>();
	private int moneyBoxType = CommonConstants.MoneyBoxType.MoneyBox_1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.money_box);

		spMoneyBoxType = findViewById(R.id.spinner_moneyboxnum);
		spMoneyBoxType.setSelection(0);
		moneyBoxTypeAdapter = generateAdapterFromClass("com.common.CommonConstants$MoneyBoxType", moneyBoxHashMap);
		spMoneyBoxType.setAdapter(moneyBoxTypeAdapter);
		moneyBoxTypeAdapter.notifyDataSetChanged();
		spMoneyBoxType.setOnItemSelectedListener(spinnerMoneyBoxListener);

		moneybox = (Button) findViewById(R.id.open_moneybox);
		moneybox.setOnClickListener(new OnClickListener() {


			public void onClick(View arg0) {
				int result = MoneyBox.open(moneyBoxType, 450);

				if (result == ErrorCode.OK)
				{
					Toast.makeText(MoneyBoxActivity.this, getString(R.string.success_test), Toast.LENGTH_SHORT).show();
				}else if(result == ErrorCode.ERR_SYS_UNEXPECT){
					Toast.makeText(MoneyBoxActivity.this, getString(R.string.unexpect_text), Toast.LENGTH_SHORT).show();
				}else if(result == ErrorCode.ERR_SYS_NOT_SUPPORT){
					Toast.makeText(MoneyBoxActivity.this, getString(R.string.not_support_test), Toast.LENGTH_SHORT).show();
				}

			}
		});

		findViewById(R.id.btn_get_moneybox_state).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int ret = MoneyBox.getState(moneyBoxType);
				@StringRes int strId = R.string.fail_test;
				if (ret == 1) {
					strId = R.string.moneybox_toast_text_open;
				}else if(ret == 0){
					strId = R.string.moneybox_toast_text_close;
				}else if(ret == ErrorCode.ERR_SYS_UNEXPECT){
					strId = R.string.unexpect_text;
				}else if(ret == ErrorCode.ERR_SYS_NOT_SUPPORT){
					strId = R.string.not_support_test;
				}
				Toast.makeText(MoneyBoxActivity.this, strId, Toast.LENGTH_SHORT).show();
			}
		});

		if(MoneyBox.getState(moneyBoxType) == ResultCode.ERR_SYS_NOT_SUPPORT){
			findViewById(R.id.btn_get_moneybox_state).setVisibility(View.GONE);
		}
	}

	private AdapterView.OnItemSelectedListener spinnerMoneyBoxListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			String name = moneyBoxTypeAdapter.getItem(position);
			String value = moneyBoxHashMap.get(name);
			moneyBoxType = Integer.parseInt(value == null ? String.valueOf(CommonConstants.MoneyBoxType.MoneyBox_1) : value);
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
