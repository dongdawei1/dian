package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.chuzufang.Rent;

@Mapper
public interface RentMapper {

	int countNum(int releaseType, long userId);

	int create_rent(Rent rent);

}
