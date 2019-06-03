package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/realName/")
public class RealNameController {

	

    @Autowired
    private RealNameService  realNameService;
    //用户实名
    @RequestMapping(value = "newRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> newRealName(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest){
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	if(user == null){
    		return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    	}
    	ServerResponse<String> serverResponse= realNameService.newRealName(user.getId(),params);
        return serverResponse;
    }
	
	
    
    
    
    
}
