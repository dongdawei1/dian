package com.dian.guolvAndlanjie;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.google.common.collect.Maps;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import com.dian.mmall.util.IpUtils;
/**
 * 进入controller 之前进行拦截
 * 拦截所有请求吧请求 /img/的映射到绝对路径
 */

public class ImgAuthorityInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(ImgAuthorityInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    
	    
	    Enumeration<String> headerNames=request.getHeaderNames();
	    for(Enumeration<String> e=headerNames;e.hasMoreElements();){
	     String thisName=e.nextElement().toString();
	    String thisValue=request.getHeader(thisName);
	    System.out.println("header的key:"+thisName+"--------------header的value:"+thisValue);
	    }
		return true;
	}



	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
	}

}
