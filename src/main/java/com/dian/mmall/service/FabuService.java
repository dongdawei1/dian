package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface FabuService {

	ServerResponse<String> createfabu(User user, Map<String, Object> params);


	ServerResponse<Object> getfabuad(Map<String, Object> params);


	ServerResponse<Object> getmyfabu(User user, Map<String, Object> params);


	ServerResponse<String> upfabu(User user, Map<String, Object> params);


	ServerResponse<Object> getmyfabubyid(long userId, long id);

}
