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
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/foodAndGrain/")
public class FoodAndGrainController {
private String StringPath="/home/foodAndGrain";
    
	@Autowired
	private FoodAndGrainService  foodAndGrainService;
	//创建电器/维修
    @RequestMapping(value = "create_foodAndGrain",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_foodAndGrain(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
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
   
        
        return foodAndGrainService.create_foodAndGrain(user,params);
    
    }
    
	//商户获取获取自己发布的除删除外的全部信息
	
    @RequestMapping(value = "get_myFoodAndGrain_list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_FoodAndGrain_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return foodAndGrainService.get_myFoodAndGrain_list(user,params);
    
    }

	//操作列
	
    @RequestMapping(value = "operation_userFoodAndGrain",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> operation_userFoodAndGrain(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return foodAndGrainService.operation_userFoodAndGrain(user,params);
    
    }
    
//商户根据id获取详请编辑
	
    @RequestMapping(value = "get_userFoodAndGrain_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_userFoodAndGrain_id(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	int role=user.getRole();
		if(role!=1  &&role!=4 ) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
       return foodAndGrainService.get_userFoodAndGrain_id(user.getId(),id);
    
    }
   //没有用
    
    @RequestMapping(value = "getFoodAndGrainList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getFoodAndGrainList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData(); 
     
     	
     		params.put("StringPath", StringPath);
     
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
 	
    	return foodAndGrainService.getFoodAndGrainList(params);
    	
    } 
    
    
//根据类型获取全部标题
	
    @RequestMapping(value = "getFoodAndGrainTitleList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getFoodAndGrainTitleList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
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
     	
       return foodAndGrainService.getFoodAndGrainTitleList(params);
    
    }
    
    
    //公开展示灭虫装修等列表
    
    @RequestMapping(value = "getFoodAndGrainPublicList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getFoodAndGrainPublicList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
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
 	
    	return foodAndGrainService.getFoodAndGrainPublicList(params);
    	
    }
	//公开获取id
	
    @RequestMapping(value = "getFoodAndGrainDetails",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getFoodAndGrainDetails(HttpServletRequest httpServletRequest,@RequestParam long id){
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
       return foodAndGrainService.getFoodAndGrainDetails(id);
    
    }
}
