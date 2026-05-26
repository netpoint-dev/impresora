package com.common.demo.system;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.demo.R;

import java.text.SimpleDateFormat;

public class SPopupWindow extends PopupWindow {

    private OnClickButton onClickButton;
    private final NumberPicker day;
    private final NumberPicker hour;
    private final NumberPicker minte;

    private final TextView quxiao;
    private final TextView queren;
    private View view;
    public String Stiem;
    private final String[] days;
    private final String[] hours;
    private final String[] minits;
    private final Integer iyear;
    private final Integer imonth;
    private final Integer iday;
    private final Integer ihour;
    private final Integer iminte;

    public SPopupWindow(Context context, View.OnClickListener clickListener){

        this.view = LayoutInflater.from(context).inflate(R.layout.popupwindow,null);
        day = (NumberPicker) view.findViewById(R.id.day);
        hour = (NumberPicker) view.findViewById(R.id.hour);
        minte = (NumberPicker) view.findViewById(R.id.minte);
        quxiao = (TextView) view.findViewById(R.id.quxiao);

        this.setOutsideTouchable(true);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dismiss();
            }
        });
        queren = (TextView) view.findViewById(R.id.queren);
       queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 时间自动添加十分钟 上传至后台的时间格式 time
                 */
                int dayValue = day.getValue();
                int hourValue = hour.getValue();
                int minteValue = minte.getValue();
                String time = null;

                switch (minteValue){
                    case 0:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+1)+":"+"00"+":"+"00";
                        break;
                    case 1:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+1)+":"+"10"+":"+"00";
                        break;
                    case 2:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+1)+":"+"20"+":"+"00";
                        break;
                    case 3:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+1)+":"+"30"+":"+"00";
                        break;
                    case 4:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+1)+":"+"40"+":"+"00";
                        break;
                    case 5:
                        time =iyear+"-"+imonth+"-"+(iday+dayValue)+" "+(hourValue+2)+":"+"50"+":"+"00";
                        break;
                }
                /**
                 * 时间自动添加十分钟 显示在发单界面得时间 time1
                 */
                String time1 =null;
                if (minteValue==5){
                    time1= ""+days[dayValue]+" "+hours[hourValue+1]+" "+minits[0];
                }else {
                    switch (minteValue){
                        case 0:
                            time1 = ""+days[dayValue]+" "+hours[hourValue]+" "+minits[minteValue];
                            break;
                        case 1:
                            time1 = ""+days[dayValue]+" "+hours[hourValue]+" "+minits[minteValue];
                            break;
                        case 2:
                            time1 = ""+days[dayValue]+" "+hours[hourValue]+" "+minits[minteValue];
                            break;
                        case 3:
                            time1 = ""+days[dayValue]+" "+hours[hourValue]+" "+minits[minteValue];
                            break;
                        case 4:
                            time1 = ""+days[dayValue]+" "+hours[hourValue]+" "+minits[minteValue];
                            break;
                    }
                }

        if (onClickButton!=null){
            onClickButton.OnClickButton(time1,time);
        }
        dismiss();
    }
});

        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

           /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);


        long t = System.currentTimeMillis();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat df2 = new SimpleDateFormat("MM");
        SimpleDateFormat df3 = new SimpleDateFormat("dd");
        SimpleDateFormat df4 = new SimpleDateFormat("HH");
        SimpleDateFormat df5 = new SimpleDateFormat("mm");
        //年
        iyear = Integer.valueOf(df1.format(t));
        //月
        imonth = Integer.valueOf(df2.format(t));
        // 日
        iday = Integer.valueOf(df3.format(t));
        // 时
        ihour = Integer.valueOf(df4.format(t));
        //分
        iminte = Integer.valueOf(df5.format(t));
        Log.d("tag", "SPopupWindow: Current Time--"+iday+" "+ihour+" "+iminte);
        days = new String[]{context.getString(R.string.system_time_today),context.getString(R.string.system_time_tomorrow),
                context.getString(R.string.system_time_the_day_after_tomorrow)};
        day.setDisplayedValues(days);
        day.setMaxValue(days.length-1);
        day.setMinValue(0);
        day.setValue(0);
        if(context.getString(R.string.judge_language).equals("ZH")){
            hours = new String[]{"1点","2点","3点","4点","5点","6点","7点","8点","9点","10点","11点","12点","13点","14点","15点","16点","17点","18点","19点","20点","21点","22点","23点","凌晨"};
        }else{
            hours = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","00"};
        }
        hour.setDisplayedValues(hours);
        hour.setMaxValue(hours.length-1);
        hour.setMinValue(0);
        if (ihour==0){
            hour.setValue(hours.length-1);
        }else {
            hour.setValue(ihour-1);
        }
        if(context.getString(R.string.judge_language).equals("ZH")){
            minits = new String[]{"00分","10分","20分","30分","40分","50分"};
        }else{
            minits = new String[]{"00","10","20","30","40","50"};
        }
        minte.setDisplayedValues(minits);
        minte.setMaxValue(minits.length-1);
        minte.setMinValue(0);
        if (iminte>0&&iminte<10){
            minte.setValue(1);
        }else if (iminte>10&&iminte<20){
            minte.setValue(2);
        }else if (iminte>20&&iminte<30){
            minte.setValue(3);
        }else if (iminte>30&&iminte<40){
            minte.setValue(4);
        }else if (iminte>40&&iminte<50){
            minte.setValue(5);
        }else if (iminte>50){
            minte.setValue(0);
        }

        day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               day.setValue(newVal);
                if (oldVal!=newVal){
                    hour.setValue(1);
                }else {
                    hour.setValue(Integer.valueOf(ihour));
                }
            }
        });

        hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour.setValue(newVal);
                if (oldVal!=newVal){
                    minte.setValue(1);
                }else {
                    minte.setValue(Integer.valueOf(iminte));
                }
            }
        });
        minte.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

    }
    public interface OnClickButton{
        void OnClickButton(String s1, String s);
    }

    public void setOnCilckButton(OnClickButton onClickButton){
        this.onClickButton= onClickButton;

    }

}
