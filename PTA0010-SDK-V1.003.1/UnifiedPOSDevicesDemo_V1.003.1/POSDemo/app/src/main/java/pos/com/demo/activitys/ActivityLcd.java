package pos.com.demo.activitys;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pos.com.demo.R;
import pos.com.demo.utils.FileUtil;
import pos.com.demo.utils.LogUtil;
import upos.LineDispaly;
import upos.LineDisplayConst;
import upos.POSException;

/**
 * LCD 测试页
 * 1 打开LCD 类型： 0 9VFD; 1 32X144; 2 65X132; 3 24X128; 4 32X240 5 COG;
 * 2 操作LCD  开关背光灯  调节对比度 显示字符串 显示图片
 * 3 关闭LCD
 */
public class ActivityLcd extends BaseActivity implements LineDisplayConst{

    private LineDispaly devLcd                   = null;
    private Button btnLcdOpen               = null;
    private Button      btnLcdClose              = null;
    private Button      btnLcdClear              = null;
    private Button      btnLcdLight              = null;
    private Button      btnLcdContrast           = null;
    private Button      btnLcdSendChar           = null;
    private Button      btnLcdSendPic            = null;
    private Spinner spLcdType                 = null;
    private boolean     bFlagLcdOpen            = false;
    private boolean     bFlagLcdLight           = false;
    private String        strLcdLight             = null;
    private String        strLcdOff                = null;
    private int         iLcdContrast              = 4;
    private Spinner  spLcdFontSize      = null;
    private EditText    etLcdText       = null;

    protected int findLayoutId(){
        return R.layout.activity_lcd;
    }
    protected void findViews(){
        btnLcdOpen  = (Button)findViewById(R.id.lcd_btn_open);
        btnLcdClose  = (Button)findViewById(R.id.lcd_btn_close);
        btnLcdClear  = (Button)findViewById(R.id.lcd_btn_clear);
        btnLcdLight  = (Button)findViewById(R.id.lcd_btn_light);
        btnLcdContrast  = (Button)findViewById(R.id.lcd_btn_contrast);
        btnLcdSendChar  = (Button)findViewById(R.id.lcd_btn_char);
        btnLcdSendPic  = (Button)findViewById(R.id.lcd_btn_pic);
        spLcdType      = (Spinner)findViewById(R.id.lcd_sp_type);
        strLcdLight     = getResources().getString(R.string.lcd_light);
        strLcdOff     = getResources().getString(R.string.lcd_off);

        spLcdFontSize  = (Spinner)findViewById(R.id.lcd_sp_size);
        etLcdText = (EditText)findViewById(R.id.lcd_et_text);
    }
    protected void init(){
        setTitle(R.string.flag_lcd);
        initLcd();
        uiShow(false);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnBack:
                closePage();
                break;
            case R.id.lcd_btn_open:
                openLcd();
                break;
            case R.id.lcd_btn_close:
                closeLcd();
                break;
            case R.id.lcd_btn_clear:
                clearLcd();
                break;
            case R.id.lcd_btn_light:
                onLcdLightClick(true);
                break;
            case R.id.lcd_btn_contrast:
                onLcdContrastClick();
                break;
            case R.id.lcd_btn_char:
                sendLcdMessage();
                break;
            case R.id.lcd_btn_pic:
                sendLcdPicture();
                break;
        }
    }

    /**
     * 退出页面
     */
    private void closePage(){
        closeLcd();
        this.finish();
    }

    /**
     * 设置按键使能
     * @param bOpen 设备打开成功时 ture  打开按钮变灰，其他操作按钮使能;  false 打开按钮使能，其他按钮变灰
     */
    private void uiShow(boolean bOpen){
        btnLcdOpen.setEnabled(!bOpen);
        btnLcdClose.setEnabled(bOpen);
        btnLcdLight.setEnabled(bOpen);
        btnLcdSendPic.setEnabled(bOpen);
        btnLcdSendChar.setEnabled(bOpen);
        btnLcdContrast.setEnabled(bOpen);
        btnLcdClear.setEnabled(bOpen);
    }


    //-------------------------------------------------

    /**
     * 初始化设备
     */
    private void initLcd(){
        devLcd  = new LineDispaly();
    }

    /**
     *打开设备
     */
    private void openLcd(){
        if(!bFlagLcdOpen){
            int iPos = spLcdType.getSelectedItemPosition();
            if(iPos!=6){
                LogUtil.info("openlcd:"+iPos);
                try {
                    devLcd.open(String.valueOf(iPos));
                    bFlagLcdOpen = true;
                }catch (POSException e){
                    bFlagLcdOpen = false;
                }
                showLog(bFlagLcdOpen?"Open Lcd Successfully":"Open Lcd Failed");
                //getLcdRect();
                uiShow(bFlagLcdOpen);
            }
        }
    }

    /**
     * 关闭设备
     */
    private void closeLcd(){
        if(bFlagLcdOpen){
            clearLcd();
            bFlagLcdOpen    = false;
            onLcdLightClick(false);
            devLcd.close();
            uiShow(false);
        }
    }

    /**
     * 背光灯的开启关闭
     * @param bLight   true 根据bFlagLcdLight开启背光灯, false 关闭背光灯
      */
    private void onLcdLightClick(boolean bLight){
        bFlagLcdLight   = !bFlagLcdLight&&bLight;
        btnLcdLight.setText(bFlagLcdLight?strLcdOff:strLcdLight);
        devLcd.setDescriptor(LINEDISPLAY_DESCRIPTOR_BACKLIGHT,bFlagLcdLight?1:0);
        showLog(bFlagLcdLight?"Turn On Lcd Back Light":"Turn Off Lcd Light");
    }

    /**
     * 清除屏幕内容
     */
    private void clearLcd() {
        if (bFlagLcdOpen){
            devLcd.clearText();
            showLog("Clear Lcd");
        }
    }

    /**
     *设置LCD对比度 0-9
     */
    private void onLcdContrastClick(){
        if(bFlagLcdOpen){
            if(++iLcdContrast>9){
                iLcdContrast    = 0;
            }

            devLcd.setDescriptor(LINEDISPLAY_DESCRIPTOR_CONTRAST,iLcdContrast);
            showLog("Set Lcd Contrast:"+String.valueOf(iLcdContrast));
        }
    }

    /**
     * 显示字符串
     */
    private void sendLcdMessage(){
        if(bFlagLcdOpen){
            String strText = etLcdText.getText().toString();

            String strSize = spLcdFontSize.getSelectedItem().toString();
            int iFont = Integer.valueOf(strSize).intValue();
            if(strText.isEmpty()){
                String str  = getDateString();
                devLcd.displayText(str,iFont);
                showLog("Send Words to Lcd");
            }else{
                devLcd.displayText(strText,iFont);
                showLog("Send to Lcd size:"+strSize+" text:"+strText);
            }
        }
    }

    /**
     * 先检查 path的文件是否存在，如果不存在，用 inputStream 拷贝到path的位置
     * @param path  文件路径
     * @param inputStream    输入流数据
     * @return path文件存在 返回true,否则返回false
     */
    private boolean copyFile(String path,InputStream inputStream){
        return FileUtil.copyFile(path,inputStream);
    }

    /**
     * 显示图片
     * Write bitmap's dot data to Lcd;
     */
    private void sendLcdPicture(){
        if(bFlagLcdOpen){
            //Bitmap bitmap = null;
            try {
                InputStream inputStream = getAssets().open("android_man.bmp");
                //bitmap = BitmapFactory.decodeStream(inputStream);
                String strStorage = Environment.getExternalStorageDirectory().toString();
                strStorage += "/Download/android_man.bmp";
                LogUtil.info("------------print path----------- path:"+strStorage);
                boolean bExist = copyFile(strStorage,inputStream);//确保图片存在
                inputStream.close();
                //devLcd.displayBitmap(bitmap,0,0,0);
                devLcd.displayBitmap(strStorage,0,0,0);
                showLog("Send bitmap to Lcd");
            } catch (Exception e) {
                showErr("send picture to lcd Error" + e.toString());
            }
        }
    }

    /**
     * 获取日期信息
     * @return
     */
    private String getDateString(){
        String FORM_STRING = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat date = new SimpleDateFormat(FORM_STRING, Locale.getDefault());
        return date.format(new java.util.Date());
    }
}
