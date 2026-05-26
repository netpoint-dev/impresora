package com.common.demo.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;

import com.common.apiutil.CommonException;
import com.common.apiutil.printer.UsbThermalPrinter;
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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class UsbPrinterActivity extends BaseActivity {

	private String printVersion;
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
	private final int NOBLACKBLOCK = 15;
	private final int PRINTSHORTCONTENT = 16;
	private final int PRINTLONGPICTURE = 17;
	private final int PRINTLONGTEXT = 18;
	private final int PRINTBLACK = 19;
	private final int PRINTCOLUMNS = 20;
	private final int CUTPAPER = 21;

	private final int PRINT_LABLE_GAP = 22;

	private final int PRINT_LABLE_ADAPT = 23;

	private final int PRINT_LABLE_BACK = 24;
	private final int PRINT_LABLE_TYPE = 25;
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

	private LinearLayout print_text, print_pic;
	private TextView text_index, pic_index, textPrintVersion,set_wordFont_textview;
	MyHandler handler;
	private EditText editTextLeftDistance, editTextLineDistance, editTextWordFont, editTextPrintGray, editTextBarcode,
			editTextQrcode, editTextPaperWalk, editGap, editAdapter, editBack, editType,editTextContent, editText_maker_search_distance,
			editText_maker_walk_distance, editText_string_offset, editText_short_text, editText_cricle_print;
	private Button buttonBarcodePrint, buttonPaperWalkPrint, buttonContentPrint, buttonColumnsPrint, buttonQrcodePrint,
			buttonGetExampleText, buttonGetZhExampleText, buttonGetFRExampleText, buttonClearText, button_papercut, button_maker,
			button_print_picture, button_print_short_text, button_print_long_picture, button_print_long_text,
			button_print_black, button_cricle_print,setBold, setGap, setAdapt, setBack,setType;

	private CheckBox setMonoCheckBox;
	private ToggleButton button_auto_linefeed;
	private String Result;
	private Boolean nopaper = false;
	private boolean LowBattery = false;

	public static String barcodeStr;
	public static String qrcodeStr;
	public static int paperWalk;
	public static int paperGap;
	public static int paperAdapter;
	public static int paperBack;
	public static int paperType;//0： 普通纸；2：标签纸
	public static String printContent;
	private int leftDistance = 0;
	private int lineDistance;
	private int wordFont;
	private int printGray;
	public String shortContent;
	private int stringOffset;
	private int interval;
	private boolean isMono = false;
	private ProgressDialog progressDialog;
	private final static int MAX_LEFT_DISTANCE = 255;
	private boolean isCircle = false;
	ProgressDialog dialog;
	UsbThermalPrinter mUsbThermalPrinter;
	private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";
	private String picturePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/222.png";
	private String picturePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/333.png";
	private String picturePath4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/444.png";

	byte print_connected = 0;
	String path;
	int baudrate;
	int priter_width;
	public Toast mToast;
	final String PRT_TAG = "com.print.service.printservice";
	static byte flag = 0;
	private boolean isStart = false;
	private boolean isBold = false;
	
	private class MyHandler extends Handler {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOPAPER:
				noPaperDlg();
				break;
			case LOWBATTERY:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(UsbPrinterActivity.this);
				alertDialog.setTitle(R.string.operation_result);
				alertDialog.setMessage(getString(R.string.LowBattery));
				alertDialog.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialogInterface, int i) {
								
							}
						});
				alertDialog.show();
				break;
			case NOBLACKBLOCK:
				Toast.makeText(UsbPrinterActivity.this, R.string.maker_not_find, Toast.LENGTH_SHORT).show();
				break;
			case PRINTVERSION:
				dialog.dismiss();
				if (msg.obj.equals("1")) {
					textPrintVersion.setText(printVersion);
				} else {
					Toast.makeText(UsbPrinterActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
				}
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
				case PRINT_LABLE_GAP:
					new lableGapThread().start();
					break;
				case PRINT_LABLE_ADAPT:
					new lableAdapterThread().start();
					break;
				case PRINT_LABLE_BACK:
					new lableBackThread().start();
					break;
				case PRINT_LABLE_TYPE:
					new paperTypeThread().start();
					break;
			case PRINTCONTENT:
				new contentPrintThread().start();
				break;
			case PRINTCOLUMNS:
				new ColumnsPrintThread().start();
				break;
			case MAKER:
				new MakerThread().start();
				break;
			case PRINTPICTURE:
				new printPicture().start();
				break;
			case CANCELPROMPT:
				if (progressDialog != null && !UsbPrinterActivity.this.isFinishing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				break;
			case OVERHEAT:
				if(!isCircle){
					AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(UsbPrinterActivity.this);
					overHeatDialog.setTitle(R.string.operation_result);
					overHeatDialog.setMessage(getString(R.string.overTemp));
					overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm),
							new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialogInterface, int i) {
								}
							});
					overHeatDialog.show();
				}
				
				break;
			case PRINTSHORTCONTENT:
				new ShortTextPrintThread().start();
				break;
			case PRINTLONGPICTURE:
				new printLongPicture().start();
				break;
			case PRINTLONGTEXT:
				new printLongText().start();
				break;
			case PRINTBLACK:
				new printBlackPicture().start();
				break;
			case CUTPAPER:
				new cutPaper().start();
				break;
			default:
				Toast.makeText(UsbPrinterActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	private void initView() {
		print_text = (LinearLayout) findViewById(R.id.print_text);
		print_pic = (LinearLayout) findViewById(R.id.print_code_and_pic);
		text_index = (TextView) findViewById(R.id.index_text);
		pic_index = (TextView) findViewById(R.id.index_pic);
		editText_string_offset = (EditText) findViewById(R.id.edittext_string_offset);
		editText_short_text = (EditText) findViewById(R.id.edittext_short_text);
		button_print_short_text = (Button) findViewById(R.id.button_print_short_text);
		button_auto_linefeed = (ToggleButton) findViewById(R.id.auto_linefeed);
		setAutoAdjGray = (Button) findViewById(R.id.setAutoAdjGray);
		setItalic = (Button) findViewById(R.id.setItalic);
		setThreeHeight = (Button) findViewById(R.id.setThreeHeight);
		setTwoWidth = (Button) findViewById(R.id.setTwoWidth);

		if ("TPS575".equals(SystemUtil.getInternalModel()) || !SystemUtil.isInstallServiceApk()) {
			setItalic.setVisibility(View.VISIBLE);
			setThreeHeight.setVisibility(View.VISIBLE);
			setTwoWidth.setVisibility(View.VISIBLE);
		}
	}

	boolean isOpeningItalic = false;
	public void setItalic(View view){
		
		if(isOpeningItalic){
			setItalic.setText(R.string.print_style_italic_enable);
		}else{
			setItalic.setText(R.string.print_style_italic_disable);
		}
		isOpeningItalic = !isOpeningItalic;
	}
	
	boolean isOpeningThreeHeight = false;
	public void setThreeHeight(View view){
		
		if(isOpeningThreeHeight){
			setThreeHeight.setText(R.string.print_style_three_height_enable);
		}else{
			setThreeHeight.setText(R.string.print_style_three_height_disable);
		}
		isOpeningThreeHeight = !isOpeningThreeHeight;
	}
	
	boolean isOpeningTwoWidth = false;
	public void setTwoWidth(View view){
		
		if(isOpeningTwoWidth){
			setTwoWidth.setText(R.string.print_style_two_width_enable);
		}else{
			setTwoWidth.setText(R.string.print_style_two_width_disable);
		}
		isOpeningTwoWidth = !isOpeningTwoWidth;
	}
	
	Button setAutoAdjGray,setItalic,setThreeHeight,setTwoWidth;
	
	int deviceType;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.usbprint_text);
		mUsbThermalPrinter = new UsbThermalPrinter(UsbPrinterActivity.this);

		deviceType = SystemUtil.getDeviceType();
		
		//enlarge_width = (EditText) findViewById(R.id.enlarge_width);
		//enlarge_height = (EditText) findViewById(R.id.enlarge_height);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		Intent intent = new Intent(PRT_TAG);
		intent.setPackage(getPackageName());
		intent.setComponent(new ComponentName("com.print.service", "com.print.service.printservice"));

		// startService(intent);
		
		initView();
		//savepic();
		/*if (!isSupportAutoBreak()) {
			button_auto_linefeed.setEnabled(isSupportAutoBreak());
			Toast.makeText(this, getString(R.string.not_support), Toast.LENGTH_SHORT).show();
		}*/
		handler = new MyHandler();
		buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

		editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
		editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
		editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
		editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
		editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
		editTextBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
		editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
		editGap = findViewById(R.id.set_lable_gap);
		editAdapter = findViewById(R.id.set_lable_adapter);
		editBack = findViewById(R.id.set_lable_rollback);
		editType = findViewById(R.id.set_paper_type);
		editTextContent = (EditText) findViewById(R.id.set_content);
		textPrintVersion = (TextView) findViewById(R.id.print_version);
		editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
		editText_cricle_print = (EditText) findViewById(R.id.edittext_spin);
		editText_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
		editText_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
		buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);

		if (deviceType == StringUtil.DeviceModelEnum.TPS900.ordinal() || 
				deviceType == StringUtil.DeviceModelEnum.TPS900MB.ordinal()) {
			editTextPrintGray.setText("5");
		}
		button_papercut = (Button) findViewById(R.id.button_papercut);
		button_papercut.setVisibility(View.VISIBLE);
		button_papercut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(CUTPAPER, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
		buttonQrcodePrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();//灰度
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				qrcodeStr = editTextQrcode.getText().toString();
				if (qrcodeStr == null || qrcodeStr.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this,
									getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));

					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
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
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				
				barcodeStr = editTextBarcode.getText().toString();
				if(barcodeStr.length()>60) {
					Toast.makeText(UsbPrinterActivity.this, "字符过多", Toast.LENGTH_LONG).show();
					return;
				}
				if (barcodeStr == null || barcodeStr.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
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
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.walk_paper_intput_value),
							Toast.LENGTH_LONG).show();
					return;
				}
				paperWalk = Integer.parseInt(exditText);
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
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
				String str = "\n---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "---------------------------\n"/* + "---------------------------\n"
						+ "Print Test:\n" + "Device Base Information\n" + "Printer Version:\n" + "V05.2.0.3\n"
						+ "Printer Gray:3\n" + "Soft Version:\n" + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n"
						+ "CSQ Value:24\n" + "IMEI:86378902177527\n" + "---------------------------\n"
						+ "---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "---------------------------\n"*/;
				editTextContent.setText(str);
			}
		});
		
		setBold = (Button) findViewById(R.id.setBold);
		setBold.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isBold) {
					isBold = !isBold;
					setBold.setText("取消加粗");
				}else {
					isBold = !isBold;
					setBold.setText("字体加粗");
				}
			}
		});
		setGap = findViewById(R.id.setGap);
		setGap.setOnClickListener(v -> {
			Log.i("ThermalPrinterService", "setGap.setOnClickListen: ");
			String exditText;
			exditText = editGap.getText().toString();
			if (exditText == null || exditText.length() == 0) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
				return;
			}
			if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.walk_paper_intput_value),
						Toast.LENGTH_LONG).show();
				return;
			}
			paperGap = Integer.parseInt(exditText);
			if (LowBattery == true) {
				handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
			} else {
				if (!nopaper) {
					if(progressDialog == null) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
					}
					handler.sendMessage(handler.obtainMessage(PRINT_LABLE_GAP, 1, 0, null));
				} else {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		setAdapt = findViewById(R.id.btn_lable_adapter);
		setAdapt.setOnClickListener(v -> {
			String exditText;
			exditText = editAdapter.getText().toString();
			if (exditText == null || exditText.length() == 0) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
				return;
			}
			if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.walk_paper_intput_value),
						Toast.LENGTH_LONG).show();
				return;
			}
			paperAdapter = Integer.parseInt(exditText);
			if (LowBattery == true) {
				handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
			} else {
				if (!nopaper) {
					if(progressDialog == null) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
					}
					handler.sendMessage(handler.obtainMessage(PRINT_LABLE_ADAPT, 1, 0, null));
				} else {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		setBack = findViewById(R.id.btn_rollback);
		setBack.setOnClickListener(v -> {
			String exditText;
			exditText = editBack.getText().toString();
			if (exditText == null || exditText.length() == 0) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
				return;
			}
			if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.walk_paper_intput_value),
						Toast.LENGTH_LONG).show();
				return;
			}
			paperBack = Integer.parseInt(exditText);
			if (LowBattery == true) {
				handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
			} else {
				if (!nopaper) {
					if(progressDialog == null) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
					}
					handler.sendMessage(handler.obtainMessage(PRINT_LABLE_BACK, 1, 0, null));
				} else {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		setType = findViewById(R.id.btn_paper_type);
		setType.setOnClickListener(v -> {
			String exditText;
			exditText = editType.getText().toString();
			if (exditText == null || exditText.length() == 0) {
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
				return;
			}
			if (Integer.parseInt(exditText) == 0 || Integer.parseInt(exditText) == 2) {
				paperType = Integer.parseInt(exditText);
				handler.sendMessage(handler.obtainMessage(PRINT_LABLE_TYPE, 1, 0, null));
			}else{
				Toast.makeText(UsbPrinterActivity.this, getString(R.string.enter_0_2),
						Toast.LENGTH_LONG).show();
			}
		});

		setMonoCheckBox = findViewById(R.id.setMonoCheckBox);
		setMonoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isMono = isChecked;
			}
		});

		buttonGetZhExampleText = (Button) findViewById(R.id.getZhPrintExample);
		buttonGetZhExampleText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String str = "\n             烧烤" + "\n---------------------------" + "\n日期：2015-01-01 16:18:20"
						+ "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n---------------------------"
						+ "\n    项目        数量   单价  小计" + "\n秘制烤羊腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤鸡腿    1      56      56" + "\n烤牛腿            2      50      100"
						+ "\n烤猪蹄            1      200    200" + "\n秘制烤牛腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤猪腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全牛            1      200    200" + "\n特色烤鸭腿    1      56      56"
						+ "\n烤土鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全羊            1      200    200" + "\n秘制烤鸡腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全羊            1      200    200" + "\n秘制烤牛筋    1      56      56"
						+ "\n烤土鸡            2      50      100" + "\n烤白鸽            1      200    200"
						+ "\n秘制鸭下巴    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全牛            1      200    200" + "\n 合计：1000:00元" + "\n----------------------------"
						+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
						+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
				editTextContent.setText(str);
			}
		});

		buttonGetFRExampleText = (Button) findViewById(R.id.getFrPrintExample);
		buttonGetFRExampleText.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				String str = "\nÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóô\n"
						/*"\nعربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎عربي/عربى‎\n"*/;
				editTextContent.setText(str);
			}

		});

		buttonContentPrint = (Button) findViewById(R.id.print_content);
		buttonContentPrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				
				/*String width,height;
				width = enlarge_width.getText().toString();
				height = enlarge_height.getText().toString();
				if(!"".equals(width) && !"".equals(height)){
					enlargeWidth = Integer.valueOf(width);
					enlargeHeight = Integer.valueOf(height);
				}*/
				
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				/*if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}*/
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				}

				if (lineDistance > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				}
				if (wordFont > 64 || wordFont < 8) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				}
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}

			}
		});

		buttonColumnsPrint = (Button) findViewById(R.id.print_columns);
		buttonColumnsPrint.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}

				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();

				lineDistance = Integer.parseInt(exditText);
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				}
				if (lineDistance > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				}
				if (wordFont > 64 || wordFont < 8) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				}
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTCOLUMNS, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}

			}
		});

		button_maker = (Button) findViewById(R.id.button_maker);
		button_maker.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				if (editText_maker_search_distance.getText().length() == 0
						|| editText_maker_walk_distance.getText().length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(editText_maker_search_distance.getText().toString()) < 0
						|| Integer.parseInt(editText_maker_search_distance.getText().toString()) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(editText_maker_walk_distance.getText().toString()) < 0
						|| Integer.parseInt(editText_maker_walk_distance.getText().toString()) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.maker),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		button_print_picture = (Button) findViewById(R.id.button_print_picture);
		button_print_picture.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
		/*new Thread(new Runnable() {

			
			public void run() {
				try {
					mUsbThermalPrinter.start(0);
					mUsbThermalPrinter.reset();
					printVersion = mUsbThermalPrinter.getVersion();
				} catch (Exception e) {
					e.printStackTrace();
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
		}).start();*/
		button_print_short_text.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					String offset_str = editText_string_offset.getText().toString();
					if ("".equals(offset_str)) {
						offset_str = "0";
					}
					int offset = Integer.parseInt(offset_str);
					String text = editText_short_text.getText().toString();
					int text_length = 0;
					if (!"".equals(text)) {
						text_length = mUsbThermalPrinter.measureText(text);
					}
					if (text_length + offset > 384) {
						button_print_short_text.setEnabled(false);
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.text_too_long), Toast.LENGTH_SHORT)
								.show();
						return;
					}
				} catch (CommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.text_format_error), Toast.LENGTH_SHORT)
							.show();
					editText_string_offset.setText("0");
					return;
				}

				String exditText;
				exditText = editText_string_offset.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				stringOffset = Integer.parseInt(exditText);
				shortContent = editText_short_text.getText().toString();
				if (stringOffset > MAX_LEFT_DISTANCE) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				}
				if (stringOffset < 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.param_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (stringOffset % 8 != 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.param_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (shortContent == null || shortContent.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTSHORTCONTENT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
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
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTLONGPICTURE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
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
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTLONGTEXT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
		
		button_print_black = (Button) findViewById(R.id.button_print_black); 
		button_print_black.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 200) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						if(progressDialog == null) {
							progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
									getString(R.string.printing_wait));
						}
						handler.sendMessage(handler.obtainMessage(PRINTBLACK, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
		
		button_cricle_print = (Button) findViewById(R.id.button_cricle_print);
		button_cricle_print.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(!isCircle) {
					
					String exditText;
					exditText = editTextLeftDistance.getText().toString();
					if (exditText == null || exditText.length() < 1) {
						Toast.makeText(UsbPrinterActivity.this,
								getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
								.show();
						return;
					}
					leftDistance = Integer.parseInt(exditText);
					exditText = editTextLineDistance.getText().toString();
					/*if (exditText == null || exditText.length() < 1) {
						Toast.makeText(UsbPrinterActivity.this,
								getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
								.show();
						return;
					}*/
					lineDistance = Integer.parseInt(exditText);
					exditText = editTextWordFont.getText().toString();
					if (exditText == null || exditText.length() < 1) {
						Toast.makeText(UsbPrinterActivity.this,
								getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
								.show();
						return;
					}
					wordFont = Integer.parseInt(exditText);
					exditText = editTextPrintGray.getText().toString();
					if (exditText == null || exditText.length() < 1) {
						Toast.makeText(UsbPrinterActivity.this,
								getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
								.show();
						return;
					}
					printGray = Integer.parseInt(exditText);
					if (leftDistance > MAX_LEFT_DISTANCE) {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
						return;
					}
					if (lineDistance > 255) {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
						return;
					}
					if (wordFont > 64 || wordFont < 8) {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
						return;
					}
					if (printGray < 0 || printGray > 200) {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
						return;
					}
					
					printContent = "\n---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
							+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
							+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
							+ "IMEI:86378902177527\n" + "---------------------------\n" + "---------------------------\n"
							+ "Print Test:\n" + "Device Base Information\n" + "Printer Version:\n" + "V05.2.0.3\n"
							+ "Printer Gray:3\n" + "Soft Version:\n" + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n"
							+ "CSQ Value:24\n" + "IMEI:86378902177527\n" + "---------------------------\n"
							+ "---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
							+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
							+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
							+ "IMEI:86378902177527\n" + "---------------------------\n";
					printContent += "\n             烧烤" + "\n---------------------------" + "\n日期：2015-01-01 16:18:20"
							+ "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n---------------------------"
							+ "\n    项目        数量   单价  小计" + "\n秘制烤羊腿    1      56      56"
							+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
							+ "\n秘制烤鸡腿    1      56      56" + "\n烤牛腿            2      50      100"
							+ "\n烤猪蹄            1      200    200" + "\n秘制烤牛腿    1      56      56"
							+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
							+ "\n秘制烤猪腿    1      56      56" + "\n烤火鸡            2      50      100"
							+ "\n烤全牛            1      200    200" + "\n特色烤鸭腿    1      56      56"
							+ "\n烤土鸡            2      50      100" + "\n烤全羊            1      200    200"
							+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
							+ "\n烤全羊            1      200    200" + "\n秘制烤鸡腿    1      56      56"
							+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
							+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
							+ "\n烤全羊            1      200    200" + "\n秘制烤牛筋    1      56      56"
							+ "\n烤土鸡            2      50      100" + "\n烤白鸽            1      200    200"
							+ "\n秘制鸭下巴    1      56      56" + "\n烤火鸡            2      50      100"
							+ "\n烤全牛            1      200    200" + "\n 合计：1000:00元" + "\n----------------------------"
							+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
							+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
					
					try {
						String str = editText_cricle_print.getText().toString();
						if (str == null || str.length() < 1) {
							Toast.makeText(UsbPrinterActivity.this,
									getString(R.string.print_interval) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
									.show();
							return;
						}
						interval = Integer.parseInt(str);
						if(interval < 1) {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.interval_format_error), Toast.LENGTH_SHORT)
									.show();
							return;
						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.interval_format_error), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (LowBattery == true) {
						handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
					} else {
						if (!nopaper) {
							isCircle = true;
							if(progressDialog == null) {
								progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
										getString(R.string.printing_wait));
							}
							handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
						} else {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
									.show();
						}
					}
					button_cricle_print.setText(getString(R.string.print_cricle_stop));
				} else {
					isCircle = false;
					//button_cricle_print.setEnabled(false);
				}
			}
		});

	}

	/* Called when the application resumes */
	
	protected void onResume() {
		super.onResume();
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
				if (deviceType == StringUtil.DeviceModelEnum.TPS390.ordinal() /*||
						"TPS320".equals(SystemUtil.getInternalModel())*/) {
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
		AlertDialog.Builder dlg = new AlertDialog.Builder(UsbPrinterActivity.this);
		dlg.setTitle(getString(R.string.noPaper));
		dlg.setMessage(getString(R.string.noPaperNotice));
		dlg.setCancelable(false);
		dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});
		dlg.show();
	}

	private class paperWalkPrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.walkPaper(paperWalk);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class lableGapThread extends Thread {

		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setLableFeedOutNextGap(paperGap);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.common.apiutil.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.common.apiutil.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class lableAdapterThread extends Thread {

		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setLableAdapt(paperAdapter);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.common.apiutil.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.common.apiutil.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class lableBackThread extends Thread {

		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.rollback(paperBack);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.common.apiutil.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.common.apiutil.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class paperTypeThread extends Thread {
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgorithm(paperType);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				Log.i("UsbPrinterActivity", "setAlgorithm error ");
			}
		}
	}

	private class barcodePrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setMonoSpace(isMono);
				Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
				if (bitmap != null) {
					mUsbThermalPrinter.printLogo(bitmap, true);
				}
				mUsbThermalPrinter.addString(barcodeStr);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	boolean circleQinCheng = true;
	long paperCount = 0;
	private class qrcodePrintThread extends Thread {
		
		public void run() {
			super.run();
			//while(circleQinCheng){//qincheng test
				try {
					mUsbThermalPrinter.reset();
					mUsbThermalPrinter.setGray(printGray);
					mUsbThermalPrinter.setMonoSpace(isMono);
					
					//qincheng test
					//qrcodeStr = "hQVDUFYwMWFzTwZRQ0FUMDFjacECA5bCAgGwwwRgWZYIxAJ6d8YCAbDJAQHLAQHMAQrOAwGGPNECERfTBTA2MDY13jgCMDUCGQCYWwnI/OueUruoiz/9GmMXxp4TBW6sWOkCGG4J3AMTb0M5pu+Dxsg8ujMzbguBGlGhsg==hQVDUFYwMWFzTwZRQ0FUMDFjacECA5bCAgGwwwRgWZYIxAJ6d8YCAbDJAQHLAQHMAQrOAwGGPNECERfTBTA2MDY13jgCMDUCGQCYWwnI/OueUruoiz/9GmMXxp4TBW6sWOkCGG4J3AMTb0M5pu+Dxsg8ujMzbguBGlGhsg==";
					
					int qrcodeSize = 256;
					
					try {
						//qincheng test
						//qrcodeSize = Integer.valueOf(editTextQrcode.getText().toString());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, /*256, 256*/qrcodeSize, qrcodeSize);
					if (bitmap != null) {
						mUsbThermalPrinter.printLogo(bitmap, true);
					}
					paperCount++;
					//qincheng test
					//mUsbThermalPrinter.addString("第"+paperCount+"张---------------\n");
					mUsbThermalPrinter.addString(qrcodeStr);
					mUsbThermalPrinter.printString();
					mUsbThermalPrinter.walkPaper(20);
				} catch (Exception e) {
					e.printStackTrace();
					Result = e.toString();
					if (Result.contains("NoPaperException")) {
						nopaper = true;
					} else if (Result.contains("OverHeatException")) {
						handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
					} else {
						handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
					}
				} finally {
					handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
					if (nopaper) {
						handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
						nopaper = false;
						return;
					}
				}
				
				//qincheng test
				/*int printTimeout = 1000;
				try {
					printTimeout = Integer.valueOf(editTextBarcode.getText().toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(printTimeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
			//}//qincheng test
		}
	}
	
	private class contentPrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
				mUsbThermalPrinter.setLeftIndent(leftDistance);
				mUsbThermalPrinter.setLineSpace(lineDistance);
				mUsbThermalPrinter.setBold(isBold);
				mUsbThermalPrinter.setTextSize(wordFont);
				mUsbThermalPrinter.setMonoSpace(isMono);
				/*if (isSupportAutoBreak()) {
					mUsbThermalPrinter.autoBreakSet(button_auto_linefeed.isChecked());
				}*/
				mUsbThermalPrinter.setGray(printGray);
				//mUsbThermalPrinter.enlargeFontSize(enlargeWidth, enlargeHeight);
				
				if(isOpeningItalic){
					mUsbThermalPrinter.setItalic(true);
				}
				
				if(isOpeningThreeHeight){
					mUsbThermalPrinter.setThripleHeight(true);
				}
				
				if(isOpeningTwoWidth){
					mUsbThermalPrinter.enlargeFontSize(2, 1);
				}
				
				mUsbThermalPrinter.addString(printContent);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				if(isCircle) {
					handler.sendMessageDelayed(handler.obtainMessage(PRINTCONTENT), /*interval * */1000);
				} else {
					runOnUiThread(new Runnable() {
						
						
						public void run() {
							// TODO Auto-generated method stub
							button_cricle_print.setEnabled(true);
							button_cricle_print.setText(getString(R.string.print_cricle_start));
						}
					});
				}
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class ColumnsPrintThread extends Thread {

		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
				mUsbThermalPrinter.setLeftIndent(leftDistance);
				mUsbThermalPrinter.setLineSpace(lineDistance);
				mUsbThermalPrinter.setBold(isBold);
				mUsbThermalPrinter.setTextSize(wordFont);
				mUsbThermalPrinter.setMonoSpace(isMono);

				mUsbThermalPrinter.setGray(printGray);

				if(isOpeningItalic){
					mUsbThermalPrinter.setItalic(true);
				}

				if(isOpeningThreeHeight){
					mUsbThermalPrinter.setThripleHeight(true);
				}

				if(isOpeningTwoWidth){
					mUsbThermalPrinter.enlargeFontSize(2, 1);
				}

				mUsbThermalPrinter.addColumnsString(new String[]{"AAA","BBB","CCC"}, new int[]{6,6,6}, new int[]{0,0,0}, 16);
				mUsbThermalPrinter.addColumnsString(new String[]{"AAA","BBB","CCC"}, new int[]{6,6,6}, new int[]{0,0,0}, 16);
				mUsbThermalPrinter.addColumnsString(new String[]{"AAA","BBB","CCC"}, new int[]{6,6,6}, new int[]{0,0,0}, 16);
				mUsbThermalPrinter.addColumnsString(new String[]{"AAAAAAAAAAAA","BBBBBBBBBBBB","CCCCCCCCCCCC"}, new int[]{6,6,6}, new int[]{0,0,0}, 16);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);

			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class MakerThread extends Thread {

		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.searchMark(Integer.parseInt(editText_maker_search_distance.getText().toString()),
						Integer.parseInt(editText_maker_walk_distance.getText().toString()));
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString(); 
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else if (Result.contains("BlackBlockNotFoundException")) {
					handler.sendMessage(handler.obtainMessage(NOBLACKBLOCK, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printPicture extends Thread {

		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				File file = new File(picturePath);
				//if (file.exists()) {
					mUsbThermalPrinter.printLogo(getBitmap(UsbPrinterActivity.this, R.drawable.syhlogo), false);
					mUsbThermalPrinter.walkPaper(20);
				/*} else {
					runOnUiThread(new Runnable() {

						
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}*/
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class ShortTextPrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				String exditText = editTextPrintGray.getText().toString();//灰度
				printGray = Integer.parseInt(exditText);
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setMonoSpace(isMono);
				mUsbThermalPrinter.addStringOffset(stringOffset, shortContent);
				mUsbThermalPrinter.endLine();
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}
	
	private Bitmap changeScale(Bitmap bm) {
		int width = bm.getWidth();  
	    int height = bm.getHeight();  
	    int newWidth = 384;  
	    int newHeight = 2280;  
	    float scaleWidth = ((float) newWidth) / width;  
	    float scaleHeight = ((float) newHeight) / height;  
	    Matrix matrix = new Matrix();  
	    matrix.postScale(scaleWidth, 1);
	    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,  
	      true);  
	    return newbm;
	}

	private class printLongPicture extends Thread {

		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				File file = new File(picturePath);
				//if (file.exists()) {
					mUsbThermalPrinter.walkPaper(20);
					mUsbThermalPrinter.printLogo(getBitmap(UsbPrinterActivity.this, R.drawable.b1), false);
					mUsbThermalPrinter.walkPaper(20);
				/*} else {
					runOnUiThread(new Runnable() {

						
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}*/

				File file2 = new File(picturePath2);
				//if (file2.exists()) {
					mUsbThermalPrinter.walkPaper(20);
					Bitmap bitmap = getBitmap(UsbPrinterActivity.this, R.drawable.p001);
					bitmap = ThumbnailUtils.extractThumbnail(bitmap, 384, bitmap.getHeight());
					mUsbThermalPrinter.printLogo(bitmap, false);
					mUsbThermalPrinter.walkPaper(20);
				/*} else {
					runOnUiThread(new Runnable() {

						
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}*/

				File file3 = new File(picturePath3);
				//if (file3.exists()) {
					mUsbThermalPrinter.walkPaper(20);
					mUsbThermalPrinter.printLogo(getBitmap(UsbPrinterActivity.this, R.drawable.op), false);
					mUsbThermalPrinter.walkPaper(20);
				/*} else {
					runOnUiThread(new Runnable() {

						
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}*/

				/*File file4 = new File(picturePath4);
				if (file4.exists()) {
					mUsbThermalPrinter.walkPaper(100);
					mUsbThermalPrinter.printLogo(changeScale(BitmapFactory.decodeFile(picturePath4)), false);
					mUsbThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {

						
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}*/
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printLongText extends Thread {
		
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setLineSpace(5);
				mUsbThermalPrinter.setBold(isBold);
				mUsbThermalPrinter.setTextSize(wordFont);
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setMonoSpace(isMono);
				if(isOpeningItalic){
					mUsbThermalPrinter.setItalic(true);
				}
				
				if(isOpeningThreeHeight){
					mUsbThermalPrinter.setThripleHeight(true);
				}
				
				if(isOpeningTwoWidth){
					mUsbThermalPrinter.enlargeFontSize(2, 1);
				}
				mUsbThermalPrinter.addString("1\n" + printContent1);
				mUsbThermalPrinter.addString("2\n" + printContent1);
				mUsbThermalPrinter.addString("3\n" + printContent1);
				mUsbThermalPrinter.addString("4\n" + printContent1);
				mUsbThermalPrinter.addString("5\n" + printContent1);
				mUsbThermalPrinter.addString("6\n" + printContent1);
				mUsbThermalPrinter.addString("7\n" + printContent1);
				mUsbThermalPrinter.addString("8\n" + printContent1);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(50);

				mUsbThermalPrinter.reset();

				Bitmap bitmap = CreateCode("123", BarcodeFormat.QR_CODE, 248, 248);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.addString(" ");
				mUsbThermalPrinter.printString();
				if (bitmap != null) {
					mUsbThermalPrinter.printLogo(bitmap, false);
					mUsbThermalPrinter.walkPaper(50);
					mUsbThermalPrinter.addString(" ");
					mUsbThermalPrinter.printString();
					mUsbThermalPrinter.printLogo(bitmap, false);
					mUsbThermalPrinter.walkPaper(50);
				}

				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.addString(" ");
				mUsbThermalPrinter.printString();
				//
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printBlackPicture extends Thread {

		
		public void run() {
			super.run();
			try {
				/*mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(7);//error
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				Bitmap bitmap = Bitmap.createBitmap(384, 384, Bitmap.Config.ARGB_8888);
				bitmap.eraseColor(Color.BLACK);
				mUsbThermalPrinter.printLogo(bitmap, false);
				mUsbThermalPrinter.walkPaper(20);*/
				
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.printLogo(getBitmap(UsbPrinterActivity.this, R.drawable.black), false);
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class cutPaper extends Thread {


		public void run() {
			super.run();
			try {

				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.paperCut();
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.contains("NoPaperException")) {
					nopaper = true;
				} else if (Result.contains("OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	protected void onStart() {
		// TODO Auto-generated method stub
		
		IntentFilter pIntentFilter = new IntentFilter();
		pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
		registerReceiver(printReceive, pIntentFilter);
		
		dialog = new ProgressDialog(UsbPrinterActivity.this);
		dialog.setTitle(R.string.idcard_czz);
		dialog.setMessage(getText(R.string.watting));
		dialog.setCancelable(false);
		dialog.show();
		new Thread(new Runnable() {

			
			public void run() {
				try {
					mUsbThermalPrinter.start(0);//低速
//					mUsbThermalPrinter.start(1);//高速
					mUsbThermalPrinter.reset();
					printVersion = mUsbThermalPrinter.getVersion();
				} catch (CommonException e) {
					e.printStackTrace();
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
		super.onStart();
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		if (progressDialog != null && !UsbPrinterActivity.this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		isCircle = false;
		unregisterReceiver(printReceive);
		mUsbThermalPrinter.stop();
		circleQinCheng = false;
		super.onStop();
	}

	/*
	protected void onDestroy() {
		if (progressDialog != null && !UsbPrinterActivity.this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		isCircle = false;
		unregisterReceiver(printReceive);
		mUsbThermalPrinter.stop();
		super.onDestroy();
	}*/

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight)
			throws WriterException {
		Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
		mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
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
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public void selectIndex(View view) {
		switch (view.getId()) {
		case R.id.index_text:
			text_index.setEnabled(false);
			pic_index.setEnabled(true);
			print_text.setVisibility(View.VISIBLE);
			print_pic.setVisibility(View.GONE);

			break;

		case R.id.index_pic:

			text_index.setEnabled(true);
			pic_index.setEnabled(false);
			print_text.setVisibility(View.GONE);
			print_pic.setVisibility(View.VISIBLE);
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

	private boolean isSupportAutoBreak() {
		Class<?> thermalPrinter = null;
		Method method = null;
		Object obj = null;

		if (deviceType == StringUtil.DeviceModelEnum.TPS900.ordinal() || 
				deviceType == StringUtil.DeviceModelEnum.TPS900MB.ordinal()) {
			try {
				thermalPrinter = Class.forName("com.common.sdk.thermalprinter.ThermalPrinterServiceManager");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			obj = getSystemService("ThermalPrinter");
			try {
				method = thermalPrinter.getMethod("setAutoBreak", boolean.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				thermalPrinter = Class.forName("com.common.sdk.printer.UsbPrinterManager");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			obj = getSystemService("UsbPrinter");
			try {
				method = thermalPrinter.getMethod("setAutoBreak", boolean.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}
