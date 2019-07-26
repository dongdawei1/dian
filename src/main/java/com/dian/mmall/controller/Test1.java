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
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.MD5Util;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer.FromDecimalArguments;


public class Test1 {
public static void main(String[] args) {
	//Integer  gender=Integer.valueOf("rr");
	//System.out.println(gender);
	
	Map<String, String> sMap=new HashMap<String, String>();
	sMap.put("aa", "dd");
	sMap.remove("dd");
	String string=sMap.get("dde");
//	try {
//		String string=sMap.get("dde");
//		System.out.println("Test1.main()");
//	} catch (Exception e) {
//		e.getMessage();
//	}
	
	System.out.println(sMap.toString());
	sMap.remove("aa");
	System.out.println(sMap.toString());
	
	List<String>  aList=new ArrayList<String>();
	aList.add("11");
	aList.add("12");
	aList.add("13");
	aList.add("14");
	List<String>  aList1=new ArrayList<String>();
	aList1.add("11");
	aList1.add("121");
	aList1.add("131");
	aList1.add("141");
	
	for(int a=0; a<aList.size();a++) {
		
		for (int i = 0; i < aList1.size(); i++) {
			if (aList.get(a).equals(aList1.get(i))) {
				System.out.println("Test1.main()"+aList1.get(i));
				
			}
		}
	}
	
	
}
}