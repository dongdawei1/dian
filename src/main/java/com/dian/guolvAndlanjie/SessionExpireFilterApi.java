package com.dian.guolvAndlanjie;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SessionExpireFilterApi implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
	        //请求向下执行
	        filterChain.doFilter(servletRequest,servletResponse);
	        //响应时前执行    System.out.println("执行完controller在执行" );
		
	}

}
