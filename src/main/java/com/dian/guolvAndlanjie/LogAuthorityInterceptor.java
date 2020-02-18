package com.dian.guolvAndlanjie;

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

import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.IpUtils;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
/**
 * 进入controller 之前进行拦截
 * 拦截除登陆外的虽有请求
 */
public class LogAuthorityInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(ImgAuthorityInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//请求controller中的方法名
		
        System.out.println("AuthorityInterceptor.preHandle()"+"2222");
		
        
       // System.out.println("AuthorityInterceptor.preHandle()"+request.getAttribute("a"));
        
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        
      //获取所有的头部参数
        Enumeration<String> headerNames=request.getHeaderNames();
        for(Enumeration<String> e=headerNames;e.hasMoreElements();){
         String thisName=e.nextElement().toString();
        String thisValue=request.getHeader(thisName);
        System.out.println("header的key:"+thisName+"--------------header的value:"+thisValue);
        }
        System.out.println("ip 3333"+IpUtils.getIpAddr(request));
        
//        header的key:host--------------header的value:localhost:8080
//        header的key:content-length--------------header的value:146
//        header的key:origin--------------header的value:http://localhost:8090
//        header的key:appid--------------header的value:p
//        header的key:user-agent--------------header的value:Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1
        //header的key:user-agent--------------header的value:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.5702.400 QQBrowser/10.2.1893.400
        //        header的key:content-type--------------header的value:application/json;charset=UTF-8
//        header的key:accept--------------header的value:application/json, text/plain, */*
//        header的key:referer--------------header的value:http://localhost:8090/home/wholesaleMarket
//        header的key:accept-encoding--------------header的value:gzip, deflate, br
//        header的key:accept-language--------------header的value:zh-CN,zh;q=0.8
        
		//解析HandlerMethod

		String methodName = handlerMethod.getMethod().getName();
		String className = handlerMethod.getBean().getClass().getSimpleName();
	
		
		StringBuffer requestParamBuffer = new StringBuffer();
		Map paramMap = request.getParameterMap();
		Iterator it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String mapKey = (String) entry.getKey();
			String mapValue = "";
			
			//request的这个参数map的value返回的是一个String[]
			Object obj = entry.getValue();
			if (obj instanceof String[]){
				String[] strs = (String[])obj;
				mapValue = Arrays.toString(strs);
			}
			requestParamBuffer.append(mapKey).append("=").append(mapValue);
		}

		//对于拦截器中拦截manage下的login.do的处理,对于登录不拦截，直接放行
//		if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
//			logger.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);//如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
//			return true;
//		}

		logger.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuffer);

//		User user = (User)request.getSession().getAttribute(Const.CURRENT_USER);

		User user = null;
		String loginToken  = CookieUtil.readLoginToken(request);
		if(StringUtils.isNotEmpty(loginToken)){
			String userJsonStr = RedisShardedPoolUtil.get(loginToken);
			user = JsonUtil.string2Obj(userJsonStr,User.class);
		}

//		if(user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
//			//返回false.即不会调用到controller里的方法
//			response.reset();//geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this response
//			response.setCharacterEncoding("UTF-8");//geelynote 这里要设置编码，否则会乱码
//			response.setContentType("application/json;charset=UTF-8");//geelynote 这里要设置返回值类型，因为全部是json接口。
//
//			PrintWriter out = response.getWriter();
//
//
//			//上传由于富文本的控件要求，要特殊处理返回值，这里面区分是否登录以及是否有权限
//			if(user == null){
//				if(StringUtils.equals(className,"ProductManageController") && (StringUtils.equals(methodName,"richtextImgUpload") )){
//					Map resultMap = Maps.newHashMap();
//					resultMap.put("success",false);
//					resultMap.put("msg","请登录管理员");
//					out.print(JsonUtil.obj2String(resultMap));
//				}else{
//					out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
//				}
//
//			}else{
//				if(StringUtils.equals(className,"ProductManageController") && (StringUtils.equals(methodName,"richtextImgUpload") )){
//					Map resultMap = Maps.newHashMap();
//					resultMap.put("success",false);
//					resultMap.put("msg","无权限操作");
//					out.print(JsonUtil.obj2String(resultMap));
//
//				}else{
//					out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
//				}
//			}
//
//			out.flush();//geelynote 这里要关闭流
//			out.close();
//
//			return false;//这里虽然已经输出，但是还会走到controller，所以要return false
//		}
		return true;
	}



	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		//System.out.println("postHandle");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		//System.out.println("afterCompletion");
	}

}

