package com.dian.mmall.service;

import java.util.Map;
import java.util.SortedMap;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface OrderService {

	ServerResponse<String> create_wholesaleCommodity_order(long userId, Map<String, Object> params);
	//进行中的订单
	ServerResponse<Object> get_conduct_order(long wholesaleCommodityId,int orderStatus);
	ServerResponse<String> create_purchase_order(User user, Map<String, Object> params);
	ServerResponse<String> create_order_evaluation(User user, Map<String, Object> params);
	ServerResponse<Object> get_conduct_purchase_order(User user);
	ServerResponse<String> operation_purchase_order(User user, Map<String, Object> params);
	void timerOrderStatus();
	void timerSelsetPayOrder();
	ServerResponse<String> native_pay_order(User user, String spbillCreateIp, long id);
	ServerResponse<String> callback(SortedMap<String, String> sortedMap);
	ServerResponse<String> get_pay_order_all(long userId);
	ServerResponse<String> get_pay_order_byOrderId(long userId, long orderId, String appid);
	ServerResponse<Object> peceiptGetPendingOrders(long userId,Map<String, Object> params);
	ServerResponse<Object> myPurchaseOrder(long userId, Map<String, Object> params);
	ServerResponse<Object> mySaleOrder(long userId, Map<String, Object> params);
	ServerResponse<Object> native_pay_order_app(User user, String spbillCreateIp, long id);
	
	ServerResponse<Object> createjiedan(long id, Map<String, Object> params);

	ServerResponse<Object> getdaibaojia(User user, int releaseType, int orderStatus);
	
	
}
