package pos.com.demo.activitys;

import android.view.View;
import android.widget.Button;

import pos.com.demo.R;
import upos.CashDrawer;
import upos.POSException;

/**
 * 钱箱测试页面
 * 1 打开钱箱设备 openDevice();
 * 2 开钱箱 doOpenDrawer();
 * 3 关闭钱箱设备 closeDevice();
 */
public class ActivityDrawer extends BaseActivity {

    private CashDrawer devDrawer                 = null;
    private boolean     bFlagDrawerOpen            = false;
    private Button btnDrawerOpen               = null;
    private Button      btnDrawerClose              = null;
    private Button      btnDrawerDo                  = null;

    protected int findLayoutId(){
        return R.layout.activity_drawer;
    }
    protected void findViews(){

        btnDrawerOpen  = (Button)findViewById(R.id.drawer_btn_open);
        btnDrawerClose  = (Button)findViewById(R.id.drawer_btn_close);
        btnDrawerDo  = (Button)findViewById(R.id.drawer_btn_do);
    }
    protected void init(){
        setTitle(R.string.flag_drawer);
        initDrawer();
        uiShow(false);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnBack:
                closePage();
                break;
            case R.id.drawer_btn_open:
                openDevice();
                break;
            case R.id.drawer_btn_close:
                closeDevice();
                break;
            case R.id.drawer_btn_do:
                doOpenDrawer();
                break;
        }
    }

    /**
     * 退出页面
     */
    private void closePage(){
        closeDevice();
        this.finish();
    }

    /**
     * 设置按键使能
     * @param bOpen 设备打开成功时 ture  打开按钮变灰，其他操作按钮使能;  false 打开按钮使能，其他按钮变灰
     */
    private void uiShow(boolean bOpen){
        btnDrawerOpen.setEnabled(!bOpen);
        btnDrawerClose.setEnabled(bOpen);
        btnDrawerDo.setEnabled(bOpen);
    }
    //-----------------------------------------------------------------

    /**
     * 初始化设备
     */
    private void initDrawer(){
        devDrawer = new CashDrawer();
    }


    /**
     *打开设备
     */
    private void openDevice(){
        if(!bFlagDrawerOpen){
            try {
                devDrawer.open("");
                bFlagDrawerOpen = true;
            }catch (POSException e){
                bFlagDrawerOpen = false;
            }
            uiShow(bFlagDrawerOpen);
        }
    }

    /**
     * 关闭设备
     */
    private void closeDevice(){
        if(bFlagDrawerOpen){
            bFlagDrawerOpen = false;
            devDrawer.close();
            uiShow(bFlagDrawerOpen);
        }
    }

    /**
     * 开启钱箱
     */
    private void doOpenDrawer(){
        if(bFlagDrawerOpen){
            devDrawer.openDrawer();
        }
    }
}
