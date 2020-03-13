package com.dian.mmall.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

public class Strin {
	public static ServerResponse<String> setTocken(String dString,int length) {
		
		Matcher slashMatcher = Pattern.compile("/").matcher(dString);
	    int mIdx = 0;

	    while(slashMatcher.find()) {
	        mIdx++;
	        //当"/"符号第三次出现的位置
	        if(mIdx == length){
	            break;
	        }
	    }
	    if(mIdx<length) {
	    	return ServerResponse.createByError();
	    }
	    
	   // %北京市/市辖区/东城区%
	   // dString.substring(0, slashMatcher.start())+"%");
		return ServerResponse.createBySuccessMessage( dString.substring(0, slashMatcher.start()));
	}
public static ServerResponse<String> setTockenapp(String dString,int length) {
		
		Matcher slashMatcher = Pattern.compile("/").matcher(dString);
	    int mIdx = 0;

	    while(slashMatcher.find()) {
	        mIdx++;
	        //当"/"符号第三次出现的位置
	        if(mIdx == length){
	            break;
	        }
	    }
	    if(mIdx<length) {
	    	return ServerResponse.createByError();
	    }
	    String subdString=dString.substring(slashMatcher.start()+1);
	
	    
	   // %北京市/市辖区/东城区%
	   // dString.substring(0, slashMatcher.start())+"%");
		return ServerResponse.createBySuccessMessage( "/pages/"+setTocken(subdString,1).getMsg()+"/"+subdString);
	}
	
}
