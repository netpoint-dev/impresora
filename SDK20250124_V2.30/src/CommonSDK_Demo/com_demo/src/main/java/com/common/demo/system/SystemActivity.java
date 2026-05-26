package com.common.demo.system;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.common.apiutil.system.SystemApiUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SystemActivity extends BaseActivity implements View.OnClickListener {

    private SystemApiUtil mSystemLib;
    private TextView tv_board;
    private EditText silence_install_path_edt;
    private EditText silence_install_package_edt;
    private TextView tvCpuTemperature;
    private TextView tvCpuUsage;
    private TextView tvMemoryUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());
//        judgeCpuType();
//        int childCount = mBinding.getRoot().getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = mBinding.getRoot().getChildAt(i);
//            if (childAt instanceof Button) {
//                childAt.setOnClickListener(this);
//            }
//        }
        setContentView(R.layout.activity_system);
        tv_board = findViewById(R.id.board);
        silence_install_path_edt = findViewById(R.id.silence_install_path_edt);
        silence_install_package_edt = findViewById(R.id.silence_install_package_edt);
        tvCpuTemperature = findViewById(R.id.tvCpuTemperature);
        tvCpuUsage = findViewById(R.id.tvCpuUsage);
        tvMemoryUsage = findViewById(R.id.tvMemoryUsage);
        judgeCpuType();
        mSystemLib = new SystemApiUtil(this);
        mSystemLib.registerWakeUpAppBroadcast();
//        mSystemLib.hideStatusBar();
//        mSystemLib.hideNavigationBar();
        tvCpuTemperature.setText(SystemUtil.getCpuTem() + "");
        tvCpuUsage.setText(SystemUtil.getCpuRate() + "");
        tvMemoryUsage.setText(SystemUtil.getMemRate() + "");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.show_status_bar) {
            mSystemLib.showStatusBar();
        } else if (id == R.id.hide_status_bar) {
            mSystemLib.hideStatusBar();
        } else if (id == R.id.show_navigation_bar) {
            mSystemLib.showNavigationBar();
        } else if (id == R.id.hide_navigation_bar) {
            mSystemLib.hideNavigationBar();
        } else if (id == R.id.set_system_time) {
            SPopupWindow takePhotoPopWin = new SPopupWindow(this,this);
            // 设置Popupwindow显示位置（从底部弹出）
            takePhotoPopWin.showAtLocation(findViewById(R.id.system_view), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
            //当弹出Popupwindow时，背景变半透明
            params[0].alpha=0.7f;
            getWindow().setAttributes(params[0]);
            //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
            takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params[0] = getWindow().getAttributes();
                    params[0].alpha=1f;
                    getWindow().setAttributes(params[0]);
                }
            });
            takePhotoPopWin.setOnCilckButton(new SPopupWindow.OnClickButton() {
                @Override
                public void OnClickButton(String s1,String s) {
                    Log.d("tt", "OnClickButton:----------- "+s1+"//---"+s);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(s);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long ts = date.getTime();
                    mSystemLib.setSystemTime(ts);
                }
            });
//            long currentTime = System.currentTimeMillis();
//            currentTime += 120;
//            mSystemLib.setSystemTime(currentTime);

        } else if (id == R.id.silence_install) {
            mSystemLib.installApp(silence_install_path_edt.getText().toString(), silence_install_package_edt.getText().toString());
        } else if (id == R.id.silence_uninstall) {
            mSystemLib.uninstallApp(silence_install_package_edt.getText().toString());
        } else if (id == R.id.reboot) {
            mSystemLib.rebootDevice();
        } else if (id == R.id.shutdown) {
            mSystemLib.shutdown();
        } else if (id == R.id.install_package) {
            mSystemLib.installPackage();
        }
    }

    /**
     * 判断机器所用的CPU类型
     */
    private void judgeCpuType() {
        String board = Build.BOARD;
        Log.e("Hello", "judgeCpuType == > " + board);
        board = board.toLowerCase().trim();
        if (board.contains("rk")) {
            tv_board.setText(getString(R.string.system_rk_platform));
        }
        if (board.contains("msm") || board.contains("sda")) {
            tv_board.setText(getString(R.string.system_qualcomm_platform));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSystemLib.unRegisterWakeUpAppBroadcast();
    }
}