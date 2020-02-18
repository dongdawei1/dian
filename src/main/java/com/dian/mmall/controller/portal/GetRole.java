package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI+"getRole/")
public class GetRole {
 
	
    //权限检查 ，展示按钮判断
    @RequestMapping(value = "getIsRole",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getIsRole(HttpServletRequest httpServletRequest,@RequestParam String StringPath){
    
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	
     	serverResponse=CheckLand.checke_isButten(user,StringPath);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	
     	 return ServerResponse.createBySuccess(serverResponse.getData());
    }
}
