package com.dian.mmall.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LegalCheck {
   //判断字段输入是否合法
	public static boolean LegalCheckFrom(Map<String, Object> params){
		 Set<String> key=params.keySet();
		  for(String s11  : key){
			  
			  Object s12=params.get(s11);	
			  
			  if((s12  instanceof String) && s12!= null){
				if(((String) s12).toLowerCase().indexOf("delete")>=0 || ((String) s12).toLowerCase().indexOf("update")>=0 ) {
					log.info("非法字段--->"+s12);
					
					return false;
				}    
		      }		  
		  }
		  return true;
		
	}
	//判断是否实名
	
}
