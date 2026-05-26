package com.common.demo.lcd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.common.demo.R;
import com.common.demo.databinding.ActivityLcdBinding;


public class LcdActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "LCD";
	private int mDefaultBrightness=10;//系统默认亮度
	private int mBrightness=0;
	private ActivityLcdBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		binding = ActivityLcdBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.brightnessSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				mBrightness=seekBar.getProgress();
				if(mBrightness<=10){
					mBrightness=10;
				}
				setBrightness(LcdActivity.this, mBrightness);

			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}
		});
		initBrightness();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		setBrightness(this, mDefaultBrightness);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.lcdBtn:
				Intent intent = new Intent();
				intent.setClass(this, LcdShowActivity.class);
				startActivity(intent);
			break;
			case R.id.lcdWhiteBalanceBtn:
				intent = new Intent();
				intent.putExtra("color", 4);
				intent.setClass(this, LcdShowActivity.class);
				startActivity(intent);
				break;
		default:
			break;

		}
	}

	private void initBrightness(){
		mDefaultBrightness = getScreenBrightness(this);
		binding.brightnessSeekbar.setProgress(mDefaultBrightness);
		setBrightness(this, mDefaultBrightness);
	}

	/**
	 * 改变当前应用界面亮度方式
	 * */
	private static void setBrightness(Activity activity, int brightness) {
		Log.d(TAG, "setBrightness: " + brightness);

		if(brightness<=0){
			brightness=1;
		}
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
//	     lp.alpha = Float.valueOf(brightness) * (1f / 255f);
		activity.getWindow().setAttributes(lp);
	}

	/**
	 * 改变系统亮度方式
	 * */
	private void saveBrightness(Activity activity, int brightness) {
		if(brightness==0){
			brightness=1;
		}
		Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
		ContentResolver resolver = activity.getContentResolver();
		Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
		resolver.notifyChange(uri, null);
	}

	/**
	 * 获取屏幕亮度
	 */
	private static int getScreenBrightness(Activity activity) {
		int nowBrightnessValue = 0;
		ContentResolver resolver = activity.getContentResolver();
		try{
			nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}
}
