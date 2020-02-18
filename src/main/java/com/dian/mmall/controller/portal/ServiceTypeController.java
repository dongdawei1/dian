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

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.ServiceTypeService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI+"serviceType/")
public class ServiceTypeController {

	@Autowired
	private ServiceTypeService serviceTypeService;
	//创建服务名称
    @RequestMapping(value = "create_serviceType",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_serviceType(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	  if(user.getIsAuthentication()!=2) {
       	   return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
          }
   
        
        return serviceTypeService.create_serviceType(user,params);
    
    }


  //获取服务名称
    @RequestMapping(value = "get_serviceType",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_serviceType(HttpServletRequest httpServletRequest,@RequestParam Integer releaseType ,@RequestParam(value = "serviceType", required = false ) String serviceType){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	  if(user.getIsAuthentication()!=2) {
       	   return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
          }
        return serviceTypeService.get_serviceType(releaseType,serviceType,user.getId());
    
    }

    
    //获取服务名称
    @RequestMapping(value = "get_serviceTypeUrl",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> get_serviceTypeUrl(HttpServletRequest httpServletRequest,@RequestParam Integer releaseType ,@RequestParam(value = "serviceType", required = false ) String serviceType){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	  if(user.getIsAuthentication()!=2) {
       	   return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
          }
        return serviceTypeService.get_serviceTypeUrl(releaseType,serviceType,user.getId());
    
    }
}
