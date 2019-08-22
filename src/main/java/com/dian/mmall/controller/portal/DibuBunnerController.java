package com.dian.mmall.controller.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/bunner/")
public class DibuBunnerController {
	@Autowired
	private BunnerService bunnerService;
	
	
    @RequestMapping(value = "getBunner",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getBunner(HttpServletRequest httpServletRequest,@RequestParam Integer permissionid,@RequestParam Integer bunnerType){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     
       return bunnerService.getBunnerList(user.getRole(),permissionid,bunnerType);
    
    }
}
