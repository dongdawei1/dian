package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.CheckLand;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.zhiwei.Position;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/releaseWelfare/")
public class ReleaseWelfareController {
	 @Autowired
	    private ReleaseWelfareService releaseWelfareService;
	//创建职位信息
	    @RequestMapping(value = "create_position",method = RequestMethod.POST)
	    @ResponseBody
	    public ServerResponse<String> create_position(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
	    	
	    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest,"/home/recruitWorkers");
	     	//检查登陆和是否有权限
	     	if(serverResponse.getStatus()!=0 ) {
	     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	     	}
	        User user=	(User) serverResponse.getData();
	        
	        return releaseWelfareService.create_position(user,params);
	    
	    }
		
	
	//获取发布的除删除外的全部信息
	
	    @RequestMapping(value = "get_position_list",method = RequestMethod.POST)
	    @ResponseBody
	    public ServerResponse<Object> get_position_list(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
	    	
	    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest,"/home/recruitWorkers");
	     	//检查登陆和是否有权限
	     	if(serverResponse.getStatus()!=0 ) {
	     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	     	}
	        User user=	(User) serverResponse.getData();
	        
	        return releaseWelfareService.get_position_list(user,params);
	    
	    }
	

  //获取职位类型
    
    @RequestMapping(value = "get_position",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Object> getPosition(HttpServletRequest httpServletRequest){
   
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	if(user != null){
    		int role=user.getRole();
    		List<String> list=new ArrayList<String>();
    		
    		Position[] positions=	Position.values();
    		
    			if(role==1 || role==2) {
    			for(int i=0;i<positions.length;i++) {
    			if(positions[i].getRoleId()==2) {
    				list.add(positions[i].getPositionType());
    			}}
    		}else if(role==1 || role==5) {
    			for(int i=0;i<positions.length;i++) {
    			if(positions[i].getRoleId()==5) {
    				list.add(positions[i].getPositionType());
    			}}
    		}
    		return ServerResponse.createBySuccess(list);
    	}
    	return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    }
	
    
    
    
	
}
