package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.user.OrderUser;

@Mapper
public interface OrderUserMapper {

	int admin_create_orderUser(OrderUser orderUser);

	OrderUser getOrderUserById(long userId);

	int updateOrderUser(OrderUser orderUser);

}
