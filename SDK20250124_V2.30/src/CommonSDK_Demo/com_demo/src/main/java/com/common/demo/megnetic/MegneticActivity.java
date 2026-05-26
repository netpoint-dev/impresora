package com.common.demo.megnetic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.common.apiutil.CommonException;
import com.common.demo.R;
import com.common.apiutil.magnetic.MagneticCard;
import com.common.demo.bean.BaseActivity;

/**
 * For Magnetic stripe card test.
 * @author linhx
 * @date 2015-02-27
 *
 */
public class MegneticActivity extends BaseActivity {
    private EditText editText1,editText2,editText3;
	private Button swipe,quit;
	Thread readThread;
	private final static String TAG = "MegneticActivity";

	
	Handler handler = new Handler() {

		
		public void handleMessage(Message msg) {
            editText1.setText("");
            editText2.setText("");
            editText3.setText("");
			String[] TracData = (String[])msg.obj;
			if(TracData!=null) {
                for (int i = 0; i < 3; i++) {
                    if (TracData[i] != null) {
                        Log.d(TAG, "tracdata:" + TracData[i]);
                        switch (i) {
                            case 0:
                                editText1.setText(TracData[i]);
                                break;
                            case 1:
                                editText2.setText(TracData[i]);
                                break;
                            case 2:
                                editText3.setText(TracData[i]);
                                break;
                        }
                    }
                }
            }
		}
    };
	
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tagg", "onCreate >>>");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.magnetic_main);
        editText1 = (EditText) findViewById(R.id.editText_track1);
        editText2 = (EditText) findViewById(R.id.editText_track2);
        editText3 = (EditText) findViewById(R.id.editText_track3);
        swipe = (Button) findViewById(R.id.button_swipe);
        quit = (Button) findViewById(R.id.button_quit);
        quit.setEnabled(false);

        try {
            MagneticCard.open(MegneticActivity.this);
        } catch (Exception e) {
            swipe.setEnabled(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MegneticActivity.this);
            alertDialog.setTitle(R.string.error);
            alertDialog.setMessage(R.string.error_open_magnetic_card);
            alertDialog.setPositiveButton(R.string.dialog_comfirm,new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    MegneticActivity.this.finish();
                }
            });
            alertDialog.show();
        }

        swipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	/*try {
                    MagneticCard.open(MegneticActivity.this);
                } catch (Exception e) {
        			swipe.setEnabled(false);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MegneticActivity.this);
                    alertDialog.setTitle(R.string.error);
                    alertDialog.setMessage(R.string.error_open_magnetic_card);
                    alertDialog.setPositiveButton(R.string.dialog_comfirm,new DialogInterface.OnClickListener() {
                        
                        public void onClick(DialogInterface dialog, int which) {
                            MegneticActivity.this.finish();
                        }
                    });
                    alertDialog.show();
                }*/
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
				readThread = new ReadThread();
				readThread.start();
				swipe.setEnabled(false);
				quit.setEnabled(true);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	
            	readThread.interrupt();
            	readThread = null;

            	//MagneticCard.close();
            	swipe.setEnabled(true);
            	quit.setEnabled(false);
            }
        });
    }
    
    private class ReadThread extends Thread {
    	
    	String[] TracData = null;
    	
		
		public void run() {
			MagneticCard.startReading();
			while(!Thread.interrupted()) {
				try{
					TracData = MagneticCard.check(5000);
					handler.sendMessage(handler.obtainMessage(1, TracData));
					MagneticCard.startReading();
				}catch (CommonException e){
					e.printStackTrace();
				}
			}
		}
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        MagneticCard.close();
    }

}
