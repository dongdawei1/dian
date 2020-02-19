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

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.MenuAndRenovationAndPestControlService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI+"menuAndRenovationAndPestControl/")
public class MenuAndRenovationAndPestControlController {
  
	@Autowired
	private MenuAndRenovationAndPestControlService menuAndRenovationAndPestControlService;
	
	//创建灭虫
    @RequestMapping(value = "create_menuAndRenovationAndPestControl",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_menuAndRenovationAndPestControl(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	User user =	(User) httpServletRequest.getAttribute("user");
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return serverResponse1;
    	}
   
        
        return menuAndRenovationAndPestControlService.create_menuAndRenovationAndPestControl(user,params);
    
    }
    
    
	//商户获取获取自己发布的除删除外的全部信息
	
    @RequestMapping(value = "get_usermrp_list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_usermrp_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
    	User user =	(User) httpServletRequest.getAttribute("user");
    	//检查权限
     	params.put("StringPath", "menuAndRenovationAndPestControl");
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
        
        return menuAndRenovationAndPestControlService.get_usermrp_list(user,params);
    
    }


	//灭虫操作列
	
    @RequestMapping(value = "operation_usermrp",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> operation_usermrp(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	
    	User user =	(User) httpServletRequest.getAttribute("user");
    	//检查权限
     	params.put("StringPath", "menuAndRenovationAndPestControl");
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
        
        return menuAndRenovationAndPestControlService.operation_usermrp(user,params);
    
    }

	//商户根据id获取详细编辑
	
    @RequestMapping(value = "get_usermrp_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_usermrp_id(HttpServletRequest httpServletRequest,@RequestParam long id){
    	User user =	(User) httpServletRequest.getAttribute("user");   
     	Map<String, Object> params= new HashMap<String, Object>();
     	params.put("StringPath", "menuAndRenovationAndPestControl");
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
       return menuAndRenovationAndPestControlService.get_usermrp_id(user,id);
    
    }

    
    //公开展示灭虫装修等列表
    
    @RequestMapping(value = "getmrpList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getmrpList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	
     	//检查权限
     	params.put("StringPath", "menuAndRenovationAndPestControl");
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see((User) httpServletRequest.getAttribute("user"),params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
 	
    	return menuAndRenovationAndPestControlService.getmrpList(params);
    	
    }
    
//根据类型获取全部标题
	
    @RequestMapping(value = "getReleaseTitleList",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getReleaseTitleList(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    
     	params.put("StringPath", "menuAndRenovationAndPestControl");
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see((User) httpServletRequest.getAttribute("user"),params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
     	
       return menuAndRenovationAndPestControlService.getReleaseTitleList(params);
    
    }
    
    
    
	//公开获取id
	
    @RequestMapping(value = "getMrpDetails",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getMrpDetails(HttpServletRequest httpServletRequest,@RequestParam long id){
  
     	Map<String, Object> params= new HashMap<String, Object>();
     	params.put("StringPath", "menuAndRenovationAndPestControl");
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see((User) httpServletRequest.getAttribute("user"),params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
       return menuAndRenovationAndPestControlService.getMrpDetails(id);
    
    }
    
}
