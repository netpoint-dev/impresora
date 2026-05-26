package com.common.demo.lcd;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.demo.R;
import com.common.demo.databinding.ActivityLcdShowBinding;

public class LcdShowActivity extends Activity {
    boolean isRun = true;
    private ActivityLcdShowBinding binding;
    private int mTestCount = 0;

    private boolean isWhiteBalance = false;

    private final Thread mAutoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (isRun) {
                mTestCount = 1 + mTestCount;
                if (mTestCount > 5) {
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showClodr(mTestCount);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        hideBottomUIMenu();
        binding = ActivityLcdShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int color = intent.getIntExtra("color", -1);
        if(color == 4){
            isWhiteBalance = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isWhiteBalance){
            showClodr(4);
        }else {
            mTestCount = 0;
            Toast.makeText(this, getString(R.string.lcd_please_touch), Toast.LENGTH_SHORT).show();
            showClodr(mTestCount);
        }
//		mAutoThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

    private void showClodr(int showNum) {
        if (showNum > 4) return;
        int color = 0;
        switch (showNum) {
            case 0:
                color = getResources().getColor(R.color.lcd_black);
                break;
            case 1:
                color = getResources().getColor(R.color.lcd_red);
                break;
            case 2:
                color = getResources().getColor(R.color.lcd_green);
                break;
            case 3:
                color = getResources().getColor(R.color.lcd_blue);
                break;
            case 4:
                color = getResources().getColor(R.color.lcd_white);
                break;
            default:
                break;
        }

        binding.backgroundMain.setBackgroundColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!isWhiteBalance){
                mTestCount = 1 + mTestCount;
                if (mTestCount > 4) {
                    finish();
                }
                showClodr(mTestCount);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
