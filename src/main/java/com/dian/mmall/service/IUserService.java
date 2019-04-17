package com.dian.mmall.service;

import java.util.List;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

/**
 * Created by geely
 */
public interface IUserService {
   //登陆
    ServerResponse<User> login(String username, String password);
    //注册完以后登录
    ServerResponse<User> login(String username);
    
    
    
    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(long l);

    ServerResponse checkAdminRole(User user);
//测试mybaits
	List<Map<String, Object>> getall();
//注册检查用户名是否可用
	ServerResponse<User> checkUsername(String username);
//注册
	long createUser(User user);
	//根据用户名查id
	long selectUserId(User user);
}
