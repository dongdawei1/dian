package com.dian.guolvAndlanjie;



import org.apache.commons.lang.StringUtils;

import com.dian.mmall.common.Const;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 过滤器早于拦截器执行
 * Created by geely  过滤器  用户的操作增加   session时长 
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      

    
        //请求向下执行
        filterChain.doFilter(servletRequest,servletResponse);
        
        //响应时前执行    System.out.println("执行完controller在执行" );
    }


    @Override
    public void destroy() {

    }
}
