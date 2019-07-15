package com.dian.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by geely
 */
public class DateTimeUtil {

    //joda-time

    //str->Date
    //Date->str
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";



    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

     /**
          * @param date
          * @param day 想要获取的日期与传入日期的差值 比如想要获取传入日期前四天的日期 day=-4即可
          * @return
          */
    public static ServerResponse<Object> a_few_days_later(int length){
        if(length <=0 || length >100 ){
            return ServerResponse.createByErrorMessage(ResponseMessage.youxiaoqibuhefa.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, length);
                return ServerResponse.createBySuccessMessage(DateTimeUtil.dateToStr(calendar.getTime(),"yyyy-MM-dd HH:mm:ss"));
    }
    
    public static String dateToDay(){

        DateTime dateTime = new DateTime(new Date());
        return dateTime.toString("yyyy-MM-dd");
    }
    public static String dateToAll(){

        DateTime dateTime = new DateTime(new Date());
        return dateTime.toString(STANDARD_FORMAT);
    }
    
    public static void main(String[] args) {
    	a_few_days_later(20);
    	System.out.println(DateTimeUtil.dateToDay());
        System.out.println(DateTimeUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.strToDate("2010-01-01 11:11:11","yyyy-MM-dd HH:mm:ss"));

    }


}
