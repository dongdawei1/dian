package com.dian.mmall.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.javassist.expr.NewArray;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.util.LegalCheck;

public class Test1 {
public static void main(String[] args) {
	 System.out.println( (float)9/4);
	 
	 Map<String,Object > map=new HashMap<String, Object>();
	// map.put("wwwww", "delete");
	 map.put("wwwww1", "1wwwwDElete1");
	// map.put("wwwww2", "oeeeer");
	// map.put("wwwww3", "update");
	// map.put("wwwww4", "=");
	 ;
	 System.out.println(LegalCheck.legalCheckFrom(map).getStatus());
}
}
