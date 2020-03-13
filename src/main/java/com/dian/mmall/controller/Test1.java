package com.dian.mmall.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.javassist.expr.NewArray;
import org.json.JSONObject;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.Strin;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer.FromDecimalArguments;

public class Test1 {
	public static void main(String[] args) {
		Float float1= (float) 12;
		
		
		System.out.println(float1/4);
		System.out.println(new Double(10*0.06).intValue());
    StringBuffer serviceDetailed = new StringBuffer("北京市/市辖区/东城区");
   
    
  //  /details
    String string = "/details/wineAndTablewareDetails/10/8";
    
System.out.println(Strin.setTocken(string, 2).getMsg());  //北京市/市辖区

System.out.println(Strin.setTockenapp(string, 2).getMsg());     


    //这里是获取"/"符号的位置
    Matcher slashMatcher = Pattern.compile("/").matcher(string);
    int mIdx = 0;

    while(slashMatcher.find()) {
        System.out.println(slashMatcher.group());
        mIdx++;
        //当"/"符号第三次出现的位置
        if(mIdx == 2){
            break;
        }
    }
    
    
    
   // %北京市/市辖区/东城区%  北京市/市辖区%
    System.out.println(string.substring(0, slashMatcher.start())+"%");
 
    
    int a=1;
    
    a+=3;
    
    System.out.println(a);
    a+=2;
    System.out.println(a);
    a+=3;
    System.out.println(a);
    
    System.out.println(1220/100);
    
	}

	
	
	
	public static void test(String event, String eventDesc) {
		Integer c = 0;
		if (event.equals("ABC")) {
			String num1 = eventDesc.split("#")[0];
			System.out.println(num1);
			String num2 = eventDesc.split("#")[1];
			System.out.println(num2);
			c = Integer.parseInt(num1) + Integer.parseInt(num2);
		}
		System.out.println(c);
	}

}