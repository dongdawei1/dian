package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/rent/")
public class RentController {
	
	@Autowired 
	private RentService rentService;
	//创建出租
    @RequestMapping(value = "create_rent",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_rent(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	
     	String releaseType=params.get("releaseType").toString().trim();
        if(releaseType==null || releaseType.equals("")) {
        	return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
        }		
        int createType=Integer.valueOf(releaseType);
     	if(createType==14) {
     		params.put("StringPath", "/home/lease");
     	}else if(createType==15) {
     		params.put("StringPath", "/home/rentalBooth");
     	}else {
     		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
     	}
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return serverResponse1;
    	}
        return rentService.create_rent(user,params);
    
    }
}
