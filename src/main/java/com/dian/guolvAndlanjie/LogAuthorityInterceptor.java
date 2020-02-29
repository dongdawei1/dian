package com.dian.guolvAndlanjie;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.IpUtils;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 进入controller 之前进行拦截 拦截除登陆外的虽有请求
 */
@Slf4j
public class LogAuthorityInterceptor implements HandlerInterceptor {
	// log.warn("类型转换",e);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 和前端约定前端传入的请求方式 p =pc端 a=app端

		String loginToken = CookieUtil.readLoginToken(request);
		if (StringUtils.isNotEmpty(loginToken)) {
			String userJsonStr = RedisShardedPoolUtil.get(loginToken);
			User user = JsonUtil.string2Obj(userJsonStr, User.class);
			// 登录判断
			if (user != null) {
				String appId = request.getHeader("appid");
				if (appId.equals(Const.APPAPPIDP)) {
					// 更新时长 pc 的所有get请求增加 市场，app增加时间去crAndUp拦截器中
					RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				} else {
					int week = DateTimeUtil.getWeek();
					if (week == 1 || week == 3) {
						RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIMEAPP);
					}
				}
				// 把用户放入请求中
				request.setAttribute("user", user);
				return true;
			}
		}
		PrintWriter out = response.getWriter();
		response.reset();// geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this
		response.setCharacterEncoding("utf-8");// geelynote 这里要设置编码，否则会乱码
		response.setContentType("application/json;charset=utf-8");// geelynote 这里要设置返回值类型，因为全部是json接口。
		// 直接返回ServerResponse=== [status=1, msg=用户未登录,无法获取当前用户的信息, data=null]
		out.print(JsonUtil
				.obj2String(ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage())));
		out.flush(); // geelynote 这里要关闭流
		out.close();

		log.warn("no log {} ", IpUtils.getIpAddr(request));
		return false;// 这里虽然已经输出，但是还会走到controller，所以要return false
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
