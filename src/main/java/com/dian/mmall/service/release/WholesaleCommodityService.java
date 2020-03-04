package com.dian.mmall.service.release;

import java.util.List;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.user.User;

import ch.qos.logback.core.status.Status;

public interface WholesaleCommodityService {

	ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params);


	ServerResponse<Object> get_wholesaleCommodity_serviceType(long userId, Map<String, Object> params);


	ServerResponse<Object> get_myWholesaleCommodity_list(long userId, Map<String, Object> params);


	ServerResponse<Object> adminWholesaleCommodity(Map<String, Object> params);


	ServerResponse<String> operation_userWholesaleCommodity(long userId, Map<String, Object> params);


	ServerResponse<Object> get_userWholesaleCommodity_id(long userId, long id);


	ServerResponse<Object> wholesaleCommodity_serviceType( Map<String, Object> params);


	ServerResponse<Object> getWholesaleCommodityPublicList(Map<String, Object> params);


	ServerResponse<Object> getWholesaleCommodityPublicId(long id);
	
	ServerResponse<Object> getWholesaleCommodityBoolean(WholesaleCommodity wholesaleCommodity);
	
	ServerResponse<Object> getWholesalecommodity(String selectedOptions,int releaseType);

	 List<Integer> getCommodityJiage(WholesaleCommodity wholesaleCommodity);


	
}
