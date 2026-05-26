package pos.com.demo.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

public class FileUtil {

	/**
	 * 判断是否存在sdcard
	 *@return
	 *boolean 是否存在sdcard
	 */
	private static boolean isHasSdcard(){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true ;
		}
		return false ;
	}
	/**
	 * 获取当前应用的files文件目录
	 *@param context 上下文
	 *@return
	 *String 文件目录
	 */
	private static String getAppFilesPath(Context context){
		/**
		 * 	/data/data/com.xql.testnet/files
		 */
		return context.getApplicationContext().getFilesDir().getAbsolutePath() ;
	}
	/**
	 * 获取sdcard目录
	 *@return
	 *String 文件目录
	 */
	public static String getSdcardPath(){
		/**
		 * 	/mnt/sdcard
		 */
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获得保存图片的文件目录
	 *@param context 上下文
	 *@return
	 *String 文件保存的目录
	 */
	public static String getSaveFileDir(Context context){
		StringBuilder stringBuilder = new StringBuilder();
		if (isHasSdcard()) {
			stringBuilder.append(context.getExternalFilesDir("crashlog"));
		}else {
			stringBuilder.append(getAppFilesPath(context)).append(File.separator).append("crashlog");
		}
		File saveDir = new File(stringBuilder.toString());
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取图片文件路径
	 *@param context
	 *@return
	 *String
	 */
	public static String getPicturePath(Context context){
		return getSaveFileDir(context)+File.separator + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss") + ".jpg";
	}
	/**
	 * 获取文件路径
	 *@param context
	 *@return
	 *String
	 */
	public static String getFilePath(Context context,String fileName){
		return getSaveFileDir(context)+File.separator + fileName;
	}

	/**
	 * 将本地文件转换成图片
	 *@param srcPath 文件
	 *@return
	 *Bitmap 图片
	 */
	public static Bitmap getLoacalBitmap(String srcPath) {
//		FileInputStream fileInputStream = null ;
		Bitmap bitmap = null ;
		try {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			//开始读入图片，此时把options.inJustDecodeBounds 设回true了
//			newOpts.inJustDecodeBounds = true;
//			bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
//
//			newOpts.inJustDecodeBounds = false;
//			int w = newOpts.outWidth;
//			int h = newOpts.outHeight;
//			//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//
//			int width = context.getd
//
//			float hh = 800f;//这里设置高度为800f
//			float ww = 480f;//这里设置宽度为480f
//			//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//			int be = 1;//be=1表示不缩放
//			if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//				be = (int) (newOpts.outWidth / ww);
//			} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//				be = (int) (newOpts.outHeight / hh);
//			}
//			if (be <= 0)
//				be = 1;
			newOpts.inSampleSize = 8;//设置缩放比例
			//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//			return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
		}
		catch (Exception e) {
		}finally{
//			if (null!=fileInputStream) {
//				try {
//					fileInputStream.close();
//				} catch (IOException e) {
//				}
//			}
		}
		return bitmap;
	}

	/**
	 * 获得竖屏的图片
	 *@param picPath
	 *@param bitmap
	 *@return
	 *Bitmap
	 */
	public static Bitmap getPortraitPicture(String picPath,Bitmap bitmap){
		return rotaingPicture(readPictureDegree(picPath), bitmap);
	}

	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
    private static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
   /**
    * 旋转图片 
    *@param angle
    *@param bitmap
    *@return
    *Bitmap
    */
   public static Bitmap rotaingPicture(int angle , Bitmap bitmap) {
       //旋转图片 动作   
       Matrix matrix = new Matrix();;  
       matrix.postRotate(angle);  
       // 创建新的图片   
       
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
       return resizedBitmap;  
   }

	public static Bitmap convertGreyImg(Bitmap img,float fRateRed,float fRateGreen,float fRateBlue){
		int width		= img.getWidth();
		int height 		= img.getHeight();
		int[] pixels	= new int[width*height];
		img.getPixels(pixels,0,width,0,0,width,height);
		int alpha = 0xFF<<24;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				int grey		= pixels[width*i+j];
				int red			= ((grey&0x00ff0000)>>16);
				int green		= ((grey&0x0000ff00)>>8);
				int blue		= ((grey&0x000000ff));
				grey			= (int)((float)red*fRateRed+(float)green*fRateGreen+(float)blue*fRateBlue);
				grey 			= alpha|(grey<<16)|(grey<<8)|grey;
				pixels[width*i+j]	= grey;
			}
		}
		Bitmap result 	= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
		result.setPixels(pixels,0,width,0,0,width,height);
		return result;
	}
//	/**
//     * 读取文件的内容
//     * @param filename 文件名称
//     * @return
//     * @throws Exception
//     */
//    public static String readFile(File readFile)
//    {
//        //得到文件的二进制数据
//		byte[] data = null;
//		try {
//			//获得输入流
//			FileInputStream inStream = new FileInputStream(readFile);
//			//FileInputStream inStream = m_context.openFileInput(filename);
//			//new一个缓冲区
//			byte[] buffer = new byte[1024];
//			int len = 0;
//			//使用ByteArrayOutputStream类来处理输出流
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			while( (len = inStream.read(buffer))!= -1)
//			{
//			    //写入数据
//			    outStream.write(buffer, 0, len);
//			}
//			data = outStream.toByteArray();
//			//关闭流
//			outStream.close();
//			inStream.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        try {
//			return new String(data,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//		}
//        return null ;
//    }

	/**
	 * 将文件转换成base64字符串
	 * @param file
	 * @return 字符串
	 */
	public static String getStrFromFileByBase64(File file) {
		byte[] bytes = null;
		FileInputStream inputStream = null ;
		try {
			inputStream = new FileInputStream(file);
			bytes = new byte[inputStream.available()];
			inputStream.read(bytes, 0, bytes.length);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally{
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		if (bytes==null) {
			return "" ;
		}else {
			return Base64.encodeToString(bytes, 0);
		}
	}

	/**
	 * 将base64字符串转换成bitmap
	 * @param iconBase64 字符串
	 * @return bitmap 图片
	 */
	public static byte[] getFileFromStrByBase64(String iconBase64) {
		return Base64.decode(iconBase64, 0) ;
	}

	/**
	 * 文本文件转换为指定编码的字符串
	 * @param file         文本文件
	 * @return 转换后的字符串
	 */
	public static String readFile(File file) {

		return getStrFromFileByBase64(file);

//		StringBuffer stringBuffe=new StringBuffer();
//		try {
//			FileInputStream fileInputStream=new FileInputStream(file);
//			// 将输入流写入输出流
//						byte[] buffer = new byte[1024];
//						int n = 0;
//						while (-1 != (n = fileInputStream.read(buffer))) {
//							stringBuffe.append(new String(buffer, 0, n));
//						}
//						fileInputStream.close();
//
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return stringBuffe.toString();

		/*InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			// 将输入流写入输出流
			char[] buffer = new char[1024];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// 返回转换结果
			return writer.toString();*/
	}

	/**
	 * 将字符串写入指定文件
	 * @param fileContent            原字符串
	 * @param context 上下文
	 * @return
	 */
	public static void writeFile(String fileContent,Context context,String fileName) {

		FileOutputStream out = null ;
		try {
			byte[] byteArray = getFileFromStrByBase64(fileContent);
			out = new FileOutputStream(getFilePath(context, fileName));
			out.write(byteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	finally{
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

//            BufferedReader bufferedReader = null;
//            BufferedWriter bufferedWriter = null;
//            try {
//                    File distFile = new File(getFilePath(context, fileName));
//                    bufferedReader = new BufferedReader(new StringReader(fileContent));
//                    bufferedWriter = new BufferedWriter(new FileWriter(distFile));
//                    char buf[] = new char[1024];         //字符缓冲区
//                    int len;
//                    while ((len = bufferedReader.read(buf)) != -1) {
//                            bufferedWriter.write(buf, 0, len);
//                    }
//                    bufferedWriter.flush();
//                    bufferedReader.close();
//                    bufferedWriter.close();
//            } catch (IOException e) {
//                    e.printStackTrace();
//            } finally {
//                    if (bufferedReader != null) {
//                            try {
//                                    bufferedReader.close();
//                            } catch (IOException e) {
//                                    e.printStackTrace();
//                            }
//                    }
//            }
	}

	/**
	 * 打开文件
	 * @param file
	 */
	public static void openFile(Activity activity ,File file){

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型
		String type = getMIMEType(file);
		//设置intent的data和Type属性。
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//跳转
		activity.startActivity(intent);

	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file	文件
	 * @return String 文件类型
	 */
	private static String getMIMEType(File file) {

		String type="*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
		/* 获取文件的后缀名*/
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for(int i=0;i<MIME_MapTable.length;i++){
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}
	//文件后缀类型
	private static String[][] MIME_MapTable={
			//{后缀名，MIME类型}
			{".3gp",    "video/3gpp"},
			{".apk",    "application/vnd.android.package-archive"},
			{".asf",    "video/x-ms-asf"},
			{".avi",    "video/x-msvideo"},
			{".bin",    "application/octet-stream"},
			{".bmp",    "image/bmp"},
			{".c",  "text/plain"},
			{".class",  "application/octet-stream"},
			{".conf",   "text/plain"},
			{".cpp",    "text/plain"},
			{".doc",    "application/msword"},
			{".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls",    "application/vnd.ms-excel"},
			{".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe",    "application/octet-stream"},
			{".gif",    "image/gif"},
			{".gtar",   "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h",  "text/plain"},
			{".htm",    "text/html"},
			{".html",   "text/html"},
			{".jar",    "application/java-archive"},
			{".java",   "text/plain"},
			{".jpeg",   "image/jpeg"},
			{".jpg",    "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log",    "text/plain"},
			{".m3u",    "audio/x-mpegurl"},
			{".m4a",    "audio/mp4a-latm"},
			{".m4b",    "audio/mp4a-latm"},
			{".m4p",    "audio/mp4a-latm"},
			{".m4u",    "video/vnd.mpegurl"},
			{".m4v",    "video/x-m4v"},
			{".mov",    "video/quicktime"},
			{".mp2",    "audio/x-mpeg"},
			{".mp3",    "audio/x-mpeg"},
			{".mp4",    "video/mp4"},
			{".mpc",    "application/vnd.mpohun.certificate"},
			{".mpe",    "video/mpeg"},
			{".mpeg",   "video/mpeg"},
			{".mpg",    "video/mpeg"},
			{".mpg4",   "video/mp4"},
			{".mpga",   "audio/mpeg"},
			{".msg",    "application/vnd.ms-outlook"},
			{".ogg",    "audio/ogg"},
			{".pdf",    "application/pdf"},
			{".png",    "image/png"},
			{".pps",    "application/vnd.ms-powerpoint"},
			{".ppt",    "application/vnd.ms-powerpoint"},
			{".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop",   "text/plain"},
			{".rc", "text/plain"},
			{".rmvb",   "audio/x-pn-realaudio"},
			{".rtf",    "application/rtf"},
			{".sh", "text/plain"},
			{".tar",    "application/x-tar"},
			{".tgz",    "application/x-compressed"},
			{".txt",    "text/plain"},
			{".wav",    "audio/x-wav"},
			{".wma",    "audio/x-ms-wma"},
			{".wmv",    "audio/x-ms-wmv"},
			{".wps",    "application/vnd.ms-works"},
			{".xml",    "text/plain"},
			{".z",  "application/x-compress"},
			{".zip",    "application/x-zip-compressed"},
			{"",        "*/*"}
	};

	/**
	 * 格式化文件大小
	 *@param paramLong	文件大小
	 *@return
	 *String	格式化后的文件大小
	 */
	public static String formetFileSize(long paramLong)
	{
		DecimalFormat localDecimalFormat = new DecimalFormat("#.00");
		String str;
		if (paramLong == 0L)
			str = "0B";
		else {
			if (paramLong < 1024L)
				str = localDecimalFormat.format(paramLong) + "B";
			else if (paramLong < 1048576L)
				str = localDecimalFormat.format(paramLong / 1024.0D) + "KB";
			else if (paramLong < 1073741824L)
				str = localDecimalFormat.format(paramLong / 1048576.0D) + "MB";
			else
				str = localDecimalFormat.format(paramLong / 1073741824.0D) + "GB";
		}
		return str;
	}

	/**
	 * 格式化文件大小
	 *@param paramLong	文件大小
	 *@return
	 *String	格式化后的文件大小
	 */
	public static String formatFileSize(long paramLong)
	{
		DecimalFormat localDecimalFormat = new DecimalFormat("#.00");
		String str;
		if (paramLong == 0L)
			str = "0";
		else {
			str = localDecimalFormat.format(paramLong / 1024.0D);
		}
		return str;
	}

	/**
	 * 先检查 path的文件是否存在，如果不存在，用 inputStream 拷贝到path的位置
	 * @param strPath  文件路径
	 * @param inputStream    输入流数据
	 * @return   strPath 文件存在 返回true ，否则 false
	 */
	public static boolean copyFile(String strPath,InputStream inputStream){
		File    file    = new File(strPath);
		boolean	bRet = false;
		if(!file.exists()||(file.exists()&&file.length()<=0)){
			try {
				OutputStream outputStream	= new FileOutputStream(file);
				int iTotal = copyStream(inputStream,outputStream);
				LogUtil.info("copyFile size:"+iTotal+" file:"+strPath);
				outputStream.close();
				bRet	= iTotal>0;
			}catch (Exception e){
				e.printStackTrace();
			}
		}else{
			bRet	= true;
		}
		return bRet;
	}

	private static void  copyFile(Context context, Uri srcUri, File dstFile){
		try {
			InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
			if(inputStream==null){
				return;
			}
			OutputStream outputStream	= new FileOutputStream(dstFile);
			copyStream(inputStream,outputStream);
			inputStream.close();
			outputStream.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static String getFilePathFromURI(Context context, Uri contentUri){
		File rootDataDir = context.getExternalFilesDir(null);
		//Log.d(Tag,"rootDataDir:"+rootDataDir);
//		String fileName = getFileName(contentUri);
		String fileName = getRealNameFromURI(context,contentUri);
		if(!TextUtils.isEmpty(fileName)){
			File  dstFile = new File(rootDataDir+File.separator+fileName);
			copyFile(context,contentUri,dstFile);
			return dstFile.getAbsolutePath();
		}
		return null;
	}
	public static String getRealNameFromURI(Context context,final Uri uri){
		if(null==uri){
			return null;
		}
		final String scheme = uri.getScheme();
		String data = null;
		if(scheme==null){
			data = uri.getPath();
		}else if(ContentResolver.SCHEME_FILE.equals(scheme)){
			data = uri.getPath();
		}else if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
			//Cursor cursor = context.getContentResolver().query(uri,new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
			Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
			if(null!=cursor){
//				int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
				int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				if(cursor.moveToFirst()){
					if(index>-1){
						data = cursor.getString(index);
						LogUtil.info("index:"+index+" data:"+data);
					}else {
						LogUtil.error("index -1");
					}
				}else {
					LogUtil.error("moveToFirst false");
				}
				cursor.close();
			}else {
				LogUtil.error("cursor null");
			}
		}else{
			LogUtil.error("data return null scheme:"+scheme);
		}
		if(data!=null){
			int cut = data.lastIndexOf('/');
			if(cut!=-1){
				data = data.substring(cut+1);
			}
		}
		return data;
	}

	private static String getFileName(Uri uri){
		if(uri==null){
			return null;
		}
		String fileName = null;
		String path = uri.getPath();
		int cut = path.lastIndexOf('/');
		if(cut!=-1){
			fileName = path.substring(cut+1);
		}
		return fileName;
	}

	private static int copyStream(InputStream input, OutputStream output)throws Exception,IOException {
		int count = 0,n = 0;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try{
			final int BUFFER_SIZE = 1024*2;
			byte[] buffer = new byte[BUFFER_SIZE];
			in = new BufferedInputStream(input,BUFFER_SIZE);
			out = new BufferedOutputStream(output,BUFFER_SIZE);
			//int count = 0,n = 0;
			while((n=in.read(buffer,0,BUFFER_SIZE))!=-1){
				out.write(buffer,0,n);
				count += n;
			}
			out.flush();
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try{
				if(out!=null){
					out.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
			try{
				if(in!=null){
					in.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return count;
	}


	static public final int  CODE_FILE      = 0;
	static public final int  CODE_FOLD      = 1;


	static public void getPermissionPath(Activity view, int iCode){
		if(Build.VERSION.SDK_INT>=21){
			Intent intent  = null;
			switch (iCode){
				case CODE_FILE:
					intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//获取文件
					intent.setType("*/*");
					break;
				case CODE_FOLD:
					intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);//获取目录
					break;
			}
			view.startActivityForResult(intent, iCode);
		}
	}
}
