package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.user.OrderUser;

@Mapper
public interface OrderMapper {

	int get_conduct_order(long wholesaleCommodityId, int orderStatus);
	
	int create_order(Order order);

	long getId(Order order);


	List<Order> get_shut_orders(long purchaseUserId, String between, String and, Object orderStatus_liStrings,int type);

	Order getOrderById(long id, long purchaseUserId);


	int operation_purchase_order(Order order);

	List<Order> timerOrderStatus();

}
