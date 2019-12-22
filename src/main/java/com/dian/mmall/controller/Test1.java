package com.dian.mmall.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer.FromDecimalArguments;


public class Test1 {
public static void main(String[] args) {
	  String tel = "13";
      System.out.println(tel.matches("1[358][29]\\d{8}"));
	
      System.out.println(tel.matches("\\d{3,18}") + "  svvvvs");
	String test33="1dd";
	  System.out.println(test33.matches("[^234]+(.*)") + "  svvvvs");

	  
	//判断是不是以什么开头  ，中间包含什么字符，以什么结尾的
	   test33="1this is test";
	  System.out.println(test33.matches(".*(iq).*[t]$") + "  svvvvs"); //true
	  
	//利用 Apache 的 Commons Lang 库：
  //import org.apache.commons.lang.StringUtils;
	int count = StringUtils.countMatches("232323323", "23");
	//或用 Spring Framework 提供的接口：
System.out.println("Test1.main()"+count);
	//int occurrence = StringUtils.countOccurrencesOf("<主串>", "<子串>");
	
	  String string="dde2e3e4ewe5ewe5e5eewe";
	  
	  
	  System.out.println("Test1.main()"+string.indexOf("5e"));
	  System.out.println("Test1.main()"+string.substring(11,11+"5e".length()));
	  
	  
	  string=string.replace("//D*", "ddd");
	  
	
	  System.out.println("Test1.main()"+string);
	  System.out.println("Test1.main()"+string.length());
	  List _first=new ArrayList();
      _first.add("jim");
      _first.add("tom");
      _first.add("jack");
      //集合二
      
      System.out.println(_first.size());
      List _second=new ArrayList();
      _second.add("jack");
      _second.add("happy");
      _second.add("sun");
      _second.add("good");
      Collection exists=new ArrayList(_second);
      Collection notexists=new ArrayList(_second);
      exists.removeAll(_first); //把 exists 中和_first 中重复的删除
      System.out.println("_second中不存在于_set中的："+exists);
      notexists.removeAll(exists); //把notexists 
          //中和exists一样的删除 ,notexists==exists ,剩余的部分就是 和_first相同的
      System.out.println("_second中存在于_set中的："+notexists); 
      Collection _first1=new ArrayList(_first);
      _first1.removeAll(notexists);   
      System.out.println("_first1中不存在于_set中的："+_first1);
      
//      _second中不存在于_set中的：[happy, sun, good]
//    		  _second中存在于_set中的：[jack]
//	
	
	int a=1;
	System.out.println(2+a++);

	System.out.println(a);
	
	
	
	
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
	String str ="2019-09-17";
//    //获得第一个点的位置
//    int index=str.indexOf("/");
//    System.out.println(index);
//    //根据第一个点的位置 获得第二个点的位置
//    index=str.indexOf("/", index+1);
//    //根据第二个点的位置，截取 字符串。得到结果 result
//    String result=str.substring(0,index);
//    //输出结果19-09-27
//    System.out.println(result);
	
	System.out.println(2>2);
	String aString1="123456";
	System.out.println(new StringBuffer(aString1).reverse());
	
	String aString="abuuba";
	
	StringBuffer sb = new StringBuffer(aString);
	sb.reverse();// 将Str中的字符串倒置
  
	if(sb.toString().equals(aString)) {
		System.out.println("Test1.main()"+aString);
	}else {
		System.out.println("Test1.main()");
	}
	
	
	//test("ABC","1#A13");
//	System.out.println(str.substring(1,11));
//	System.out.println(str.substring(13,23).trim());
}
public static void test(String event, String eventDesc){
    Integer c = 0;
    if(event.equals("ABC")){
        String num1 = eventDesc.split("#")[0];
        System.out.println(num1);
        String num2 = eventDesc.split("#")[1];
        System.out.println(num2);
        c = Integer.parseInt(num1) + Integer.parseInt(num2);
    }
    System.out.println(c);
}

}