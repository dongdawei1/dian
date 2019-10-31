package com.dian.mmall.controller.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/wholesaleCommodity/")
public class WholesaleCommodityController {
	private String StringPath="/home/wholesaleMarket";
	@Autowired
	private WholesaleCommodityService  wholesaleCommodityService;
	//批发创建
    @RequestMapping(value = "create_wholesaleCommodity",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_wholesaleCommodity(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
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
   
        
        return wholesaleCommodityService.create_wholesaleCommodity(user,params);
    
    }
	//获取商品名
    @RequestMapping(value = "get_wholesaleCommodity_serviceType",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_wholesaleCommodity_serviceType(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	params.put("StringPath", StringPath);
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
   
        
        return wholesaleCommodityService.get_wholesaleCommodity_serviceType(user.getId(),params);
    
    }
    
	//获取商品名
    @RequestMapping(value = "wholesaleCommodity_serviceType",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> wholesaleCommodity_serviceType(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	params.put("StringPath", StringPath);
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
   
        
        return wholesaleCommodityService.wholesaleCommodity_serviceType(params);
    
    }
    
    
    @RequestMapping(value = "get_myWholesaleCommodity_list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_myWholesaleCommodity_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
        
        return wholesaleCommodityService.get_myWholesaleCommodity_list(user.getId(),params);
    
    }

  //批发操作列
    @RequestMapping(value = "operation_userWholesaleCommodity",method = RequestMethod.POST)
    @ResponseBody
   public ServerResponse<String> operation_userWholesaleCommodity(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
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
        return wholesaleCommodityService.operation_userWholesaleCommodity(user.getId(),params);
   }
    
//商户根据id获取详请编辑
	
    @RequestMapping(value = "get_userWholesaleCommodity_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_userWholesaleCommodity_id(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	//检查权限
     	Map<String, Object> params=new HashMap<String, Object>();
     	params.put("StringPath", StringPath);
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return  ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
       return wholesaleCommodityService.get_userWholesaleCommodity_id(user.getId(),id);
    
    }


   //公开展示灭虫装修等列表
    
    @RequestMapping(value = "getWholesaleCommodityPublicList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getWholesaleCommodityPublicList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData(); 
     	//检查权限
     	params.put("StringPath", StringPath);
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
 	
    	return wholesaleCommodityService.getWholesaleCommodityPublicList(params);
    	
    }

}
