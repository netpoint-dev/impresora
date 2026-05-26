package com.common.demo.serial;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.common.apiutil.serial.Serial;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SerialTestActivity extends BaseActivity {

	private Integer[] mBaud_array={9600,19200,38400,57600,115200,230400,460800,500000,576000,921600,1000000};

	private Serial mSerial;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private EditText DataSendEdit;
	private TextView tv_serial;
	private TextView serial_tv_receive_data;
	private Spinner serialspinner;
	private ArrayAdapter<String> mAdapter;
	private Spinner baudspinner;
	private ArrayAdapter<Integer> mBaudAdapter;
	private Button serial_bt_open,DataSendBn;
	private String currenpath = "";
	private int currenBaud = 9600;
	private boolean flag = true;
	private static StringBuilder stringBuilder;
	
	private String pass_failed = null;
	private SharedPreferences preference;
	private SharedPreferences.Editor editor;
	private List<String> serialList;

	
	private TextView serial_00,serial_01,serial_02,serial_03,serial_04;
	private boolean serial_00_boolean,serial_01_boolean,serial_02_boolean,serial_03_boolean,serial_04_boolean;
	private String serial_result;
	private boolean Second400B = false;
	
	private int serialNum0 = -1;
	private int serialNum1 = -1;
	
	private ReadThread mReadThread;
	private String mSendStr="";
	private String mReceiveStr="";
	private int count=0;
	
	/*private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				serial_bt_open.performClick();
				break;
			case 2:
				DataSendBn.performClick();
				break;
			case 3:
				//400A:usb1,usb0,s4,s0
				if((((SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400.ordinal()
						&&!Build.MODEL.equals("TPS400H")
						&&!Build.MODEL.equals("TPS400F"))
					|| SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400A.ordinal()
					*//*|| SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS615.ordinal()*//*)
					&& stringBuilder.toString().equals("abcdefghijklmnopqrstuvwsyz1234567890abcdefghijklmnopqrstuvwsyz1234567890abcdefghijklmnopqrstuvwsyz1234567890abcdefghijklmnopqrstuvwsyz1234567890"))
					|| ((SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400B.ordinal() 
					|| SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400C.ordinal()
					|| Build.MODEL.equals("TPS400H"))
					&& stringBuilder.toString().equals("abcdefghijklmnopqrstuvwsyz1234567890abcdefghijklmnopqrstuvwsyz1234567890"))
					|| (((SystemUtil.getDeviceType() != StringUtil.DeviceModelEnum.TPS400.ordinal()&&!Build.MODEL.equals("TPS400H"))
					&& SystemUtil.getDeviceType() != StringUtil.DeviceModelEnum.TPS400B.ordinal() 
					&& SystemUtil.getDeviceType() != StringUtil.DeviceModelEnum.TPS400C.ordinal()
					&& SystemUtil.getDeviceType() != StringUtil.DeviceModelEnum.TPS400A.ordinal()
					*//*&& SystemUtil.getDeviceType() != StringUtil.DeviceModelEnum.TPS615.ordinal()*//*)
					&& stringBuilder.toString().equals("abcdefghijklmnopqrstuvwsyz1234567890"))
					) {
					pass_failed = getString(R.string.pass_btn);
					editor.putString(getString(R.string.s_Serial), pass_failed);
					editor.commit();
					NextObjectTestBn.performClick();
				}else if((SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400B.ordinal()
						|| SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400C.ordinal()
						|| Build.MODEL.equals("TPS400H")) && !Second400B) {
					currenpath = serialList.get(1);
					Second400B = true;
					handler1.sendEmptyMessageDelayed(7, 300);
					handler1.sendEmptyMessageDelayed(2, 600);
					handler1.sendEmptyMessageDelayed(3, 1000);
				}else {
					pass_failed = getString(R.string.fail_btn);
					editor.putString(getString(R.string.s_Serial), pass_failed);
					editor.commit();
				}
				break;
			case 4:
				if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400A.ordinal()) {
					currenpath = serialList.get(1);
					serial_00_boolean = false;
					serial_01_boolean = true;
					serial_02_boolean = false;
					serial_03_boolean = false;
					serial_04_boolean = false;
					initSerial();
				}else {
					currenpath = serialList.get(1);
					serial_00_boolean = false;
					serial_01_boolean = true;
					serial_02_boolean = false;
					serial_03_boolean = false;
					serial_04_boolean = false;
					initSerial();
				}
				break;
			case 5:
				if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400A.ordinal()) {
					currenpath = serialList.get(3);
					serial_00_boolean = false;
					serial_01_boolean = false;
					serial_02_boolean = false;
					serial_03_boolean = true;
					serial_04_boolean = false;
					initSerial();
				}else {
					currenpath = serialList.get(3);
					serial_00_boolean = false;
					serial_01_boolean = false;
					serial_02_boolean = false;
					serial_03_boolean = true;
					serial_04_boolean = false;
					initSerial();
				}
				break;
			case 6:
				if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS400A.ordinal()) {
					currenpath = serialList.get(0);
					serial_00_boolean = false;
					serial_01_boolean = false;
					serial_02_boolean = false;
					serial_03_boolean = true;
					serial_04_boolean = false;
					initSerial();
				}else {
					currenpath = serialList.get(0);
					serial_00_boolean = true;
					serial_01_boolean = false;
					serial_02_boolean = false;
					serial_03_boolean = false;
					serial_04_boolean = false;
					initSerial();
				}
				
				break;
			case 7:
				serial_bt_open.performClick();
				break;
			case 8:
				MyApplication.setserialAuto(true);
				break;
			case 9:
				NextObjectTestBn.performClick();
				break;
			default:
				break;
			}
		};
	};
	private String internal_Model;*/

//	private class ReadThread extends Thread {
//		@Override
//		public synchronized void run() {
//			super.run();
//			while (flag) {
//				sleep(10);
//				if(mInputStream != null){
//					int size = 0;
//					byte[] buffer = new byte[64];
//					try{
//						size = mInputStream.available();
//						if(size > 0){
//							size = mInputStream.read(buffer);
//							if(size > 0){
//
//								serial_result = (new String(buffer, 0, size, "UTF-8")).toString();
//								Log.d("idcard demo", "serial data:"+serial_result);
//								if(stringBuilder==null){
//									stringBuilder = new StringBuilder();
//								}
//								stringBuilder.append(serial_result);
//									runOnUiThread(new Runnable() {
//
//										@Override
//										public void run() {
//
//											if((stringBuilder != null) && (stringBuilder.toString().length() > 0)){
//												serial_tv_receive_data.setText(stringBuilder.toString());
//											}
//										}
//									});
//								}
//						}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}
//		}

	private class ReadThread extends Thread {

		int count = 0;

		@Override
		public void run() {
			super.run();
			try {
				while (!isInterrupted()) {

						int size;
						if (mInputStream != null && mInputStream.available() > 0) {
							count = 0;
							byte[] buffer = new byte[64];
							size = mInputStream.read(buffer);
							buffer = Arrays.copyOfRange(buffer, 0, size);
							stringBuilder.append(StringUtil.toHexString(buffer));
							Log.d("tagg","recv[" + StringUtil.toHexString(buffer) + "]");
//                        if (stringBuilder.length() > 0) {
//                            sendHandler(0, DataProcessUtil.hexStringToByte(stringBuilder.toString()));
//                            stringBuilder.setLength(0);
//                        }
						} else {
							//8次没收到输入流，则认为一串内容接收结束
							count++;
							Thread.sleep(10);
							if (count == 8) {
								count = 0;
								if (stringBuilder != null){
									if (stringBuilder.length() > 0) {
	//                                sendHandler(0, DataProcessUtil.hexStringToByte(stringBuilder.toString()));
										byte[] recvData = StringUtil.toBytes(stringBuilder.toString());
										final String result = new String(recvData, "UTF-8");
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												serial_tv_receive_data.setText(result);
											}
										});
										stringBuilder.setLength(0);
									}
							}
						}
					}


				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
//				e.printStackTrace();
				interrupt();
			} finally {
				if (mInputStream != null) {
					try {
						mInputStream.close();
					} catch (Exception e) {
					}
					mInputStream = null;
				}
			}
		}
	}

//		private ByteBuffer rcvBuffer = ByteBuffer.allocate(4096);
//		private class ReadThread extends Thread {
//			StringBuilder stringBuilder = new StringBuilder("");
//			int count = 0;
//
//			@Override
//			public void run() {
//				super.run();
//				try {
//
//					byte[] buffer = new byte[1024];
//					while (!this.isInterrupted()) {
//						SystemClock.sleep(100L);
//						if (mInputStream != null && mInputStream.available() > 0) {
//							int size = mInputStream.read(buffer);
//							if (size > 0) {
//								synchronized (rcvBuffer) {
//									if (rcvBuffer.hasRemaining()) {
//										Log.d("tagg", "Read thread hasRemaining!");
//										int count = rcvBuffer.remaining() < size ? rcvBuffer.remaining() : size;
//										rcvBuffer.put(buffer, 0, count);
//									}
//								}
//								Log.d("tagg","recv[" + StringUtil.toHexString(buffer) + "]");
////                            mHandler.sendEmptyMessage(0);
//								final byte[] buf = new byte[1024];
//								int count = get(buf);
//								if (count != 0) {
//									final String result = new String(buf, "UTF-8");
//									runOnUiThread(new Runnable() {
//
//										@Override
//										public void run() {
//											serial_tv_receive_data.setText(result);
//										}
//									});
//								}
//							}
//						}
//
////                    SystemClock.sleep(500L);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//					return;
//				} finally {
//					if (mInputStream != null) {
//						try {
//							mInputStream.close();
//						} catch (Exception e) {
//						}
//						mInputStream = null;
//					}
//				}
//			}
//		}
//
//		public int get(byte[] buf) {
//			int count = 0;
//			synchronized (this.rcvBuffer) {
//				this.rcvBuffer.flip();
//				if (this.rcvBuffer.hasRemaining()) {
//					count = this.rcvBuffer.remaining() < buf.length ? this.rcvBuffer.remaining() : buf.length;
//					this.rcvBuffer.get(buf, 0, count);
//				}
//
//				this.rcvBuffer.compact();
//				return count;
//			}
//		}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.serialport_test);

		serialspinner = (Spinner) findViewById(R.id.serial_dev);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		serialspinner.setAdapter(mAdapter);
//		serialList = getSerialPath();
//		for (int i = 0; i < serialList.size(); i++) {
//			mAdapter.add(serialList.get(i));
//		}

		if(SystemUtil.getInternalModel().equals("C1Pro")){
			mAdapter.add("/dev/ttyHS1");
			serialspinner.setClickable(false);
		}else if(SystemUtil.getInternalModel().equals("C1P")){
			mAdapter.add("/dev/ttyS0");
			serialspinner.setClickable(false);
		}else{
			serialList = getSerialPath();
			for (int i = 0; i < serialList.size(); i++) {
				mAdapter.add(serialList.get(i));
			}
		}

		serialspinner.setAdapter(mAdapter);



		mBaudAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item,mBaud_array);
		mBaudAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		baudspinner = (Spinner) findViewById(R.id.serial_baud);
		baudspinner.setAdapter(mBaudAdapter);

		serial_tv_receive_data = (TextView) findViewById(R.id.serial_tv_receive_data);
		DataSendEdit = (EditText) findViewById(R.id.DataSendEdit);
		DataSendEdit.setText("abcdefghijklmnopqrstuvwsyz1234567890");
		serial_bt_open = (Button) findViewById(R.id.serial_bt_open);
		DataSendBn = (Button) findViewById(R.id.DataSendBn);
		tv_serial= (TextView) findViewById(R.id.tv_serial);

		serialspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currenpath = mAdapter.getItem(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		baudspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currenBaud = mBaudAdapter.getItem(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		DataSendBn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				serial_tv_receive_data.setText("");
				String SendBuff = DataSendEdit.getText().toString();
				if(SendBuff == null || SendBuff.length() < 1)
					return;
				sendString(SendBuff);
			}
		});

		serial_bt_open.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(serial_bt_open.getText().toString().contains(getString(R.string.open_serial))){

					initSerial();
					serial_bt_open.setText(getString(R.string.close_serial));

				}else if(serial_bt_open.getText().toString().contains(getString(R.string.close_serial))){
					closeSerial();
					serial_bt_open.setText(getString(R.string.open_serial));
					tv_serial.setText("");
				}
			}
		});

	}
	
	private void initSerial(){
		try{
			mSerial = new Serial(currenpath, currenBaud, 0);
			mInputStream = mSerial.getInputStream();
			mOutputStream = mSerial.getOutputStream();
			tv_serial.setText(getString(R.string.serial_port) + ":"+currenpath+ " " + getString(R.string.serial_port) + ":"+currenBaud);
		}catch(Exception e){
			e.printStackTrace();
			if(mInputStream!=null){
				try {
					mInputStream.close();
					mInputStream=null;
					tv_serial.setText("open fail");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			return;
		}
		stringBuilder = new StringBuilder("");
		if(mReadThread == null){
			flag=true;
			mReadThread = new ReadThread();
			mReadThread.start();
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private void sendString(String string) {
		if (mOutputStream != null) {
			try {
				for (int i = 0; i < string.length(); i++) {
					mOutputStream.write(string.charAt(i));
				}

				Log.d("tagg","send[" + string + "]");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, getString(R.string.no_serial_device), Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		flag = false;
		try {
			if(mReadThread!= null){
				mReadThread.interrupt();
				mReadThread = null;
				flag=false;
			}
			if(mInputStream != null){
				mInputStream.close();
				mInputStream = null;
			}
			if(mOutputStream != null){
				mOutputStream.close();
				mOutputStream = null;
			}
			if(mSerial != null){
				mSerial.close();
				mSerial = null;
			}
			if(stringBuilder != null){
				stringBuilder = null;
			}
		} catch (IOException e) {
			
		}
	}
	
	public void closeSerial(){
		try {
			if(mReadThread != null){
				mReadThread.interrupt();
				mReadThread = null;
				flag=false;
			}

			if(mInputStream != null){
				mInputStream.close();
				mInputStream = null;
			}
			if(mOutputStream != null){
				mOutputStream.close();
				mOutputStream = null;
			}
			if(mSerial != null){
				mSerial.close();
				mSerial = null;
			}
			if(stringBuilder != null){
				stringBuilder = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getSerialPath() {
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
				Log.i("CommonUtil:getttySPath", lineStr);
//				if ((lineStr.contains("ttyS")) || (lineStr.contains("ttyMT"))|| (lineStr.contains("ttyUSB"))) {
//					ttysList.add("/dev/" + lineStr.trim());
//				}
				if (lineStr.contains("tty")) {
					if(lineStr.equals("tty")){

					}else{
						ttysList.add("/dev/" + lineStr.trim());
					}
				}
			}
			// 检查命令是否执行失败。
			if (p.waitFor() != 0 && p.exitValue() == 1) {
				// p.exitValue()==0表示正常结束，1：非正常结束
				Log.e("CommonUtil:getttySPath", "命令执行失败!");
			}
		} catch (Exception e) {
			Log.e("CommonUtil:getttySPath", e.toString());
			// return Environment.getExternalStorageDirectory().getPath();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
