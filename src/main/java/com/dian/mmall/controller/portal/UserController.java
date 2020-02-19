package com.dian.mmall.controller.portal;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.guolvAndlanjie.ImgAuthorityInterceptor;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Role;
import com.dian.mmall.pojo.TUserRole;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.yanzhengma.CheckPicCode;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.TUserRoleService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping(Const.PCAPI+"user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    //退出
    @RequestMapping(value = "logout",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        try {
        	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        	CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        	RedisShardedPoolUtil.del(loginToken);
        	return ServerResponse.createBySuccess();
			
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		}
    }

	//修改用户基本信息
   //修改用户基本信息，如果修改了密码和用户名就强制用户重新登陆，如果只修改手机号不重新登陆
    @RequestMapping(value = "update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_information(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session, @RequestBody Map<String, Object> params){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage(ResponseMessage.DengLuGuoQi.getMessage());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage(ResponseMessage.DengLuGuoQi.getMessage());
        }
        
        ServerResponse<String> serverResponse= iUserService.update_information(currentUser.getId(),params);
         if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
        	 if(serverResponse.getMsg().equals(ResponseMessage.BianJiChengGongChongXinDengLu.getMessage())) {
        		//如果修改了密码就要重新登陆 
             	CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
             	RedisShardedPoolUtil.del(loginToken);
             	return serverResponse;
        	 }
        	 
            CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
          	RedisShardedPoolUtil.del(loginToken);
          	
          	CookieUtil.writeLoginToken(httpServletResponse,session.getId());
 			//把用户session当做key存到数据库中，时长是 30分钟
 			RedisShardedPoolUtil.setEx(session.getId(),serverResponse.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME); 
        	 return ServerResponse.createBySuccessMessage(ResponseMessage.BianJiChengGong.getMessage());
         }else {
        	 return serverResponse;
         }
    
    
    }
	
	
	
	
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

  

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }


    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }



    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String passwordOld,String passwordNew){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }



    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);

        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }






























}
