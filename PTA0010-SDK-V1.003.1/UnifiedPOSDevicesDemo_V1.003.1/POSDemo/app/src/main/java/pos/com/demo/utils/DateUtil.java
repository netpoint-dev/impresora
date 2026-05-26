package pos.com.demo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String YYYYMMDDLINE = "yyyy-MM-dd";
	public static final String YYYYMMDDHHMMSSLINE = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDDHHMMSSSSSLINE = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 日期转化为字符串
	 * 
	 * @param date
	 *            具体的日期
	 * @param parser
	 *            解析器格式
	 * @return String 格式化后的日期字符串
	 */
	public static String formatDate(Date date, String parser) {
		DateFormat simpleDateFormat = new SimpleDateFormat(parser);
		String dateString = "" ;
		try {
			dateString = simpleDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}

	/**
	 * 字符串转化为日期格式
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param formatStr
	 *            转化器格式
	 * @return Date
	 * 
	 *         格式化后的日期
	 */
	public static Date StringToDate(String dateStr, String formatStr) {
		DateFormat dd = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static SimpleDateFormat getYyyymmddhhmmAgainFormatter(){
		return new SimpleDateFormat(YYYYMMDDHHMM);
	}

	public static SimpleDateFormat getYyyymmddhhmmssFormatter(){
		return new SimpleDateFormat(YYYYMMDDHHMMSSLINE);
	}
	/**
	 * 把日期转为yyyy-MM-dd HH:mm:ss格式的字符串
	 *
	 * @author  沈诵君
	 * @date 	2010-8-1
	 * @param value
	 * @return
	 */
	public static String getYmdhms(Date value){
		return getYyyymmddhhmmssFormatter().format(value);
	}
	public static String getYmdhm(Date value){
		return getYyyymmddhhmmAgainFormatter().format(value);
	}
	public static String getYmdhms(){
		return getYmdhms(new Date());
	}
	public static String getYmdhm(){
		return getYmdhm(new Date());
	}
}
