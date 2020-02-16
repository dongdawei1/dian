package com.dian.mmall.controller.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.dian.mmall.common.Const;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

public class SessionExpireFilterApi implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
	     
	    	HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
	        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
	        if(StringUtils.isNotEmpty(loginToken)){
	            //判断logintoken是否为空或者""；
	            //如果不为空的话，符合条件，继续拿user信息

	            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
	            User user = JsonUtil.string2Obj(userJsonStr,User.class);
	            if(user != null){
	                //如果user不为空，则重置session的时间，即调用expire命令
	                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
	            }
	        }
	        //请求向下执行
	        filterChain.doFilter(servletRequest,servletResponse);
	        //响应时前执行    System.out.println("执行完controller在执行" );
		
	}

}
