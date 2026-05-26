package com.common.demo.nfc;

import com.common.demo.R;
import com.common.apiutil.nfc.PN512;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.bean.BaseActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class NfcActivity extends BaseActivity {
	
	private TextView tv_show_nfc,successCountTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    String tempSuccessData;
    boolean firstData = false;
    int successCount = 0;
    
    private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				new readNFC().execute();
				break;
			default:
				break;
			}
		};
	};
    
	ImageView headBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_nfc);
		tv_show_nfc = (TextView) findViewById(R.id.tv_show_nfc);
		successCountTextView = (TextView) findViewById(R.id.successCount);
		//successCountTextView.setVisibility(View.VISIBLE);
		
		if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS573.ordinal() ){
			read = true;
			new readNFC().execute();
		}else{
			NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
	        mNfcAdapter = mNfcManager.getDefaultAdapter();
            if(Build.VERSION.SDK_INT > 30)
                mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
            else
                mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this,getClass()), 0);
//	        mPendingIntent =PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
	        init_NFC();
		}
		headBitmap = (ImageView) findViewById(R.id.headBitmap);
	}
	
	boolean read = false;
	long validCardNum = 0;
	private class readNFC extends AsyncTask<Void, Integer, Long>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		
		@Override
		protected Long doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return Long.parseLong(PN512.readnfc(), 16);
		}
		
		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==-1){
				tv_show_nfc.setText("卡号:");
			}else{
				validCardNum = result;
				tv_show_nfc.setText("卡号:"+result);
			}
			if(read){
				handler1.sendEmptyMessageDelayed(2, 100);
			}
			
		}
		
	}
	
    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            Log.d("tagg", this.getIntent().getAction() + "");
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
                processIntent(this.getIntent());
            }
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        processIntent(intent);
    }
    
    
    long getCardTime = 0;
    public void processIntent(Intent intent) {
        long startTime = System.currentTimeMillis();
//    	String time = "";
//    	if(getCardTime == 0){
//    	}else{
//    		time = "" + (System.currentTimeMillis() - getCardTime);
//    	}
//    	getCardTime = System.currentTimeMillis();
    	
    	String data = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        
        String[] techList = tag.getTechList();
        byte[] ID = new byte[20];
        data = tag.toString();
        ID =  tag.getId();
        data += "\n\nUID:\n" +bytesToHexString(ID);
        data += "\nData format:";
        for (String tech : techList) {
            data += "\n" + tech;
        }
        long endTime = System.currentTimeMillis();
        //tv_show_nfc.setText(data+"\n时间["+time+"]");
        tv_show_nfc.setText(data);
        if(!firstData){
        	firstData = !firstData;
        	tempSuccessData = data;
        	successCount++;
        }else if(data.equals(tempSuccessData)){
        	successCount++;
        }
        successCountTextView.setText("成功次数:"+successCount + "\n" + "读卡时间：" + (endTime - startTime) + " ms");
        
    }
    
    
    /**
    * 将字节数组转换为ImageView可调用的Bitmap对象
    * @param bytes
    * @param opts 转换属性设置
    * @return
    **/
     
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
         if (bytes != null)
 
        	 return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                
         return null;
 
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	read = false;
    }
    
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
    
    private void init_NFC() {
    	IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }
}
