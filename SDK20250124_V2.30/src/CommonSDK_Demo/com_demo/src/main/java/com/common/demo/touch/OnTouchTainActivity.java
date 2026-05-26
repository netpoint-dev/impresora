package com.common.demo.touch;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.apiutil.system.SystemApiUtil;
import com.common.demo.databinding.ActivityOntouchTianBinding;

/**
 * Created by ljl on 2018/1/29.
 */

public class OnTouchTainActivity extends Activity {

    private ActivityOntouchTianBinding binding;
    private SystemApiUtil systemApiUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        hideBottomUIMenu();
        binding = ActivityOntouchTianBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.touchView.setOnActiveAreaListener(new TouchView.onActiveAreaListener() {
            @Override
            public void onActiveFinish() {
                // 清除全屏标志
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除全屏标志
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}