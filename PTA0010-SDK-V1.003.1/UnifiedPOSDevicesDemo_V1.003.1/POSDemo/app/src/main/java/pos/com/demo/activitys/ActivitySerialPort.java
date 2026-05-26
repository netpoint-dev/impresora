package pos.com.demo.activitys;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pos.com.demo.R;
import pos.com.demo.utils.Utils;
import upos.POSException;
import upos.Serialport;
import upos.events.DataEvent;
import upos.events.DataReceiveListener;

/**
 * 1 打开串口
 * 2 开始读取串口数据 devPort.startReadData(true) 停止读取串口数据devPort.startReadData(false) 数据通过listener dataReceiveOccurred回调
 *   发送数据 devPort.writeData(bSend,bSend.length);
 * 3 关闭串口
 */
public class ActivitySerialPort extends BaseActivity {

    private Serialport devPort                = null;
    private Button btnUartOpen               = null;
    private Button      btnUartClose              = null;
    private Button      btnUartWrite              = null;
    private Button      btnUartRead               = null;
    private CheckBox cbUartHex                  = null;
    private Spinner spUartPath                 = null;
    private Spinner     spUartBaud                 = null;
    //private Spinner     spUartStop                 = null;
    private TextView tvUartRead                 = null;
    private EditText etUartWrite                = null;
    private Spinner     spUartCmd                 = null;
    private boolean     bFlagUartOpen            = false;
    private boolean     bFlagUartRead            = false;
    private List<String> list   = null;
    private int  mIndexData     = 0;
    private byte[]  bufData = new byte[1024];

    protected int findLayoutId(){
        return R.layout.activity_serial_port;
    }
    protected void findViews(){
        btnUartOpen  = (Button)findViewById(R.id.uart_btn_open);
        btnUartClose  = (Button)findViewById(R.id.uart_btn_close);
        btnUartWrite  = (Button)findViewById(R.id.uart_btn_write);
        btnUartRead  = (Button)findViewById(R.id.uart_btn_read);
        cbUartHex   = (CheckBox)findViewById(R.id.uart_cb_hex);
        spUartPath  = (Spinner)findViewById(R.id.uart_sp_path);
        spUartBaud  = (Spinner)findViewById(R.id.uart_sp_baudrate);
        //spUartStop  = (Spinner)findViewById(R.id.uart_sp_stopbit);
        tvUartRead  = (TextView)findViewById(R.id.uart_tv_read);
        etUartWrite = (EditText)findViewById(R.id.uart_et_write);
        spUartCmd  = (Spinner)findViewById(R.id.uart_sp_cmd);
    }
    protected void init(){
        setTitle(R.string.flag_uart);

        initSerialport();
        uiShow(false);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnBack:
                closePage();
                break;
            case R.id.uart_btn_open:
                showInfo(null);
                openSerialport();
                break;
            case R.id.uart_btn_close:
                closeSerialport();
                break;
            case R.id.uart_btn_read:
                onBtnReadClick(true);
                break;
            case R.id.uart_btn_write:
                sendData();
                break;
        }
    }

    /**
     * 退出页面
     */
    private void closePage(){
        closeSerialport();
        this.finish();
    }




    private StringBuilder m_strLog = new StringBuilder();

    /**
     * 显示数据
     * @param strInfo   null 时清空数据
     */
    private void showInfo(String strInfo){
        if(strInfo!=null){
            m_strLog.insert(0,strInfo+"\n");
            tvUartRead.setText(m_strLog.toString());
        }else{
            m_strLog 	= new StringBuilder();
            tvUartRead.setText("");
        }
    }

    /**
     * 设置按键使能
     * @param bOpen 设备打开成功时 ture  打开按钮变灰，其他操作按钮使能;  false 打开按钮使能，其他按钮变灰
     */
    private void uiShow(boolean bOpen){
        btnUartOpen.setEnabled(!bOpen);
        btnUartClose.setEnabled(bOpen);
        btnUartRead.setEnabled(bOpen);
        btnUartWrite.setEnabled(bOpen);
    }

    //---------------------------------------------------------------

    /**
     * 初始化设备
     */
    private void initSerialport(){
        devPort = new Serialport();
        list    = getSerialportList();
        if(list!=null&&list.size()>0){
            ArrayAdapter<String> adapter_Com = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
            adapter_Com.addAll(list);
            adapter_Com.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spUartPath.setAdapter(adapter_Com);
        }
        devPort.addDataReceiveListener(new DataReceiveListener() {
            @Override
            public void dataReceiveOccurred(DataEvent dataEvent) {
                showInfo("！！！dataReceiveOccurred   :");
                int iLen = dataEvent.getData(bufData,1024);
                String strData = String.format("%3d:len:%3d ",++mIndexData,iLen);
                if(cbUartHex.isChecked()){//16进制的方式显示数据
                    strData += Utils.getInstance().bytesToHexString(bufData,iLen);
                }else{//显示字符串
                    strData += new String(bufData,0,iLen);
                }
                showInfo(strData);
            }
        });
        showInfo("initSerialport");
    }

    /**
     *打开设备
     */
    private void openSerialport(){
        if(!bFlagUartOpen){
            int iPos    = spUartPath.getSelectedItemPosition();
            if(iPos>=0){
                String strPath   = list.get(iPos);
                int iBaudrate   = Integer.valueOf(spUartBaud.getSelectedItem().toString());
                try {
                    String strPara = strPath+","+String.valueOf(iBaudrate);
                    showInfo(strPara);
                    devPort.open(strPara);
                    bFlagUartOpen   = true;
                }catch (POSException e){
                    bFlagUartOpen   = false;
                }
                uiShow(bFlagUartOpen);
                showInfo(bFlagUartOpen?"Open successfully":"Open failed");
            }
        }
    }

    /**
     * 关闭设备
     */
    private void closeSerialport(){
        if(bFlagUartOpen){
            onBtnReadClick(false);
            devPort.close();
            bFlagUartOpen   = false;
            uiShow(false);
        }
    }

    /**
     * 往串口发送数据
     */
    private void sendData(){
        if(bFlagUartOpen){
            String strSend = etUartWrite.getText().toString();
            if(strSend.isEmpty()){
                strSend     = spUartCmd.getSelectedItem().toString();
            }
            if(strSend!=null&&!strSend.isEmpty()){
                byte[] bSend = null;
                if(cbUartHex.isChecked()){// 16进制的数据
                    if(strSend.contains(" ")){
                        strSend = strSend.replace(" ","");
                    }
                    bSend   = Utils.getInstance().stringToHexBytes(strSend);//strSend  010203aba0  return bSend 5 bytes [0x01,0x02,0x03,0xab,0xa0]
                }else{// 普通字符串
                    bSend   = strSend.getBytes();
                }
                if(bSend!=null){
                    devPort.writeData(bSend,bSend.length);
//                    Log.d(tag,"kwq sendUartData len:"+bSend.length+" data:"+tool.bytesToHexString(bSend,bSend.length));
                    showInfo("\nSerialport Send Data:"+bSend.length+"\ndata: "+Utils.getInstance().bytesToHexString(bSend,bSend.length));
                }
            }
        }
    }

    /**
     * 开始/停止  读取串口数据
     * @param bRead
     */
    private void onBtnReadClick(boolean bRead){
        bFlagUartRead   = !bFlagUartRead&&bRead;
        btnUartRead.setText(bFlagUartRead?R.string.read_stop:R.string.mag_read_start);
        devPort.startReadData(bFlagUartRead);
    }

    /**
     * @return list of UART path name
     */
    private List<String> getSerialportList(){
        List<String> list = new ArrayList<String>();
        File root = new File("/dev");
        if(root.isDirectory()){
            String[] childFiles = root.list();
            if(childFiles!=null &&childFiles.length>0){
                for (String string : childFiles) {
                    if(string.startsWith("ttyS")||string.startsWith("ttyMFD")||
                            string.startsWith("ttyUS")||string.startsWith("ttyHSL")||
                            string.startsWith("ttyACM")||string.startsWith("ttyMSM"))
                        list.add("/dev/"+string);
                }
            }
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);

        return list;
    }
}
