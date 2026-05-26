package com.common.demo.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;

import com.common.apiutil.CommonException;
import com.common.apiutil.printer.CheckC1Bprinter;
import com.common.apiutil.printer.GateOpenException;
import com.common.apiutil.printer.HardwareAlarmException;
import com.common.apiutil.printer.NoPaperException;
import com.common.apiutil.printer.OverHeatException;
import com.common.apiutil.printer.PaperCutException;
import com.common.apiutil.printer.ThermalPrinter;
import com.common.apiutil.printer.ThermalPrinterSY581;
import com.common.apiutil.serial.Serial;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.bean.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PrinterActivitySY581 extends BaseActivity {
    private static final String TAG = "PrinterActivitySY581";

    private static String printVersion;
    private Bitmap bitmap = null;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int EXECUTECOMMAND = 15;
    private final int PRINTLONGPICTURE = 16;
    private final int PRINTLONGTEXT = 17;
    private final int PRINTUNSUPPORTEDCONTENT = 18;
    private final int OPENPRINTERDOOR = 19;
    private final int OVERTIME = 20;
    private final int HARDWAREALARM = 21;
    private final int STATUSCUTWRONG = 22;

    private LinearLayout print_text, print_pic, print_comm;
    private TextView text_index, pic_index, comm_index, textPrintVersion, wordFont_textview, textview_gray, print_count,print_cut_count;
    MyHandler handler;
    private EditText editTextLeftDistance, editTextLineDistance, editTextWordFont, editTextPrintGray, editTextBarcode,
            editTextQrcode, editTextPaperWalk, editTextContent, edittext_maker_search_distance,
            edittext_maker_walk_distance, edittext_input_command, set_tem;
    private Button buttonBarcodePrint, buttonPaperWalkPrint, buttonContentPrint, buttonQrcodePrint,
            buttonGetExampleText, buttonGetZhExampleText, buttonClearText, button_maker, button_papercut, button_inverse,
            button_print_picture, button_execute_command, button_print_long_picture, button_print_long_text,
            button_print_georgia, check_status, print_setTem, getPrintExamplePersian, getPrintExampleFrance;
    private CheckBox cbPrintTextCount;
    private EditText edtPrintTextCount;
    private EditText edtPrintInterval;
    private TextView tvPrintTextLog;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;

    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.png";
    private String picturePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/222.png";
    private String picturePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/333.png";
    private String picturePath4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/444.png";

    Serial mSerialPort;

    InputStream mInputStream;
    ReadThread mReadThread;
    boolean doorIsOpen = false;
    boolean isNoPaper = false;
    private boolean printingLongPicture = false;
    private int printingLongText = 0;
    private int countLongPicture = 0;
    private boolean isDialogDismiss = false;
    private boolean circle = false;
    //static boolean xon = false;
    //static boolean xoff = false;
    private String tempVersion = null;

    //boolean isPersian = false;
    //boolean isFrance = false;
    boolean needInverse = false;
    public static String printContent1 = "\n             烧烤" + "\n----------------------------"
            + "\n日期：2015-01-01 16:18:20" + "\n卡号：12378945664" + "\n单号：1001000000000529142"
            + "\n----------------------------" + "\n  项目    数量   单价  小计" + "\n秘制烤羊腿  1    56    56"
            + "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
            + "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
            + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100"
            + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50"
            + "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
            + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
            + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56"
            + "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
            + "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
            + "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
            + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
            + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n 合计：1000：00元" + "\n----------------------------"
            + "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
            + "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";

    class ReadThread extends Thread {


        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    int size;
                    if (mInputStream != null && mInputStream.available() > 0) {
                        byte[] buffer = new byte[64];
                        size = mInputStream.read(buffer);

                        Log.d("printtest", StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)));

                        if (size > 0) {
                            Log.d(TAG, "sy581 receive buffer:" + StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)));
                            if (StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)).contains("14")) {
                                tempVersion = StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size));
                            }
                        }

                        if (size > 0) {
                            byte name = buffer[0];

                            if (StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)).equals("B0B0")) {
                                Log.d(TAG, "B0B0");
                                //B0,正常无故障
                                cancelProgressDialog();
                            } else if (name == (byte) 0xa8) {
                                Log.d("printtest", "print complete!");
                                //printTicket(getShoppingReceipt(), false, true);
                            	
                            	/*if(time < 3){
                            		time++;
                            		try {
    									Thread.sleep(500);
    								} catch (InterruptedException e) {
    									// TODO Auto-generated catch block
    									e.printStackTrace();
    								}
                            		printTicket(getShoppingReceipt(), false, true);
                            	}*/
                                if (printingLongPicture) {
                                    countLongPicture++;
                                    Log.d(TAG, "countLongPicture:" + countLongPicture);
                                    if (countLongPicture == /*3*/1) {
                                        printingLongPicture = false;
                                        countLongPicture = 0;
                                        cancelProgressDialog();
                                    }
                                } else if (printingLongText == 1 || printingLongText == 2) {
                                    if (printingLongText == 1) {
                                        printingLongText = 2;
                                        try {
                                            ThermalPrinter.addString("4\n" + getString(R.string.printContent1));
                                            ThermalPrinter.addString("5\n" + getString(R.string.printContent1));
                                            ThermalPrinter.addString("6\n" + getString(R.string.printContent1));
                                            if (useLanguage == null) {
                                                ThermalPrinter.printString();
                                            } else {
                                                ThermalPrinter.printString(useLanguage);
                                            }
                                        } catch (CommonException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    } else if (printingLongText == 2) {
                                        printingLongText = 0;
                                        try {
                                            ThermalPrinter.addString("7\n" + getString(R.string.printContent1));
                                            ThermalPrinter.addString("8\n" + getString(R.string.printContent1));
                                            if (useLanguage == null) {
                                                ThermalPrinter.printString();
                                            } else {
                                                ThermalPrinter.printString(useLanguage);
                                            }
                                        } catch (CommonException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    cancelProgressDialog();
                                }
                            } else if (name == (byte) 0xa7) {
                                ThermalPrinter.errorStop = true;
                                cancelProgressDialog();
                                Log.d(TAG, "print cut wrong");
                            } else if (name == (byte) 0xa5) {
                                ThermalPrinter.errorStop = true;
                                Log.d("tagg", "errorStop[" + ThermalPrinter.errorStop + "]");
                                Log.d(TAG, "print box open!");
                                cancelProgressDialog();
                                if (!doorIsOpen && !isNoPaper) {
                                    handler.sendMessage(handler.obtainMessage(OPENPRINTERDOOR, 1, 0, null));
                                }
                            } else if (name == (byte) 0xa1 || name == (byte) 0xb2) {
                                ThermalPrinter.errorStop = true;
                                circle = false;
                                Log.d(TAG, "print no paper");
                                cancelProgressDialog();
                                if (!doorIsOpen && !isNoPaper) {
                                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                                }
                            } else if (name == (byte) 0xa3) {
                                ThermalPrinter.errorStop = true;
                                Log.d(TAG, "print over tempreture");
                                cancelProgressDialog();
                                handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                            } else if (name == (byte) 0xa2) {
                                cancelProgressDialog();
                                runOnUiThread(new Runnable() {


                                    public void run() {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(PrinterActivitySY581.this, "打印机温度恢复正常!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.d(TAG, "print tempreture usual");
                            } else if (name == (byte) 0x11) {
                                Log.d(TAG, "on:print continue");
                                ThermalPrinter.xon = true;
                            } else if (name == (byte) 0x13) {
                                Log.d(TAG, "off:print stop");
                                ThermalPrinter.xon = false;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case HARDWAREALARM:
                    hardwareAlarmDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrinterActivitySY581.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (has80mmUsbPrinter){
                        print_count.setText(getString(R.string.print_count)+ThermalPrinter.getPrintCount());
                        print_cut_count.setText(getString(R.string.print_cut_count)+ThermalPrinter.getCutCount());
                    }

                    if (msg.obj.equals("1")) {
                        //textPrintVersion.setText(printVersion);
                        int index = printVersion.indexOf("+");
                        if (index > 0) {
                            textPrintVersion.setText(getString(R.string.hal_version) + printVersion.substring(0, index) + " " + getString(R.string.soft_version) + printVersion.substring(index + 1, printVersion.length()));
                        } else {
                            textPrintVersion.setText(printVersion);
                        }
                        if (has80mmUsbPrinter) {
                            textPrintVersion.append(" (USB)");
                        } else {
                            textPrintVersion.append(" (串口)");
                        }
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    //power_off.performClick();
                    break;
                case PRINTBARCODE:
                    new barcodePrintThread().start();
                    break;
                case PRINTQRCODE:
                    new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case MAKER:
                    new MakerThread().start();
                    break;
                case PRINTPICTURE:
                    new printPicture().start();
                    break;
                case CANCELPROMPT:

                    if (has80mmUsbPrinter && progressDialog != null && !PrinterActivitySY581.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        print_count.setText(getString(R.string.print_count)+ThermalPrinter.getPrintCount());
                    }else if(has80mmUsbPrinter && !PrinterActivitySY581.this.isFinishing()){

                        print_cut_count.setText(getString(R.string.print_cut_count)+ThermalPrinter.getCutCount());
                    }

                    //统计文字打印的时间
                    tvPrintTextLog.setText("Time[" + ThermalPrinter.getPrintTextTime() + "ms]");
                    break;
                case EXECUTECOMMAND:
                    new executeCommand().start();
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(PrinterActivitySY581.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    overHeatDialog.show();
                    break;
                case STATUSCUTWRONG:
                    AlertDialog.Builder cutWrongDialog = new AlertDialog.Builder(PrinterActivitySY581.this);
                    cutWrongDialog.setTitle(R.string.operation_result);
                    cutWrongDialog.setMessage(getString(R.string.paperCutError));
                    cutWrongDialog.setPositiveButton(getString(R.string.dialog_comfirm),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    cutWrongDialog.show();
                    break;
                case PRINTLONGPICTURE:
                    new printLongPicture().start();
                    break;
                case PRINTLONGTEXT:
                    new printLongText().start();
                    break;
                case PRINTUNSUPPORTEDCONTENT:
                    new printUnsupportedText().start();
                    break;
                case OPENPRINTERDOOR:
                    doorIsOpen = true;
                    AlertDialog.Builder openPrinterDoor = new AlertDialog.Builder(PrinterActivitySY581.this);
                    openPrinterDoor.setTitle(R.string.operation_result);
                    openPrinterDoor.setMessage(getString(R.string.openDoor));
                    openPrinterDoor.setPositiveButton(getString(R.string.dialog_comfirm),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    doorIsOpen = false;
                                }
                            });
                    openPrinterDoor.show();
                    break;
                case OVERTIME:
                    if (!isDialogDismiss)
                        cancelProgressDialog();
                    break;
                case 402:
                    if (circle) {
                        buttonContentPrint.performClick();
                    }
                default:
                    Toast.makeText(PrinterActivitySY581.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void initView() {
        print_text = (LinearLayout) findViewById(R.id.print_text);
        print_pic = (LinearLayout) findViewById(R.id.print_code_and_pic);
        print_comm = (LinearLayout) findViewById(R.id.print_comm);
        text_index = (TextView) findViewById(R.id.index_text);
        pic_index = (TextView) findViewById(R.id.index_pic);
        comm_index = (TextView) findViewById(R.id.index_comm);
    }

    ArrayAdapter<String> select_language_mAdapter;
    ArrayAdapter<String> mSelectPaperWidthAdapter;
    Spinner select_language;
    Spinner sprPaperWidth;
    String useLanguage = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tagg", "sy581 activity");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.print_text581);
        initView();
        //savepic();
        Log.d("TAG", "sy581 printer activity");
        handler = new MyHandler();
        button_papercut = (Button) findViewById(R.id.button_papercut);
        button_inverse = (Button) findViewById(R.id.button_inverse);
        buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
        registerReceiver(printReceive, pIntentFilter);

        editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
        editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
        editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
        editTextWordFont.setText("0");
        wordFont_textview = (TextView) findViewById(R.id.wordFont_textview);
        wordFont_textview.setText("0-4");
        editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
        editTextPrintGray.setText("2");
        textview_gray = (TextView) findViewById(R.id.textview_gray);
        textview_gray.setText("0-5");
        editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
        editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
        editTextContent = (EditText) findViewById(R.id.set_content);
        textPrintVersion = (TextView) findViewById(R.id.print_version);
        print_count = (TextView) findViewById(R.id.print_count);
        print_cut_count = (TextView) findViewById(R.id.print_cut_count);
        editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
        edittext_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
        edittext_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
        edittext_input_command = (EditText) findViewById(R.id.edittext_input_command);
        buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);
        set_tem = (EditText) findViewById(R.id.set_tem);
        print_setTem = (Button) findViewById(R.id.print_setTem);
        cbPrintTextCount = (CheckBox) findViewById(R.id.cbPrintCount);
        edtPrintTextCount = (EditText)findViewById(R.id.edtPrintTextCount);
        edtPrintInterval = (EditText)findViewById(R.id.edtPrintTextInterval);
        tvPrintTextLog = (TextView)findViewById(R.id.tvPrintTextLog);

        select_language = (Spinner) findViewById(R.id.select_language);
        select_language_mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        select_language_mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_language_mAdapter.add(getString(R.string.thailand_example));
        select_language_mAdapter.add(getString(R.string.cambodia_example));
        select_language_mAdapter.add(getString(R.string.laos_example));
        select_language_mAdapter.add(getString(R.string.chinese_example));
        select_language_mAdapter.add(getString(R.string.hongkong_example));
        select_language_mAdapter.add(getString(R.string.english_example));
        select_language_mAdapter.add(getString(R.string.russian_example));
        select_language_mAdapter.add(getString(R.string.portuguese_example));
        select_language_mAdapter.add(getString(R.string.spanish_example));
        select_language_mAdapter.add(getString(R.string.italian_example));
        select_language_mAdapter.add(getString(R.string.german_example));
        select_language_mAdapter.add(getString(R.string.persian_example));
        select_language_mAdapter.add(getString(R.string.french_example));
        select_language_mAdapter.add(getString(R.string.japanese_example));
        select_language_mAdapter.add(getString(R.string.korean_example));

        select_language.setAdapter(select_language_mAdapter);
        select_language.setOnItemSelectedListener(new OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectName = (String) select_language.getSelectedItem();

                String str = "";
                if (getString(R.string.chinese_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.CHINESE;
                    str = "\n             烧烤" + "\n----------------------------" + "\n日期：2015-01-01 16:18:20"
                            + "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n----------------------------"
                            + "\n  项目          数量    单价    小计"
                            + "\n秘制烤羊腿     1       56       56"
                            + "\n黯然牛排        2       24       48"
                            + "\n烤火鸡           2       50       100"
                            + "\n炭烧鳗鱼        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝树烧鸡     1       50       50"
                            + "\n冰镇乳鸽        2       23       46"
                            + "\n秘制烤羊腿     1       56       56"
                            + "\n黯然牛排        2       24       48"
                            + "\n烤火鸡           2       50       100"
                            + "\n炭烧鳗鱼        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝树烧鸡     1       50       50"
                            + "\n冰镇乳鸽        2       23       46"
                            + "\n秘制烤羊腿     1       56       56"
                            + "\n黯然牛排        2       24       48"
                            + "\n烤火鸡           2       50       100"
                            + "\n炭烧鳗鱼        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝树烧鸡     1       50       50"
                            + "\n冰镇乳鸽        2       23       46"
                            + "\n秘制烤羊腿     1       56       56"
                            + "\n黯然牛排        2       24       48"
                            + "\n烤火鸡           2       50       100"
                            + "\n炭烧鳗鱼        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝树烧鸡     1       50       50"
                            + "\n冰镇乳鸽        2       23       46"
                            + "\n秘制烤羊腿     1       56       56"
                            + "\n黯然牛排        2       24       48"
                            + "\n烤火鸡           2       50       100"
                            + "\n炭烧鳗鱼        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝树烧鸡     1       50       50"
                            + "\n冰镇乳鸽        2       23       46"
                            + "\n 合计：1000：00元" + "\n----------------------------"
                            + "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
                            + "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                } else if (getString(R.string.english_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.ENGLISH;
                    str = "\n-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                            + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                            + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                            + "IMEI:86378902177527\n" + "-----------------------------\n"
                            + "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                            + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                            + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                            + "IMEI:86378902177527\n" + "-----------------------------\n"
                            + "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                            + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                            + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                            + "IMEI:86378902177527\n" + "-----------------------------\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                } else if (getString(R.string.persian_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.PERSIAN;
                    str = "وقتی در مدرسه اولیه چهار کلاس بودم، پدرم به من یاد داد چگونه از کامپیوتر استفاده کنم. بعد از آن، من خیلی علاقه داشتم به بازی کامپیوتر. من فیلم‌ها را دیدم، بازی‌های کامپیوتری را بازی می‌کردم، اینترنت را جستجو می‌کردم و به موسیقی روی کامپیوتر گوش می‌دادم. چون پدرم باید توی کامپیوترش کار کنه، پس دیگه برام خرید. خيلي خوشحالم که کامپيوتر خودم رو داشتم در مطالعه قرار داده شد. من هميشه خيلي مراقب از استفاده از کامپيوترم هستم چون نگرانم که شکسته بشم اغلب با دوستان شبکه‌ام صحبت می‌کنم. به خاطر کامپیوترم، من خیلی از دوستان می سازم که خیلی مشترک دارند. البته، از کامپیوترم در مطالعه استفاده می کنم.\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if (getString(R.string.french_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.FRENCH;
                    str = "Mon père m'a appris à utiliser l'ordinateur en quatrième année de l'école primaire. Depuis lors, je m'intéresse beaucoup aux ordinateurs. Je regarde des films, je joue à des jeux informatiques, je surfe sur Internet, j'écoute de la musique sur mon ordinateur. Parce que mon père devait travailler sur son ordinateur, il m'en a acheté un autre. Je suis content d'avoir mon propre ordinateur. Il a été mis dans le Bureau. J'utilise toujours mon ordinateur très prudemment parce que j'ai peur de m'écraser. Je parle souvent à mes amis en ligne. À cause de mon ordinateur, je me suis fait beaucoup d'amis qui ont beaucoup en commun. Bien sûr, j'utilise l'ordinateur dans mes études. Il y a beaucoup de ressources en ligne. Je peux en apprendre plus après les cours. Ça m'a beaucoup aidé.\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if (getString(R.string.japanese_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.JAPANESE;
                    str = "小学校4年生のとき、父はコンピューターの使い方を教えてくれました。その後、コンピューターの演奏にとても興味がありました。私は映画を見て、コンピュータゲームをして、インターネットを捜して、コンピュータで音楽を聞きました。父はコンピューターで仕事をしなければならないので、別のものを買ってくれた。自分のコンピューターを持っていてとても嬉しかったです。それは研究に置かれました。私は私のコンピュータを使用することは常に注意してください、私はブレークする心配しているので。私はよく私のネット友達とチャット。私のコンピューターのおかげで、たくさんの友達がたくさんいます。もちろん、私は自分のコンピュータを勉強しています。インターネットにリソースがあります。授業の後でもっと学べる。それは私に大きな助けをする。\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if (getString(R.string.korean_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.KOREAN;
                    str = "내가 초등학교 4학년 때 아버지께서 나에게 컴퓨터를 어떻게 사용하는지 가르쳐 주셨다.그 이후로 나는 컴퓨터 게임에 대해 매우 흥미를 느꼈다.나는 영화를 보고, 컴퓨터 게임을 하고, 인터넷에 접속하고, 컴퓨터의 음악을 듣는다.나의 아버지는 반드시 그의 컴퓨터에서 일해야 하기 때문에, 그는 나에게 다른 한 대를 사주었다.나는 내 컴퓨터가 생겨서 매우 기쁘다.그것은 서재에 놓여 있다.나는 내가 붕괴될까 봐 항상 매우 조심스럽게 내 컴퓨터를 사용한다.나는 자주 나의 네티즌과 채팅을 한다.내 컴퓨터 때문에, 나는 많은 공통점이 있는 친구를 사귀었다.물론, 나는 학습 중에 컴퓨터를 사용한다.인터넷에는 많은 자원이 있다.수업 후에 나는 더 많은 것을 배울 수 있다.이것은 나에게 매우 큰 도움이 된다.\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if (getString(R.string.russian_example).equals(selectName)) {
                    useLanguage = ThermalPrinter.RUSSIAN;
                    str = "когда я учился в 4 - м классе, отец учил меня пользоваться компьютером. С тех пор я очень заинтересован в компьютерных играх. Я смотрю фильмы, играю в компьютерные игры, Фи, слушаю музыку на компьютере. Потому что мой отец должен работать в своем компьютере, поэтому он купил мне другой столик. Я рада, что у меня есть компьютер. Его поместили в кабинет. Я всегда очень осторожно использую свой компьютер, потому что боюсь, что я упаду. Я часто разговариваю со своими пользователями. Потому что у меня есть компьютер, и у меня есть много друзей, которые имеют много общего. Конечно, я использую компьютер в учебе. в Интернете много ресурсов. после занятий я могу учиться больше. Это очень помогло мне.\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if(getString(R.string.thailand_example).equals(selectName)){
                    useLanguage = ThermalPrinter.THAILAND;
//                    str = "ตอนอยู่ป. 4 พ่อสอนผมใช้คอมพิวเตอร์ฉันสนใจเล่นคอมพิวเตอร์หลังจากนั้นฉันดูหนังเล่นเกมคอมพิวเตอร์ค้นหาออนไลน์ และฟังเพลงจากคอมพิวเตอร์ของฉันซื้ออย่างอื่นให้ฉันเพราะพ่อต้องทำงานกับคอมพิวเตอร์ฉันดีใจ ที่มีคอมพิวเตอร์ของตัวเองคน ที่ถูกใส่ไว้ในงานวิจัยฉันใช้คอมพิวเตอร์ของฉันตลอดเวลาเพราะฉันกลัวว่า มันจะพังฉันคุยกับเพื่อนในเน็ตบ่อย ๆต้องขอบคุณคอมพิวเตอร์ของฉันฉันมีเพื่อนมากมายแน่นอนฉันกำลังเรียนคอมพิวเตอร์ของตัวเองฉันมีแหล่งข้อมูลออนไลน์เรียนรู้มากขึ้นหลังจากเรียนอันนั้น จะช่วยฉันได้มาก\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                    str = "เลขที่ใบเสร็จ: 987654321\n" +
                            "วันที่และเวลา: 24/02/2023 09:25:47\n" +
                            "ชื่อและที่อยู่ของบริษัท: บริษัท XYZ จำกัด\n" +
                            "456/78 ถนนพระรามสี่ เขตปทุมวัน กรุงเทพมหานคร 10330\n" +
                            "โทรศัพท์: 02-7654321\n" +
                            "ชื่อและที่อยู่ของลูกค้า: นาย ABC\n" +
                            "123/45 ถนนสุขุมวิท เขตวัฒนา กรุงเทพมหานคร 10110\n" +
                            "โทรศัพท์: 02-1234567\n" +
                            "รายการสินค้าหรือบริการ:\n" +
                            "- เครื่องปรับอากาศ (1) x (15000) = (15000)\n" +
                            "- เคียงปีกไก่ (5) x (15) = (75)\n" +
                            "- เคียงไส้กรอก (5) x (20) = (100)\n" +
                            "- เคียงผักสด (1) x (50) = (50)\n" +
                            "- เคียงผลไม้ต่างๆ (1) x (100) = (100)\n" +
                            "ยอดรวม: (15325)\n" +
                            "ภาษีมูลค่าเพิ่ม (%7): (1072.75)\n" +
                            "ส่วนลด (%5): (-766.25)\n" +
                            "จำนวนเงินชำระ: (15631.5)\n" +
                            "การชำระเงิน:\n" +
                            "- เงินสด: (-16000)\n" +
                            "- เงินทอน: (-368.5)\n" +
                            "\n" +
                            "_____________________________\n" +
                            "ผู้ให้บริการ / Service Provider\n" +
                            "\n" +
                            "_____________________________\n" +
                            "ผู้ได้รับบริการ / Service Recipient";

                } else if(getString(R.string.cambodia_example).equals(selectName)){
                    useLanguage = ThermalPrinter.CAMBODIA;
//                    str = "ពេល ខ្ញុំ នៅ ក្នុង ថ្នាក់ ទី បួន នៃ សាលា បឋម សិក្សា ឪពុក ខ្ញុំ បាន បង្រៀន ខ្ញុំ ឲ្យ ប្រើ កុំព្យូទ័រ ។ ក្រោយ ពី នោះ មក ខ្ញុំ ចាប់ អារម្មណ៍ នឹង ការ លេង កុំព្យូទ័រ។ ខ្ញុំ បាន មើល ភាពយន្ត លេង ហ្គេម កុំព្យូទ័រ ស្វែងរក អ៊ីនធឺណិត ហើយ បាន ស្ដាប់ តន្ត្រី នៅ លើ កុំព្យូទ័រ។ ដោយសារ ឪពុក ខ្ញុំ ត្រូវ តែ ធ្វើ ការ នៅ លើ កុំព្យូទ័រ គាត់ បាន ទិញ អ្វី ផ្សេង ទៀត ដល់ ខ្ញុំ ។ ខ្ញុំ រីករាយ ដែល ខ្ញុំ មាន កុំព្យូទ័រ ផ្ទាល់ ខ្លួន របស់ ខ្ញុំ ។ វា ត្រូវ បាន ដាក់ លើ ការ ស្រាវជ្រាវ នេះ ។ ខ្ញុំ បាន ប្រើ កុំព្យូទ័រ របស់ ខ្ញុំ ពីព្រោះ ខ្ញុំ ភ័យ ខ្លាច ថា វា នឹង ធ្លាក់ ។ ជាញឹកញាប់ ខ្ញុំ ជជែក ជាមួយ សំណាញ់ របស់ ខ្ញុំ ។ អរគុណដល់កុំព្យូទ័រខ្ញុំ ខ្ញុំមានមិត្តជាច្រើន។ ពិតណាស់ខ្ញុំរៀនកុំព្យូទ័រខ្លួនឯង។ ខ្ញុំ មាន ធនធាន តាម អ៊ិនធើរណែត ។ អ្នក អាច រៀន បន្ថែម ទៀត បន្ទាប់ ពី ថ្នាក់ រៀន ។ វា នឹង ជួយ ខ្ញុំ ច្រើន ។\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                    str = "លេខបង្កាន់ដៃ៖ 987654321\n" +
                            "កាលបរិច្ឆេទ និងពេលវេលា៖ 24/02/2023 09:25:47\n" +
                            "ឈ្មោះក្រុមហ៊ុន និងអាសយដ្ឋាន៖ XYZ Co., Ltd.\n" +
                            "456/78 ផ្លូវ Rama IV ស្រុក Pathum Wan បាងកក 10330\n" +
                            "ទូរស័ព្ទ៖ ០២-៧៦៥៤៣២១\n" +
                            "ឈ្មោះ និងអាសយដ្ឋានរបស់អតិថិជន៖ លោក ABC\n" +
                            "123/45 ផ្លូវ Sukhumvit, Watthana, Bangkok 10110\n" +
                            "ទូរស័ព្ទ៖ ០២-១២៣៤៥៦៧\n" +
                            "បញ្ជីផលិតផល ឬសេវាកម្ម៖\n" +
                            "- ម៉ាស៊ីនត្រជាក់ (1) x (15000) = (15000)\n" +
                            "- ស្លាបមាន់ (5) x (15) = (75)\n" +
                            "- សាច់ក្រក (5) x (20) = (100)\n" +
                            "- បន្លែស្រស់ (1) x (50) = (50)\n" +
                            "- ចំហៀងជាមួយផ្លែឈើផ្សេងៗ (1) x (100) = (100)\n" +
                            "សរុប៖ (១៥៣២៥)\n" +
                            "VAT (%7): (1072.75)\n" +
                            "ការបញ្ចុះតម្លៃ (%5): (-766.25)\n" +
                            "ចំនួនទឹកប្រាក់ទូទាត់៖ (15631.5)\n" +
                            "ការទូទាត់៖\n" +
                            "-សាច់ប្រាក់៖ (-១៦០០០)\n" +
                            "- ការផ្លាស់ប្តូរ៖ (-៣៦៨.៥)\n" +
                            "\n" +
                            "_____________________________\n" +
                            "អ្នកផ្តល់សេវា / អ្នកផ្តល់សេវា\n" +
                            "\n" +
                            "_____________________________\n" +
                            "អ្នកទទួលសេវា / អ្នកទទួលសេវា";
                } else if(getString(R.string.laos_example).equals(selectName)){
                    useLanguage = ThermalPrinter.LAOS;
//                    str = "ຕອນ ຂ້າ ພະ ເຈົ້າ ຮຽນ ຢູ່ ຊັ້ນ ປໍ ທີ ສີ່, ພໍ່ ຂອງ ຂ້າ ພະ ເຈົ້າ ໄດ້ ສອນ ຂ້າ ພະ ເຈົ້າ ໃຫ້ ໃຊ້ ຄອມ ພິວ ເຕີ. ຫຼັງ ຈາກ ນັ້ນ , ຂ້າ ພະ ເຈົ້າ ໄດ້ ສົນ ໃຈ ກັບ ການ ຫຼິ້ນ ຄອມ ພິວ ເຕີ . ຂ້າພະເຈົ້າໄດ້ເບິ່ງຮູບເງົາ, ຫຼິ້ນເກມຄອມພິວເຕີ, ຄົ້ນຫາອິນເຕີເນັດ, ແລະຟັງເພງໃນຄອມພິວເຕີ. ເນື່ອງຈາກວ່າພໍ່ຂອງຂ້ອຍຕ້ອງເຮັດວຽກໃນຄອມພີວເຕີ ລາວຈຶ່ງຊື້ສິ່ງອື່ນໃຫ້ຂ້ອຍ. ຂ້ອຍດີໃຈທີ່ຂ້ອຍມີຄອມພິວເຕີຂອງຂ້ອຍເອງ. ທີ່ໄດ້ໃສ່ໃນການຄົ້ນຄວ້າ. ຂ້ອຍເຄີຍໃຊ້ຄອມພີວເຕີຂອງຂ້ອຍເພາະຢ້ານວ່າມັນຈະຕົກ. ຂ້ອຍມັກລົມກັບnetizens ຂອງຂ້ອຍ. ຂອບໃຈຄອມພິວເຕີຂອງຂ້ອຍ, ຂ້ອຍມີຫມູ່ເພື່ອນຫຼາຍຄົນ. ແນ່ນອນ, ຂ້ອຍກໍາລັງຮຽນຮູ້ຄອມພິວເຕີຂອງຂ້ອຍເອງ. ຂ້ອຍມີຊັບພະຍາກອນອອນໄລນ໌. ທ່ານສາມາດຮຽນຮູ້ເພີ່ມເຕີມຫຼັງຈາກຫ້ອງຮຽນ. ນັ້ນຈະຊ່ວຍຂ້ອຍໄດ້ຫຼາຍ.\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                    str = "ໝາຍເລກຮັບ: 987654321\n" +
                            "ວັນທີ ແລະ ເວລາ: 24/02/2023 09:25:47\n" +
                            "ຊື່ບໍລິສັດ ແລະທີ່ຢູ່: XYZ Co., Ltd.\n" +
                            "456/78 ຖະໜົນ ຣາມາ IV, ເມືອງ ປະທຸມວັນ, ບາງກອກ 10330\n" +
                            "ເບີໂທ: 02-7654321\n" +
                            "ຊື່ ແລະທີ່ຢູ່ຂອງລູກຄ້າ: ທ່ານ ABC\n" +
                            "123/45 ຖະໜົນສຸຂຸມາ, ວັດທະນາ, ບາງກອກ 10110\n" +
                            "ໂທລະສັບ: 02-1234567\n" +
                            "ລາຍຊື່ຜະລິດຕະພັນ ຫຼືບໍລິການ:\n" +
                            "- ແອ (1) x (15000) = (15000)\n" +
                            "- ປີກໄກ່ (5) x (15) = (75)\n" +
                            "- ໄສ້ກອກ (5) x (20) = (100)\n" +
                            "- ຜັກສົດ (1) x (50) = (50)\n" +
                            "- ຂ້າງທີ່ມີຫມາກໄມ້ຕ່າງໆ (1) x (100) = (100)\n" +
                            "ທັງໝົດ: (15325)\n" +
                            "VAT (%7): (1072.75)\n" +
                            "ສ່ວນຫຼຸດ (%5): (-766.25)\n" +
                            "ຈໍານວນການຈ່າຍເງິນ: (15631.5)\n" +
                            "ການຈ່າຍເງິນ:\n" +
                            "- ເງິນສົດ: (-16000)\n" +
                            "- ການປ່ຽນແປງ: (-368.5)\n" +
                            "\n" +
                            "_____________________________\n" +
                            "ຜູ້ໃຫ້ບໍລິການ / ຜູ້ໃຫ້ບໍລິການ\n" +
                            "\n" +
                            "_____________________________\n" +
                            "ຜູ້ຮັບການບໍລິການ / ຜູ້ຮັບການບໍລິການ";
                } else if (getString(R.string.portuguese_example).equals(
                        selectName)) {
                    useLanguage = ThermalPrinter.PORTUGUESE;
                    str = "\n-----------------------------\n"
                            + "Teste de Impressão:\n"
                            + "Informações Básicas do Dispositivo\n"
                            + "Versão da Impressora:\n"
                            + "V05.2.0.3\n"
                            + "Nível de Cinza da Impressora:3\n"
                            + "Versão do Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nível da Bateria:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Teste de Impressão:\n"
                            + "Informações Básicas do Dispositivo\n"
                            + "Versão da Impressora:\n"
                            + "V05.2.0.3\n"
                            + "Nível de Cinza da Impressora:3\n"
                            + "Versão do Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nível da Bateria:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Teste de Impressão:\n"
                            + "Informações Básicas do Dispositivo\n"
                            + "Versão da Impressora:\n"
                            + "V05.2.0.3\n"
                            + "Nível de Cinza da Impressora:3\n"
                            + "Versão do Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nível da Bateria:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                } else if (getString(R.string.spanish_example).equals(
                        selectName)) {
                    useLanguage = ThermalPrinter.SPANISH;
                    str = "\n-----------------------------\n"
                            + "Prueba de Impresión:\n"
                            + "Información Básica del Dispositivo\n"
                            + "Versión de la Impresora:\n"
                            + "V05.2.0.3\n"
                            + "Nivel de Gris de la Impresora:3\n"
                            + "Versión de Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nivel de Batería:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Prueba de Impresión:\n"
                            + "Información Básica del Dispositivo\n"
                            + "Versión de la Impresora:\n"
                            + "V05.2.0.3\n"
                            + "Nivel de Gris de la Impresora:3\n"
                            + "Versión de Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nivel de Batería:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Prueba de Impresión:\n"
                            + "Información Básica del Dispositivo\n"
                            + "Versión de la Impresora:\n"
                            + "V05.2.0.3\n"
                            + "Nivel de Gris de la Impresora:3\n"
                            + "Versión de Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Nivel de Batería:100%\n"
                            + "Valor CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                } else if (getString(R.string.italian_example).equals(
                        selectName)) {
                    useLanguage = ThermalPrinter.ITALIAN;
                    str = "\n-----------------------------\n"
                            + "Test di Stampa:\n"
                            + "Informazioni di Base Dispositivo\n"
                            + "Versione Stampante:\n"
                            + "V05.2.0.3\n"
                            + "Livello di Grigio Stampante:3\n"
                            + "Versione Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Livello Batteria:100%\n"
                            + "Valore CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Test di Stampa:\n"
                            + "Informazioni di Base Dispositivo\n"
                            + "Versione Stampante:\n"
                            + "V05.2.0.3\n"
                            + "Livello di Grigio Stampante:3\n"
                            + "Versione Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Livello Batteria:100%\n"
                            + "Valore CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Test di Stampa:\n"
                            + "Informazioni di Base Dispositivo\n"
                            + "Versione Stampante:\n"
                            + "V05.2.0.3\n"
                            + "Livello di Grigio Stampante:3\n"
                            + "Versione Software:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Livello Batteria:100%\n"
                            + "Valore CSQ:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

                } else if (getString(R.string.german_example).equals(
                        selectName)) {
                    useLanguage = ThermalPrinter.GERMAN;
                    str = "\n-----------------------------\n"
                            + "Drucktest:\n"
                            + "Gerätebasisinformationen\n"
                            + "Druckerversion:\n"
                            + "V05.2.0.3\n"
                            + "Druckgrau:3\n"
                            + "Softwareversion:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Batteriestand:100%\n"
                            + "CSQ-Wert:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Drucktest:\n"
                            + "Gerätebasisinformationen\n"
                            + "Druckerversion:\n"
                            + "V05.2.0.3\n"
                            + "Druckgrau:3\n"
                            + "Softwareversion:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Batteriestand:100%\n"
                            + "CSQ-Wert:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n"
                            + "-----------------------------\n"
                            + "Drucktest:\n"
                            + "Gerätebasisinformationen\n"
                            + "Druckerversion:\n"
                            + "V05.2.0.3\n"
                            + "Druckgrau:3\n"
                            + "Softwareversion:\n"
                            + "Demo.G50.0.Build140313\n"
                            + "Batteriestand:100%\n"
                            + "CSQ-Wert:24\n"
                            + "IMEI:86378902177527\n"
                            + "-----------------------------\n!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
                } else if (getString(R.string.hongkong_example).equals(
                        selectName)) {
                    useLanguage = ThermalPrinter.HONGKONG_CHINA;
                    str = "\n             燒烤" + "\n----------------------------" + "\n日期：2015-01-01 16:18:20"
                            + "\n卡號：12378945664" + "\n單號：1001000000000529142" + "\n----------------------------"
                            + "\n  項目          數量    單價    小計"
                            + "\n秘製烤羊腿     1       56       56"
                            + "\n暗然牛排        2       24       48"
                            + "\n烤火雞           2       50       100"
                            + "\n炭燒鰻魚        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝樹燒雞     1       50       50"
                            + "\n冰鎮乳鴿        2       23       46"
                            + "\n秘製烤羊腿     1       56       56"
                            + "\n暗然牛排        2       24       48"
                            + "\n烤火雞           2       50       100"
                            + "\n炭燒鰻魚        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝樹燒雞     1       50       50"
                            + "\n冰鎮乳鴿        2       23       46"
                            + "\n秘製烤羊腿     1       56       56"
                            + "\n暗然牛排        2       24       48"
                            + "\n烤火雞           2       50       100"
                            + "\n炭燒鰻魚        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝樹燒雞     1       50       50"
                            + "\n冰鎮乳鴿        2       23       46"
                            + "\n秘製烤羊腿     1       56       56"
                            + "\n暗然牛排        2       24       48"
                            + "\n烤火雞           2       50       100"
                            + "\n炭燒鰻魚        1       40       40"
                            + "\n烤全羊           1       200     200"
                            + "\n荔枝樹燒雞     1       50       50"
                            + "\n冰鎮乳鴿        2       23       46"
                            + "\n 合計：1000：00元" + "\n----------------------------"
                            + "\n本卡金額：10000.00" + "\n累計消費：1000.00" + "\n本卡結餘：9000.00" + "\n----------------------------"
                            + "\n 地址：廣東省佛山市南海區桂城街道桂瀾南路45號鵬瑞利廣場A317.B-18號舖" + "\n歡迎您的再次光臨\n!";
                }

                editTextContent.setText(str);
            }


            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprPaperWidth = (Spinner) findViewById(R.id.sprPaperWidth);
        mSelectPaperWidthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSelectPaperWidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectPaperWidthAdapter.add(getString(R.string.printer_paper_80mm));
        mSelectPaperWidthAdapter.add(getString(R.string.printer_paper_58mm));
        sprPaperWidth.setAdapter(mSelectPaperWidthAdapter);
        ThermalPrinter.setPaperWidth(ThermalPrinter.PAPER_80mm);
        sprPaperWidth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectName = (String) sprPaperWidth.getSelectedItem();
                if (getString(R.string.printer_paper_80mm).equals(selectName)) {
                    ThermalPrinter.setPaperWidth(ThermalPrinter.PAPER_80mm);
                }else if (getString(R.string.printer_paper_58mm).equals(selectName)) {
                    ThermalPrinter.setPaperWidth(ThermalPrinter.PAPER_58mm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        print_setTem.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (set_tem.getText().toString().equals("")) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.tem_cannt_null), Toast.LENGTH_LONG).show();
                    return;
                }
                int protectTem = Integer.parseInt(set_tem.getText().toString());
                if (protectTem < 60 || protectTem > 127) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.tem_set_wrong), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    ThermalPrinter.setTem(protectTem);
                } catch (CommonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Toast.makeText(PrinterActivitySY581.this, getString(R.string.tem_set_success), Toast.LENGTH_LONG).show();
            }
        });

        buttonQrcodePrint.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                qrcodeStr = editTextQrcode.getText().toString();
                if (qrcodeStr == null || qrcodeStr.length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this,
								getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.D_barcode_loading));
                        progressDialog.setMessage(getString(R.string.generate_barcode_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        editTextContent.setOnTouchListener(new OnTouchListener() {


            public boolean onTouch(View v, MotionEvent arg1) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        buttonBarcodePrint.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                barcodeStr = editTextBarcode.getText().toString();
                if (barcodeStr == null || barcodeStr.length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonPaperWalkPrint = (Button) findViewById(R.id.print_paperWalk);
        buttonPaperWalkPrint.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                String exditText;
                exditText = editTextPaperWalk.getText().toString();
                if (exditText == null || exditText.length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.walk_paper_intput_value), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                paperWalk = Integer.parseInt(exditText);
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonClearText = (Button) findViewById(R.id.clearText);
        buttonClearText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                editTextContent.setText("");
            }
        });
        buttonGetExampleText = (Button) findViewById(R.id.getPrintExample);
        buttonGetExampleText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = "\n-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                        + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                        + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                        + "IMEI:86378902177527\n" + "-----------------------------\n"
                        + "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                        + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                        + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                        + "IMEI:86378902177527\n" + "-----------------------------\n"
                        + "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
                        + "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
                        + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
                        + "IMEI:86378902177527\n" + "-----------------------------\n";
                editTextContent.setText(str);
            }
        });

        buttonGetZhExampleText = (Button) findViewById(R.id.getZhPrintExample);
        buttonGetZhExampleText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String str = "\n             烧烤" + "\n----------------------------" + "\n日期：2015-01-01 16:18:20"
                        + "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n----------------------------"
                        + "\n  项目          数量    单价    小计"
                        + "\n秘制烤羊腿     1       56       56"
                        + "\n黯然牛排        2       24       48"
                        + "\n烤火鸡           2       50       100"
                        + "\n炭烧鳗鱼        1       40       40"
                        + "\n烤全羊           1       200     200"
                        + "\n荔枝树烧鸡     1       50       50"
                        + "\n冰镇乳鸽        2       23       46"
                        + "\n秘制烤羊腿     1       56       56"
                        + "\n黯然牛排        2       24       48"
                        + "\n烤火鸡           2       50       100"
                        + "\n炭烧鳗鱼        1       40       40"
                        + "\n烤全羊           1       200     200"
                        + "\n荔枝树烧鸡     1       50       50"
                        + "\n冰镇乳鸽        2       23       46"
                        + "\n秘制烤羊腿     1       56       56"
                        + "\n黯然牛排        2       24       48"
                        + "\n烤火鸡           2       50       100"
                        + "\n炭烧鳗鱼        1       40       40"
                        + "\n烤全羊           1       200     200"
                        + "\n荔枝树烧鸡     1       50       50"
                        + "\n冰镇乳鸽        2       23       46"
                        + "\n秘制烤羊腿     1       56       56"
                        + "\n黯然牛排        2       24       48"
                        + "\n烤火鸡           2       50       100"
                        + "\n炭烧鳗鱼        1       40       40"
                        + "\n烤全羊           1       200     200"
                        + "\n荔枝树烧鸡     1       50       50"
                        + "\n冰镇乳鸽        2       23       46"
                        + "\n秘制烤羊腿     1       56       56"
                        + "\n黯然牛排        2       24       48"
                        + "\n烤火鸡           2       50       100"
                        + "\n炭烧鳗鱼        1       40       40"
                        + "\n烤全羊           1       200     200"
                        + "\n荔枝树烧鸡     1       50       50"
                        + "\n冰镇乳鸽        2       23       46"
                        + "\n 合计：1000：00元" + "\n----------------------------"
                        + "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
                        + "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
                editTextContent.setText(str);
            }
        });

        getPrintExamplePersian = (Button) findViewById(R.id.getPrintExamplePersian);
        getPrintExamplePersian.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String str = "مرحبا بكم في استخدام ذكي نقاط البيع";
                editTextContent.setText(str);
            }
        });

        getPrintExampleFrance = (Button) findViewById(R.id.getPrintExampleFrance);
        getPrintExampleFrance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String str = "!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜÁÂÀÊËÈ€Ô°\n\n" +
                        "!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜÁÂÀÊËÈ€Ô°\n\n" +
                        "!\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜÁÂÀÊËÈ€Ô°\n\n---------";
                editTextContent.setText(str);
            }
        });

        buttonContentPrint = (Button) findViewById(R.id.print_content);//鏂囧瓧鎵撳嵃鎸夐挳
        buttonContentPrint.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                String exditText;
                exditText = editTextLeftDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                leftDistance = Integer.parseInt(exditText);
                exditText = editTextLineDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                lineDistance = Integer.parseInt(exditText);
                printContent = editTextContent.getText().toString();
                exditText = editTextWordFont.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                wordFont = Integer.parseInt(exditText);
                exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (leftDistance > MAX_LEFT_DISTANCE) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
                    return;
                } else if (lineDistance > 255 || lineDistance < 1) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
                    return;
                } else if (wordFont > 4 || wordFont < 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
                    return;
                } else if (printGray < 0 || printGray > 5) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (printContent == null || printContent.length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
                        //progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
                        //		getString(R.string.printing_wait));
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
                        if (circle)
                            handler.sendEmptyMessageDelayed(402, 5000);
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        button_maker = (Button) findViewById(R.id.button_maker);
        button_maker.setVisibility(View.GONE);
        button_maker.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                if (edittext_maker_search_distance.getText().length() == 0
                        || edittext_maker_walk_distance.getText().length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(edittext_maker_search_distance.getText().toString()) < 0
                        || Integer.parseInt(edittext_maker_search_distance.getText().toString()) > 255) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(edittext_maker_walk_distance.getText().toString()) < 0
                        || Integer.parseInt(edittext_maker_walk_distance.getText().toString()) > 255) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.maker),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.maker));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_papercut.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            checkStatus();
                            ThermalPrinter.paperCut();
                        } catch (Exception e) {
                            e.printStackTrace();
                            handleExceptions(e);
//                            Result = e.toString();
//                                nopaper = true;
//                                handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                            } else {
//                                handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                            }
                        } finally {
                            handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                            if (nopaper) {
//                                handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                                nopaper = false;
//                                return;
//                            }
                            //ThermalPrinter.stop(PrinterActivity.this);
                        }
                    }
                }).start();
            }
        });

        button_inverse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                needInverse = !needInverse;
                if (needInverse) {
                    button_inverse.setText(getString(R.string.not_inverse));
                } else {
                    button_inverse.setText(getString(R.string.inverse));
                }
                try {
                    ThermalPrinter.setInverse(needInverse);
                } catch (CommonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        button_print_picture = (Button) findViewById(R.id.button_print_picture);
        button_print_picture.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_execute_command = (Button) findViewById(R.id.button_execute_command);
        button_execute_command.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                if (edittext_input_command.getText().toString() == null
                        || edittext_input_command.getText().toString().length() == 0) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(EXECUTECOMMAND, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_print_long_picture = (Button) findViewById(R.id.button_print_long_picture);
        button_print_long_picture.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTLONGPICTURE, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_print_long_text = (Button) findViewById(R.id.button_print_long_text);
        button_print_long_text.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTLONGTEXT, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_print_georgia = (Button) findViewById(R.id.print_georgia);
        button_print_georgia.setVisibility(View.GONE);
        button_print_georgia.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                String exditText;
                exditText = editTextLeftDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                leftDistance = Integer.parseInt(exditText);
                exditText = editTextLineDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                lineDistance = Integer.parseInt(exditText);
                printContent = editTextContent.getText().toString();
                exditText = editTextWordFont.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                wordFont = Integer.parseInt(exditText);
                exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(PrinterActivitySY581.this,
                            getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (leftDistance > MAX_LEFT_DISTANCE) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
                    return;
                } else if (lineDistance > 255 || lineDistance < 1) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
                    return;
                } else if (wordFont > 4 || wordFont < 1) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
                    return;
                } else if (printGray < 0 || printGray > 12) {
                    Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (printContent == null || printContent.length() == 0) {
                    printContent = "აშო, ჩელა, ვიშო ბუსქა,ოუ, ნანა, ოუ ნანა, ნანინა.სი მონობაშ, გეგაფილი,ოუ, ნანა, სკალი ცოდვა, ნანინა.\n"
                            + "მისამღერი:.ოუ, ნანა, ოუ ნანა, ნანინა…სი უჩხონჩხე, სი უგურე,ოუ, ნანა, სკალი ცოდვა, ნანინა.სი კისერი ელაფირი.ოუ, ნანა, სკალი ცოდვა, ნანინა.მისამღერი..";
                    editTextContent.setText(printContent);
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(PrinterActivitySY581.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(getString(R.string.bl_dy));
                        progressDialog.setMessage(getString(R.string.printing_wait));
                        progressDialog.show();
                        handler.sendMessage(handler.obtainMessage(PRINTUNSUPPORTEDCONTENT, 1, 0, null));
                    } else {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        check_status = (Button) findViewById(R.id.check_status);

        check_status.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                int status = 0;
                try {
                    status = ThermalPrinter.checkStatus();
                    Log.d("tagg","status = " + status);
                    if (status == ThermalPrinter.STATUS_BOX_OPEN) {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.openDoor), Toast.LENGTH_SHORT).show();
                    } else if (status == ThermalPrinter.STATUS_CUT_WRONG) {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.paperCutError), Toast.LENGTH_SHORT).show();
                    } else if (status == ThermalPrinter.STATUS_NO_PAPER) {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.noPaper), Toast.LENGTH_SHORT).show();
                    } else if (status == ThermalPrinter.STATUS_OVER_HEAT) {
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.overTemp), Toast.LENGTH_SHORT).show();
                    }else if(status == ThermalPrinter.STATUS_OK){
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.coverColse), Toast.LENGTH_SHORT).show();
                    }else if(status == ThermalPrinter.STATUS_RESET){
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.printerReset), Toast.LENGTH_SHORT).show();
                    }else if(status == ThermalPrinter.STATUS_PAPER_ALREADY){
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.paperReady), Toast.LENGTH_SHORT).show();
                    }else if(status == ThermalPrinter.STATUS_HARDWARE_ALARM){
                        Toast.makeText(PrinterActivitySY581.this, getString(R.string.hardwareAlarm), Toast.LENGTH_SHORT).show();
                    }
                } catch (CommonException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void checkStatus() throws CommonException {
        int status = ThermalPrinter.checkStatus();
        if (has80mmUsbPrinter) {
            if (status == ThermalPrinter.STATUS_BOX_OPEN) {
//                handler.sendMessage(handler.obtainMessage(OPENPRINTERDOOR, 1, 0, null));
                throw new GateOpenException();
            } else if (status == ThermalPrinter.STATUS_CUT_WRONG) {
                throw new PaperCutException();
            } else if (status == ThermalPrinter.STATUS_NO_PAPER) {
//                handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                throw new NoPaperException();
            } else if (status == ThermalPrinter.STATUS_OVER_HEAT) {
//                handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                throw new OverHeatException();
            } else if (status == ThermalPrinter.STATUS_HARDWARE_ALARM) {
                throw new HardwareAlarmException();
            }
        }
    }

    private void handleExceptions(Exception e){
        if (e instanceof GateOpenException) {
            handler.sendMessage(handler.obtainMessage(OPENPRINTERDOOR, 1, 0, null));
        } else if (e instanceof PaperCutException) {
            handler.sendMessage(handler.obtainMessage(STATUSCUTWRONG, 1, 0, null));
        } else if (e instanceof  NoPaperException) {
            handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
        } else if (e instanceof  OverHeatException) {
            handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
        } else if (e instanceof  HardwareAlarmException) {
            handler.sendMessage(handler.obtainMessage(HARDWAREALARM, 1, 0, null));
        }
    }

    public void selfCheck(View view) {
        ThermalPrinterSY581.selfCheck();
		/*for(int i=0;i<3;i++){
		
			printTicket(getShoppingReceipt(), false, true);
			
		}*/

    }


    boolean getVersion;
    boolean has80mmUsbPrinter = false;

    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        dialog = new ProgressDialog(PrinterActivitySY581.this);
        dialog.setTitle(R.string.idcard_czz);
        dialog.setMessage(getText(R.string.watting));
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {


            public void run() {
                try {
                    if ("USB".equalsIgnoreCase(SystemUtil.getProperty("persist.printer.interface", ""))) {
                        has80mmUsbPrinter = ThermalPrinter.init80mmUsbPrinter(PrinterActivitySY581.this);
                        Log.d("tagg", "hasPrinterTest=" + has80mmUsbPrinter);
                    } else {
                        ThermalPrinter.init80mmSerialPrinter();
                        has80mmUsbPrinter = false;
                    }
                    ThermalPrinter.start(PrinterActivitySY581.this);
//                    checkStatus();
                    printVersion = ThermalPrinter.getVersion();
                    Log.d("tagg", "printVersion:" + printVersion);
                    getVersion = true;
                    if (!has80mmUsbPrinter) {
                        try {
                            int deviceType = SystemUtil.getDeviceType();
                            if (deviceType == StringUtil.DeviceModelEnum.TPS650T.ordinal()) {
                                mSerialPort = new Serial("/dev/ttyS0", SystemUtil.getPrinterSY581Baudrate(), 0);
                            } else if (deviceType == StringUtil.DeviceModelEnum.C1B.ordinal()) {
                                mSerialPort = new Serial(CheckC1Bprinter.checkSerialFromFile(), SystemUtil.getPrinterSY581Baudrate(), 0);
                            } else if ("C1".equals(SystemUtil.getInternalModel())) {
                                mSerialPort = new Serial("dev/ttyUSB0", SystemUtil.getPrinterSY581Baudrate(), 0);
                            } else if ("C1P".equals(SystemUtil.getInternalModel())) {
                                mSerialPort = new Serial("dev/ttyS0", SystemUtil.getPrinterSY581Baudrate(), 0);
                            } else if (deviceType == StringUtil.DeviceModelEnum.TPS680.ordinal()) {
                                mSerialPort = new Serial("/dev/ttyACM0", SystemUtil.getPrinterSY581Baudrate(), 0);
                            } else {
                                mSerialPort = new Serial("/dev/ttyS4", SystemUtil.getPrinterSY581Baudrate(), 0);
                            }
                        } catch (SecurityException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        if (mSerialPort != null)
                            mInputStream = mSerialPort.getInputStream();
                        mReadThread = new ReadThread();
                        mReadThread.start();
                    }

                    checkStatus();
                } catch (CommonException e) {
                    e.printStackTrace();
                    handleExceptions(e);
                } finally {
                    if (printVersion != null) {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "1";
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "0";
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                // TPS390 can not print,while in low battery,whether is charging or not charging
                if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()) {
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level * 5 <= scale) {
                            LowBattery = true;
                        } else {
                            LowBattery = false;
                        }
                    } else {
                        LowBattery = false;
                    }
                }
            }

            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if (status == 0) {
                    if (level < 1) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    LowBattery = false;
                }
            }
        }
    };

    private void noPaperDlg() {

        //ThermalPrinter.stop(PrinterActivity.this);
        isNoPaper = true;
        AlertDialog.Builder dlg = new AlertDialog.Builder(PrinterActivitySY581.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                isNoPaper = false;
            }
        });
        try {
            dlg.show();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void hardwareAlarmDlg() {

        AlertDialog.Builder dlg = new AlertDialog.Builder(PrinterActivitySY581.this);
        dlg.setTitle(R.string.operation_result);
        dlg.setMessage(getString(R.string.hardwareAlarm));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        try {
            dlg.show();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private class paperWalkPrintThread extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.walkPaper(paperWalk);
                handler.sendEmptyMessageDelayed(OVERTIME, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    //涓�缁寸爜
    private class barcodePrintThread extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.setGray(printGray);
                Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
                if (bitmap != null) {
                    ThermalPrinter.printLogo(bitmap);
                }
                //Thread.sleep(500);
                ThermalPrinter.addString(barcodeStr);
                if (useLanguage == null) {
                    ThermalPrinter.printString();
                } else {
                    ThermalPrinter.printString(useLanguage);
                }
                ThermalPrinter.walkPaper(200);
                handler.sendEmptyMessageDelayed(OVERTIME, 2000);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class qrcodePrintThread extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.setGray(printGray);
                Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
                if (bitmap != null) {
                    ThermalPrinter.printLogo(bitmap);
                }
                //Thread.sleep(500);
                ThermalPrinter.addString(qrcodeStr);
                if (useLanguage == null) {
                    ThermalPrinter.printString();
                } else {
                    ThermalPrinter.printString(useLanguage);
                }
                ThermalPrinter.walkPaper(200);
                handler.sendEmptyMessageDelayed(OVERTIME, 2000);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }
//    long contentPrintStartTime = 0;
//    long contentPrintEndTime = 0;
    private class contentPrintThread extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                ThermalPrinter.errorStop = false;
                checkStatus();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                ThermalPrinter.setLeftIndent(leftDistance);
                ThermalPrinter.setLineSpace(lineDistance);
                if (wordFont == 0) {
                    ThermalPrinter.setFontSize(0);
                } else if (wordFont == 1) {
                    ThermalPrinter.setFontSize(1);
                } else if (wordFont == 2) {
                    ThermalPrinter.setFontSize(2);
                } else if (wordFont == 3) {
                    ThermalPrinter.setFontSize(3);
                } else if (wordFont == 4) {
                    ThermalPrinter.setFontSize(4);
                } else if (wordFont == 5) {
                    ThermalPrinter.setFontSize(5);
                } else if (wordFont == 6) {
                    ThermalPrinter.setFontSize(6);
                } else if (wordFont == 7) {
                    ThermalPrinter.setFontSize(7);
                }
                ThermalPrinter.setGray(printGray);
                if(cbPrintTextCount.isChecked()){
                    String count = edtPrintTextCount.getText().toString();
                    String interval = edtPrintInterval.getText().toString();
                    if(!count.isEmpty() && !interval.isEmpty()){
                        for(int i=0; i<Integer.parseInt(count); i++){
                            ThermalPrinter.addString(printContent);

                            if (useLanguage == null) {
                                ThermalPrinter.printString();
                            } else {
                                ThermalPrinter.printString(useLanguage);
                            }

                            Thread.sleep(Integer.parseInt(interval));
                        }
                    }
                }else{
                    ThermalPrinter.addString(printContent);
                    if (useLanguage == null) {
                        ThermalPrinter.printString();
                    } else {
                        ThermalPrinter.printString(useLanguage);
                    }
                }

                ThermalPrinter.walkPaper(200);
                handler.sendEmptyMessageDelayed(OVERTIME, 10000);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class MakerThread extends Thread {


        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
                        Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class printPicture extends Thread {


        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);


                checkStatus();
                ThermalPrinter.setGray(printGray);
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);

                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.syhlogo);
                //ThermalPrinter.printLogo581Left(ThermalPrinter.ALGIN_LEFT, getBitmap(PrinterActivitySY581.this, R.drawable.syhlogo));

                Bitmap bitmap = getBitmap(PrinterActivitySY581.this, R.drawable.black);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 576, 2000);
                ThermalPrinter.printLogo(bitmap);
                ThermalPrinter.walkPaper(200);
//                ThermalPrinter.printLogo(bitmap);
//                ThermalPrinter.walkPaper(200);
//                ThermalPrinter.printLogo(bitmap);
//                ThermalPrinter.walkPaper(200);
//                ThermalPrinter.printLogo(bitmap);
//                ThermalPrinter.walkPaper(200);

//                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.black));
//                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.black));
//                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.black));

//                ThermalPrinter.walkPaper(200);
//                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.p001));
//                ThermalPrinter.walkPaper(200);
                handler.sendEmptyMessageDelayed(OVERTIME, 4000);


                //巨龙测试打印
				/*while (true && !isNoPaper) {
					ThermalPrinter.errorStop = false;
					checkStatus();
					ThermalPrinter.setGray(printGray);
					ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
					
					Bitmap bitmap = getBitmap(PrinterActivitySY581.this, R.drawable.head);
					ThermalPrinter.printLogo(bitmap);
					
					sleep(2000);
					
					ThermalPrinter.paperCut();
					
					sleep(2000);
				}*/


            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class executeCommand extends Thread {


        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.sendCommand(edittext_input_command.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class printLongPicture extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                printingLongPicture = true;
                checkStatus();
                ThermalPrinter.setGray(printGray);
                Log.d("tahh", "long picture printGray[" + printGray + "]");
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                //ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.b1));
                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.b1));
                ThermalPrinter.walkPaper(100);

                checkStatus();
                //ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.p001));
                ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.p001));
                ThermalPrinter.walkPaper(100);

                checkStatus();
                if (has80mmUsbPrinter) {
                    ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.op));
                    ThermalPrinter.walkPaper(100);
                } else {
                    ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.op_single));
                    ThermalPrinter.walkPaper(20);
                    ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.op_single));
                    ThermalPrinter.walkPaper(20);
                    ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.op_single));
                    ThermalPrinter.walkPaper(20);
                }
                handler.sendEmptyMessageDelayed(OVERTIME, 20000);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class printLongText extends Thread {

        public void run() {
            super.run();
            try {
                //ThermalPrinter.start(PrinterActivity.this);
                checkStatus();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                ThermalPrinter.setLeftIndent(leftDistance);
                ThermalPrinter.setLineSpace(5);
                if (wordFont == 0) {
                    ThermalPrinter.setFontSize(0);
                } else if (wordFont == 1) {
                    ThermalPrinter.setFontSize(1);
                } else if (wordFont == 2) {
                    ThermalPrinter.setFontSize(2);
                } else if (wordFont == 3) {
                    ThermalPrinter.setFontSize(3);
                } else if (wordFont == 4) {
                    ThermalPrinter.setFontSize(4);
                } else if (wordFont == 5) {
                    ThermalPrinter.setFontSize(5);
                } else if (wordFont == 6) {
                    ThermalPrinter.setFontSize(6);
                } else if (wordFont == 7) {
                    ThermalPrinter.setFontSize(7);
                }
                ThermalPrinter.setGray(printGray);
                printingLongText = 1;
                ThermalPrinter.addString("1\n" + getString(R.string.printContent1));
                ThermalPrinter.addString("2\n" + getString(R.string.printContent1));
                ThermalPrinter.addString("3\n" + getString(R.string.printContent1));
				/*ThermalPrinter.addString("4\n" + printContent1);
				ThermalPrinter.addString("5\n" + printContent1);
				ThermalPrinter.addString("6\n" + printContent1);
				ThermalPrinter.addString("7\n" + printContent1);
				ThermalPrinter.addString("8\n" + printContent1);*/
                if (useLanguage == null) {
                    ThermalPrinter.printString();
                } else {
                    ThermalPrinter.printString(useLanguage);
                }
                ThermalPrinter.walkPaper(200);
                if (wordFont == 0) {
                    handler.sendEmptyMessageDelayed(OVERTIME, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(OVERTIME, 40000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }

    private class printUnsupportedText extends Thread {

        public void run() {
            super.run();
            try {
                int printer_type = SystemUtil.getPrinterType();
                int paper_width = 576;
                if (printer_type == SystemUtil.PRINTER_PT486F08401MB) {
                    paper_width = 384;
                }
                //ThermalPrinter.start(PrinterActivity.this);
                Paint paint = new Paint();
                int fontSize = wordFont * 12;
                paint.setTextSize(fontSize);
                int gray = 0x7F;
                gray -= 0x80 / 12 * printGray;
                int color = 0xFF;
                color = color << 8 | gray;
                color = color << 8 | gray;
                color = color << 8 | gray;
                paint.setColor(color);
                Rect bounds = new Rect();
                paint.getTextBounds(printContent, 0, 1, bounds);
                int line_height = bounds.height();
                line_height = line_height > fontSize ? line_height : fontSize;
                line_height = line_height > lineDistance ? line_height : lineDistance;
                int length = printContent.length();
                int line_start_index = 0;
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (i = 0; i < length; i++) {
                    if (printContent.charAt(i) == '\n') {
                        sb.append(printContent.subSequence(line_start_index, i));
                        line_start_index = i;
                        continue;
                    }
                    if (leftDistance + paint.measureText(printContent, line_start_index, i) > paper_width) {
                        sb.append(printContent.subSequence(line_start_index, i - 1));
                        sb.append('\n');
                        line_start_index = i - 1;
                        continue;
                    }
                }
                sb.append(printContent.subSequence(line_start_index, i - 1));
                String[] lines = sb.toString().split("\n");
                int line = lines.length;
                bitmap = Bitmap.createBitmap(576, line * line_height, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);
                Canvas canvas = new Canvas(bitmap);
                for (i = 0; i < lines.length; i++) {
                    canvas.drawText(lines[i], leftDistance, (i + 1) * line_height, paint);
                }

                ThermalPrinter.walkPaper(100);
                ThermalPrinter.printLogo(bitmap);
                ThermalPrinter.walkPaper(100);
                ((ImageView) findViewById(R.id.view)).setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                handleExceptions(e);
//                Result = e.toString();
//                    nopaper = true;
//                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
//                } else {
//                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
//                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
//                if (nopaper) {
//                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
//                    nopaper = false;
//                    return;
//                }
                bitmap = null;
                //ThermalPrinter.stop(PrinterActivity.this);
            }
        }
    }


    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        circle = false;
        cancelProgressDialog();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        try {
            if (mReadThread != null) {
                mReadThread.interrupt();
                mReadThread = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        ThermalPrinter.stop();
//		java.lang.System.exit(0);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(printReceive);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 生成条码
     *
     * @param str       条码内容
     * @param type      条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
     *                  EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
     *                  RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
     * @param bmpWidth  生成位图宽,宽不能大于384，不然大于打印纸宽度
     * @param bmpHeight 生成位图高，8的倍数
     */

    public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight)
            throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void selectIndex(View view) {
        switch (view.getId()) {
            case R.id.index_text:
                text_index.setEnabled(false);
                pic_index.setEnabled(true);
                comm_index.setEnabled(true);
                print_text.setVisibility(View.VISIBLE);
                print_pic.setVisibility(View.GONE);
                print_comm.setVisibility(View.GONE);

                break;

            case R.id.index_pic:

                text_index.setEnabled(true);
                pic_index.setEnabled(false);
                comm_index.setEnabled(true);
                print_text.setVisibility(View.GONE);
                print_pic.setVisibility(View.VISIBLE);
                print_comm.setVisibility(View.GONE);
                break;
            case R.id.index_comm:
                text_index.setEnabled(true);
                pic_index.setEnabled(true);
                comm_index.setEnabled(false);
                print_text.setVisibility(View.GONE);
                print_pic.setVisibility(View.GONE);
                print_comm.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void savepic() {
        File file = new File(picturePath);
        if (!file.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("syhlogo.png");
                fos = new FileOutputStream(file);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File file2 = new File(picturePath2);
        if (!file2.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("op.png");
                fos = new FileOutputStream(file2);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File file3 = new File(picturePath3);
        if (!file3.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("test.bmp");
                fos = new FileOutputStream(file3);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File file4 = new File(picturePath4);
        if (!file4.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("test1.png");
                fos = new FileOutputStream(file4);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cancelProgressDialog() {
        if (handler.hasMessages(OVERTIME)) {
            handler.removeMessages(OVERTIME);
        }
        isDialogDismiss = true;
        if (progressDialog != null && !PrinterActivitySY581.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled = false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    private void sleepThread(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
