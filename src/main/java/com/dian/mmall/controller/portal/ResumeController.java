package com.dian.mmall.controller.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.CheckLand;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.release.ResumeService;

@Controller
@RequestMapping("/api/resume/")
public class ResumeController {
    @Autowired
    private ResumeService resumeService;
	
    private Map<String, Object> map=new HashMap<String, Object>();
	//创建简历
    @RequestMapping(value = "create_resume",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create_resume(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return serverResponse1;
    	}
   
        
        return resumeService.create_resume(user,params);
    
    }
    
	//用户获取自己创建的简历
    @RequestMapping(value = "select_resume_by_id",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> select_resume_by_id(HttpServletRequest httpServletRequest){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
     	map.put("StringPath", "myJobWanted");
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,map);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
    	}
   
        
        return resumeService.select_resume_by_id(user.getId());
    
    }
    
    
	//创建简历
    @RequestMapping(value = "operation_resume",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> operation_resume(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.getCreateRole(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return serverResponse1;
    	}
   
        
        return resumeService.operation_resume(user,params);
    
    }
    
    
}
