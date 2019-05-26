package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.PermissionService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;



@Controller
@RequestMapping("/permission")
public class DispatcherController {


	@Autowired
	private PermissionService permissionService;
	
	
	@ResponseBody
	@RequestMapping(value="/loadData" )
	public ServerResponse<Object> loadData(HttpServletRequest httpServletRequest) {
			 
		List<Permission> permissions = new ArrayList<Permission>();
		
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	if(user != null){
 
    		// 获取用户权限信息
    		List<Permission> ps = permissionService.queryPermissionsByUser(user);	
    		return ServerResponse.createBySuccess(ps);
    	}
    	return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

	}
	

}
