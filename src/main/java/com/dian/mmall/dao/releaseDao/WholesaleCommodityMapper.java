package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.WholesaleCommodity;

@Mapper
public interface WholesaleCommodityMapper {

	int create_wholesaleCommodity(WholesaleCommodity wholesaleCommodity);

	int countNum(int releaseType, long userId);

}
