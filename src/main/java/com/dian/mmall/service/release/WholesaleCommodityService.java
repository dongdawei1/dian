package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface WholesaleCommodityService {

	ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params);


	ServerResponse<Object> get_wholesaleCommodity_serviceType(long userId, Map<String, Object> params);


	ServerResponse<Object> get_myWholesaleCommodity_list(long userId, Map<String, Object> params);


	ServerResponse<Object> adminWholesaleCommodity(Map<String, Object> params);

}