package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.user.OrderUser;

@Mapper
public interface OrderMapper {

	int get_conduct_order(long wholesaleCommodityId, int orderStatus);
	
	int create_order(Order order);

}
