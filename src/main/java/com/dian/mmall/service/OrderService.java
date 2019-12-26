package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface OrderService {

	ServerResponse<String> create_wholesaleCommodity_order(long userId, Map<String, Object> params);
	//进行中的订单
	ServerResponse<Object> get_conduct_order(long wholesaleCommodityId,int orderStatus);
	ServerResponse<String> create_purchase_order(User user, Map<String, Object> params);
	ServerResponse<String> create_order_evaluation(User user, Map<String, Object> params);
	

}
