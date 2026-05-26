package pos.com.demo.application;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;


import pos.com.demo.utils.DateUtil;
import pos.com.demo.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    public static BaseApplication getInstance(){
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                handleException(e);
            }
        });
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        StringBuffer exceptionStr = new StringBuffer();
        Date nowTime = new Date() ;
        exceptionStr.append("------"+ DateUtil.formatDate(nowTime, "HH:mm:ss")+"------").append("\n");
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        pw.close();
        exceptionStr.append(writer.toString()).append("\n");
        saveCrashInfo2File(nowTime,exceptionStr.toString());
        Log.e("exception", exceptionStr.toString());
        return true;
    }

    /**
     * 保存错误日志到本地文件
     *
     *void
     */
    private void saveCrashInfo2File(Date nowDate ,String exceptionStr) {
        String date = DateUtil.formatDate(nowDate, "yyyy_MM_dd");
        String fileName = date +"_crash.txt";
        File file = new File(FileUtil.getSaveFileDir(getApplicationContext()), fileName);

        FileOutputStream fos = null ;
        try {
            fos = new FileOutputStream(file,true);
            fos.write(exceptionStr.getBytes());
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally{
            if (fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
