package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.ReleaseCommodityService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/commodity/")
public class ReleaseCommodityController {
	@Autowired
	private  ReleaseCommodityService  releaseCommodityService;
	
	  
	 //发布 商品信息
 @RequestMapping(value = "release",method = RequestMethod.POST)
 @ResponseBody
 public ServerResponse<String> commodity(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest){
		
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	if(StringUtils.isEmpty(loginToken)){
 		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
 	}
 	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
 	User user = JsonUtil.string2Obj(userJsonStr,User.class);
 	
 	if(user == null){
 		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
 	}	
 	if(user.getIsAuthentication()!=2) {
 		return ServerResponse.createByErrorMessage("发布需求需要先进行实名");
 	}
 	return releaseCommodityService.commodity(user,loginToken,params);
 		
	}

 

}
