package com.common.demo.iccard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

public class IccActivityNew extends BaseActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.iccard_main_new);
		
		
		OnClickListener listener = new OnClickListener() {

			
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.sle4442_btn:
					Intent intent4442 = new Intent(IccActivityNew.this, SLE4442Activity.class);
					startActivity(intent4442);

					break;

				case R.id.sle4428_btn:
					Intent intent4428 = new Intent(IccActivityNew.this, SLE4428Activity.class);
					startActivity(intent4428);

					break;

				case R.id.monitor_btn:
//					Intent intent = new Intent(IccActivityNew.this, MonitorActivity.class);
//					startActivity(intent);
					break;
				case R.id.smartcard:
					Intent smartcardintent = new Intent(IccActivityNew.this, SmarCardActivity.class);
					startActivity(smartcardintent);
					break;
					
				case R.id.at88sc153_btn:
//					Intent intentAT88SC153 = new Intent(IccActivityNew.this, AT88SC153Activity.class);
//					startActivity(intentAT88SC153);
					break;
					
				case R.id.typea_btn:
					AlertDialog.Builder idcard_dialog = new AlertDialog.Builder(IccActivityNew.this);
					idcard_dialog.setTitle(getString(R.string.idcard_xzgn));
					idcard_dialog.setMessage("typeA卡读取");
					idcard_dialog.setNegativeButton("串口读取", new DialogInterface.OnClickListener() {
						
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//							startActivity(new Intent(IccActivityNew.this, SerialTACardActivity/*UsbTACardActivity*/.class));
						}
					});
					idcard_dialog.setPositiveButton("USB读取", new DialogInterface.OnClickListener() {
						
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//							startActivity(new Intent(IccActivityNew.this, /*UsbTACardActivity*/USBTypeACardActivity.class));
						}
					});
					idcard_dialog.show();
					break;
				}
			}
		};
		Button sle4442_btn = (Button) findViewById(R.id.sle4442_btn);
		sle4442_btn.setOnClickListener(listener);

		Button sle4428_btn = (Button) findViewById(R.id.sle4428_btn);
		sle4428_btn.setOnClickListener(listener);

		Button monitor_btn = (Button) findViewById(R.id.monitor_btn);
		monitor_btn.setOnClickListener(listener);

		Button smart_btn = (Button) findViewById(R.id.smartcard);
		smart_btn.setOnClickListener(listener);
		
		Button at88sc153_btn = (Button) findViewById(R.id.at88sc153_btn);
		at88sc153_btn.setOnClickListener(listener);
		
		Button typea_btn = (Button) findViewById(R.id.typea_btn);
		typea_btn.setOnClickListener(listener);
		
		monitor_btn.setEnabled(false);
		int deviceType = SystemUtil.getDeviceType();
		if(deviceType == StringUtil.DeviceModelEnum.TPS390P.ordinal()
				|| deviceType == StringUtil.DeviceModelEnum.TPS390L.ordinal()
				|| deviceType == StringUtil.DeviceModelEnum.TPS900MB.ordinal()
				|| deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()){
			sle4428_btn.setEnabled(false);
			sle4442_btn.setEnabled(false);		
		}
//		
//		if(deviceType == StringUtil.DeviceModelEnum.TPS900.ordinal()
//				|| deviceType == StringUtil.DeviceModelEnum.TPS320.ordinal()){
//			sle4442_btn.setEnabled(false);		
//		}

	}
	
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
