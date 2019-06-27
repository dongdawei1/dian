package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisPoolUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/toExamine/")
public class ToExamineController {
	//管理员接口审批
	
	@Autowired  
	private RealNameService realNameService;
	
    @RequestMapping(value = "getRealNameAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getRealNameAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){

 	ServerResponse<Object> serverResponse=checke_role(httpServletRequest);
 	//检查是否有管理员权限
 	if(serverResponse.getStatus()!=0 ) {
 		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
 	}
    	return realNameService.getRealNameAll(params);
    	
    }
	//实名审核 
    @RequestMapping(value = "examineRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> examineRealName(@RequestBody Map<String,Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
		
     	ServerResponse<Object> serverResponse=checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
     User user=	(User) serverResponse.getData();
     String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	 serverResponse= realNameService.examineRealName(user,params, loginToken);
	System.out.println(serverResponse.getMsg());
	if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
		User shenheUser=(User) serverResponse.getData();
		int result=RedisPoolUtil.checkeKey(shenheUser);
		if(result==0) {
			return ServerResponse.createBySuccessMessage("成功用户登录");
		}else if(result==1) {
			return ServerResponse.createBySuccessMessage("成功用户未登录");
		}
			return ServerResponse.createBySuccessMessage("更新登录信息失败，请联系用户重新登陆");
	}
	 return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	
    }
    
    //获取所有发布招聘
    
    @Autowired  
    private ReleaseWelfareService releaseWelfareService;
    
    @RequestMapping(value = "getReleaseWelfareAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getReleaseWelfareAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	ServerResponse<Object> serverResponse=checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return releaseWelfareService.getReleaseWelfareAll(params);
    	
    }
    
  //审核招聘
    @RequestMapping(value = "examineReleaseWelfare",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> examineReleaseWelfare(@RequestBody Map<String,Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
		
     	ServerResponse<Object> serverResponse=checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
     User user=	(User) serverResponse.getData();
 	 return releaseWelfareService.examineReleaseWelfare(user,params);

    }
    
    
    
    private ServerResponse<Object> checke_role(HttpServletRequest httpServletRequest){
    	//TODO  写死的代码 如果不是这个用户名将查不到
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
     	if(StringUtils.isEmpty(loginToken)){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}
     	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
     	User user = JsonUtil.string2Obj(userJsonStr,User.class);
     	if(user == null){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}	
     	if(user.getRole()!=1 || !user.getUsername().equals("z222222221") ) {
     		return ServerResponse.createByErrorMessage("没有权限");
     	}
     	return ServerResponse.createBySuccess(user);
    }
    
}
