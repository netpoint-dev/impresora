package com.common.demo.multireader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.common.CommonConstants;
import com.common.apiutil.multireader.MultiReader;
import com.common.apiutil.util.ReaderUtils;
import com.common.apiutil.util.StringUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.common.demo.databinding.ActivityMultireaderBinding;
import com.common.entity.CardTypeMsg;
import com.common.entity.IdentityMsg;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 多功能读卡器（三合一）
 */
public class MultiReaderActivity extends BaseActivity {

	private MultiReader mMultiReader;
	private EditText blocknum,password,newdata,newdata_addsub,data_write_wallet, apdudata;
	private TextView showResult;
	private ImageView headImg;
	private CheckBox loopCheckIDChb;
	private Button checkIDBtn;
	private boolean hasOpenReader;
	private boolean isUsb;

	//UI
	private Spinner walletOperationSpr;

	// WalletOperate
	private ArrayAdapter<String> mWalletOperateAdapter;
	private final HashMap<String, String> mWalletOperateHashMap = new HashMap<>();
	private int mWalletOperate = CommonConstants.WalletOperate.ADD;

	private boolean isCircleRead = false;
	private long successCount;
	private long totalCount;
	private Thread loopCheckIDThread;
	private IdentityMsg msg = null;

	private ActivityMultireaderBinding binding;

	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		binding = ActivityMultireaderBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		Log.d("tagg", "onCreate");
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("tagg", "onStart");

		mMultiReader = new MultiReader(this);
		blocknum = (EditText) findViewById(R.id.blocknum);
		password = (EditText) findViewById(R.id.password);
		newdata = (EditText) findViewById(R.id.newdata);
		newdata_addsub = (EditText) findViewById(R.id.newdata_addsub);
		showResult = (TextView) findViewById(R.id.showResult);
		headImg = (ImageView) findViewById(R.id.headImg);
		data_write_wallet = (EditText) findViewById(R.id.data_write_wallet);
//		apdudata = (EditText) findViewById(R.id.apdudata);
		loopCheckIDChb = findViewById(R.id.loopCheckIDChb);
		checkIDBtn = findViewById(R.id.checkIDBtn);

		walletOperationSpr = findViewById(R.id.walletOperationSpr);
		walletOperationSpr.setSelection(0);
		mWalletOperateAdapter = generateAdapterFromClass("com.common.CommonConstants$WalletOperate", mWalletOperateHashMap);
		walletOperationSpr.setAdapter(mWalletOperateAdapter);
		mWalletOperateAdapter.notifyDataSetChanged();
		walletOperationSpr.setOnItemSelectedListener(spinnerWalletOperationListener);

	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("tagg", "onStop");

		isCircleRead = false;
//		mMultiReader = null;
	}
	
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMultiReader.closeReader();
		Log.d("tagg", "onDestroy");
	}

	private AdapterView.OnItemSelectedListener spinnerWalletOperationListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			String name = mWalletOperateAdapter.getItem(position);
			String value = mWalletOperateHashMap.get(name);
			mWalletOperate = Integer.parseInt(value == null ? String.valueOf(CommonConstants.WalletOperate.ADD) : value);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	public void openReader(View view) {
		if(hasOpenReader){
			showResult.setText(getString(R.string.multireader_already_open_text));
		}else{
			boolean isOpenSuccess = false;
			isOpenSuccess = mMultiReader.openReader(MultiReaderActivity.this);
			Log.d("tagg", "isOpenSuccess["+isOpenSuccess+"]");

			if(isOpenSuccess){
				hasOpenReader = true;
				isUsb = true;
				showResult.setText(getString(R.string.multireader_open) + getString(R.string.multireader_success));
			}else{
				showResult.setText(getString(R.string.multireader_open) + getString(R.string.multireader_fail));
			}
		}
	}

	
	public void closeReader(View view){
		hasOpenReader = false;
		mMultiReader.closeReader();
	}

	public void checkSN(View view){
		showResult.setText(getString(R.string.multireader_get_sn) + " ["+mMultiReader.getIDSN() + "]");
	}
	
	public void checkPhyDDR(View view){
		showResult.setText(getString(R.string.multireader_get_phyddr) + " ["+StringUtil.toHexString(mMultiReader.getIDPhyAddr()) + "]");
	}

	
	public void checkVersion(View view){
		showResult.setText(getString(R.string.multireader_get_version) + " ["+mMultiReader.getVersion() + "]");
	}
	
	public void find(View view){
		if(mMultiReader.findIDCard()){
			showResult.setText(getString(R.string.multireader_find_id) + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_find_id) + getString(R.string.multireader_fail));
		}
	}
	
	public void select(View view){
		if(mMultiReader.selectIDCard()){
			showResult.setText(getString(R.string.multireader_select_id) + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_select_id) + getString(R.string.multireader_fail));
		}
	}
	
	public void read(View view){
		if(mMultiReader.readIDCard()!=null){
			showResult.setText(getString(R.string.multireader_read_id) + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_read_id) + getString(R.string.multireader_fail));
		}
	}

	public void checkID(View view){
		if(loopCheckIDChb.isChecked()){
			loopCheckID();
			return;
		}
		totalCount = 1;
		successCount = 0;
		checkIDcard(true);
		
	}

	private void loopCheckID(){
		isCircleRead = !isCircleRead;

		if(isCircleRead){
			totalCount = 0;
			successCount = 0;
			checkIDBtn.setText(getString(R.string.multireader_stop_check_id));
			loopCheckIDThread = new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					while(isCircleRead){
						totalCount++;
						checkIDcard(true);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

						}
					});
				}
			});
			loopCheckIDThread.start();
		}else{
			checkIDBtn.setText(getString(R.string.multireader_check_id));
			if(loopCheckIDThread != null){
				loopCheckIDThread.interrupt();
				loopCheckIDThread = null;
			}
		}



	}
	
	private void checkIDcard(boolean needFringerInfo){
		
		long startTime = System.currentTimeMillis();
		msg = mMultiReader.checkIDCard(needFringerInfo);
		final long endTime = System.currentTimeMillis() - startTime;
		
		if(msg!=null){
			successCount++;
			final String name = msg.getName();
			final String sex = msg.getSex();
			final String nation = msg.getNation();
			final String born = msg.getBorn();
			final String address = msg.getAddress();
			final String apartment = msg.getApartment();
			final String period = msg.getPeriod();
			final String no = msg.getNo();
			final StringBuffer finger = new StringBuffer();
			if(needFringerInfo){
				finger.append(ReaderUtils.get_finger_info(MultiReaderActivity.this, mMultiReader.getIDFinger(msg)));
			}
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
							born + "\n" + address + "\n" + apartment + "\n" + 
							period + "\n" + no + "\n" + finger.toString() + "\nTime:" + endTime + "\nSuccess["+successCount+"] Fail["+(totalCount - successCount)+"]");
					headImg.setImageBitmap(mMultiReader.decodeIDImage(mMultiReader.getIDImage(msg)));
				}
			});

		}else{
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					showResult.setText(getString(R.string.multireader_check_id) + getString(R.string.multireader_fail)  + "\nTime:" + endTime + "\nSuccess["+successCount+"] Fail["+(totalCount - successCount)+"]");
				}
			});
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				checkIDcard(true);
				handler.sendEmptyMessageDelayed(1, 500);
				break;

				default:
				break;
			}
		};
	};
	
	public void readTypeUID(View view){
		long startTime = System.currentTimeMillis();
		CardTypeMsg cardTypeMsg = mMultiReader.readUIDType(true);
		if(cardTypeMsg != null){
			if(!cardTypeMsg.getCardType().isEmpty()){
				showResult.setText("Type: " + cardTypeMsg.getCardType() + "\n" + "UID: "+cardTypeMsg.getCardUID() + "\n" +" Time["+(System.currentTimeMillis() - startTime)+"]");
			}else{
				showResult.setText("UID: "+ cardTypeMsg.getCardUID() + "\n" +" Time["+(System.currentTimeMillis() - startTime)+"]");
			}
		}

	}

	public void sendAPDU(View view){
		/*byte[] response = mMultiReader.sendAPDU(StringUtil.toBytes(apdudata.getText().toString()));
		if(response == null){
			showResult.setText(getString(R.string.multireader_send_apdu) + getString(R.string.multireader_fail));
		}else{
			showResult.setText(getString(R.string.multireader_send_apdu) + getString(R.string.multireader_success) + " :["+StringUtil.toHexString(response) + "]");
		}*/
		String apduCountStr = binding.editTextNfcAPDUCount.getText().toString();
		NfcApduTask nfcApduTask = new NfcApduTask(Integer.valueOf(apduCountStr));
		nfcApduTask.execute();
	}
	
	public void verifyTypeA(View view){
		if(mMultiReader.passwordCheckType(CommonConstants.PasswordType.KEYA,blocknum.getText().toString(),password.getText().toString())){
			showResult.setText(getString(R.string.multireader_verify_typea) + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_verify_typea) + getString(R.string.multireader_fail));
		}
	}
	
	public void verifyTypeB(View view){
		if(mMultiReader.passwordCheckType(CommonConstants.PasswordType.KEYB, blocknum.getText().toString(),password.getText().toString())){
			showResult.setText(getString(R.string.multireader_verify_typeb) + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_verify_typeb) + getString(R.string.multireader_fail));
		}
	}
	
	public void readTypeA(View view){
		showResult.setText(getString(R.string.multireader_read_block) + " ["+mMultiReader.readDataType() + "]");
	}
	
	public void writeTypeA(View view){
		if(mMultiReader.writeDataType(newdata.getText().toString())){
			showResult.setText(getString(R.string.multireader_write_block)  + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_write_block)  + getString(R.string.multireader_fail));
		}
	}

	public void add_sub(View view){
		String data = newdata_addsub.getText().toString();
		if(mMultiReader.addSubCommand(mWalletOperate, data)){
			showResult.setText(getString(R.string.multireader_wallet_operate)  + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_wallet_operate)  + getString(R.string.multireader_fail));
		}
	}

	public void readWalletCommand(View view){
		showResult.setText(getString(R.string.multireader_read_wallet) + " ["+mMultiReader.readWalletCommand() + "]");
	}

	public void writeWallet(View view){
		String data = data_write_wallet.getText().toString();
		if(mMultiReader.writeWalletCommand(data)){
			showResult.setText(getString(R.string.multireader_write_wallet)  + getString(R.string.multireader_success));
		}else{
			showResult.setText(getString(R.string.multireader_write_wallet)  + getString(R.string.multireader_fail));
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

	private class NfcApduTask extends AsyncTask<Void, byte[], Void> {

		private int count;
		private int apduAllCount = 0;
		private int apduSuccessCount = 0;
		private boolean isRunning = true; // 标记任务是否在运行

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
			String apduStr = binding.editTextNfcAPDU.getText().toString();
			byte[] pSendAPDU = StringUtil.hexStringToBytes(apduStr);
			byte[] response = null;
			apduAllCount++;

			response = mMultiReader.sendAPDU(pSendAPDU);
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
				binding.showResult.setText((TextUtils.isEmpty(StringUtil.toHexString(response)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(response)) + "\n");
				if (!TextUtils.isEmpty(StringUtil.toHexString(response))) {
					apduSuccessCount++;
				}
			} else {
				binding.showResult.setText(getString(R.string.send_APDU_fail) + "\n");
			}

			binding.showResult.append(getString(R.string.nfc_tv_apdu_count) + " [" + apduAllCount + "] " + getString(R.string.nfc_tv_apdu_success_count) + " [" + apduSuccessCount + "]");
		}

		public void stopSendingApdu() {
			isRunning = false;
		}
	}
}
