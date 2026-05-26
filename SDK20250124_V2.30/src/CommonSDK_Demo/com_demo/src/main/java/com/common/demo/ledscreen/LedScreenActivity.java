package com.common.demo.ledscreen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.common.apiutil.ledscreen.LedScreenUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;


public class LedScreenActivity extends BaseActivity {

    private Spinner spinner_pos_serial;
    private ArrayAdapter<String> mAdapter;
    private RadioGroup radiogroup_character;
    private EditText et_input_money,et_input_baud;
    private Button bt_show_input_money,bt_clear_all,bt_reset,bt_modify_baud;
    private String POS_SERIAL = "/dev/ttyS0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledscreen);
        initUI();
    }

    private void initUI(){
        spinner_pos_serial = (Spinner) findViewById(R.id.spinner_pos_serial);
        radiogroup_character = (RadioGroup) findViewById(R.id.radiogroup_character);
        et_input_money = (EditText)findViewById(R.id.et_input_money);
        bt_show_input_money = (Button) findViewById(R.id.bt_show_input_money);
        bt_clear_all = (Button) findViewById(R.id.bt_clear_all);
        bt_reset = (Button) findViewById(R.id.bt_reset);
        et_input_baud = (EditText)findViewById(R.id.et_input_baud);
        bt_modify_baud = (Button) findViewById(R.id.bt_modify_baud);
        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<String> list = getSerialPath();
		for (int i = 0; i < list.size(); i++) {
			mAdapter.add(list.get(i));
		}
        spinner_pos_serial.setAdapter(mAdapter);
		
        mAdapter.notifyDataSetChanged();
		spinner_pos_serial.setSelection(1);// 指定串口1
		POS_SERIAL = mAdapter.getItem(1);
        
        radiogroup_character.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int data = -1;
                switch (checkedId){
                    case R.id.radiobutton_character_none_bright:
                        data = LedScreenUtil.character_none_bright;
                        break;
                    case R.id.radiobutton_character_unit_price:
                        data = LedScreenUtil.character_unit_price;
                        break;
                    case R.id.radiobutton_character_total_price:
                        data = LedScreenUtil.character_total_price;
                        break;
                    case R.id.radiobutton_character_collect_bill:
                        data = LedScreenUtil.character_collect_bill;
                        break;
                    case R.id.radiobutton_character_give_change:
                        data = LedScreenUtil.character_give_change;
                        break;
                }
                LedScreenUtil.switchCharacter(data);
            }
        });

        bt_show_input_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = et_input_money.getText().toString();
                if((data == null) || (data.length() < 1)){
                    Toast.makeText(LedScreenActivity.this,R.string.text_input_money_not_fit,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(LedScreenUtil.showInputMoney(data) != 0){
                	Toast.makeText(LedScreenActivity.this,R.string.text_money_change_error,Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	LedScreenUtil.clearAll();
            }
        });

        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	LedScreenUtil.reset();
            }
        });

        spinner_pos_serial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                POS_SERIAL = (String) parent.getSelectedItem();
                LedScreenUtil.closeLedScreen();
                LedScreenUtil.openLedScreen(POS_SERIAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        bt_modify_baud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String baud = et_input_baud.getText().toString();  
                if((baud.equals("9600")) || (baud.equals("4800"))  || (baud.equals("2400"))  || (baud.equals("1200"))  || (baud.equals("600"))  || (baud.equals("300")) ){
                	LedScreenUtil.modifyBaud(baud);
                }else{
                	Toast.makeText(LedScreenActivity.this,R.string.text_input_baud_not_fit,Toast.LENGTH_SHORT).show();
                    return;
                }
            
            }
        });
    }

    
    @Override
    protected void onResume() {
        super.onResume();
        POS_SERIAL = (String) spinner_pos_serial.getSelectedItem();
        LedScreenUtil.openLedScreen(POS_SERIAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LedScreenUtil.closeLedScreen();
    }
    
   
    public List<String> getSerialPath() {
		List<String> ttysList = new ArrayList<String>();
		String cmd = "ls /dev";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		BufferedInputStream in = null;
		BufferedReader inBr = null;
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			in = new BufferedInputStream(p.getInputStream());
			inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				Log.i("tagg", lineStr);
				if (lineStr.contains("tty")) {
					ttysList.add("/dev/" + lineStr.trim());
				}
			}
			// 检查命令是否执行失败。
			if (p.waitFor() != 0 && p.exitValue() == 1) {
				// p.exitValue()==0表示正常结束，1：非正常结束
				Log.e("tagg", "命令执行失败!");
			}
		} catch (Exception e) {
			Log.e("tagg", e.toString());
			// return Environment.getExternalStorageDirectory().getPath();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (inBr != null) {
					inBr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ttysList;
	}
}
