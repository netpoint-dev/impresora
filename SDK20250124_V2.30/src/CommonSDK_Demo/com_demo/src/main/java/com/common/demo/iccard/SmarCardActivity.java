package com.common.demo.iccard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.common.apiutil.CommonException;
import com.common.apiutil.fingerprint.FingerPrint;
import com.common.apiutil.magnetic.MagneticCard;
import com.common.apiutil.reader.SmartCardReader;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

public class SmarCardActivity extends BaseActivity
{
	private SmartCardReader reader;
	private Button readButton, protocolbtn, buttonAPDU;
	private Button getFDButton;
	
	private Button poweronButton;
	private Button poweroffButton;
	private Button openButton;
	private Button closeButton;
	private EditText mEditTextApdu;
	private TextView textReader;
	private Spinner mPasmNumSpr;
	ProgressDialog dialog;
	int deviceType;
	
	Handler handler = new Handler()
    {

		
		public void handleMessage(Message msg)
		{
            switch (msg.what) {
			case 1:
				if(openButton.isEnabled())
					openButton.performClick();
				handler.sendEmptyMessageDelayed(2, 3000);
				break;
			case 2:
				if(poweronButton.isEnabled())
					poweronButton.performClick();
				handler.sendEmptyMessageDelayed(3, 1000);
				break;
			case 3:
				if(readButton.isEnabled())
					readButton.performClick();
				handler.sendEmptyMessageDelayed(4, 1000);
				break;
			case 4:
				if(protocolbtn.isEnabled())
					protocolbtn.performClick();
				handler.sendEmptyMessageDelayed(5, 1000);
				break;
			case 5:
				if(buttonAPDU.isEnabled())
					buttonAPDU.performClick();
				handler.sendEmptyMessageDelayed(6, 1000);
				break;
			case 6:
				if(poweroffButton.isEnabled())
					poweroffButton.performClick();
				handler.sendEmptyMessageDelayed(7, 1000);
				break;
			case 7:
				if(closeButton.isEnabled())
					closeButton.performClick();
				handler.sendEmptyMessageDelayed(1, 1000);
				break;

			default:
				break;
			}
		}
    };

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_ic_psam);
		
		deviceType = SystemUtil.getDeviceType();
		
		mEditTextApdu = (EditText) findViewById(R.id.editTextAPDU);
		textReader = (TextView) findViewById(R.id.textReader);
		mPasmNumSpr = (Spinner) findViewById(R.id.mPasmNumSpr);
		mPasmNumSpr.setVisibility(View.GONE);
		
		reader = new SmartCardReader(SmarCardActivity.this, SmartCardReader.SLOT_ICC);
		
		OnClickListener listener = new OnClickListener()
		{
			
			
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.open_btn:
					Log.d("tagg", "opentask");
					new openTask().execute();
					break;
					
				case R.id.close_btn:
					if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal()) {
						FingerPrint.fingericPower(0);
					}
					reader.close();
					closeButton.setEnabled(false);
					openButton.setEnabled(true);
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(false);
					readButton.setEnabled(false);
					protocolbtn.setEnabled(false);
					getFDButton.setEnabled(false);
					buttonAPDU.setEnabled(false);
					textReader.setText("");
					break;
					
				case R.id.read_btn:
					String atrString = null;
					atrString = reader.getATRString();
					textReader.setText("ATR:" + (TextUtils.isEmpty(atrString.trim()) ? "null" : atrString));
					Toast.makeText(SmarCardActivity.this, getString(R.string.get_data_success), Toast.LENGTH_SHORT).show();
					break;
					
				case R.id.poweron_btn:
					if (reader.powerOn())
					{
						poweronButton.setEnabled(false);
						poweroffButton.setEnabled(true);
						readButton.setEnabled(true);
						protocolbtn.setEnabled(true);
						getFDButton.setEnabled(true);
						buttonAPDU.setEnabled(true);
					}
					else
					{
						Toast.makeText(SmarCardActivity.this, "ICC power on failed", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case R.id.poweroff_btn:
					reader.powerOff();
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(true);
					readButton.setEnabled(false);
					protocolbtn.setEnabled(false);
					getFDButton.setEnabled(false);
					buttonAPDU.setEnabled(false);
					break;
				case R.id.protocol_btn:
					int proto;
					proto = reader.getProtocol();
					if (proto == SmartCardReader.PROTOCOL_T0) {
						textReader.setText("protocol: T0");
					} else if (proto == SmartCardReader.PROTOCOL_T1) {
						textReader.setText("protocol: T1");
					} else {
						textReader.setText("protocol: NA");
					}
					Toast.makeText(SmarCardActivity.this, getString(R.string.get_protocol_success), Toast.LENGTH_SHORT).show();

					break;
				case R.id.get_fd__btn:
					int fd = reader.getFD();
					textReader.setText(getString(R.string.psam_read_rate) + StringUtil.toHexString(new byte[]{(byte)fd}));
					break;
				default:
					break;
				}
			}
		};		
		
		readButton = (Button)findViewById(R.id.read_btn);
		readButton.setOnClickListener(listener);
		readButton.setEnabled(false);
		poweronButton = (Button)findViewById(R.id.poweron_btn);
		poweronButton.setOnClickListener(listener);
		poweronButton.setEnabled(false);
		poweroffButton = (Button)findViewById(R.id.poweroff_btn);
		poweroffButton.setOnClickListener(listener);
		poweroffButton.setEnabled(false);
		openButton = (Button)findViewById(R.id.open_btn);
		openButton.setOnClickListener(listener);
		closeButton = (Button)findViewById(R.id.close_btn);
		closeButton.setOnClickListener(listener);
		closeButton.setEnabled(false);
		protocolbtn = (Button) findViewById(R.id.protocol_btn);
		protocolbtn.setOnClickListener(listener);
		protocolbtn.setEnabled(false);
		buttonAPDU = (Button) findViewById(R.id.buttonAPDU);
		buttonAPDU.setEnabled(false);
		getFDButton = (Button)findViewById(R.id.get_fd__btn);
		getFDButton.setOnClickListener(listener);
		getFDButton.setEnabled(false);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(reader!=null){
			reader.close();
		}
    	onDestroy();
	}

	private class openTask extends AsyncTask<Void, Integer, Boolean>
	{
		String internalModel;

		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal() ||
					deviceType == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
				dialog = new ProgressDialog(SmarCardActivity.this);
				dialog.setMessage("please wait ...");
				dialog.setCancelable(false);
				dialog.show();
			}
			internalModel = SystemUtil.getInternalModel();
			if("C10P".equals(internalModel) || "C10Y".equals(internalModel) || "C10T".equals(internalModel)){
				openButton.setEnabled(false);
			}
		}
		
		
		protected Boolean doInBackground(Void... params)
		{
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal()) {
				FingerPrint.fingericPower(1);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return reader.open();
		}
		
		
		protected void onPostExecute(Boolean result)
		{
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal() ||
					deviceType == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
				dialog.dismiss();
			}
			if (result)
			{
				openButton.setEnabled(false);
				closeButton.setEnabled(true);
				poweronButton.setEnabled(true);
			}
			else 
			{
				Toast.makeText(SmarCardActivity.this, "Open reader failed", Toast.LENGTH_SHORT).show();
				if("C10P".equals(internalModel) || "C10Y".equals(internalModel) || "C10T".equals(internalModel)){
					openButton.setEnabled(true);
				}
			}
		}
	}
	

	public void sendAPDUkOnClick(View view) {
		final byte[] pSendAPDU;

		int[] pRevAPDULen = new int[1];
		String apduStr;
		pRevAPDULen[0] = 300;
		apduStr = mEditTextApdu.getText().toString();
		pSendAPDU = toByteArray(apduStr);
		new Thread(new Runnable() {
			@Override
			public void run() {
				final byte[] result;
				result = reader.transmit(pSendAPDU);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						textReader.setText(TextUtils.isEmpty(StringUtil.toHexString(result)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(result));
						if (!TextUtils.isEmpty(StringUtil.toHexString(result))) {
							Toast.makeText(SmarCardActivity.this, getString(R.string.send_comm_success), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}).start();


	}
	
	private String BCD2Str(byte[] data)
	{
		String string;
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < data.length; i++)
		{
			string = Integer.toHexString(data[i] & 0xFF);
			if (string.length() == 1)
			{
				stringBuilder.append("0");
			}
			
			stringBuilder.append(string.toUpperCase());
			stringBuilder.append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	private byte[] str2BCD(String string)
	{
		int len;
		String str;
		String hexStr = "0123456789ABCDEF";
		
		String s = string.toUpperCase();
		
		len = s.length();
		if ((len % 2) == 1)
		{
			// ���Ȳ�Ϊż�����Ҳ�0
			str = s + "0";
			len = (len + 1) >> 1;
		}
		else 
		{
			str = s;
			len >>= 1;
		}
		
		byte[] bytes = new byte[len];
		byte high;
		byte low;
		
		for (int i = 0, j = 0; i < len; i++, j += 2)
		{
			high = (byte)(hexStr.indexOf(str.charAt(j)) << 4);
			low = (byte)hexStr.indexOf(str.charAt(j + 1));
			bytes[i] = (byte)(high | low);
		}
		
		return bytes;
	}

	public static byte[] toByteArray(String hexString) {

		int hexStringLength = hexString.length();
		byte[] byteArray = null;
		int count = 0;
		char c;
		int i;

		// Count number of hex characters
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
				count++;
			}
		}

		byteArray = new byte[(count + 1) / 2];
		boolean first = true;
		int len = 0;
		int value;
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9') {
				value = c - '0';
			} else if (c >= 'A' && c <= 'F') {
				value = c - 'A' + 10;
			} else if (c >= 'a' && c <= 'f') {
				value = c - 'a' + 10;
			} else {
				value = -1;
			}

			if (value >= 0) {

				if (first) {

					byteArray[len] = (byte) (value << 4);

				} else {

					byteArray[len] |= value;
					len++;
				}

				first = !first;
			}
		}

		return byteArray;
	}
	
	ReadThread readThread;
	private class ReadThread extends Thread {
    	
    	String[] TracData = null;
    	
		
		public void run() {
			MagneticCard.startReading();
			while (!Thread.interrupted()) {
				try{
					TracData = MagneticCard.check(5000);
					runOnUiThread(new Runnable() {
						
						
						public void run() {
							// TODO Auto-generated method stub
							StringBuffer buffer = new StringBuffer();
							for(int i=0; i<3; i++){
			                    if(TracData[i] != null){
			                    	buffer.append(TracData[i]+";");
			                    }
			                }
							Toast.makeText(SmarCardActivity.this, buffer.toString(), Toast.LENGTH_LONG).show();
						}
					});
					int ret = MagneticCard.startReading();
				}catch (CommonException e){
					e.printStackTrace();
				}
			}
		}
    }
	
}
