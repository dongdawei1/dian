package com.dian.mmall.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.ibatis.javassist.expr.NewArray;
import org.json.JSONObject;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.MD5Util;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer.FromDecimalArguments;


public class Test1 {
public static void main(String[] args) {
	//Integer  gender=Integer.valueOf("rr");
	//System.out.println(gender);
	
//	List<Map<String, String>> sMap=new ArrayList();
//	
//	Map<String, String> sMap1=new HashMap<String,  String>();
//	sMap1.put("name", "这是类型滴滴1");
//	sMap1.put("value", "这是内容1");
//   
//	Map<String, String> sMap2=new HashMap<String,  String>();
//	sMap2.put("name", "这是类型滴滴2");
//	sMap2.put("value", "这是内容2");
//	
//	sMap.add(sMap1);
//	sMap.add(sMap2);
	//String string=sMap.get("dde");
//	try {
//		String string=sMap.get("dde");
//		System.out.println("Test1.main()");
//	} catch (Exception e) {
//		e.getMessage();
//	}
	
//	System.out.println(JsonUtil.obj2String(sMap));
//	sMap.remove("aa");
//	System.out.println(sMap.toString());
//	
//	List<String>  aList=new ArrayList<String>();
//	aList.add("11");
//	aList.add("12");
//	aList.add("13");
//	aList.add("14");
//	List<String>  aList1=new ArrayList<String>();
//	aList1.add("11");
//	aList1.add("121");
//	aList1.add("131");
//	aList1.add("141");
//	
//	for(int a=0; a<aList.size();a++) {
//		
//		for (int i = 0; i < aList1.size(); i++) {
//			if (aList.get(a).equals(aList1.get(i))) {
//				System.out.println("Test1.main()"+aList1.get(i));
//				
//			}
//		}
//	}
//	  String str="a1b dddd张 c";  //待判断的字符串
//	    char[] ch = str.toCharArray();
//        for (char c : ch) {
//            if (c < 0x4E00 || c > 0x9FBF) {
//              System.out.println("Test1.main()"+c);
//            }
//        }
	String str ="[2019-09-24, 2019-09-27]";
//    //获得第一个点的位置
//    int index=str.indexOf("/");
//    System.out.println(index);
//    //根据第一个点的位置 获得第二个点的位置
//    index=str.indexOf("/", index+1);
//    //根据第二个点的位置，截取 字符串。得到结果 result
//    String result=str.substring(0,index);
//    //输出结果19-09-27
//    System.out.println(result);
	
	System.out.println(str.length());
	System.out.println(str.substring(1,11));
	System.out.println(str.substring(13,23).trim());
}
}