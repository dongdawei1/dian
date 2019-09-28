package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface RealNameService {

	ServerResponse<String> newRealName(User user,String loginToken, Map<String, Object> params);

	ServerResponse<Object> getRealName(User user);

	ServerResponse<String> updateRealName(User user, String loginToken, Map<String, Object> params);

	ServerResponse<Object> getRealNameAll(Map<String, Object> params);

	ServerResponse<Object> examineRealName(User user, Map<String, Object> params,String loginToken);

	ServerResponse<Object> getRealNameById(long id);

	ServerResponse<Object> getUserRealName(User user);


	ServerResponse<String> addOrder(User user, Map<String, Object> params);

	ServerResponse<Object> admin_select_addOrder(Map<String, Object> params);

	ServerResponse<String> admin_update_addOrder(String commitAddReceiptName, Map<String, Object> params);

}
