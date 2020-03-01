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
    ServerResponse<Object> login(Map<String, Object> params);
    
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
	ServerResponse<String> checkUsername(String username);
   //注册
	ServerResponse<Object> createUser(Map<String,Object> params);
	//根据用户名查id
	long selectUserId(User user);
	//编辑用户基本信息
	ServerResponse<String> update_information(long id, Map<String, Object> params);

	ServerResponse<Object> selectUserById(long userId);
}
