package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

	int get_conduct_order(long userId, int releaseType, String serviceType,int orderStatus);

}
