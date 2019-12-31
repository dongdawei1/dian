package com.dian.mmall.controller.portal;

import java.util.Map;
import java.util.Set;

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
    
  //商户发布采购订单 预估价格
    @RequestMapping(value = "create_order_evaluation",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_order_evaluation(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	
     	if(user.getRole()!=1 && user.getRole()!=2 ) {
     		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
     	}
   
        
        return orderService.create_order_evaluation(user,params);
    
    }
    
	//商户发布采购订单
    @RequestMapping(value = "create_purchase_order",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_purchase_order(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	
     	if(user.getRole()!=1 && user.getRole()!=2 ) {
     		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
     	}
   
        
        return orderService.create_purchase_order(user,params);
    
    }
    
	//商户今天发布的采购订单
    @RequestMapping(value = "get_conduct_purchase_order",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_conduct_purchase_order(HttpServletRequest httpServletRequest){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	
     	if(user.getRole()!=1 && user.getRole()!=2 ) {
     		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
     	}
   
        
        return orderService.get_conduct_purchase_order(user);
    
    }
    
}
