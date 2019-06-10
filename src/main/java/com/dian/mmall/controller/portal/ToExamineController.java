package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/toExamine/")
public class ToExamineController {
	//管理员接口审批
	
	@Autowired  
	private RealNameService realNameService;
	
    @RequestMapping(value = "getRealNameAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getRealNameAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	if(StringUtils.isEmpty(loginToken)){
 		return ServerResponse.createByErrorMessage("用户登陆已过期");
 	}
 	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
 	User user = JsonUtil.string2Obj(userJsonStr,User.class);
 	if(user == null){
 		return ServerResponse.createByErrorMessage("用户登陆已过期");
 	}	
 	//TODO写死的代码 如果不是这个用户名将查不到
 	if(user.getRole()!=1 || !user.getUsername().equals("z222222221") ) {
 		return ServerResponse.createByErrorMessage("没有权限");
 	}
 	
    	return realNameService.getRealNameAll(params);
    	
    }
	//examine
    
    
    
    @RequestMapping(value = "examineRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> examineRealName(@RequestBody Map<String,Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	if(StringUtils.isEmpty(loginToken)){
 		return ServerResponse.createByErrorMessage("用户登陆已过期");
 	}
 	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
 	User user = JsonUtil.string2Obj(userJsonStr,User.class);
 	if(user == null){
 		return ServerResponse.createByErrorMessage("用户登陆已过期");
 	}	
 	//TODO写死的代码 如果不是这个用户名将查不到
 	if(user.getRole()!=1 || !user.getUsername().equals("z222222221") ) {
 		return ServerResponse.createByErrorMessage("没有权限");
 	}
 	
 	ServerResponse<Object> serverResponse= realNameService.examineRealName(user,params);
	System.out.println(serverResponse.getMsg());
	if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
		   
		User shenheUser=(User) serverResponse.getData();
		System.out.println( shenheUser.toString());
		
		
//		    CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
//         	RedisShardedPoolUtil.del(loginToken);
//         	CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//			//把用户session当做key存到数据库中，时长是 30分钟
//			RedisShardedPoolUtil.setEx(session.getId(),serverResponse.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME); 
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
	}
	
	 return ServerResponse.createByErrorMessage(serverResponse.getMsg());
 	
    	
    }
    
}
