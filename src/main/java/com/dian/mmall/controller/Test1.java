package com.dian.mmall.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.javassist.expr.NewArray;

import com.dian.mmall.common.ServerResponse;

public class Test1 {
public static void main(String[] args) {
	  Map<String, Object> map=new HashMap<String, Object>();
	
	  
	  map.put("1a1","1b1");
	  map.put("2a1","2b1");
	  map.put("3a1","3b1");
	
	  Set<String> key=map.keySet();
	  for(String s11  : key){
	  System.out.println( map.get(s11));
	  }
	  if(1==1 && 1!=1){
          System.out.println("yes");
      }
       
	  //System.out.println( map.getClass().isArray());
}
}
