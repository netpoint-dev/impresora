package pos.com.demo.activitys;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pos.com.demo.R;
import upos.MSR;
import upos.POSException;
import upos.events.DataEvent;
import upos.events.DataReceiveListener;

/**
 * 1 打开设备
 * 2 读取磁卡数据  devMag.authenticateDevice(null)   停止读取devMag.deauthenticateDevice(null)
 * 3 关闭设备
 */
public class ActivityMagcard extends BaseActivity {

    private int     iTimeShow   = 5000;//显示磁卡数据 时间

    private MSR devMag               = null;
    private Button btnMagOpen              = null;
    private Button      btnMagClose             = null;
    private Button      btnMagRead              = null;
    private TextView tvMagTrack1             = null;
    private TextView    tvMagTrack2             = null;
    private TextView    tvMagTrack3             = null;
    private String      strMagReadStart        = null;
    private String      strReadStop         = null;
    private String      strMagTrack1           = null;
    private String      strMagTrack2           = null;
    private String      strMagTrack3           = null;
    private boolean     bFlagMagOpen          = false;
    private boolean     bFlagMagRead          = false;

    protected int findLayoutId(){
        return R.layout.activity_magcard;
    }
    protected void findViews(){
        btnMagOpen  = (Button)findViewById(R.id.mag_btn_open);
        btnMagClose  = (Button)findViewById(R.id.mag_btn_close);
        btnMagRead  = (Button)findViewById(R.id.mag_btn_read);

        tvMagTrack1 = (TextView)findViewById(R.id.mag_tv_track1);
        tvMagTrack2 = (TextView)findViewById(R.id.mag_tv_track2);
        tvMagTrack3 = (TextView)findViewById(R.id.mag_tv_track3);
        strMagReadStart = getResources().getString(R.string.mag_read_start);
        strReadStop = getResources().getString(R.string.read_stop);
        strMagTrack1 = getResources().getString(R.string.mag_track1);
        strMagTrack2 = getResources().getString(R.string.mag_track2);
        strMagTrack3 = getResources().getString(R.string.mag_track3);
    }
    protected void init(){
        setTitle(R.string.flag_magcard);
        initMagcard();
        uiShow(false);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnBack:
                closePage();
                break;
            case R.id.mag_btn_open:
                openMagcard();
                break;
            case R.id.mag_btn_close:
                closeMagcard();
                break;
            case R.id.mag_btn_read:
                onBtnReadClick(true);
                break;
        }
    }

    /**
     * 退出页面
     */
    private void closePage(){
        closeMagcard();
        this.finish();
    }

    /**
     * 设置按键使能
     * @param bOpen 设备打开成功时 ture  打开按钮变灰，其他操作按钮使能;  false 打开按钮使能，其他按钮变灰
     */
    private void uiShow(boolean bOpen){
        btnMagOpen.setEnabled(!bOpen);
        btnMagClose.setEnabled(bOpen);
        btnMagRead.setEnabled(bOpen);
    }

    /**
     * 界面显示数据
     * @param bFlag  true 界面显示磁卡数据; false 清空界面磁卡数据
     * @param listData  磁卡数据
     */
    private void showMagcardData(boolean bFlag,String[] listData){

        if(bFlag){
            if(listData!=null){
                tvMagTrack1.setText(strMagTrack1+listData[0]);
                if(listData.length>1){
                    tvMagTrack2.setText(strMagTrack2+listData[1]);
                }
                if(listData.length>2){
                    tvMagTrack3.setText(strMagTrack3+listData[2]);
                }
            }
        }else {
            tvMagTrack1.setText(strMagTrack1);
            tvMagTrack2.setText(strMagTrack2);
            tvMagTrack3.setText(strMagTrack3);
        }
    }

    private static final int  MSG_CLEAR     = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean bRet = false;
            switch (msg.what){
                case MSG_CLEAR://清除界面磁卡数据
                    bRet    = true;
                    showMagcardData(false,null);
                    break;
            }
            return bRet;
        }
    });
    //-----------------------------------

    /**
     * 初始化设备
     */
    private void initMagcard(){
        devMag  = new MSR();
        devMag.addDataReceiveListener(new DataReceiveListener() {
            @Override
            public void dataReceiveOccurred(DataEvent dataEvent) {
                String[] strings    = dataEvent.getDataString();
                showMagcardData(true,strings);
                handler.sendEmptyMessageDelayed(MSG_CLEAR,iTimeShow);
            }
        });
    }

    /**
     *打开设备
     */
    private void openMagcard(){
        if(!bFlagMagOpen){
            try {
                devMag.open("");
                bFlagMagOpen    = true;
            }catch (POSException e){
                bFlagMagOpen    = false;
            }
            showLog(bFlagMagOpen?"Open Mag Successfully":"Open Mag failed");
            uiShow(bFlagMagOpen);
        }
    }

    /**
     * 关闭设备
     */
    private void closeMagcard(){
        if(bFlagMagOpen){
            bFlagMagOpen    = false;
            onBtnReadClick(false);
            devMag.close();
            showLog("Mag Close");
            uiShow(bFlagMagOpen);
        }
    }

    /**
     *  开始/停止  读取磁卡数据
     *  读取到磁卡数据后 通过listener回调 onReadString(String[] strings)
     * @param bRead
     */
    private void onBtnReadClick(boolean bRead){

        bFlagMagRead    = !bFlagMagRead&&bRead;
        if(bFlagMagRead){
            showLog("Mag Clear Data,Begin Read");
            showMagcardData(false,null);
        }else{
            showLog("Mag Stop Read Data ");
        }
        if(bFlagMagRead){
            devMag.authenticateDevice(null);
        }else{
            devMag.deauthenticateDevice(null);
        }
        btnMagRead.setText(bFlagMagRead?strReadStop:strMagReadStart);
    }
}
