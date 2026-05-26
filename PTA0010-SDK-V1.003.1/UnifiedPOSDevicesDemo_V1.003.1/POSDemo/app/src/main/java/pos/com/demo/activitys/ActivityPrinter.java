package pos.com.demo.activitys;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pos.com.demo.R;
import pos.com.demo.utils.FileUtil;
import pos.com.demo.utils.LogUtil;
import pos.com.demo.utils.Utils;
import upos.POSException;
import upos.POSPrinter;
import upos.POSPrinterConst;
import upos.events.StatusUpdateEvent;
import upos.events.StatusUpdateListener;

/**
 * 1 打开打印机 打印机状态通过listener statusUpdateOccurred 回调
 * 2 打印黑白图片 devPrn.printBitmap(bitmapPrint,0,0);  发送ESC数据sendEscData()
 * 3 关闭打印机
 */
public class ActivityPrinter extends BaseActivity implements POSPrinterConst{

    private POSPrinter devPrn                      = null;
    private Spinner spPrnType                 = null;
    private int         iPtnType                = 0;//0 2inch; 1 3inch
    private Button btnPrnOpen             = null;
    private Button      btnPrnClose            = null;
    private Button      btnPrnFeed             = null;
    private Button      btnPrnPrintBmp         = null;
    private Button      btnPrnPrintEsc         = null;
    private TextView tvPrnStatus            = null;
    private String      strStatusPaperExist   = null;
    private String      strStatusPaperNoExist = null;
    private boolean     bFlagPrinterOpen        = false;

    private Button btnPrnBarcode            = null;
    private Button btnPrnSelect            = null;

    protected int findLayoutId(){
        return R.layout.activity_printer;
    }
    protected void findViews(){
        btnPrnOpen  = (Button)findViewById(R.id.prn_btn_open);
        btnPrnClose  = (Button)findViewById(R.id.prn_btn_close);
        btnPrnFeed  = (Button)findViewById(R.id.prn_btn_feed);
        btnPrnPrintBmp  = (Button)findViewById(R.id.prn_btn_printbmp);
        btnPrnPrintEsc  = (Button)findViewById(R.id.prn_btn_printesc);
        spPrnType       = (Spinner)findViewById(R.id.prn_sp_type);
        btnPrnBarcode  = (Button)findViewById(R.id.prn_btn_printbarcode);
        btnPrnSelect    = (Button)findViewById(R.id.prn_btn_select);
        tvPrnStatus     = (TextView)findViewById(R.id.prn_tv_status);
        strStatusPaperExist = getResources().getString(R.string.prn_havepaper);
        strStatusPaperNoExist = getResources().getString(R.string.prn_nopaper);
    }
    protected void init(){
        setTitle(R.string.flag_prn);
        initPrinter();
        uiShow(false);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnBack:
                closePage();
                break;
            case R.id.prn_btn_open:
                openPrinter();
                break;
            case R.id.prn_btn_close:
                closePrinter();
                break;
            case R.id.prn_btn_feed:
                doFeed();
                break;
            case R.id.prn_btn_printbmp:
                printBmp();
                break;
            case R.id.prn_btn_printesc:
                printESCData();
                break;
            case R.id.prn_btn_printbarcode:
                printBarcode();
                break;
            case R.id.prn_btn_select:
                getPermissionPath(FileUtil.CODE_FILE);
                break;
        }
    }

    /**
     * 退出页面
     */
    private void closePage(){
        closePrinter();
        this.finish();
    }

    /**
     * 设置按键使能
     * @param bOpen 设备打开成功时 ture  打开按钮变灰，其他操作按钮使能;  false 打开按钮使能，其他按钮变灰
     */
    private void uiShow(boolean bOpen){
        btnPrnOpen.setEnabled(!bOpen);
        btnPrnClose.setEnabled(bOpen);
        btnPrnFeed.setEnabled(bOpen);
        btnPrnPrintEsc.setEnabled(bOpen);
        btnPrnPrintBmp.setEnabled(bOpen);
        btnPrnBarcode.setEnabled(bOpen);
        btnPrnSelect.setEnabled(bOpen);
    }
    //------------------------------------------------------------------------------------

    /**
     * 初始化设备
     */
    private void initPrinter(){

        devPrn  = new POSPrinter();
        devPrn.addStatusUpdateListener(new StatusUpdateListener() {
            @Override
            public void statusUpdateOccurred(StatusUpdateEvent statusUpdateEvent) {
                tvPrnStatus.setText(statusUpdateEvent.getString());
            }
        });
        tvPrnStatus.setText("");
    }

    /**
     * 打开打印机
     */
    private void openPrinter(){
        if(!bFlagPrinterOpen){
            iPtnType          = spPrnType.getSelectedItemPosition();
            try {
                devPrn.open(iPtnType==0?PTR_CP_2INCH:PTR_CP_3INCH);
                bFlagPrinterOpen    = true;
            }catch (POSException e){
                bFlagPrinterOpen    = false;
            }
            uiShow(bFlagPrinterOpen);
        }
    }

    /**
     * 关闭打印机
     */
    private void closePrinter(){
        showLog("closePrinter");
        if(bFlagPrinterOpen){
            devPrn.close();
            bFlagPrinterOpen = false;
            uiShow(false);
            tvPrnStatus.setText("");
        }
    }

    /**
     * 走纸
     */
    private void doFeed(){
        //devPrn.doFeed(5);
        byte[] cmd = new byte[] { 0x1B, 0x64, 0x05};
        sendEscData(cmd);
    }

    private byte[] bmpToBytes(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
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
     * 打印图片
     */
    private void printBmp(){
        try {
            InputStream inputStream = getAssets().open(iPtnType==0?"sample1.bmp":"sample2.bmp");
            Bitmap bitmapPrint = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            byte[] arrData =  bmpToBytes(bitmapPrint);
            devPrn.printMemoryBitmap(0,arrData,PTR_BMT_BMP,bitmapPrint.getWidth(),1);
            devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸

           String strStorage = Environment.getExternalStorageDirectory().toString();
           //strStorage +=(iPtnType==0?"/Download/sample1.bmp":"/Download/sample2.bmp");
            strStorage +="/Download/android_man.bmp";

            InputStream input = getAssets().open("android_man.bmp");

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            boolean bExist = copyFile(strStorage,input);//确保图片存在
            input.close();
            int iWidth = bitmap.getWidth();
            LogUtil.info("print exist:"+bExist+" path:"+strStorage+" width:"+iWidth);

            if(bExist){
                devPrn.printBitmap(0,strStorage,iWidth,1);// 2inch 384; 3 inch 576
                devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸
            }else{
                arrData =  bmpToBytes(bitmap);
                devPrn.printMemoryBitmap(0,arrData,PTR_BMT_BMP,bitmap.getWidth(),1);
                devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片点阵数据
     * @param is
     * @return
     * @throws IOException
     */
    private  byte[] InputStreamToByte(InputStream is)throws IOException{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int ch;
        while((ch=is.read())!=-1){
            byteStream.write(ch);
        }
        byte imgdata[] = byteStream.toByteArray();
        byteStream.close();
        return imgdata;
    }
    /**
     * 打印小票 ESC数据
     */
    private void printESCData(){
//        byte[] data = Utils.getInstance().getReceiptData(iPtnType);
//        sendEscData(data);
//        devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸
        try {
            String CODEPAGE = "CP437";
//            String CODEPAGE1 = "CP1252";
//            String CODEPAGE2 = "unicode";

            String str7 = "A, B, C, D, E, F, G, H, I, J, K, L, M, N, Ñ, O, P, Q, R, S, T, U, V, W, X, Y, Z.\n";

            String str8 = "Recargas y Pago de Servicios\n" +
                    "Imagina: Te urge hacer una llamada, llegas a un negocio a recargar saldo y no cuentan con el servicio. Oh Decepción...\n" +
                    "\n" +
                    "SICAR Punto de Venta cuenta con servicios que te ayudan a estar presente con tus clientes para cuando ellos te necesiten.\n" +
                    "\n" +
                    "Control de Inventarios\n" +
                    "Imagina: Llega un cliente y le vendes un producto, luego vas a tu almacén y no está ese producto. ¿Qué haces?\n" +
                    "\n" +
                    "SICAR Punto de Venta cuenta con reportes, cortes de caja, y reporte de movimientos que te orientan para saber que pasa con cada producto.\n" +
                    "\n" +
                    "Facturación Electrónica CFDI\n" +
                    "Imagina: Llegas a un negocio a pedir una factura y te tardan horas para al final decirte que no sirve el sistema. ¿Cómo te sientes?\n" +
                    "\n" +
                    "SICAR Punto de Venta cuenta con el timbrado más rápido de América Latina, y con un 0% de fallas al momento de realizar facturas.\n" +
                    "Actualización Factura 4.0\n" +
                    "En este video veremos cuál será la forma de actualizar la información de tus clientes para poder realizar sus facturas CFDI en su versión 4.\n";
            String strFeed = "\n";
            byte[] bFeed = strFeed.getBytes();

            byte[] bCode    = getCharacterCode(CODE_Multilingual);
            devPrn.printNormal(0,bCode);

            byte[] bData7 = str7.getBytes(CODEPAGE);
            byte[] bData8 = str8.getBytes(CODEPAGE);

            devPrn.printNormal(0,bData7);

            devPrn.printNormal(0,bFeed);
            devPrn.printNormal(0,bFeed);

            devPrn.printNormal(0,bData8);

            devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 打印条形码
     */
    private void printBarcode(){
        devPrn.printBarCode(0,"123456789012",PTR_BCS_UPCA,150,2,PTR_BC_LEFT,PTR_BC_TEXT_ABOVE);
        devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸
    }

    /**
     * 发送ESC数据
     * @param bData
     */
    private void sendEscData(byte[] bData){
        String strCmd = new String(bData);
        devPrn.printNormal(0,strCmd);
    }


    private Uri treeUri = null;
    private void getPermissionPath(int iCode){

        treeUri = null;
        FileUtil.getPermissionPath(this,iCode);
    }
    @TargetApi(19)
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        //LogUtil.info("SetOther onActivity Result:"+resultCode);
        if(resultCode == RESULT_OK){
            treeUri = data.getData();
            getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//把SD卡的权限保存

            if(treeUri!=null){
                Context context	= getApplicationContext();
                switch (requestCode){
                    case FileUtil.CODE_FOLD:


                        break;
                    case FileUtil.CODE_FILE:
                        String strFile = FileUtil.getFilePathFromURI(context,treeUri);

                        showLog("file:"+strFile+" uri:"+treeUri.getPath());
//							DocumentFile docFile = DocumentFile.fromTreeUri(context,treeUri);
//							showLog("docFile:"+docFile.getName());
                        if(strFile.contains(".bmp")||strFile.contains(".jpeg")){
                            devPrn.printBitmap(0,strFile,384,1);
                            devPrn.cutPaper(PTR_CP_PARTIALCUT);//切刀并走纸
                        }
                        break;


                }
            }
        }
    }

        /** CP347 **/
        public final static int CODE_USA        = 0;

        /** CP347 **/
        public final static int CODE_Multilingual        = 2;
        /** CP860 **/
        public final static int CODE_Portuguese = 3;
        /** CP863 **/
        public final static int CODE_Canada     = 4;
        /** CP865 **/
        public final static int CODE_Nordic     = 5;
        /** 1252**/
        public final static int CODE_Spanish    = 7;
        /** CP857 **/
        public final static int CODE_Turkey     = 8;
        /** **/
        public final static int CODE_Farsi      = 10;
        /** CP864 **/
        public final static int CODE_Arabic     = 14;
        /** CP852 **/
        public final static int CODE_Latin2     = 18;
        /** CP737 **/
        public final static int CODE_Greek      = 20;
        /** CP1254 **/
        public final static int CODE_Turkey2    = 25;
        /** **/
        public final static int CODE_Vietnam    = 27;
        /** CP1255 **/
        public final static int CODE_Israel     = 32;
        /** **/
        public final static int CODE_Rumania    = 33;
        /** CP866 **/
        public final static int CODE_Cyrillic   = 59;
        /**
         * Select page n of the character code table.<br>
         * @return success or not
         */
        public byte[] getCharacterCode(int code) {
            byte[] cmd = new byte[] { 0x1B, 0x74, 0x00};
            cmd[2] = (byte)(code);
            return cmd;
        }
}
