package com.common.demo.nfc;

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
import android.widget.TextView;
import android.widget.Toast;

import com.common.apiutil.CommonException;
import com.common.apiutil.ResultCode;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.apiutil.nfc.Nfc;
import com.common.apiutil.util.StringUtil;
import com.common.demo.bean.BaseActivity;

public class NfcActivity_tps900 extends BaseActivity {
	private final String TAG = "NfcActivity_tps900";
	private Button open_btn = null;
	private Button close_btn = null;
	private Button check_btn = null;
	private EditText uid_editText = null;
	
	private Button getAtsBtn = null;
	private TextView textViewAtsData = null;
	private Button getUidBtn = null;
	private TextView textViewUidData = null;
	
	private EditText apud_editText = null;
	private EditText apud_count_editText = null;
	private Button sendApduBtn = null;
	private TextView textNfcReader = null;
	
	private Button authenticateBtn = null;
	private EditText blockEditText = null;
	private Button writeBlockBtn   = null;
	private Button readBlockBtn    = null;
	private TextView textViewBlockData = null;
	
	private EditText valueEditText = null;
	private Button writeValueBtn   = null;
	private Button readValueBtn    = null;
	private TextView textViewValueData = null;

	private Button transferBtn = null;
	private Button restoreBtn = null;
	
	private Button incBtn = null;
	private Button decBtn = null;
	private Button haltBtn = null;
	private Button removeBtn = null;
	private Button pressureTestBtn = null;

	Thread readThread;
	Thread readPressureThread;
	Handler handler;
	private final int CHECK_NFC_TIMEOUT = 1;
	private final int SHOW_NFC_DATA     = 2;
	private byte blockNum_1 = 1;
	private byte blockNum_2 = 2;
	private final byte B_CPU = 3;
	private final byte A_CPU = 1;
	private final byte A_M1  = 2;
	
	private final byte SRC_ADDR  = 2;
	private final byte DEST_ADDR = 2;
	
	Nfc nfc = new Nfc(this);
	OnClickListener listener;

	private int checkDataCount = 0;

	private static boolean isChecking = false;
	private static boolean isPressureTest = false;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.nfc_main);
        
        listener = new OnClickListener() {
        	
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.nfc_open_btn:
						try {
							nfc.open();
						} catch (CommonException e) {
							e.printStackTrace();
						}
						open_btn.setEnabled(false);
						check_btn.setEnabled(true);
						pressureTestBtn.setEnabled(false);
						break;
						
					case R.id.nfc_check_btn:
						if(!isChecking){
							readThread = new ReadThread();
							readThread.start();
							open_btn.setEnabled(false);
							check_btn.setEnabled(false);
							close_btn.setEnabled(true);
						}
						break;
					
					case R.id.getAtsBtn:
						getAtsData();
						break;
						
					case R.id.getUidBtn:
						getUidData();
						break;
						
					case R.id.buttonNfcAPDU:
//						sendAPDUData();
						String apduCountStr = apud_count_editText.getText().toString();
						NfcApduTask nfcApduTask = new NfcApduTask(Integer.valueOf(apduCountStr));
						nfcApduTask.execute();
						break;
						
					case R.id.authenticateBtn:
						m1CardAuthenticate();
						break;
						
					case R.id.writeBlockBtn:
						writeBlockData();
						break;
						
					case R.id.readBlockBtn:
						readBlockData();
						break;
					
					case R.id.writeValueBtn:
						writeValueData();
						break;
						
					case R.id.readValueBtn:
						readValueData();
						break;

					case R.id.transferBtn:
						transferData();
						break;

					case R.id.restoreBtn:
						restoreData();

					case R.id.incBtn:
						m1IncOperation();
						break;
						
					case R.id.decBtn:
						m1DecOperation();
						break;
						
					case R.id.haltBtn:
						m1HaltOperation();
						break;
						
					case R.id.removeBtn:
						m1RemoveOperation();
						break;
						
					case R.id.nfc_close_btn:
						isChecking = false;
						if(readThread != null){
							readThread.interrupt();
							readThread = null;
						}
						try {
							nfc.close();
						} catch (CommonException e) {
							e.printStackTrace();
						}
						open_btn.setEnabled(true);						
						check_btn.setEnabled(false);
						getAtsBtn.setEnabled(false);
						getUidBtn.setEnabled(false);
						sendApduBtn.setEnabled(false);
						authenticateBtn.setEnabled(false);
						writeBlockBtn.setEnabled(false);
						readBlockBtn.setEnabled(false);
						writeValueBtn.setEnabled(false);
						readValueBtn.setEnabled(false);
						transferBtn.setEnabled(false);
						restoreBtn.setEnabled(false);
						incBtn.setEnabled(false);
						decBtn.setEnabled(false);
						haltBtn.setEnabled(false);
						removeBtn.setEnabled(false);
						close_btn.setEnabled(false);
						pressureTestBtn.setEnabled(true);
						uid_editText.setText("");
						textNfcReader.setText("");
						textViewAtsData.setText("");
						textViewUidData.setText("");
						textViewBlockData.setText("");
						textViewValueData.setText("");
						break;

					case R.id.nfc_pressure_test_btn:
						isPressureTest = !isPressureTest;
						if(isPressureTest){
							pressureTestBtn.setText(getString(R.string.nfc_pressure_test_btn_stop));
							checkDataCount = 0;
							readPressureThread = new PressureThread();
							readPressureThread.start();
							open_btn.setEnabled(false);
						}else{
							pressureTestBtn.setText(getString(R.string.nfc_pressure_test_btn_start));

							if(readPressureThread != null){
								try {
									readPressureThread.join();
								} catch (InterruptedException e) {
									throw new RuntimeException(e);
								}
								readPressureThread.interrupt();

                                readPressureThread = null;
							}
							open_btn.setEnabled(true);

							try {
								nfc.close();
							} catch (CommonException e) {
								e.printStackTrace();
							}
						}

						break;
					default:
						break;
				}
			}
		};  
        
        open_btn  = (Button) findViewById(R.id.nfc_open_btn);
        open_btn.setOnClickListener(listener);
        close_btn = (Button) findViewById(R.id.nfc_close_btn);
        close_btn.setOnClickListener(listener);
        check_btn = (Button) findViewById(R.id.nfc_check_btn);
        check_btn.setOnClickListener(listener);
        uid_editText  = (EditText) findViewById(R.id.nfc_uid_data);
        
        getAtsBtn = (Button) findViewById(R.id.getAtsBtn);
        getAtsBtn.setOnClickListener(listener);
        textViewAtsData = (TextView) findViewById(R.id.textViewAtsData);
        getUidBtn = (Button) findViewById(R.id.getUidBtn);
        getUidBtn.setOnClickListener(listener);
        textViewUidData = (TextView) findViewById(R.id.textViewUidData);
		apud_count_editText = (EditText)findViewById(R.id.editTextNfcAPDUCount);
        apud_editText = (EditText)findViewById(R.id.editTextNfcAPDU);
        sendApduBtn   = (Button)findViewById(R.id.buttonNfcAPDU);
        sendApduBtn.setOnClickListener(listener);
        textNfcReader = (TextView)findViewById(R.id.textNfcReader);
        
        authenticateBtn = (Button) findViewById(R.id.authenticateBtn);
        authenticateBtn.setOnClickListener(listener);
        blockEditText = (EditText) findViewById(R.id.editTexWriteBlock);
        writeBlockBtn = (Button) findViewById(R.id.writeBlockBtn);
        writeBlockBtn.setOnClickListener(listener);
        readBlockBtn  = (Button) findViewById(R.id.readBlockBtn);
        readBlockBtn.setOnClickListener(listener);
        textViewBlockData = (TextView) findViewById(R.id.textViewBlockData);

		transferBtn = findViewById(R.id.transferBtn);
		restoreBtn = findViewById(R.id.restoreBtn);
		transferBtn.setOnClickListener(listener);
		restoreBtn.setOnClickListener(listener);
        
        valueEditText = (EditText) findViewById(R.id.editTexWriteValue);
        writeValueBtn = (Button) findViewById(R.id.writeValueBtn);
        writeValueBtn.setOnClickListener(listener);
        readValueBtn  = (Button) findViewById(R.id.readValueBtn);
        readValueBtn.setOnClickListener(listener);
        textViewValueData = (TextView) findViewById(R.id.textViewValueData);
        
        incBtn        = (Button) findViewById(R.id.incBtn);
        incBtn.setOnClickListener(listener);
        decBtn        = (Button) findViewById(R.id.decBtn);
        decBtn.setOnClickListener(listener);
        haltBtn       = (Button) findViewById(R.id.haltBtn);
        haltBtn.setOnClickListener(listener);
        removeBtn     = (Button) findViewById(R.id.removeBtn);
        removeBtn.setOnClickListener(listener);

		pressureTestBtn = (Button) findViewById(R.id.nfc_pressure_test_btn);
		pressureTestBtn.setOnClickListener(listener);
        
        open_btn.setEnabled(true);
		check_btn.setEnabled(false);
		getAtsBtn.setEnabled(false);
		getUidBtn.setEnabled(false);
		sendApduBtn.setEnabled(false);
		authenticateBtn.setEnabled(false);
		writeBlockBtn.setEnabled(false);
		readBlockBtn.setEnabled(false);
		writeValueBtn.setEnabled(false);
		readValueBtn.setEnabled(false);
		transferBtn.setEnabled(false);
		restoreBtn.setEnabled(false);
		incBtn.setEnabled(false);
		decBtn.setEnabled(false);
		haltBtn.setEnabled(false);
		removeBtn.setEnabled(false);
		close_btn.setEnabled(false);

		NFCBoundingBoxView boundingBox = (NFCBoundingBoxView) findViewById(R.id.boundingBox);
		boundingBox.setVisibility(View.GONE);
		if(SystemUtil.getInternalModel().equals("H2T")){
			boundingBox.setVisibility(View.VISIBLE);

			Toast.makeText(this, getString(R.string.nfc_area_text), Toast.LENGTH_SHORT).show();
		}
		
        handler = new Handler() {
			
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case CHECK_NFC_TIMEOUT: {
						Toast.makeText(NfcActivity_tps900.this, "未检测到卡片超时 !", Toast.LENGTH_LONG).show();
						open_btn.setEnabled(true);
						close_btn.setEnabled(false);
						check_btn.setEnabled(false);
					}break;
					case SHOW_NFC_DATA:{
						byte[] uid_data = (byte[]) msg.obj;
						
						Log.d("tagg", "nfcdata["+StringUtil.toHexString(uid_data)+"]");

						if (uid_data[0] == 0x42) {
							// TYPE B类（暂时只支持cpu卡）
							byte[] atqb = new byte[uid_data[16]];
							byte[] pupi = new byte[4];
							String type = null;
							
							System.arraycopy(uid_data, 17, atqb, 0, uid_data[16]);
							System.arraycopy(uid_data, 29, pupi, 0, 4);
							
							if (uid_data[1] == B_CPU) {
								type = "CPU";
								if(!isPressureTest) {
									sendApduBtn.setEnabled(true);
									getAtsBtn.setEnabled(true);
									getUidBtn.setEnabled(true);
								}
							} else {
								type = "unknow";
							}
							
							uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
									"\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) + 
									"\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi));
							
						} else if (uid_data[0] == 0x41) {
							// TYPE A绫伙紙CPU, M1锛�
							byte[] atqa = new byte[2];
							byte[] sak = new byte[1];
							byte[] uid = new byte[uid_data[5]];
							String type = null;
							
							System.arraycopy(uid_data, 2, atqa, 0, 2);//[1]~[2]
							System.arraycopy(uid_data, 4, sak, 0, 1);//[3]
							System.arraycopy(uid_data, 6, uid, 0, uid_data[5]);
							
							if (uid_data[1] == A_CPU) {
								type = "CPU";
								if(!isPressureTest) {
									sendApduBtn.setEnabled(true);
									getAtsBtn.setEnabled(true);
									getUidBtn.setEnabled(true);
								}
							} else if (uid_data[1] == A_M1) {
								type = "M1";
								if(!isPressureTest) {
									authenticateBtn.setEnabled(true);
								}
							} else {
								type = "unknow";
							}
							
							uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
									"\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
									"\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
									"\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid));
						} else if(uid_data[0] == 0x46){
							// F卡
							Log.d("tagg", "Felica uid_data:"+StringUtil.toHexString(uid_data));
							byte[] idm = new byte[8];
							byte[] pmm = new byte[8];
							System.arraycopy(uid_data, 33, idm, 0, 8);
							System.arraycopy(uid_data, 41, pmm, 0, 8);
							Log.d("tagg", "Felica idm:"+StringUtil.toHexString(idm));
							Log.d("tagg", "Felica pmm:"+StringUtil.toHexString(pmm));
							String type = "Felica card";
							uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_c) + " " + type +
									"\r\n" + "idm:" + StringUtil.toHexString(idm) +
									"\r\n" + "pmm:" + StringUtil.toHexString(pmm));
							
						}else {
							Log.e(TAG, "unknow type card!!");
						}
						if(isPressureTest) {
							uid_editText.append("\r\n" + getString(R.string.checked_count) + checkDataCount);
						}
					}break;
					
					default:break;
				}
			}	
        };
    }


    protected void onDestroy() {
    	try {
    		nfc.close();
		} catch (CommonException e) {
			e.printStackTrace();
		}
    	super.onDestroy();
    }
    
    private class ReadThread extends Thread {
    	byte[] nfcData = null;


		public void run() {
			isChecking = true;
			while (isChecking){
				try {
					nfcData = nfc.activate(10 * 1000); // 10s
					if (null != nfcData) {
						handler.sendMessage(handler.obtainMessage(SHOW_NFC_DATA, nfcData));
						isChecking = false;
						break;
					} else {
						Log.d(TAG, "Check MagneticCard timeout...");
						handler.sendMessage(handler.obtainMessage(CHECK_NFC_TIMEOUT, null));
					}
				} catch (CommonException e) {
					e.printStackTrace();
				}
			}
		}
    }

	private class PressureThread extends Thread {
		byte[] nfcData = null;

		public void run() {
			while (isPressureTest){
                try {
					nfc.open();

					nfcData = nfc.activate(10 * 1000); // 10s
					if (null != nfcData) {
						checkDataCount++;
						handler.sendMessage(handler.obtainMessage(SHOW_NFC_DATA, nfcData));
					} else {
						Log.d(TAG, "Check MagneticCard timeout...");
						handler.sendMessage(handler.obtainMessage(CHECK_NFC_TIMEOUT, null));
					}

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
						interrupt();
                    }
                    nfc.close();
				} catch (CommonException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    public void sendAPDUData() {
		byte[] pSendAPDU;
		byte[] result = null;
		String apduStr;
		int iRet = 0;
		int length = 0;
		apduStr = apud_editText.getText().toString();
		pSendAPDU = toByteArray(apduStr);
		length = pSendAPDU.length;
		try {
			result = nfc.transmit(pSendAPDU, length);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		textNfcReader.setText(TextUtils.isEmpty(StringUtil.toHexString(result)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(result));
		if (!TextUtils.isEmpty(StringUtil.toHexString(result))) {
			Toast.makeText(NfcActivity_tps900.this,
					getString(R.string.send_comm_success), Toast.LENGTH_SHORT).show();
		}
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
    	
	public void writeBlockData() {
		byte[] blockData = null;
		String blockStr;
		Boolean status = true;
		
		Log.d(TAG, "writeBlockData...");
		blockStr = blockEditText.getText().toString();
		blockData = toByteArray(blockStr);
		
		try {
			nfc.m1_write_block(blockNum_1, blockData, blockData.length);
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			Log.d(TAG, "writeBlockData success!");
			Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
		} else {
			Log.e(TAG, "writeBlockData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void readBlockData() {
		byte[] data = null;
		try {
			data = nfc.m1_read_block(blockNum_1);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		if (data == null) {
			Log.e(TAG, "readBlockBtn fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		} else {
			textViewBlockData.setText(StringUtil.toHexString(data));
		}
	}
	
	public void writeValueData() {
		byte[] valueData = null;
		String valueStr;
		boolean status = true;
		
		Log.d(TAG, "writeValueBtn...");
		valueStr = valueEditText.getText().toString();
		valueData = toByteArray(valueStr);
		
		try {
			nfc.m1_write_value(blockNum_2, /*valueData, valueData.length*/Integer.valueOf(valueStr));
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			Log.d(TAG, "writeValueData success!");
			Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
		} else {
			Log.e(TAG, "writeValueData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}	
	}
	
	public void readValueData() {
		byte[] data = null;
		try {
			data = nfc.m1_read_value(blockNum_2);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		
		if (null == data) {
			Log.e(TAG, "readValueBtn fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		} else {
			textViewValueData.setText(StringUtil.toHexString(data));
		}
	}


	public void transferData() {
		int result = -1;
		try {
			result = nfc.m1_transfer(blockNum_2);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		if (result == ResultCode.FAIL) {
			Log.e(TAG, "transferData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
		}
	}

	public void restoreData() {
		int result = -1;
		try {
			result = nfc.m1_restore(blockNum_2);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		if (result == ResultCode.FAIL) {
			Log.e(TAG, "restoreData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
			transferBtn.setEnabled(true);
		}
	}

	public void getAtsData() {
		byte[] data = null;
		Boolean status = true;
		try {
			data = nfc.cpu_get_ats();
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			if (data == null) {
				textViewAtsData.setText("null");
			} else  {
				textViewAtsData.setText(StringUtil.toHexString(data));
			}
		} else {
			Log.e(TAG, "getAtsData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void getUidData() {
		byte[] data = null;
		Boolean status = true;
		try {
			data = nfc.read_idcard(5000);
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			if (data == null) {
				textViewUidData.setText("null");
			} else  {
				textViewUidData.setText(StringUtil.toHexString(data));
			}
		} else {
			Log.e(TAG, "getUidData fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void m1CardAuthenticate() {
		Boolean status = true;		
		byte[] passwd={(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};
		try {
			nfc.m1_authenticate(blockNum_1, (byte)0x0A, passwd);
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			Log.d(TAG, "m1CardAuthenticate success!");
			authenticateBtn.setEnabled(false);
			writeBlockBtn.setEnabled(true);
			readBlockBtn.setEnabled(true);
			writeValueBtn.setEnabled(true);
			readValueBtn.setEnabled(true);
			incBtn.setEnabled(true);
			decBtn.setEnabled(true);
			restoreBtn.setEnabled(true);
		} else {
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
			Log.d(TAG, "m1CardAuthenticate fail!");
		}
	}
	
	public void m1IncOperation() {
		Boolean status = true;
		byte[] data = new byte[4];
		data[0] = 0x01;
		data[1] = 0x00;
		data[2] = 0x00;
		data[3] = 0x00;
		
		try {
			nfc.m1_increment(SRC_ADDR, DEST_ADDR, /*data, 4*/Integer.valueOf(StringUtil.toHexString(data)));
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();	
		}
		
		if (status) {
			Log.d(TAG, "m1IncOperation success!");
			Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
			Log.d(TAG, "m1IncOperation fail!");
		}
	}
	
	public void m1DecOperation() {
		Boolean status = true;
		byte[] data = new byte[4];
		data[0] = 0x01;
		data[1] = 0x00;
		data[2] = 0x00;
		data[3] = 0x00;
		
		try {
			nfc.m1_decrement(SRC_ADDR, DEST_ADDR, /*data, 4*/Integer.valueOf(StringUtil.toHexString(data)));
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			Log.d(TAG, "m1DecOperation success!");
			Toast.makeText(this, getString(R.string.open_success), Toast.LENGTH_SHORT).show();
		} else {
			Log.d(TAG, "m1DecOperation fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void m1HaltOperation() {
		Boolean status = true;
		
		try {
			nfc.halt();
		} catch (CommonException e) {
			status = false;
			e.printStackTrace();
		}
		
		if (status) {
			Log.d(TAG, "m1HaltOperation success!");
			Toast.makeText(this, getString(R.string.open_success), Toast.LENGTH_SHORT).show();
		} else {
			Log.d(TAG, "m1HaltOperation fail!");
			Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void m1RemoveOperation() {
	}

	private class NfcApduTask extends AsyncTask<Void, byte[], Void> {

		private int count;
		private int apduAllCount = 0;
		private int apduSuccessCount = 0;
		private boolean isRunning = true; // 标记任务是否在运行
		private long time;

		public NfcApduTask(int count) {
			this.count = count;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			while (isRunning && count > 0) {
				sendApdu();
				count--;
				try {
					Thread.sleep(200); // 延时1秒后发送下一条命令
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private void sendApdu() {
			String apduStr = apud_editText.getText().toString();
			byte[] pSendAPDU = StringUtil.hexStringToBytes(apduStr);
			byte[] response = null;
			apduAllCount++;

			int length = pSendAPDU.length;
			try {
				response = nfc.transmit(pSendAPDU, length);
			} catch (CommonException e) {
				e.printStackTrace();
			}
			publishProgress(response);
		}

		@Override
		protected void onProgressUpdate(byte[]... responses) {
			super.onProgressUpdate(responses);
			byte[] response = responses[0];
			updateUi(response);
		}

		private void updateUi(byte[] response) {
			if (response != null) {
				textNfcReader.setText((TextUtils.isEmpty(StringUtil.toHexString(response)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(response)) + "\n");
				if (!TextUtils.isEmpty(StringUtil.toHexString(response))) {
					apduSuccessCount++;
				}
			} else {
				textNfcReader.setText(getString(R.string.send_APDU_fail) + "\n");
			}

			textNfcReader.append(getString(R.string.nfc_tv_apdu_count) + " [" + apduAllCount + "] " + getString(R.string.nfc_tv_apdu_success_count) + " [" + apduSuccessCount + "]");
		}

		public void stopSendingApdu() {
			isRunning = false;
		}
	}
}
