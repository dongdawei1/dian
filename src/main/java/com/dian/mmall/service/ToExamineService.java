package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ToExamineService {

	ServerResponse<String> examineAll(User user, Map<String, Object> params);


	ServerResponse<Object> getAddressDetailed(Map<String, Object> params);


	ServerResponse<String> createAddressDetailed(String username, Map<String, Object> params);


	ServerResponse<Object> getAccurateressDetailed(Map<String, Object> params);


	ServerResponse<String> admin_create_orderUser(String examineName, Map<String, Object> params);


	ServerResponse<Object> getUserCreate(User user);


	ServerResponse<String> adminupall(String username, Map<String, Object> params);

}
