package com.dian.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.expr.NewArray;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by geely
 */
public class DateTimeUtil {

	// joda-time

	// str->Date
	// Date->str
	public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String STANDARD_DATA = "yyyy-MM-dd";

	public static Date strToDate(String dateTimeStr, String formatStr) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}

	public static String dateToStr(Date date, String formatStr) {
		if (date == null) {
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(formatStr);
	}

	public static Date strToDate(String dateTimeStr) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}

	public static String dateToStr(Date date) {
		if (date == null) {
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMAT);
	}

	/**
	 * @param date
	 * @param day  想要获取的日期与传入日期的差值 比如想要获取传入日期前四天的日期 day=-4即可
	 * @return
	 */
	public static String a_few_days_later(int length) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, length);
		return DateTimeUtil.dateToStr(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

	public static String dateToDay() {

		DateTime dateTime = new DateTime(new Date());
		return dateTime.toString("yyyy-MM-dd");
	}

	public static String dateToAll() {

		DateTime dateTime = new DateTime(new Date());
		return dateTime.toString(STANDARD_FORMAT);
	}

	public static String betweenAnd(int type) {

		if (type == 1) {
			DateTime dateTime = new DateTime(strToDate(a_few_days_later(-2)));
			return dateTime.toString(STANDARD_DATA) + " 00:00:00";
		}
		DateTime dateTime = new DateTime(new Date());
		return dateTime.toString(STANDARD_DATA) + " 23:59:59";

	}

	/**
	 * 判断是否是过去的日期
	 * 
	 * @param str输入的日期
	 * @return
	 * @return
	 */
	public static ServerResponse<Object> isPastDate(String str, int type) {

		boolean flag = false;
		Date nowDate = new Date();
		Date pastDate = null;
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
		// 在日期字符串非空时执行
		if (str != null && !"".equals(str)) {
			try {
				// 将字符串转为日期格式，如果此处字符串为非合法日期就会抛出异常。
				pastDate = sdf.parse(str);
				// 调用Date里面的before方法来做判断
				flag = pastDate.before(nowDate);
				if (flag) {
					// System.out.println("该日期早于今日");
					return ServerResponse.createBySuccess(!flag);
				} else {
					// System.out.println("该日期晚于今日");
					return ServerResponse.createBySuccess(!flag);
				}
			} catch (ParseException e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqikong.getMessage());
		}

	}

	
	/**
	 * 判断是否是过去的日期
	 * 
	 * @param str输入的日期
	 * @return
	 * @return
	 */
	public static ServerResponse<Object> isPastDate2(String str, int type) {
      
		//限制死的 收货时间必须是两小时后
		boolean flag = false;
		Date nowDate = new Date();
		nowDate=new Date(nowDate.getTime()+2*60*60*1000);
		
		Date pastDate = null;
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
		// 在日期字符串非空时执行
		if (str != null && !"".equals(str)) {
			try {
				// 将字符串转为日期格式，如果此处字符串为非合法日期就会抛出异常。
				pastDate = sdf.parse(str);
				// 调用Date里面的before方法来做判断
				flag = pastDate.before(nowDate);
				if (flag) {
					// System.out.println("该日期早于今日");
					return ServerResponse.createBySuccess(!flag);
				} else {
					// System.out.println("该日期晚于今日");
					return ServerResponse.createBySuccess(!flag);
				}
			} catch (ParseException e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqikong.getMessage());
		}

	}
	
	
	/**
	 * 判断是否晚与传入的日期
	 * 
	 * @param str输入的日期
	 * @return
	 * @return
	 */
	public static ServerResponse<Object> dateCompare(String str, int length) {

		boolean flag = false;

		Date pastDate = null;
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);

		// 在日期字符串非空时执行
		if (str != null && !"".equals(str)) {
			try {
				Date nowDate = sdf.parse(a_few_days_later(length));
				// 将字符串转为日期格式，如果此处字符串为非合法日期就会抛出异常。
				pastDate = sdf.parse(str);
				// 调用Date里面的before方法来做判断
				flag = pastDate.before(nowDate);
				if (flag) {
					// System.out.println("该日期早于指定日期");
					return ServerResponse.createBySuccess(flag);
				} else {
					// System.out.println("该日期晚于指定日期");
					return ServerResponse.createBySuccess(flag);
				}
			} catch (ParseException e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqikong.getMessage());
		}

	}

	public static void main(String[] args) {

		Date nowDate = new Date();
		long lon= nowDate.getTime()+2*60*60*1000;
		System.out.println(nowDate);
		System.out.println(lon);
		System.out.println(nowDate.getTime());
		nowDate=new Date(lon);
		System.out.println(nowDate);
		
		SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
		
		DateTime dateTime = new DateTime(strToDate(a_few_days_later(-2)));
		dateTime.toString("yyyy-MM-dd");
		System.out.println(dateTime.toString("yyyy-MM-dd"));
		// dateCompare(0);ServerResponse [status=0, msg=null, data=true]
		System.out.println(DateTimeUtil.betweenAnd(1));

		System.out.println(strToDate(a_few_days_later(-2)).toString());

		// Wed Jan 01 10:20:59 CST 2020
		System.out.println(new Date());
		System.out.println(DateTimeUtil.strToDate("2010-01-01 11:11:11", "yyyy-MM-dd HH:mm:ss"));

	}

}
