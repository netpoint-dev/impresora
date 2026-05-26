package com.common.demo.idcard;

import com.common.apiutil.fingerprint.FingerPrint;
import com.common.demo.bean.BaseActivity;
import com.common.entity.IdentityMsg;
import com.common.apiutil.idcard.ReadCallBack;
import com.common.apiutil.idcard.T2OReader;
import com.common.apiutil.idcard.T2OReaderCallBack;
import com.common.apiutil.util.ReaderUtils;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TwoInOneReaderActivity extends BaseActivity {
	
	T2OReader t2oReader;
	EditText blocknum,password,newdata,newdata_addsub,data_write_wallet, apdudata;
	TextView showResult;
	ImageView headImg;
	boolean hasOpenReader;
	boolean isUsb;
	Button select_add_sub;
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.test_layout);
		Log.d("tagg", "onCreate");
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("tagg", "onStart");
		int deviceType = SystemUtil.getDeviceType();
		if(deviceType == StringUtil.DeviceModelEnum.TPS510.ordinal() ||
				deviceType == StringUtil.DeviceModelEnum.TPS510A.ordinal() ||
				deviceType == StringUtil.DeviceModelEnum.TPS510A_NHW.ordinal()){
			FingerPrint.idcardPower(1);
		}
		t2oReader = new T2OReader(this);
		blocknum = (EditText) findViewById(R.id.blocknum);
		password = (EditText) findViewById(R.id.password);
		newdata = (EditText) findViewById(R.id.newdata);
		newdata_addsub = (EditText) findViewById(R.id.newdata_addsub);
		showResult = (TextView) findViewById(R.id.showResult);
		headImg = (ImageView) findViewById(R.id.headImg);
		select_add_sub = (Button) findViewById(R.id.select_add_sub);
		data_write_wallet = (EditText) findViewById(R.id.data_write_wallet);
		apdudata = (EditText) findViewById(R.id.apdudata);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("tagg", "onStop");
		int deviceType = SystemUtil.getDeviceType();
		if(deviceType == StringUtil.DeviceModelEnum.TPS510.ordinal() ||
				deviceType == StringUtil.DeviceModelEnum.TPS510A.ordinal() ||
				deviceType == StringUtil.DeviceModelEnum.TPS510A_NHW.ordinal()){
			//FingerPrint.idcardPower(0);
		}
		isCircleRead = false;
		t2oReader.closeReader();
		t2oReader = null;
		System.exit(0);
	}
	
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("tagg", "onDestroy");
	}
	
	public void circleReadID(View view){
		findViewById(R.id.circleReadID).setEnabled(false);
		findViewById(R.id.circleReadIDnoFinger).setEnabled(false);
		new ReadIDTask().execute();
	}
	
	public void circleReadIDnoFinger(View view){
		findViewById(R.id.circleReadID).setEnabled(false);
		findViewById(R.id.circleReadIDnoFinger).setEnabled(false);
		new ReadIDnoFingerTask().execute();
	}
	
	class ReadIDTask extends AsyncTask<Void, Void, Void>{

		IdentityMsg msg;
		Bitmap bitmap;
		boolean hasReader = false;
		long startTime = 0;
		long endTime = 0;
		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startTime = System.currentTimeMillis();
			//if(!hasReader){
				try {
					hasReader = t2oReader.openReader(TwoInOneReaderActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			//}
			
		}

		
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			if(hasReader){
				try {
					msg = t2oReader.checkIDCard();
					endTime = System.currentTimeMillis();
					if(msg != null){
						bitmap = t2oReader.decodeIDImage(msg.getHead_photo());
						//bitmap = t2oReader.old32DecodeIDImage(msg.getHead_photo());
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				return null;
			}
			
			return null;
		}
		
		
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(msg != null){
				String name = msg.getName();
				String sex = msg.getSex();
				String nation = msg.getNation();
				String born = msg.getBorn();
				String address = msg.getAddress();
				String apartment = msg.getApartment();
				String period = msg.getPeriod();
				String no = msg.getNo();
				String finger = ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg));
				
				showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
												born + "\n" + address + "\n" + apartment + "\n" + 
												period + "\n" + no + "\n" + finger);
			}else{
				showResult.setText("read fail ...");
			}
			showResult.append("\n读卡时间["+(endTime - startTime)+"]");
			
			if(bitmap != null){
				headImg.setImageBitmap(bitmap);
			}
			
			t2oReader.closeReader();
			
			new ReadIDTask().execute();
			//findViewById(R.id.circleReadID).setEnabled(true);
		}
	}
	
	class ReadIDnoFingerTask extends AsyncTask<Void, Void, Void>{

		IdentityMsg msg;
		Bitmap bitmap;
		boolean hasReader = false;
		long startTime = 0;
		long endTime = 0;
		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startTime = System.currentTimeMillis();
			//if(!hasReader){
				try {
					hasReader = t2oReader.openReader(TwoInOneReaderActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			//}
			
		}

		
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			if(hasReader){
				try {
					msg = t2oReader.checkIDCard(false);
					endTime = System.currentTimeMillis();
					if(msg != null){
						bitmap = t2oReader.decodeIDImage(msg.getHead_photo());
						//bitmap = t2oReader.old32DecodeIDImage(msg.getHead_photo());
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				return null;
			}
			
			return null;
		}
		
		
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(msg != null){
				String name = msg.getName();
				String sex = msg.getSex();
				String nation = msg.getNation();
				String born = msg.getBorn();
				String address = msg.getAddress();
				String apartment = msg.getApartment();
				String period = msg.getPeriod();
				String no = msg.getNo();
				String finger = ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg));
				
				showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
												born + "\n" + address + "\n" + apartment + "\n" + 
												period + "\n" + no + "\n" + finger);
			}else{
				showResult.setText("read fail ...");
			}
			showResult.append("\n读卡时间["+(endTime - startTime)+"]");
			
			if(bitmap != null){
				headImg.setImageBitmap(bitmap);
			}
			
			t2oReader.closeReader();
			
			new ReadIDnoFingerTask().execute();
			//findViewById(R.id.circleReadID).setEnabled(true);
		}
	}
	
	public void openReaderUSB(View view){
		if(hasOpenReader){
			showResult.setText("请先关闭读卡器");
		}else{
			boolean isOpenSuccess = false;
			if(t2oReader.isUSBReader(TwoInOneReaderActivity.this)){
				isOpenSuccess = t2oReader.openReader(TwoInOneReaderActivity.this);
			}Log.d("T2OReader", "isOpenSuccess["+isOpenSuccess+"]");
			
			if(isOpenSuccess){
				hasOpenReader = true;
				isUsb = true;
				showResult.setText("打开读卡器成功");
			}else{
				showResult.setText("打开读卡器失败");
			}
		}
	}
	
	public void openReaderSerial(View view){
		if(hasOpenReader){
			showResult.setText("请先关闭读卡器");
		}else{
			boolean isOpenSuccess = false;
			isOpenSuccess = t2oReader.openReader();
			if(isOpenSuccess){
				hasOpenReader = true;
				isUsb = false;
				showResult.setText("打开读卡器成功");
			}else{
				showResult.setText("打开读卡器失败");
			}
		}
	}
	
	public void closeReader(View view){
		hasOpenReader = false;
		t2oReader.closeReader();
	}
	
	
	
	public void checkSN(View view){
		showResult.setText("模块SN:"+t2oReader.getIDSam());
	}
	
	public void checkPhyDDR(View view){
		showResult.setText("二代证物理地址:"+StringUtil.toHexString(t2oReader.getIDPhyAddr()));
	}
	
	
	public void ramRead(View view){
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					t2oReader.checkID_IC(new ReadCallBack() {
						
						
						public void checkIDfailed() {
							// TODO Auto-generated method stub
							Log.d("tagg", "check id failed");
						}
						
						
						public void checkIDSuccess(IdentityMsg msg) {
							// TODO Auto-generated method stub
							Log.d("tagg", "check id success + "+msg.getName());
						}
						
						
						public void checkICfailed() {
							// TODO Auto-generated method stub
							Log.d("tagg", "check ic failed");
						}
						
						
						public void checkICSuccess(String cardNum) {
							// TODO Auto-generated method stub
							Log.d("tagg", "check ic success + "+cardNum);
						}
					});
				}
			}
		}).start();
	}
	
	public void update(View view){
		if(hasOpenReader){
			showResult.setText("升级中...");
			new Thread(new Runnable() {
				
				
				public void run() {
					// TODO Auto-generated method stub
					if(isUsb){
						if(t2oReader.usbModuleUpdate(TwoInOneReaderActivity.this)){
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showResult.setText("升级成功");
								}
							});
						}else{
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showResult.setText("升级失败");
								}
							});
						}
					}else{
						Log.d("tagg", "serial update start");
						t2oReader.serialModuleUpdate(new T2OReaderCallBack() {
							
							
							public void updateComplete(final boolean isUpdateSuccess) {
								// TODO Auto-generated method stub
								runOnUiThread(new Runnable() {
									
									
									public void run() {
										// TODO Auto-generated method stub
										if(isUpdateSuccess){
											showResult.setText("升级成功");
										}else{
											showResult.setText("升级失败");
										}
									}
								});
							}
						});
					}
				}
			}).start();
		}else{
			showResult.setText("请先打开读卡器");
		}
	}
	
	public void checkVersion(View view){
		showResult.setText("模块版本:"+t2oReader.getVersion());
	}
	
	public void find(View view){
		if(t2oReader.findIDCard()){
			showResult.setText("寻卡成功");
		}else{
			showResult.setText("寻卡失败");
		}
	}
	
	public void select(View view){
		if(t2oReader.selectIDCard()){
			showResult.setText("选卡成功");
		}else{
			showResult.setText("选卡失败");
		}
	}
	
	public void read(View view){
		if(t2oReader.readIDCard()!=null){
			showResult.setText("读卡成功");
		}else{
			showResult.setText("读卡失败");
		}
	}
	
	IdentityMsg msg = null;   
	public void check(View view){
		totalCount = 1;
		successCount = 0;
		checkIDcard(true);
		
	}
	
	boolean isCircleRead = false;
	long successCount;
	long totalCount;
	public void checkWhile(View view){
		isCircleRead = true;
		totalCount = 0;
		successCount = 0;
		findViewById(R.id.checkWhile).setEnabled(false);
		new Thread(new Runnable() {
			
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
						// TODO Auto-generated method stub
						findViewById(R.id.checkWhile).setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	public void stopCheckWhile(View view){
		isCircleRead = false;
	}

	public void check32(View view){
		checkIDcard(false);
	}
	
	private void checkIDcard(boolean needFringerInfo){
		
		long startTime = System.currentTimeMillis();
		msg = t2oReader.checkIDCard(needFringerInfo);
		final long endTime = System.currentTimeMillis() - startTime;
		
		if(msg!=null){
			successCount++;
			final boolean isNewForeign = "Y".equals(msg.getCard_type());//外国人 新证
			final boolean isOldForeign = "I".equals(msg.getCard_type());//外国人 旧证
			final String name = msg.getName();
			final String sex = msg.getSex();
			final String nation = msg.getNation();
			final String country = msg.getCountry();
			final String cnName = msg.getCn_name();
			final String born = msg.getBorn();
			final String address = msg.getAddress();
			final String apartment = msg.getApartment();
			final String period = msg.getPeriod();
			final String no = msg.getNo();
			final String issueNum = msg.getIssuesNum();
			final String oldID = msg.getOldID();
			final StringBuffer finger = new StringBuffer();
			if(needFringerInfo){
				finger.append(ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg)));
			}
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					if(isNewForeign){
						showResult.setText("英文名:"+name + "\n中文名:" + cnName + "\n性别:" + sex + "\n国籍:" + country + "\n出生日期:" + born + "\n有效期限:" +
								period + "\n证件号码:" + no + "\n换证次数:" + issueNum + "\n" + (oldID==null?"":("既往版本证件号码:"+oldID)) + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
					}else if(isOldForeign){
						showResult.setText("英文名:"+name + "\n中文名:" + cnName + "\n性别:" + sex + "\n国籍:" + country + "\n出生日期:" + born + "\n有效期限:" +
								period + "\n证件号码:" + no + "\n签发机关:" + apartment + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
					}else{
						showResult.setText(name + "\n" + sex + "\n" + nation + "\n" +
								born + "\n" + address + "\n" + apartment + "\n" +
								period + "\n" + no + "\n" + finger.toString() + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
					}
					headImg.setImageBitmap(t2oReader.decodeIDImage(t2oReader.getIDImage(msg)));
				}
			});
			
			//headImg.setImageBitmap(t2oReader.old32DecodeIDImage(msg.getHead_photo()));
		}else{
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					showResult.setText("读取二代证失败" + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
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
	
	public void getIDFinger(View view){
		showResult.setText(ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg)));
	}
	
	public void typeAUID(View view){
		long startTime = System.currentTimeMillis();
		String uid = t2oReader.readUIDTypeA();
		showResult.setText("typeA uid:"+uid+" time["+(System.currentTimeMillis() - startTime)+"]");
	}

	public void typeBUID(View view){
		long startTime = System.currentTimeMillis();
		String uid = t2oReader.readUIDTypeA(T2OReader.KEYB);
		showResult.setText("typeB uid:"+uid+" time["+(System.currentTimeMillis() - startTime)+"]");
	}

	public void typeCPUUID(View view){
		long startTime = System.currentTimeMillis();
		String uid = t2oReader.readUIDTypeA(T2OReader.CPU);
		showResult.setText("CPU uid:"+uid+" time["+(System.currentTimeMillis() - startTime)+"]");
	}
	
	public void sendAPDU(View view){
		byte[] response = t2oReader.sendAPDU(StringUtil.toBytes(apdudata.getText().toString()));
		if(response == null){
			showResult.setText("发送apdu失败");
		}else{
			showResult.setText("发送apdu成功 receive:"+StringUtil.toHexString(response));
		}
	}
	
	public void verifyTypeA(View view){
		if(t2oReader.passwordCheckTypeA(blocknum.getText().toString(),password.getText().toString())){
			showResult.setText("keyA验证成功");
		}else{
			showResult.setText("keyA验证失败");
		}
	}
	
	public void verifyTypeB(View view){
		if(t2oReader.passwordCheckTypeA(blocknum.getText().toString(),password.getText().toString(),T2OReader.KEYB)){
			showResult.setText("keyB验证成功");
		}else{
			showResult.setText("keyB验证失败");
		}
	}
	
	public void readTypeA(View view){
		showResult.setText("区块数据:"+t2oReader.readDataTYPEA());
	}
	
	public void writeTypeA(View view){
		if(t2oReader.writeDataTypeA(newdata.getText().toString())){
			showResult.setText("写入成功");
		}else{
			showResult.setText("写入失败");
		}
	}

	boolean isAdd = true;
	public void select_add_sub(View view){
		isAdd = !isAdd;
		if(isAdd){
			select_add_sub.setText("add");
		}else{
			select_add_sub.setText("sub");
		}
	}

	public void add_sub(View view){
		String data = newdata_addsub.getText().toString();
		if(t2oReader.addSubCommand(isAdd?1:2, data)){
			showResult.setText("写入成功");
		}else{
			showResult.setText("写入失败");
		}
	}

	public void readWalletCommand(View view){
		showResult.setText("读电子钱包:"+t2oReader.readWalletCommand());
	}

	public void writeWallet(View view){
		String data = data_write_wallet.getText().toString();
		if(t2oReader.writeWalletCommand(data)){
			showResult.setText("写入成功");
		}else{
			showResult.setText("写入失败");
		}
	}
}
