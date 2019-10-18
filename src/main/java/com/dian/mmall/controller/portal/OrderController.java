package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.service.release.WineAndTablewareService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/order/")
public class OrderController {
	private String StringPath="/home/order";
	@Autowired
	private OrderService orderService;
	//零售商创建批发订单
    @RequestMapping(value = "create_wholesaleCommodity_order",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_wholesaleCommodity_order(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	params.put("StringPath", StringPath);
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return serverResponse1;
    	}
   
        
        return orderService.create_wholesaleCommodity_order(user.getId(),params);
    
    }
}
