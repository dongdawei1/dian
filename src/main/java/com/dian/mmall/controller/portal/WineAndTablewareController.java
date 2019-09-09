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
import com.dian.mmall.service.release.FoodAndGrainService;
import com.dian.mmall.service.release.WineAndTablewareService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/wineAndTableware/")
public class WineAndTablewareController {
	
	private String StringPath="/home/wineAndTableware";
	@Autowired
	private WineAndTablewareService  wineAndTablewareService;
	//酒水
    @RequestMapping(value = "create_wineAndTableware",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_wineAndTableware(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
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
   
        
        return wineAndTablewareService.create_wineAndTableware(user,params);
    
    }
    
	//商户获取获取自己发布的除删除外的全部信息
	
    @RequestMapping(value = "get_myWineAndTableware_list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_myWineAndTableware_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return wineAndTablewareService.get_myWineAndTableware_list(user,params);
    
    }

	//操作列
	
    @RequestMapping(value = "operation_userWineAndTableware",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> operation_userWineAndTableware(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return wineAndTablewareService.operation_userWineAndTableware(user,params);
    
    }
    
//商户根据id获取详请编辑
	
    @RequestMapping(value = "get_userWineAndTableware_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_userWineAndTableware_id(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	int role=user.getRole();
		if(role!=1  &&role!=5 ) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
       return wineAndTablewareService.get_userWineAndTableware_id(user.getId(),id);
    
    }
  
    
    
//根据类型获取全部标题
	
    @RequestMapping(value = "getWineAndTablewareTitleList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getWineAndTablewareTitleList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	params.put("StringPath", StringPath);
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
     	
       return wineAndTablewareService.getWineAndTablewareTitleList(params);
    
    }
    
    
    //公开列表
    
    @RequestMapping(value = "getWineAndTablewarePublicList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getWineAndTablewarePublicList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
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
 	
    	return wineAndTablewareService.getWineAndTablewarePublicList(params);
    	
    }
	//公开获取id
	
    @RequestMapping(value = "getWineAndTablewareDetails",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getWineAndTablewareDetails(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	Map<String, Object> params= new HashMap<String, Object>();
     	params.put("StringPath", StringPath);
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
       return wineAndTablewareService.getWineAndTablewareDetails(id);
    
    }
}
