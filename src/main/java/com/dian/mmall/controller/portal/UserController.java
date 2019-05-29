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

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.controller.common.interfaceo.AuthorityInterceptor;
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
@RequestMapping("/api/user/")
public class UserController {
	
	private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService iUserService;
  
    @Autowired
    private TUserRoleService tUserRoleService;
    
   
    //用户登录
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> login(@RequestBody Map<String, Object> params, HttpSession session,
    		HttpServletResponse httpServletResponse){
 
    	 String captcha  =params.get("captcha").toString().trim() ; 
    	 String getPicCode=RedisShardedPoolUtil.get(params.get("uuid").toString().trim());
    	if( ! captcha.equalsIgnoreCase(getPicCode)) {	
    		  return ServerResponse.createByErrorMessage(ResponseMessage.YanZhengMaCuoWu.getMessage());
    	}
    	ServerResponse<String> response = iUserService.login(params);
    	System.out.println("ddd  "+response.getMsg());
    	System.out.println("ddd  "+response.getStatus()+"   ddd  "+ResponseCode.SUCCESS.getCode());
    	if(response.getStatus()==ResponseCode.SUCCESS.getCode()){
          CookieUtil.writeLoginToken(httpServletResponse,session.getId());
               //把用户session当做key存到数据库中，时长是 30分钟
        	   RedisShardedPoolUtil.setEx(session.getId(), response.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        	   return response;
    	}else {
    		return response;
        }
        
    }
    
    
    //用户注册
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> create(@RequestBody Map<String,Object> params,
    		HttpSession session, HttpServletResponse httpServletResponse){
         
   	 String uuid  =params.get("uuid").toString().trim() ; 
	 String captcha  =params.get("captcha").toString().trim() ; 
	 String getPicCode=RedisShardedPoolUtil.get(uuid);
	if( ! captcha.equalsIgnoreCase(getPicCode)) {		
		  return ServerResponse.createByErrorMessage(ResponseMessage.YanZhengMaCuoWu.getMessage());
	}
    	
	ServerResponse<String> serverResponse= iUserService.createUser(params);
    	
 		if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
 			CookieUtil.writeLoginToken(httpServletResponse,session.getId());
 			//把用户session当做key存到数据库中，时长是 30分钟
 			RedisShardedPoolUtil.setEx(session.getId(),serverResponse.getMsg(),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
 			return serverResponse;
 		}
	     	return serverResponse;
    }
    
  //获取用户信息
    
    @RequestMapping(value = "get_user_info",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getUserInfo(HttpServletRequest httpServletRequest){
   
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	if(user != null){
    		return ServerResponse.createBySuccess(JsonUtil.obj2String(user));
    	}
    	return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
    }
    
    
    
    
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


    //获取验证码    @RequestBody Map<String,Object> params
	@ResponseBody
	@RequestMapping(value = "captcha", method = RequestMethod.GET)
	public ServerResponse<String>  captcha(HttpServletResponse response , @RequestParam String uuid) {
		String base64PicCodeImage;
		String getPicCode;
		System.out.println("UserController.captcha()"+uuid);
		
		try {
			base64PicCodeImage = CheckPicCode.encodeBase64ImgCode();
			getPicCode=CheckPicCode.getPicCode();
			
			System.out.println(getPicCode);

			if(base64PicCodeImage != null  && getPicCode!=null){
				
				if( RedisShardedPoolUtil.exists(uuid)) {
			     //根据ip把验证码放到数据库
					RedisShardedPoolUtil.del(uuid);
					RedisShardedPoolUtil.setEx(uuid,getPicCode,8*10);
				}else {
					RedisShardedPoolUtil.setEx(uuid,getPicCode,8*10);
				}
				
				
	    		return ServerResponse.createBySuccessMessage(base64PicCodeImage);
	    	}
		} catch (IOException e) {
			return ServerResponse.createByErrorMessage("验证码生成错误");
		}
		
		
		 return ServerResponse.createByErrorMessage("验证码生成错误");

	}
    
  
	
   //修改用户基本信息，如果修改了密码和用户名就强制用户重新登陆，如果只修改手机号不重新登陆
    @RequestMapping(value = "update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update_information(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("编辑失败，登陆过期");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);

        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        
        return iUserService.update_information(currentUser.getId(),params);
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
