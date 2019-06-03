package com.dian.mmall.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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
String isbusiness_string="false";
		
	
if(!isbusiness_string.equals("true") && !isbusiness_string.equals("false") ) {
	System.out.println("Test1.main()");
	//return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
	}
	boolean isbusiness=Boolean.valueOf(isbusiness_string);
	
	System.out.println(isbusiness);
	
}
}
