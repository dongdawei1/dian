package com.dian.mmall.controller.portal;

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
import com.dian.mmall.service.release.EquipmentService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/equipment/")
public class EquipmentController {
	
	private String StringPath="/home/equipment";
    
	@Autowired
	private EquipmentService equipmentService;
	//创建电器/维修
    @RequestMapping(value = "create_equipment",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_equipment(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
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
   
        
        return equipmentService.create_equipment(user,params);
    
    }
    
	//商户获取获取自己发布的除删除外的全部信息
	
    @RequestMapping(value = "get_myEquipment_list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_myEquipment_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return equipmentService.get_myEquipment_list(user,params);
    
    }

	//电器操作列
	
    @RequestMapping(value = "operation_userequipment",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> operation_userequipment(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
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
        
        return equipmentService.operation_userequipment(user,params);
    
    }
    
//商户根据id获取详请编辑
	
    @RequestMapping(value = "get_userequipment_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_userequipment_id(HttpServletRequest httpServletRequest,@RequestParam long id){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();   
     	int role=user.getRole();
		if(role!=1  &&role!=3  ) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
       return equipmentService.get_userequipment_id(user.getId(),id);
    
    }
    
}
