package com.common.demo.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.common.apiutil.CommonException;
import com.common.apiutil.printer.ThermalPrinter;
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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrinterActivity extends BaseActivity {

	private final static String TAG = "PrinterActivity";
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
	/*public static String printContent1 = "\n             烧烤" + "\n----------------------------"
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
			+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";*/

	private LinearLayout print_text, print_pic, print_comm;
	private TextView text_index, pic_index, comm_index, textPrintVersion;
	MyHandler handler;
	private EditText editTextLeftDistance, editTextLineDistance, editTextWordFont, editTextPrintGray, editTextBarcode,
			editTextQrcode, editTextPaperWalk, editTextContent, edittext_maker_search_distance,
			edittext_maker_walk_distance, edittext_input_command;
	private Button buttonBarcodePrint, buttonPaperWalkPrint, buttonContentPrint, buttonQrcodePrint,
			buttonGetExampleText, buttonGetZhExampleText, buttonClearText, button_maker, button_papercut,
			button_print_picture, button_execute_command, button_print_long_picture, button_print_long_text,
			button_print_georgia,print_foreign,print_circle;
	private String Result;
	private Boolean nopaper = false;
	private boolean LowBattery = false;
	private boolean circle = false;

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

	private class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case NOPAPER:
					ThermalPrinter.stop(PrinterActivity.this);
					noPaperDlg();
					break;
				case LOWBATTERY:
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrinterActivity.this);
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
					if (msg.obj.equals("1")) {
						textPrintVersion.setText(printVersion);
					} else {
						Toast.makeText(PrinterActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
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
					if (progressDialog != null && !PrinterActivity.this.isFinishing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					break;
				case EXECUTECOMMAND:
					new executeCommand().start();
					break;
				case OVERHEAT:
					AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(PrinterActivity.this);
					overHeatDialog.setTitle(R.string.operation_result);
					overHeatDialog.setMessage(getString(R.string.overTemp));
					overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialogInterface, int i) {
								}
							});
					overHeatDialog.show();
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
				default:
					Toast.makeText(PrinterActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
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

	int deviceType;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "pt72 activity...");
		deviceType = SystemUtil.getDeviceType();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.print_text);
		initView();
//		savepic();
		handler = new MyHandler();
		button_papercut = (Button) findViewById(R.id.button_papercut);
		buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

		//new ThermalPrinter(PrinterActivity.this);
		IntentFilter pIntentFilter = new IntentFilter();
		pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
		registerReceiver(printReceive, pIntentFilter);

		editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
		editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
		editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
		editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
		editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
		editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
		editTextContent = (EditText) findViewById(R.id.set_content);
		textPrintVersion = (TextView) findViewById(R.id.print_version);
		editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
		edittext_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
		edittext_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
		edittext_input_command = (EditText) findViewById(R.id.edittext_input_command);
		buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);
		print_circle = (Button) findViewById(R.id.print_circle);
		print_circle.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				// TODO Auto-generated method stub
				circle = true;
				if(circle) {
					buttonContentPrint.performClick();
				}
			}
		});

		buttonQrcodePrint.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				qrcodeStr = editTextQrcode.getText().toString();
				if (qrcodeStr == null || qrcodeStr.length() == 0) {
					Toast.makeText(PrinterActivity.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this,
								getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
						handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
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
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				barcodeStr = editTextBarcode.getText().toString();
				if (barcodeStr == null || barcodeStr.length() == 0) {
					Toast.makeText(PrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
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
					Toast.makeText(PrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.walk_paper_intput_value), Toast.LENGTH_LONG)
							.show();
					return;
				}
				paperWalk = Integer.parseInt(exditText);
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
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
				String str = getString(R.string.printContent1);
				editTextContent.setText(str);
			}
		});

		buttonContentPrint = (Button) findViewById(R.id.print_content);//文字打印按钮
		buttonContentPrint.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				} else if (lineDistance > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				} else if (wordFont > 4 || wordFont < 1) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				} else if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {
					Toast.makeText(PrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
						if(circle)
							handler.sendEmptyMessageDelayed(402, 5000);
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		button_maker = (Button) findViewById(R.id.button_maker);
		button_maker.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				if (edittext_maker_search_distance.getText().length() == 0
						|| edittext_maker_walk_distance.getText().length() == 0) {
					Toast.makeText(PrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_search_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_search_distance.getText().toString()) > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_walk_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_walk_distance.getText().toString()) > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.maker),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_papercut.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				new Thread(new Runnable() {

					public void run() {
						try {
							ThermalPrinter.start(PrinterActivity.this);
							ThermalPrinter.reset();
							ThermalPrinter.paperCut();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ThermalPrinter.stop(PrinterActivity.this);
						}
					}
				}).start();
			}
		});

		button_print_picture = (Button) findViewById(R.id.button_print_picture);
		button_print_picture.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_execute_command = (Button) findViewById(R.id.button_execute_command);
		button_execute_command.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				/*if (edittext_input_command.getText().toString() == null
						|| edittext_input_command.getText().toString().length() == 0) {
					Toast.makeText(PrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}*/
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(EXECUTECOMMAND, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
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
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTLONGPICTURE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
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
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTLONGTEXT, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_print_georgia = (Button) findViewById(R.id.print_georgia);
		button_print_georgia.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				} else if (lineDistance > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				} else if (wordFont > 4 || wordFont < 1) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				} else if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {

					printContent = getString(R.string.foreignLanguage);

					//越南语
					/*printContent = "Xin chào Hôm nay là thứ 4. Hôm nay là thứ 4. Xin chào Xin chào Hôm nay là thứ 4. Hôm nay là thứ 4. Xin chào \n"
							+"Xin chào Hôm nay là thứ 4. Hôm nay là thứ 4. Xin chào ,Xin chào Hôm nay là thứ 4. Hôm nay là thứ 4."
							 +"Xin chào ,Xin chào Hôm nay là thứ 4. Hôm nay là thứ 4. Xin chào ....";*/

					editTextContent.setText(printContent+"\n \n");
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTUNSUPPORTEDCONTENT, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		print_foreign = (Button) findViewById(R.id.print_foreign);
		print_foreign.setOnClickListener(new OnClickListener() {


			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				} else if (lineDistance > 255) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				} else if (wordFont > 4 || wordFont < 1) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				} else if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}

				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTUNSUPPORTEDCONTENT, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		dialog = new ProgressDialog(PrinterActivity.this);
		dialog.setTitle(R.string.idcard_czz);
		dialog.setMessage(getText(R.string.watting));
		dialog.setCancelable(false);
		dialog.show();

		new Thread(new Runnable() {


			public void run() {
				try {
					ThermalPrinter.start(PrinterActivity.this);
					ThermalPrinter.reset();
					printVersion = ThermalPrinter.getVersion();
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
					ThermalPrinter.stop(PrinterActivity.this);
				}
			}
		}).start();

	}


	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		circle = false;
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
				if (deviceType == StringUtil.DeviceModelEnum.TPS390.ordinal() ||
						deviceType == StringUtil.DeviceModelEnum.TPS390A.ordinal()) {
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
		AlertDialog.Builder dlg = new AlertDialog.Builder(PrinterActivity.this);
		dlg.setTitle(getString(R.string.noPaper));
		dlg.setMessage(getString(R.string.noPaperNotice));
		dlg.setCancelable(false);
		dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialogInterface, int i) {
				ThermalPrinter.stop(PrinterActivity.this);
			}
		});
		dlg.show();
	}

	private class paperWalkPrintThread extends Thread {

		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.walkPaper(paperWalk);
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class barcodePrintThread extends Thread {

		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setGray(printGray);
				Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
				if (bitmap != null) {
					ThermalPrinter.printLogo(bitmap);
				}
				ThermalPrinter.addString(barcodeStr);
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(100);
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class qrcodePrintThread extends Thread {

		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				ThermalPrinter.addString("------------");
				ThermalPrinter.printString();

				Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
				if (bitmap != null) {
					ThermalPrinter.printLogo(bitmap);
				}
				ThermalPrinter.addString(qrcodeStr);
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(100);
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class contentPrintThread extends Thread {

		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				ThermalPrinter.setLeftIndent(leftDistance);
				ThermalPrinter.setLineSpace(lineDistance);
				if (wordFont == 4) {
					ThermalPrinter.setFontSize(2);
					ThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 3) {
					ThermalPrinter.setFontSize(1);
					ThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 2) {
					ThermalPrinter.setFontSize(2);
				} else if (wordFont == 1) {
					ThermalPrinter.setFontSize(1);
				}
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.addString(printContent);
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(50);
				if(deviceType == StringUtil.DeviceModelEnum.TPS616.ordinal()) {
					ThermalPrinter.paperCut();
				}
				ThermalPrinter.walkPaper(50);
				//ThermalPrinter.reset();

				// 打印二维码
				/*Bitmap bitmap = CreateCode("Com Common", BarcodeFormat.QR_CODE, 248, 248);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				ThermalPrinter.setGray(12);
				if (bitmap != null) {
					ThermalPrinter.printLogo(bitmap);
					ThermalPrinter.walkPaper(50);
				}*/
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
					circle = false;
					return;
				}
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class MakerThread extends Thread {


		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
						Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printPicture extends Thread {


		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				ThermalPrinter.addString(" ");
				ThermalPrinter.printString();
				File file = new File(picturePath);
				if (file.exists()) {
					ThermalPrinter.walkPaper(100);

					if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()) {
						printLogo681(BitmapFactory.decodeFile(picturePath));
					}else {
						ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath));
					}
					ThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {


						public void run() {
							Toast.makeText(PrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	public static Bitmap imageCrop(Bitmap bitmap, int width) {
		// 得到图片的宽，高
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		//width最大不能超过长方形的短边
		if (w < width || h < width) {
			width = w > h ? h : w;
		}

		int retX = (w - width) / 2;
		int retY = (h - width) / 2;

		return Bitmap.createBitmap(bitmap, retX, retY, width, width, null, false);
	}

	private class executeCommand extends Thread {


		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				//ThermalPrinter.sendCommand(edittext_input_command.getText().toString());
				byte[] cmdStr = {27, 64, 27, 33, 48, 29, 33, 17, 27, 71, 1, 29, 66, 0, 27, 97, 1, -61, -59, -75, -22, -41, -44, -52, -31, -75, -91, 10, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 1, 29, 66, 0, 27, 97, 1, -61, -59, -75, -22, -93, -70, -79, -29, -64, -5, -73, -28, -95, -92, -49, -42, -76, -6, -77, -57, -42, -48, -62, -73, 83, 79, 72, 79, -49, -42, -76, -6, -77, -57, 65, -41, -7, -75, -22, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 1, -75, -40, -42, -73, -93, -70, -79, -79, -66, -87, -54, -48, -77, -81, -47, -12, -57, -8, -67, -88, -71, -6, -62, -73, 56, 56, -70, -59, 83, 79, 72, 79, -49, -42, -76, -6, -77, -57, 65, -41, -7, 50, 54, 48, 51, 10, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 1, -41, -44, -52, -31, -62, -21, 10, 27, 33, 48, 29, 33, 17, 27, 71, 1, 29, 66, 0, 27, 97, 1, 48, 53, 51, 50, 10, 27, 100, 1, 27, 33, 48, 29, 33, 17, 27, 71, 1, 29, 66, 0, 27, 97, 1, 49, 51, 50, 57, 56, 51, 56, 55, 57, 50, 57, 10, 27, 100, 1, 29, 40, 107, 3, 0, 49, 67, 10, 29, 40, 107, 3, 0, 49, 69, 51, 29, 40, 107, 17, 0, 49, 80, 48, 49, 48, 48, 49, 57, 51, 50, 50, 55, 57, 56, 57, 51, 52, 27, 97, 1, 29, 40, 107, 3, 0, 49, 81, 48, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, -74, -87, -75, -91, -79, -32, -70, -59, -93, -70, 32, 49, 48, 48, 49, 57, 51, 50, 50, 55, 57, 56, 57, 51, 52, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, -49, -62, -75, -91, -54, -79, -68, -28, -93, -70, 32, 50, 48, 49, 55, 45, 48, 54, 45, 50, 55, 32, 49, 51, 58, 51, 52, 58, 50, 49, 10, 27, 33, 0, 27, 97, 0, 27, 69, 0, 27, 71, 0, 29, 33, 0, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 10, 27, 33, 32, 29, 33, 16, 27, 71, 1, 29, 66, 0, 27, 97, 0, 70, 70, -57, -8, 32, -71, -78, 49, -73, -35, 10, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 1, 29, 66, 0, 27, 97, 0, -70, -48, -41, -80, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, 49, 32, -51, -63, -74, -71, -20, -64, -59, -93, -56, -30, -95, -86, -70, -61, -74, -32, -59, -93, -56, -30, -68, -45, -63, -65, -80, -26, 40, -48, -62, 32, 53, 46, 54, 51, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, 32, -75, -22, -65, -86, -46, -75, 52, 46, 57, -43, -37, 41, 32, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, 49, 32, -65, -89, -32, -84, -68, -90, -65, -23, 40, -48, -62, -75, -22, -65, -86, -46, -75, 52, 46, 57, -43, -37, 41, 32, 52, 46, 52, 49, 10, 27, 33, 0, 27, 97, 0, 27, 69, 0, 27, 71, 0, 29, 33, 0, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, -55, -52, -58, -73, -41, -36, -54, -3, -93, -70, 49, 32, -41, -36, -67, -16, -74, -18, -93, -70, 49, 48, 46, 48, 52, -44, -86, 32, 10, 27, 33, 0, 27, 97, 0, 27, 69, 0, 27, 71, 0, 29, 33, 0, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, -54, -75, -72, -74, -67, -16, -74, -18, -93, -70, 49, 48, 46, 48, 52, -44, -86, 10, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 0, -42, -89, -72, -74, -73, -67, -54, -67, -93, -70, -50, -94, -48, -59, -42, -89, -72, -74, 10, 27, 33, 0, 27, 97, 0, 27, 69, 0, 27, 71, 0, 29, 33, 0, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 10, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 1, 29, 66, 0, 27, 97, 1, -68, -16, -69, -11, -51, -22, -77, -55, -62, -21, 10, 27, 100, 1, 29, 40, 107, 3, 0, 49, 67, 10, 29, 40, 107, 3, 0, 49, 69, 51, 29, 40, 107, 24, 0, 49, 80, 48, 49, 48, 48, 49, 57, 51, 50, 50, 55, 57, 56, 57, 51, 52, 95, 115, 111, 114, 116, 101, 100, 27, 97, 1, 29, 40, 107, 3, 0, 49, 81, 48, 27, 100, 3, 27, 33, 0, 29, 33, 0, 27, 71, 1, 29, 66, 0, 27, 97, 1, -41, -44, -52, -31, -51, -22, -77, -55, -62, -21, 10, 27, 100, 1, 29, 40, 107, 3, 0, 49, 67, 10, 29, 40, 107, 3, 0, 49, 69, 51, 29, 40, 107, 24, 0, 49, 80, 48, 49, 48, 48, 49, 57, 51, 50, 50, 55, 57, 56, 57, 51, 52, 95, 112, 105, 99, 107, 101, 100, 27, 97, 1, 29, 40, 107, 3, 0, 49, 81, 48, 27, 100, 1, 27, 100, 1, 27, 33, 0, 29, 33, 0, 27, 71, 0, 29, 66, 0, 27, 97, 1, -75, -38, 49, -76, -50, -76, -14, -45, -95, 32, 50, 48, 49, 55, 45, 54, 45, 50, 55, 32, 49, 51, 58, 51, 52, 58, 52, 56, 10, 27, 100, 1, 29, 86, 66, 16};
				ThermalPrinter.sendCommand(cmdStr, cmdStr.length);
				ThermalPrinter.checkStatus();
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
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private Bitmap changeScale(Bitmap bm) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 设置想要的大小
		int newWidth = 384;
		int newHeight = 2280;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, 1);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	private class printLongPicture extends Thread {


		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);

				File file3 = new File(picturePath3);
				if (file3.exists()) {
					ThermalPrinter.walkPaper(100);
					if(deviceType == StringUtil.DeviceModelEnum.TPS616.ordinal()) {
						ThermalPrinter.printLogo(changeScale(BitmapFactory.decodeFile(picturePath3)));
					}else if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()){
						printLogo681(changeScale(BitmapFactory.decodeFile(picturePath3)));
					}else {
						ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath3));
					}
					ThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {


						public void run() {
							Toast.makeText(PrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file4 = new File(picturePath4);
				if (file4.exists()) {
					ThermalPrinter.walkPaper(100);
					if(deviceType == StringUtil.DeviceModelEnum.TPS616.ordinal()) {
						ThermalPrinter.printLogo(changeScale(BitmapFactory.decodeFile(picturePath4)));
					}else if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()){
						printLogo681(changeScale(BitmapFactory.decodeFile(picturePath4)));
					}else {
						ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath4));
					}
					ThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {


						public void run() {
							Toast.makeText(PrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file = new File(picturePath);
				if (file.exists()) {
					ThermalPrinter.reset();
					ThermalPrinter.walkPaper(100);
					if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()) {
						printLogo681(BitmapFactory.decodeFile(picturePath));
					}else {
						ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath));
					}
					ThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {


						public void run() {
							Toast.makeText(PrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file2 = new File(picturePath2);
				if (file2.exists()) {
					ThermalPrinter.walkPaper(100);
					if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()) {
						printLogo681(BitmapFactory.decodeFile(picturePath2));
					}else {
						ThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath2));
					}
					ThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {


						public void run() {
							Toast.makeText(PrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}
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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printLongText extends Thread {

		public void run() {
			super.run();
			try {
				ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.reset();
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				ThermalPrinter.setLineSpace(5);
				if (wordFont == 4) {
					ThermalPrinter.setFontSize(2);
					ThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 3) {
					ThermalPrinter.setFontSize(1);
					ThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 2) {
					ThermalPrinter.setFontSize(2);
				} else if (wordFont == 1) {
					ThermalPrinter.setFontSize(1);
				}
				ThermalPrinter.setGray(12);
				ThermalPrinter.addString("1\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("2\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("3\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("4\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("5\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("6\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("7\n" + getString(R.string.printContent1));
				if(deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal())
					ThermalPrinter.printString();
				ThermalPrinter.addString("8\n" + getString(R.string.printContent1));
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(50);

				if(deviceType == StringUtil.DeviceModelEnum.TPS616.ordinal()) {
					ThermalPrinter.paperCut();
				}

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
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printUnsupportedText extends Thread {

		public void run() {
			super.run();
			try {
				int printer_type = SystemUtil.getPrinterType();
				int paper_width = 576;
				if(printer_type == SystemUtil.PRINTER_PT486F08401MB) {
					paper_width = 384;
				}
				ThermalPrinter.start(PrinterActivity.this);
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
				paint.getTextBounds(/*printContent*/editTextContent.getText().toString(), 0, 1, bounds);
				int line_height = bounds.height();
				line_height = line_height > fontSize ? line_height : fontSize;
				line_height = line_height > lineDistance ? line_height : lineDistance;
				int length = editTextContent.getText().toString().length();
				int line_start_index = 0;
				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (i = 0; i < length; i++) {
					if (editTextContent.getText().toString().charAt(i) == '\n') {
						sb.append(editTextContent.getText().toString().subSequence(line_start_index, i));
						line_start_index = i;
						continue;
					}
					if (leftDistance + paint.measureText(editTextContent.getText().toString(), line_start_index, i) > paper_width) {
						sb.append(editTextContent.getText().toString().subSequence(line_start_index, i - 1));
						sb.append('\n');
						line_start_index = i - 1;
						continue;
					}
				}
				sb.append(editTextContent.getText().toString().subSequence(line_start_index, i - 1));
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
				runOnUiThread(new Runnable() {
					public void run() {
						((ImageView) findViewById(R.id.view)).setImageBitmap(bitmap);
					}
				});

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
				bitmap = null;
				ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}


	protected void onDestroy() {
		if (progressDialog != null && !PrinterActivity.this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		unregisterReceiver(printReceive);
		ThermalPrinter.stop();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
		super.onDestroy();
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
	 * @param str
	 *            条码内容
	 * @param type
	 *            条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
	 *            EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
	 *            RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
	 * @param bmpWidth
	 *            生成位图宽,宽不能大于384，不然大于打印纸宽度
	 * @param bmpHeight
	 *            生成位图高，8的倍数
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

	private void printLogo681(Bitmap target) {
		int targetHeight = target.getHeight();
		if (targetHeight>200) {
			int targetHeight_1 = targetHeight/200;
			int targetHeight_2 = targetHeight%200;
			for(int i=0;i<targetHeight_1*200;i+=200) {
				try {
					ThermalPrinter.printLogo(Bitmap.createBitmap(target, 0, i, target.getWidth(), 200));
				} catch (CommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				ThermalPrinter.printLogo(Bitmap.createBitmap(target, 0, targetHeight_1*200, target.getWidth(), targetHeight_2));
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				ThermalPrinter.printLogo(target);
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
