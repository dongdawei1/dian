package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.CheckLand;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.service.release.ReleaseCommodityService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/getPublishings/")
public class GetPublishingsController {
   String recruitWorkers="/home/recruitWorkers";
	
	
	@Autowired
	private  GetPublishingsService getPublishingsService;
	@Autowired
	private ReleaseWelfareService releaseWelfareService;
	
//查询商品接口
    
    @RequestMapping(value = "getGoods",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getGoods(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	if(StringUtils.isEmpty(loginToken)){
 		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
 	}
 	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
 	User user = JsonUtil.string2Obj(userJsonStr,User.class);
 	
 	if(user == null){
 		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
 	}	
 	
    	return getPublishingsService.getMenuList(user, params);
    	
    }
    
    //职位获取电话或者邮箱   
    @RequestMapping(value = "getContact",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getContact(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
   
    	ServerResponse<Object> serverResponse=CheckLand.checke_see(httpServletRequest,recruitWorkers);
     	//检查登陆和是否有权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
        User user=	(User) serverResponse.getData();
    	return releaseWelfareService.getContact(user, params);
    }
}
