package com.dian.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import com.dian.mmall.common.Const;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by geely
 */
@Slf4j
public class CookieUtil {

	private final static String COOKIE_DOMAIN = ".localhost"; // ".dian.com";//
	// 种到服务端的cookie名字
	private final static String COOKIE_NAME = "dian_token";

//    1. 获得客户机信息
//    getRequestURL方法返回客户端发出请求时的完整URL。
//    getRequestURI方法返回请求行中的资源名部分。
//    getQueryString 方法返回请求行中的参数部分。
//    getRemoteAddr方法返回发出请求的客户机的IP地址 
//    getRemoteHost方法返回发出请求的客户机的完整主机名
//    getRemotePort方法返回客户机所使用的网络端口号
//    getLocalAddr方法返回WEB服务器的IP地址。
//    getLocalName方法返回WEB服务器的主机名 
//    getMethod得到客户机请求方式
// 2.获得客户机请求头 
//    getHeader(string name)方法 
//    getHeaders(String name)方法 
//    getHeaderNames方法 
//
// 3. 获得客户机请求参数(客户端提交的数据)
//    getParameter(name)方法
//    getParameterValues（String name）方法
//    getParameterNames方法 
//    getParameterMap方法

//读取cookie
	public static String readLoginToken(HttpServletRequest request) {
		if (request.getHeader("appid").equals(Const.APPAPPIDP)) {
			Cookie[] cks = request.getCookies();
			if (cks != null) {
				for (Cookie ck : cks) {
					log.info("read cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
					if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
						log.info("return cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
						return ck.getValue();
					}
				}
			}

		} else if (request.getHeader("appid").equals(Const.APPAPPIDA)) {
			return request.getHeader(COOKIE_NAME);
		}
		return null;
	}

	// X:domain=".happymmall.com"
	// a:A.happymmall.com cookie:domain=A.happymmall.com;path="/"
	// b:B.happymmall.com cookie:domain=B.happymmall.com;path="/"
	// c:A.happymmall.com/test/cc cookie:domain=A.happymmall.com;path="/test/cc"
	// d:A.happymmall.com/test/dd cookie:domain=A.happymmall.com;path="/test/dd"
	// e:A.happymmall.com/test cookie:domain=A.happymmall.com;path="/test"
//写入cookie
	public static void writeLoginToken(HttpServletResponse response, String token) {
		Cookie ck = new Cookie(COOKIE_NAME, token);
		ck.setDomain(COOKIE_DOMAIN);
		ck.setPath("/");// 代表设置在根目录
		ck.setHttpOnly(true);
		// 单位是秒。
		// 如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
		ck.setMaxAge(60 * 60 * 24 * 365);// 如果是-1，代表永久
		// log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
		response.addCookie(ck);
	}

//退出时把cookie剩余时长设为0
	public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cks = request.getCookies();
		if (cks != null) {
			for (Cookie ck : cks) {
				if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
					ck.setDomain(COOKIE_DOMAIN);
					ck.setPath("/");
					ck.setMaxAge(0);// 设置成0，代表删除此cookie。
					log.info("del cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
					response.addCookie(ck);
					return;
				}
			}
		}
	}

}
