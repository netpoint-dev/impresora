package com.common.demo.nfc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.common.demo.R;
import com.common.demo.bean.BaseActivity;


public class FelicaActivity extends BaseActivity {

    public static final int CHECK_NFC = 1;
    public static final int GET_ATS = 2;
    public static final int ACTIVATE_NEW_CARD = 3;
    public static final int FREE_MEM = 4;
    public static final int FORMAT = 5;
    public static final int GET_VERSION = 6;
    public static final int GET_APP_IDS = 7;
    public static final int CREATE_APP = 8;
    public static final int DELETE_APP = 9;
    public static final int GET_FILE_IDS = 10;
    public static final int GET_FILE_SETTINGS = 11;
    public static final int CREATE_STD_DATA_FILE = 12;
    public static final int DELETE_FILE = 13;
    public static final int WRITE_DATA = 14;
    public static final int READ_DATA = 15;
    public static final int GET_KEY_SETTINGS = 16;
    public static final int GET_KEY_VERSION = 17;
    public static final int CREATE_BACKUP_DATA_FILE = 18;
    public static final int CREATE_VALUE_FILE = 19;
    public static final int CREDIT = 20;
    public static final int LIMITED_CREDIT = 21;
    public static final int DEBIT = 22;
    public static final int GET_VALUE = 23;
    public static final int CREATE_LINEAR_RECORD_FILE = 24;
    public static final int CREATE_CYCLIC_RECORD_FILE = 25;
    public static final int WRITE_RECORD = 26;
    public static final int UPDATE_RECORD = 27;
    public static final int READ_RECORDS = 28;
    public static final int CLEAR_RECORD_FILE = 29;
    public static final int COMMIT_TRANSACTION = 30;
    public static final int COMMIT_TRANSACTION_Option = 31;
    public static final int ABORT_TRANSATION = 32;
    //public static final int COMMIT_READER_ID = 33;
    public static final int READ_SIG = 34;
    public static final int SET_CONFIGURATION = 35;
    public static final int GET_CARD_UID = 36;
    public static final int AUTH_FIRST = 37;
    public static final int AUTH_NO_FIRST = 38;
    public static final int CHANGE_KEY = 39;
    public static final int CHANGE_KEY_EV2 = 40;
    public static final int CHANGE_KEY_SETTINGS = 41;
    public static final int CHANGE_FILE_SETTINGS = 42;
    public static final int SELECT_APP = 43;
    public static final int INITIALIZE_KEYSET = 44;
    public static final int FINALIZE_KEYSET = 45;
    public static final int ROLL_KEYSET = 46;
    public static final int CREATE_TRANSACTION_MAC_FILE = 47;
    public static final int COMMIT_READER_ID = 48;
    public static final int CREATE_DELEGATED_APP = 49;
    public static final int GET_DELEGATED_INFO = 50;
    public static final int GET_FILE_COUNTERS = 51;
    public static final int FORMAT_TO_OVER = 52;

    public static final int FELICA_POLLING = 60;
    public static final int FELICA_READ = 61;
    public static final int FELICA_WRITE = 62;

    public static final int OPEN_NFC_EV2 = 96;
    public static final int OPEN_NFC_FELICA = 97;
    public static final int CLOSE_NFC_EV2 = 98;
    public static final int CLOSE_NFC_FELICA = 99;

    @SuppressLint("StaticFieldLeak")
    private static final int OK = 0;
    private static final int FAIL = -1;
    private static Button btn_OpenNFC = null;
    private static Button btn_CheckNFC = null;
    private static Button btn_GetATS = null;
    private static Button btn_ActivateNewCard = null;
    private static Button btn_FreeMem = null;
    private static Button btn_Format = null;
    private static Button btn_GetVersion = null;
    private static Button btn_GetApplicationIDs = null;
    private static Button btn_CreateApplication = null;
    private static Button btn_DeleteApplication = null;
    private static Button btn_GetFileIDs = null;
    private static Button btn_GetFileSettings = null;
    private static Button btn_CreateStdDataFile = null;
    private static Button btn_DeleteFile = null;
    private static Button btn_WriteData = null;
    private static Button btn_ReadData = null;
    private static Button btn_GetKeySettings = null;
    private static Button btn_GetKeyVersion = null;
    private static Button btn_CloseNFC = null;
    private static Button btn_CreateBackupDataFile = null;
    private static Button btn_CreateValueFile = null;
    private static Button btn_Credit = null;
    private static Button btn_LimitedCredit = null;
    private static Button btn_Debit = null;
    private static Button btn_GetValue = null;
    private static Button btn_CreateLinearRecordFile = null;
    private static Button btn_CreateCyclicRecordFile = null;
    private static Button btn_WriteRecord = null;
    private static Button btn_UpdateRecord = null;
    private static Button btn_ReadRecords = null;
    private static Button btn_ClearRecordFile = null;
    private static Button btn_CommitTransaction = null;
    private static Button btn_AbortTransaction = null;
    //private static Button btn_CommitReaderID = null;
    private static Button btn_Read_Sig = null;
    private static Button btn_SetConfiguration = null;
    private static Button btn_GetCardUID = null;
    private static Button btn_AuthenticateEV2First = null;
    private static Button btn_AuthenticateEV2NonFirst = null;
    private static Button btn_ChangeKey = null;
    private static Button btn_ChangeKeyEV2 = null;
    private static Button btn_ChangeKeySettings = null;
    private static Button btn_ChangeFileSettings = null;
    private static Button btn_SelectApplication = null;
    private static Button btn_InitializeKeySet = null;
    private static Button btn_FinalizeKeySet = null;
    private static Button btn_RollKeySet = null;
    private static Button btn_CreateTransactionMACFile = null;
    private static Button btn_CommitReaderID = null;
    private static Button btn_CreateDelegatedApplication = null;
    private static Button btn_GetDelegatedInfo = null;
    private static Button btn_GetFileCounters = null;
    private static Button btn_Format2Over = null;

    private static Button btn_Felica_OpenNFC = null;
    private static Button btn_Felica_Polling = null;
    private static Button btn_Felica_Read = null;
    private static Button btn_Felica_Write = null;
    private static Button btn_Felica_CloseNFC = null;
    private static Button btn_Switch_JumpEv2Test = null;
    private static Button btn_Switch_JumpEv3Test = null;
    private static Button btn_Switch_JumpFelicaTest = null;

    private static TextView tv_CardInfo = null;
    private static TextView tv_CardATS = null;
    private static TextView tv_ActivateNewCard = null;
    private static TextView tv_FreeMem = null;
    private static TextView tv_Format = null;
    private static TextView tv_GetVersion = null;
    private static TextView tv_GetApplicationIDs = null;
    private static TextView tv_CreateApplication = null;
    private static TextView tv_DeleteApplication = null;
    private static TextView tv_GetFileIDs = null;
    private static TextView tv_GetFileSettings = null;
    private static TextView tv_CreateStdDataFile = null;
    private static TextView tv_DeleteFile = null;
    private static TextView tv_WriteData = null;
    private static TextView tv_ReadData = null;
    private static TextView tv_GetKeySettings = null;
    private static TextView tv_GetKeyVersion = null;
    private static TextView tv_CreateBackupDataFile = null;
    private static TextView tv_CreateValueFile = null;
    private static TextView tv_Credit = null;
    private static TextView tv_LimitedCredit = null;
    private static TextView tv_Debit = null;
    private static TextView tv_GetValue = null;
    private static TextView tv_CreateLinearRecordFile = null;
    private static TextView tv_CreateCyclicRecordFile = null;
    private static TextView tv_WriteRecord = null;
    private static TextView tv_UpdateRecord = null;
    private static TextView tv_ReadRecords = null;
    private static TextView tv_ClearRecordFile = null;
    private static TextView tv_CommitTransaction = null;
    private static TextView tv_AbortTransaction = null;
    //private static TextView tv_CommitReaderID = null;
    private static TextView tv_Read_Sig = null;
    private static TextView tv_SetConfiguration = null;
    private static TextView tv_GetCardUID = null;
    private static TextView tv_AuthenticateEV2First = null;
    private static TextView tv_AuthenticateEV2NonFirst = null;
    private static TextView tv_ChangeKey = null;
    private static TextView tv_ChangeKeyEV2 = null;
    private static TextView tv_ChangeKeySettings = null;
    private static TextView tv_ChangeFileSettings = null;
    private static TextView tv_SelectApplication = null;
    private static TextView tv_InitializeKeySet = null;
    private static TextView tv_FinalizeKeySet = null;
    private static TextView tv_RollKeySet = null;
    private static TextView tv_CreateTransactionMACFile = null;
    private static TextView tv_CommitReaderID = null;
    private static TextView tv_CreateDelegatedApplication = null;
    private static TextView tv_GetDelegatedInfo = null;
    private static TextView tv_GetFileCounters = null;
    private static TextView tv_Format2Over = null;

    private static TextView tv_Felica_Polling = null;
    private static TextView tv_Felica_Read = null;
    private static TextView tv_Felica_Write = null;

    private static EditText et_CreateApplication = null;
    private static EditText et_DeleteApplication = null;
    private static EditText et_CreateStdDataFile = null;
    private static EditText et_DeleteFile = null;
    private static EditText et_WriteData = null;
    private static EditText et_ReadData = null;
    private static EditText et_GetFileIDs = null;
    private static EditText et_GetFileSettings = null;
    private static EditText et_GetKeySettings = null;
    private static EditText et_GetKeyVersion = null;
    private static EditText et_CreateBackupDataFile = null;
    private static EditText et_CreateValueFile = null;
    private static EditText et_Credit = null;
    private static EditText et_LimitedCredit = null;
    private static EditText et_Debit = null;
    private static EditText et_GetValue = null;
    private static EditText et_CreateLinearRecordFile = null;
    private static EditText et_CreateCyclicRecordFile = null;
    private static EditText et_WriteRecord = null;
    private static EditText et_UpdateRecord = null;
    private static EditText et_ReadRecords = null;
    private static EditText et_ClearRecordFile = null;
    private static EditText et_CommitTransaction = null;
    //private static EditText et_CommitReaderID = null;
    private static EditText et_SetConfiguration = null;
    private static EditText et_AuthenticateEV2First = null;
    private static EditText et_AuthenticateEV2NonFirst = null;
    private static EditText et_ChangeKey = null;
    private static EditText et_ChangeKeyEV2 = null;
    private static EditText et_ChangeKeySettings = null;
    private static EditText et_ChangeFileSettings = null;
    private static EditText et_SelectApplication = null;
    private static EditText et_InitializeKeySet = null;
    private static EditText et_FinalizeKeySet = null;
    private static EditText et_RollKeySet = null;
    private static EditText et_CreateTransactionMACFile = null;
    private static EditText et_CommitReaderID = null;
    private static EditText et_CreateDelegatedApplication = null;
    private static EditText et_GetDelegatedInfo = null;
    private static EditText et_GetFileCounters = null;

    private static EditText et_Felica_Polling = null;
    private static EditText et_Felica_Read = null;
    private static EditText et_Felica_Write = null;

    private static TableLayout layout = null;
    private Demo demo = new Demo(this);

    private View layoutSwitch = null;
//    private View layoutEv2 = null;
//    private View layoutFelica = null;

    private static long exitTime = 0;// 退出时间
    private static boolean isSwitchUI = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if(isSwitchUI) {
                // 判断间隔时间 大于2秒就退出应用
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    // 计算两次返回键按下的时间差
                    exitTime = System.currentTimeMillis();
                } else {
                    // 关闭应用程序
                    finish();
                }
            }else {
                isSwitchUI = true;
                setContentView(layoutSwitch);
                this.setTitle("IC Card Type Select");
                demo.OP_NFC_Device_Close();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_felica);

        JumpFelicaTestUI();


    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        String intToStr = "";
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_NFC_FELICA:
                    if(msg.arg1 == OK) {
                        btn_Felica_OpenNFC.setEnabled(false);
                        btn_Felica_Polling.setEnabled(true);
                        btn_Felica_CloseNFC.setEnabled(true);
                    }
                    break;

                case CHECK_NFC:
                    tv_CardInfo.setText("");
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        if(buf[0] == 'A') {
                            tv_CardInfo.append("Card Tpye: Type A CPU\n");
                        }else if(buf[0] == 'B') {
                            tv_CardInfo.append("Card Tpye: Type B CPU\n");
                        }else if(buf[0] == 'F') {
                            tv_CardInfo.append("Card Tpye: Type F CPU\n");
                        }
                        byte[] atqa = {buf[2],buf[3]};
                        int[] strLen = {2};
                        String aqta_str = bytesToHexString(atqa, strLen);
                        tv_CardInfo.append("ATQA: "+aqta_str+"\n");

                        byte[] ask = {buf[4]};
                        strLen[0] = 1;
                        String ask_str = bytesToHexString(ask, strLen);
                        tv_CardInfo.append("ASK: "+ask_str+"\n");

                        byte[] uid = new byte[7];
                        System.arraycopy(buf, 6, uid, 0, uid.length);
                        strLen[0] = 7;
                        String uid_str = bytesToHexString(uid, strLen);
                        tv_CardInfo.append("UID: "+uid_str+"\n");
                        ButtonsSetState(false);
                    }else{
                        tv_CardInfo.setText("未检索到IC卡");
                    }
                    break;

                case FELICA_POLLING:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Polling.setText(str);
                        btn_Felica_Read.setEnabled(true);
                        btn_Felica_Write.setEnabled(true);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Polling.setText("error: "+intToStr);
                    }
                    break;

                case FELICA_READ:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Read.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Read.setText("error: "+intToStr);
                    }
                    break;

                case FELICA_WRITE:
                    if(msg.arg1 == OK) {
//                        byte[] buf = (byte[])msg.obj;
//                        int[] bufLen = {buf.length};
//                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Write.setText("success");
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Write.setText("error: "+intToStr);
                    }
                    break;

                case CLOSE_NFC_FELICA:
                    if(msg.arg1 == OK) {
                        btn_Felica_OpenNFC.setEnabled(true);
                        btn_Felica_Polling.setEnabled(false);
                        btn_Felica_Read.setEnabled(false);
                        btn_Felica_Write.setEnabled(false);
                        btn_Felica_CloseNFC.setEnabled(false);
                    }
                    break;
                default:
                    break;
            }
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            layout.requestFocus();
        }
    };

    public static void ButtonsSetState(boolean IsCloseNFC) {
        for(int i=0;i<layout.getChildCount();i++)
        {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if(!((Button) v).getText().equals("Open NFC")) {
                    v.setEnabled(!IsCloseNFC);
                }else{
                    v.setEnabled(IsCloseNFC);
                }
            }else if (v instanceof TableRow) {
                for(int j=0;j<((TableRow) v).getChildCount();j++) {
                    View b = ((TableRow) v).getChildAt(j);
                    if (b instanceof Button) {
                        b.setEnabled(!IsCloseNFC);
                    }
                }
            }
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String[] hex = hexString.split(" ");

        if(hex.length > 0) {
            String hexStr = hex[0];
            for (int i=1; i< hex.length; i++) {
                hexStr = hexStr.concat(hex[i]);
            }
            hexStr = hexStr.toUpperCase();
            int length = hexStr.length() / 2;
            char[] hexChars = hexStr.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        }
        return null;
    }

    public static String bytesToHexString(byte[] src, int[] srcLen){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || srcLen == null) {
            return null;
        }
        for (int i = 0; i < srcLen[0]; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            hv = hv.toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        stringBuilder.append("(hex)");
        return stringBuilder.toString();
    }

    public void JumpFelicaTestUI() {

        this.setTitle("Felica Test");

        layout = findViewById(R.id.TableLayout);
        for(int i=0;i<layout.getChildCount();i++)
        {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if(!((Button) v).getText().equals("Open NFC")) {
                    v.setEnabled(false);
                }
            }else if (v instanceof TableRow) {
                for(int j=0;j<((TableRow) v).getChildCount();j++) {
                    View b = ((TableRow) v).getChildAt(j);
                    if (b instanceof Button) {
                        b.setEnabled(false);
                    }
                }
            }
        }
        btn_Felica_OpenNFC = (Button)findViewById(R.id.Button_Felica_OpenNFC);
        btn_Felica_Polling = (Button)findViewById(R.id.Button_Felica_Polling);
        btn_Felica_Read = (Button)findViewById(R.id.Button_Felica_Read);
        btn_Felica_Write = (Button)findViewById(R.id.Button_Felica_Write);
        btn_Felica_CloseNFC = (Button)findViewById(R.id.Button_Felica_CloseNFC);

        et_Felica_Polling = (EditText)findViewById(R.id.EditText_Felica_Polling);
        et_Felica_Read = (EditText)findViewById(R.id.EditText_Felica_Read);
        et_Felica_Write = (EditText)findViewById(R.id.EditText_Felica_Write);

        tv_Felica_Polling = (TextView)findViewById(R.id.TextView_Felica_Polling);
        tv_Felica_Read = (TextView)findViewById(R.id.TextView_Felica_Read);
        tv_Felica_Write = (TextView)findViewById(R.id.TextView_Felica_Write);

        btn_Felica_OpenNFC.setOnClickListener(new ButtonListen());
        btn_Felica_Polling.setOnClickListener(new ButtonListen());
        btn_Felica_Read.setOnClickListener(new ButtonListen());
        btn_Felica_Write.setOnClickListener(new ButtonListen());
        btn_Felica_CloseNFC.setOnClickListener(new ButtonListen());
    }

    class ButtonListen implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Button_Felica_OpenNFC:
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Message message = new Message();
                            message.what = OPEN_NFC_FELICA;
                            if(demo.OP_NFC_Device_Open() == OK) {
                                message.arg1 = OK;
                            }else{
                                message.arg1 = FAIL;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                case R.id.Button_Felica_Polling:
                    tv_Felica_Polling.setText("");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            byte[] outData = new byte[256];
                            int[] outLen = new int[1];
                            Message message = new Message();
                            message.what = FELICA_POLLING;

                            String editText = et_Felica_Polling.getText().toString();
                            byte[] hex = hexStringToBytes(editText);
                            int rc = demo.OP_Felica_polling(hex, outData, outLen);
                            if(rc == OK) {
                                byte[] buf = new byte[outLen[0]];
                                System.arraycopy(outData, 0, buf, 0, buf.length);
                                message.obj = buf;
                                message.arg1 = OK;
                            }else {
                                message.arg1 = FAIL;
                                message.arg2 = rc;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                case R.id.Button_Felica_Read:
                    tv_Felica_Read.setText("");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            byte[] outData = new byte[256];
                            int[] outLen = new int[1];
                            Message message = new Message();
                            message.what = FELICA_READ;

                            String editText = et_Felica_Read.getText().toString();
                            byte[] hex = hexStringToBytes(editText);
                            int rc = demo.OP_Felica_read(hex, outData, outLen);
                            if(rc == OK) {
                                byte[] buf = new byte[outLen[0]];
                                System.arraycopy(outData, 0, buf, 0, buf.length);
                                message.obj = buf;
                                message.arg1 = OK;
                            }else {
                                message.arg1 = FAIL;
                                message.arg2 = rc;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                case R.id.Button_Felica_Write:
                    tv_Felica_Write.setText("");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            byte[] outData = new byte[256];
                            int[] outLen = new int[1];
                            Message message = new Message();
                            message.what = FELICA_WRITE;

                            String editText = et_Felica_Write.getText().toString();
                            byte[] hex = hexStringToBytes(editText);
                            int rc = demo.OP_Felica_write(hex, outData, outLen);
                            if(rc == OK) {
                                byte[] buf = new byte[outLen[0]];
                                System.arraycopy(outData, 0, buf, 0, buf.length);
                                message.obj = buf;
                                message.arg1 = OK;
                            }else {
                                message.arg1 = FAIL;
                                message.arg2 = rc;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                case R.id.Button_Felica_CloseNFC:
//                    LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayout_test);
//                    if(linearLayout.getVisibility() == View.GONE) {
//                        linearLayout.setVisibility(View.VISIBLE);
//                    }else {
//                        linearLayout.setVisibility(View.GONE);
//                    }
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Message message = new Message();
                            message.what = CLOSE_NFC_FELICA;
                            if(demo.OP_NFC_Device_Close() == OK) {
                                message.arg1 = OK;
                            }else{
                                message.arg1 = FAIL;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                default:
                    break;
            }
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            layout.requestFocus();
        }
    }
}
