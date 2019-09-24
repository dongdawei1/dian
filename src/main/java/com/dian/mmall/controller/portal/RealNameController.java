package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/realName/")
public class RealNameController {



    @Autowired
    private RealNameService  realNameService;
    //用户实名
    @RequestMapping(value = "newRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> newRealName(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
     	User user = (User) serverResponse1.getData();
     	
    	ServerResponse<String> serverResponse= realNameService.newRealName(user,loginToken,params);
    	
    	if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
    		   
    		    CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
             	RedisShardedPoolUtil.del(loginToken);
             	CookieUtil.writeLoginToken(httpServletResponse,session.getId());
    			//把用户session当做key存到数据库中，时长是 30分钟
    			RedisShardedPoolUtil.setEx(session.getId(),serverResponse.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME); 
    			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
    	}
    	
    	return serverResponse;
    }
	
    
    //重新用户实名
    @RequestMapping(value = "updateRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateRealName(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
     	User user = (User) serverResponse1.getData();
    	ServerResponse<String> serverResponse= realNameService.updateRealName(user,loginToken,params);
    	
    	if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
    		   
    		    CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
             	RedisShardedPoolUtil.del(loginToken);
             	CookieUtil.writeLoginToken(httpServletResponse,session.getId());
    			//把用户session当做key存到数据库中，时长是 30分钟
    			RedisShardedPoolUtil.setEx(session.getId(),serverResponse.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME); 
    			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
    	} 	
    	return serverResponse;
    }
	
    //获取实名信息
    @RequestMapping(value = "getRealName",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getRealName(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
     	User user = (User) serverResponse1.getData();
    	return realNameService.getRealName(user);
    	
    }
    
    //获取实名信息
    @RequestMapping(value = "getUserRealName",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getUserRealName(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
     	User user = (User) serverResponse1.getData();
    	return realNameService.getUserRealName(user);
    	
    }
    
    //获取实名信息
    @RequestMapping(value = "getRealNameById",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getRealNameById(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
  
     	
    	return realNameService.getRealNameById(id);
    	
    }
    
    //重新用户实名
    @RequestMapping(value = "addOrder",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addOrder(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	//检查登陆
    	ServerResponse<Object> serverResponse1=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
     	User user = (User) serverResponse1.getData();
     	if(user.getIsAuthentication()!=2 || (user.getRole()!=4 && user.getRole()!=1)) {
     		return ServerResponse.createByErrorMessage(ResponseMessage.zhiyoushiming.getMessage());
     	}
    	
    	return realNameService.addOrder(user,params);
    }
    
    
    
}
