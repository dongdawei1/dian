package com.dian.mmall.controller.portal;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.CheckPicCode;
import com.dian.mmall.pojo.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by geely
 */
@Controller
@RequestMapping("/user/")
public class UserController {


    @Autowired
    private IUserService iUserService;

    
   
    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(@RequestParam String username, @RequestParam String password,@RequestParam String yangzhengma, HttpSession session,
    		HttpServletResponse httpServletResponse,String uip){
        
    	 String getPicCode=RedisShardedPoolUtil.get(uip);
    	 
    	 String usernamrString  = username.trim() ;
    	 String passwordString  = password.trim() ;
    	 String yangzhengmaString  = yangzhengma.trim() ;
    	 
    	if( ! yangzhengmaString.equalsIgnoreCase(getPicCode)) {
    		
    		  return ServerResponse.createByErrorMessage("验证码错误或失效");
    	}
    	
    	
    	
    	ServerResponse<User> response = iUserService.login(usernamrString,passwordString);
        if(response.isSuccess()){

//            session.setAttribute(Const.CURRENT_USER,response.getData());
        	
          CookieUtil.writeLoginToken(httpServletResponse,session.getId());
        	   System.out.println(session.getId()+JsonUtil.obj2String(response.getData())+"   "+Const.RedisCacheExtime.REDIS_SESSION_EXTIME+"  "+httpServletResponse);
         //把用户session当做key存到数据库中，时长是 30分钟
        	   RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }
        return response;
    }
  //获取用户信息
    
    @RequestMapping(value = "get_user_info",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
    	
    	System.out.print(httpServletRequest.toString());
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	if(user != null){
    		return ServerResponse.createBySuccess(user);
    	}
    	return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }
    //退出
    @RequestMapping(value = "logout",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisShardedPoolUtil.del(loginToken);

//        session.removeAttribute(Const.CURRENT_USER);

        return ServerResponse.createBySuccess();
    }


    //获取验证码
	@ResponseBody
	@RequestMapping(value = "/captcha", method = RequestMethod.POST)
	public ServerResponse<String>  captcha(HttpServletResponse response , @RequestParam String uip) {
		String base64PicCodeImage;
		String getPicCode;
		try {
			base64PicCodeImage = CheckPicCode.encodeBase64ImgCode();
			getPicCode=CheckPicCode.getPicCode();
			
			System.out.println(getPicCode);
			
			
			System.out.println(base64PicCodeImage);
			
			if(base64PicCodeImage != null  && getPicCode!=null){
				
				if( RedisShardedPoolUtil.exists(uip)) {
			  //根据ip把验证码放到数据库
					RedisShardedPoolUtil.del(uip);
					RedisShardedPoolUtil.setEx(uip,getPicCode,8*10);
				}else {
					RedisShardedPoolUtil.setEx(uip,getPicCode,8*10);
				}
				
				
	    		return ServerResponse.createBySuccessMessage(base64PicCodeImage);
	    	}
		} catch (IOException e) {
			return ServerResponse.createByErrorMessage("验证码生成错误");
		}
		
		
		 return ServerResponse.createByErrorMessage("验证码生成错误");

	}
    
    
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }


    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }




    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
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


    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest,User user){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);

        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            RedisShardedPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
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
