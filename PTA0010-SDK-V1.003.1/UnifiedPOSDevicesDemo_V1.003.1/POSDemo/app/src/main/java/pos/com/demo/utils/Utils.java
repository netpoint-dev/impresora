package pos.com.demo.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Utils {

    static private Utils instance = null;
    static public Utils getInstance(){
        if(instance==null){
            instance    = new Utils();
        }
        return instance;
    }

    /**
     *
     * @param s  ex. s="010203abac0a"
     * @return   ex. btye[]{0x01,0x02,0x03,0xab,0xac,0x0a};
     */
    public byte[] stringToHexBytes(String s) {
        byte[] bRet = null;
        if(!s.isEmpty()&&s.length()>1){
            int iLen = s.length()/2;
            bRet	= new byte[iLen];
            for(int i=0;i<s.length()/2;i++){
                String tmp	= s.substring(i*2,i*2+2);
                try {
                    int ch = Integer.valueOf(tmp, 16);
                    bRet[i] = (byte)ch;
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
        return bRet;
    }

    /**
     * bytes data to string, separate by " ";<br>
     * ex. src = {1,2,9,10,20}  --> "01 02 09 0a 14"
     * @param src
     * @param len: srv valid data's length
     * @return
     */
     public   String bytesToHexString(byte[] src,int len){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        if(len>src.length){
            len = src.length;
        }

        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 小票字段
     */
    private class GoodsInfo{
        public String 	m_strName;

        public float  	m_fDiscount;
        public float  	m_fPrice;
        public float  	m_fCount;
        public float  	m_fAmount;
        public GoodsInfo(String name,float dis,float price,float cnt){
            m_strName		= name;
            m_fDiscount		= dis;
            m_fPrice		= price;
            m_fCount		= cnt;
            m_fAmount		= m_fPrice*m_fCount*m_fDiscount;
        }
    }

    /**
     *
     * @param PrinterType
     * @return
     */
    public byte[] getReceiptData(int PrinterType){

        GoodsInfo[] infoArr	= {
                new GoodsInfo("Apple", (float)1,  (float)20, (float)2),
                new GoodsInfo("Grape", (float)1,  (float)30, (float)3),
                new GoodsInfo("Lemon", (float)1, (float)35,(float)2),
                new GoodsInfo("Watermelon",(float)0.9 , (float)37, (float)1.1),
                new GoodsInfo("Pomegranate", (float)0.8, (float)4.5, (float)0.56),
                new GoodsInfo("Mango", (float)0.9, (float)3.6, (float)0.7),
                new GoodsInfo("Black Pepper Mini Steak", (float)1, (float)3.56, (float)1),
                new GoodsInfo("Cheese Lovers Pizza", (float)1, (float)4.56, (float)1),
                new GoodsInfo("Garen Veggies", (float)1, (float)4.65, (float)1),
                new GoodsInfo("Hawaiian Pizza", (float)1, (float)3.72, (float)1),
                new GoodsInfo("Jumbo Shnmp wrapped in bacon", (float)1, (float)6.55, (float)1),
                new GoodsInfo("Banana", (float)1, (float)1.33, (float)1.2),
                new GoodsInfo("Orange", (float)1, (float)2.01, (float)1.5),

        };
        ArrayList<GoodsInfo> list = new ArrayList<GoodsInfo>();
        for (GoodsInfo goodsInfo : infoArr) {
            list.add(goodsInfo);
        }

        return printInfo(list,PrinterType);
    }

    /**
     * 小票里面 分成多列时，每列的宽度是知道的，填充空格 用来对齐 第二列 第三列。。。。
     * 指令 只能用来 左对齐 右对齐 中间
     * @param list  商品信息
     * @param iInchType  打印机尺寸  0 2inch ； 1 3inch
     * @return  小票的ESC数据
     */
    private byte[] printInfo(ArrayList<GoodsInfo> list,int iInchType){
        ByteArrayOutputStream printerBuffer = new ByteArrayOutputStream();

        //每一行 要分成几列，每列占据多少字符数  在这个定义

        int		iSpaceDate		= iInchType==0?18:22;
        int		iSpaceName		= iInchType==0?10:14;//商品名列 2寸纸占据10个字符空间
        int		iSpaceDis		= iInchType==0?5:7;//折扣列
        int		iSpacePrice		= iInchType==0?6:8;//单价列
        int 	iSpaceCnt		= iInchType==0?6:8;//数量列
        int		iSpaceAmt		= iInchType==0?4:4;//amount
        int		iSpaceItems		= iInchType==0?11:21;
        int		iSpaceTalAmt	= iInchType==0?11:15;
        int   	iSpaceTalVal	= iInchType==0?8:12;

        final byte BoldChinese[]	= { 0x1c, 0x21, 0x0c };
        final byte Bold[]	= { 0x1b, 0x21, 0x38 };
        final byte Normal[]	= { 0x1b, 0x21, 0x00 };
        final byte Middle[]	= { 0x1b, 0x61, 0x01 };
        final byte Left[]	= { 0x1b, 0x61, 0x00 };
        final byte Right[]	= { 0x1b, 0x61, 0x02 };
        final byte NextRow[]	= { 0x1b, 0x4a, 0x00 };
        final byte tmpfeed[]    = {0xd,0xa};
        final byte Interval[]	= { '-', '-','-','-','-','-','-','-','-','-','-','-','-','-', '-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-' };

        final byte Init[]	  = {0x1B,0x40};

        printerBuffer.write(Init, 0, Init.length);



        printerBuffer.write(Normal,0,Normal.length);
        printerBuffer.write(Left,0,Left.length);
        byte[] bLineReceiptNum = null;
        String strNum = "#HK9718081500004";
        bLineReceiptNum	= strNum.getBytes();

        byte[] bModeWH = getPrintMode(true,true,true,false);
        printerBuffer.write(bModeWH,0,bModeWH.length);
        printerBuffer.write(bLineReceiptNum,0,bLineReceiptNum.length);
        printerBuffer.write(NextRow,0,NextRow.length);


        byte[] bModeW = getPrintMode(false,false,true,false);
        printerBuffer.write(bModeW,0,bModeW.length);
        String strCashier	= "Cashier:Admin";
        byte[] bPeople = strCashier.getBytes();
        printerBuffer.write(bPeople,0,bPeople.length);
        printerBuffer.write(NextRow,0,NextRow.length);


        byte[] bModeNormal = getPrintMode(false,false,false,false);
        printerBuffer.write(bModeNormal,0,bModeNormal.length);
        //mprinter.Write(Left);
        Date dtNow = new java.util.Date();

        final String FORM_STRING 	= "dd/MM/yyyy";
        final String FORM_TIME 		= "hh:mm:ss";
        SimpleDateFormat date = new SimpleDateFormat(FORM_STRING, Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat(FORM_TIME,Locale.getDefault());

        String strDate 	= "Date:"+date.format(dtNow);
        byte[] bDate 	= addSpacebyte(strDate.getBytes(),iSpaceDate);
        String strTime	= "Time:"+time.format(dtNow);
        byte[] bTime = strTime.getBytes();

        byte[] bFontTwo	= getFontSize(2,3);
        printerBuffer.write(bFontTwo,0,bFontTwo.length);
        printerBuffer.write(bDate,0,bDate.length);

        printerBuffer.write(bTime, 0, bTime.length);
        printerBuffer.write(NextRow,0,NextRow.length);

        byte[] bFontOne	= getFontSize(1,1);
        printerBuffer.write(bFontOne,0,bFontOne.length);

        String strHeadName		= "Name";
        byte[] bLineHeadName			= addSpacebyte(strHeadName.getBytes(),iSpaceName );

        String strHeadDiscount 	= "-%";
        byte[] bLineHeadDiscount		= addSpacebyte(strHeadDiscount.getBytes(), iSpaceDis);

        String strHeadPrice		= "$/PCS";
        byte[] bLineHeadPrice			= addSpacebyte(strHeadPrice.getBytes(), iSpacePrice);

        String strHeadCount		= "PCS";
        byte[] bLineHeadCount			= addSpacebyte(strHeadCount.getBytes(), iSpaceCnt);

        String strHeadAmount 	= "AMT";
        byte[] bLineHeadAmount			= addSpacebyte(strHeadAmount.getBytes(), iSpaceAmt);


        printerBuffer.write(bLineHeadName,0,bLineHeadName.length);
        //printerBuffer.write(bLineHeadDiscount,0,bLineHeadDiscount.length);
        printerBuffer.write(bLineHeadPrice,0,bLineHeadPrice.length);
        printerBuffer.write(bLineHeadCount,0,bLineHeadCount.length);

        printerBuffer.write(bLineHeadDiscount,0,bLineHeadDiscount.length);
        printerBuffer.write(bLineHeadAmount,0,bLineHeadAmount.length);

        printerBuffer.write(NextRow,0,NextRow.length);

        String 		strPriceUnit  	= "$/kg";
        String 		strUnit			= "kg";
        byte[]		bPriceUnit		= addSpacebyte(strPriceUnit.getBytes(), iSpacePrice);
        byte[]		bUnit			= addSpacebyte(strUnit.getBytes(),iSpaceCnt);

        printerBuffer.write(addSpacebyte(null, iSpaceName), 0, iSpaceName);
        printerBuffer.write(bPriceUnit,0,bPriceUnit.length);
        printerBuffer.write(bUnit,0,bUnit.length);

        printerBuffer.write(NextRow,0,NextRow.length);

        printerBuffer.write(Middle,0,Middle.length);
        printerBuffer.write(Interval,0,Interval.length);
        printerBuffer.write(NextRow,0,NextRow.length);
        printerBuffer.write(Left,0,Left.length);

        float	fTotal = 0;
        for (GoodsInfo b : list) {
            fTotal += b.m_fAmount;
            String strDis  = String.format("%.2f", b.m_fDiscount);
            String strPri  = String.format("%.2f", b.m_fPrice);
            String strCnt  = String.format("%.3f", b.m_fCount);
            String strTol  = String.format("%.2f", b.m_fAmount);

            byte[] bData = null;

            bData = addSpacebyte(b.m_strName.getBytes(), iSpaceName);
            //boolean bNextLine = false;
            printerBuffer.write(bData,0,bData.length);
            if(b.m_strName.getBytes().length>iSpaceName){
                //bNextLine		= true;
                printerBuffer.write(NextRow,0,NextRow.length);
                printerBuffer.write(addSpacebyte(null, iSpaceName),0,iSpaceName);
            }
//				bData = addSpacebyte(strDis.getBytes(), iSpaceDis);
//	        	printerBuffer.write(bData,0,bData.length);
            bData = addSpacebyte(strPri.getBytes(), iSpacePrice);
            printerBuffer.write(bData,0,bData.length);
            bData = addSpacebyte(strCnt.getBytes(), iSpaceCnt);
            printerBuffer.write(bData,0,bData.length);

            bData = addSpacebyte(strDis.getBytes(), iSpaceDis);
            printerBuffer.write(bData,0,bData.length);

            bData = strTol.getBytes();
            printerBuffer.write(bData,0,bData.length);
            printerBuffer.write(NextRow,0,NextRow.length);

        }
        printerBuffer.write(Middle,0,Middle.length);
        printerBuffer.write(Interval,0,Interval.length);
        printerBuffer.write(NextRow,0,NextRow.length);


        printerBuffer.write(Left,0,Left.length);
        String strItem 		= "Items:"+"("+String.valueOf(list.size())+")";
        byte[] bItem = addSpacebyte(strItem.getBytes(),iSpaceItems);
        printerBuffer.write(bItem,0,bItem.length);

        String strTotal		= "Total Amt:";
        byte[] bTotal = addSpacebyteLeft(strTotal.getBytes(),iSpaceTalAmt);
        printerBuffer.write(bTotal,0,bTotal.length);
        byte[] bValTol = addSpacebyteLeft(String.format("$%.2f", fTotal).getBytes(),iSpaceTalVal);

        //String strRecVal 	= String.format("%.1f", fTotal);
        String strRecVal    = new DecimalFormat("$0.00").format(fTotal);
//		Float	fReceipt = Float.parseFloat(strRecVal);
//     	float  fRec		= fReceipt.floatValue();
//     	byte[] bValRec = addSpacebyteLeft(String.format("$%.2f", fRec).getBytes(),iSpaceTalVal);
        byte[] bValRec = addSpacebyteLeft(strRecVal.getBytes(),iSpaceTalVal);

        printerBuffer.write(bValTol,0,bValTol.length);
        printerBuffer.write(NextRow,0,NextRow.length);

        //mprinter.Write(addSpacebyte(null,iInchType==0?15:21));
        //byte[] bSpace = addSpacebyte(null,iInchType==0?15:21);
        //printerBuffer.write(bSpace,0,bSpace.length);

        String strRec		= "Receivable:";
        byte[] bRec = addSpacebyteLeft(strRec.getBytes(),iSpaceItems+iSpaceTalAmt);
        printerBuffer.write(bRec,0,bRec.length);
        printerBuffer.write(bValRec,0,bValRec.length);
        printerBuffer.write(NextRow,0,NextRow.length);

        //mprinter.Write(addSpacebyte(null,iInchType==0?15:21));
        //printerBuffer.write(bSpace,0,bSpace.length);
        String strCash		= "Cash:";
        byte[] bCash 		= addSpacebyteLeft(strCash.getBytes(),iSpaceItems+iSpaceTalAmt);
        printerBuffer.write(bCash,0,bCash.length);
        printerBuffer.write(bValRec,0,bValRec.length);
        printerBuffer.write(NextRow,0,NextRow.length);
        printerBuffer.write(NextRow,0,NextRow.length);

//	    	String strMac 	= "机器号：XC6V";
//	    	byte[] bMac	= getChineseByte(strMac);
//     	printerBuffer.write(bMac,0,bMac.length);
        printerBuffer.write(tmpfeed,0,tmpfeed.length);
        printerBuffer.write(tmpfeed,0,tmpfeed.length);

        String strMac 	= "Machine ID:HK97";
        byte[] bMac		= strMac.getBytes();
        printerBuffer.write(bMac,0,bMac.length);
        printerBuffer.write(NextRow,0,NextRow.length);

        final byte cmd_1[] = {0x1d ,0x68 ,0x41};//CHOOSE THE BARCODE HEIGHT AS 0x35(max = 0x41)
        final byte cmd_2[] = {0x1d ,0x48 ,0x03};//CHOOSE THE PRINTING POSITION OF BARCODE VALUE 0x1 UPPER 0x02 LOWER 0x03 UPPER AND LOWER
        final byte cmd_3[] = {0x1d ,0x77 ,0x2};//CHOOSE THE BARCODE WIDTH AS 0x2(max = 0x8)

        String strCode 	= "200001";
        byte[] bCode	= createBarcode(strCode);
        printerBuffer.write(cmd_1,0,cmd_1.length);
        printerBuffer.write(cmd_2,0,cmd_2.length);
        printerBuffer.write(cmd_3,0,cmd_3.length);

        printerBuffer.write(bCode,0,bCode.length);


        return printerBuffer.toByteArray();
    }

    byte[]	createBarcode(String strCode){

        byte[] bCode = strCode.getBytes();
        int iLen = 3+1+bCode.length;
        byte[] bRet = new byte[iLen];
        final byte BAREN8[]= {0x1d ,0x6b  ,0x04 };//code39
        System.arraycopy(BAREN8, 0, bRet, 0, 3);
        System.arraycopy(bCode, 0, bRet, 3, bCode.length);
        bRet[iLen-1] = 0x00;
        return bRet;
    }

    /**
     * 当src字符数小于cnt时在字符右侧填充空格
     * @param src
     * @param cnt
     * @return
     */
    byte[] addSpacebyte(byte[] src,int cnt){
        byte[] bRet = null;
        int iCnt = src==null?0:src.length;

        if(iCnt<cnt){
            bRet = new byte[cnt+1];
            if(src!=null){
                System.arraycopy(src, 0, bRet, 0, iCnt);
            }
            for(int i=0;i<cnt-iCnt;i++){
                bRet[iCnt+i] += ' ';
            }
            bRet[cnt] = '\0';
        }else {
            bRet = src;
        }
        return bRet;
    }

    /**
     * 当src字符小于cnt时，在字符左侧填充空格
     * @param src
     * @param cnt
     * @return
     */
    byte[] addSpacebyteLeft(byte[] src,int cnt){
        byte[] bRet = null;
        int iCnt = src==null?0:src.length;

        if(iCnt<cnt){
            bRet = new byte[cnt+1];
            if(src!=null){
                System.arraycopy(src, 0, bRet, cnt-iCnt, iCnt);
            }
            for(int i=0;i<cnt-iCnt;i++){
                bRet[i] += ' ';
            }
            bRet[cnt] = '\0';
        }else {
            bRet = src;
        }
        return bRet;
    }


    /**
     *
     * @param emphasize         is select emphasize mode  加粗模式
     * @param doubleHeight      is select double-height mode 倍高模式
     * @param doubleWidth       is select double-width mode   倍宽模式
     * @param underLine         is select underline mode		下划线
     * @return byte array
     */
    public byte[] getPrintMode(boolean emphasize, boolean doubleHeight, boolean doubleWidth, boolean underLine){

        byte[] cmd = new byte[] { 0x1B, 0x21, 0x00};

        if (emphasize) {
            cmd[2] |= (1 << 3);
        }
        if (doubleHeight) {
            cmd[2] |= (1 << 4);
        }
        if (doubleWidth) {
            cmd[2] |= (1 << 5);
        }
        if (underLine) {
            cmd[2] |= (1 << 7);
        }

        return cmd;
    }

    /**
     *设置字体大小
     * @param iWidth   横向倍数 1-8
     * @param iHeight  纵向倍数 1-8
     * @return
     */
    public byte[] getFontSize(int iWidth,int iHeight){

        byte[] cmd = new byte[] { 0x1D, 0x21, 0x00};

        int iVal 	= (iWidth-1)<<4;
        iVal		+= (iHeight-1);
        cmd[2]	= (byte)iVal;

        return cmd;
    }
}
