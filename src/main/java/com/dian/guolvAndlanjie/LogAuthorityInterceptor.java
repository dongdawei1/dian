package com.dian.guolvAndlanjie;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.sql.visitor.functions.Length;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.IpUtils;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * 进入controller 之前进行拦截 拦截除登陆外的虽有请求
 */
@Slf4j
public class LogAuthorityInterceptor implements HandlerInterceptor {
	private int length = Const.APPAPI.length() - 1;

	// log.warn("类型转换",e);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		log.warn("类型转换", "ee");
//		log.debug("debug", "所属");

//		例如接口请求地址:http://192.168.1.6:8080/api/category/query?pageSize=5&pageNum=1
//			 
		System.out.println(request.getRequestURL().toString()); // http://192.168.1.6:8080/api/category/query
		// 用这个
		System.out.println(request.getRequestURI()); // /api/category/query
		System.out.println(request.getContextPath());// /api
		System.out.println(request.getQueryString()); // pageSize=5&pageNum=1
		System.out.println(request.getServletPath());// /category/query
		System.out.println(request.getRemoteAddr());// 192.168.1.6
		System.out.println(request.getServerPort());// 8080
		System.out.println("Au33333333" + request.getHeader("referer"));
		// getHeader(string name)方法：根据header参数名称获取值 ；
		// getParameter(name)方法:根据请求参数的名称获取对应的值；
		// getMethod()方法：HTTP请求的的方法名，默认是GET，也可以指定PUT或POST；
		// /api/v1/vp/
//			 http://localhost:8080/api/v1/vp/realName/getRealNameById
//				 /api/v1/vp/realName/getRealNameById
//
//				 id=29
//				 /api/v1/vp/realName/getRealNameById
//				 127.0.0.1
//				 8080
//				 Au33333333http://localhost:8090/details/foodAndGrainDetails/17/4

		// 和前端约定前端传入的请求方式 p =pc端 a=app端
		String appId = request.getHeader("appId");


		String loginToken = CookieUtil.readLoginToken(request);
		if (StringUtils.isNotEmpty(loginToken)) {
			String userJsonStr = RedisShardedPoolUtil.get(loginToken);
			User user = JsonUtil.string2Obj(userJsonStr, User.class);
			// 登录判断
			if (user != null) {
				// 更新时长
				RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				//把用户放入请求中
				request.setAttribute("user", user);
				return true;
			}
		}
		PrintWriter out = response.getWriter();
		response.reset();// geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this
		response.setCharacterEncoding("utf-8");// geelynote 这里要设置编码，否则会乱码
		response.setContentType("application/json;charset=utf-8");// geelynote 这里要设置返回值类型，因为全部是json接口。
		out.print(JsonUtil
				.obj2String(ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage())));
		out.flush(); // geelynote 这里要关闭流
		out.close();
		return false;// 这里虽然已经输出，但是还会走到controller，所以要return false


//      //获取所有的头部参数
//        Enumeration<String> headerNames=request.getHeaderNames();
//        for(Enumeration<String> e=headerNames;e.hasMoreElements();){
//         String thisName=e.nextElement().toString();
//        String thisValue=request.getHeader(thisName);
//        System.out.println("header的key:"+thisName+"--------------header的value:"+thisValue);
//        }
//        System.out.println("ip 3333"+IpUtils.getIpAddr(request));


	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// System.out.println("postHandle");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// System.out.println("afterCompletion");
	}

}
