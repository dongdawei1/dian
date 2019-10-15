package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.WholesaleCommodity;

@Mapper
public interface WholesaleCommodityMapper {

	int create_wholesaleCommodity(WholesaleCommodity wholesaleCommodity);

	int countNum(int releaseType, long userId);

	List<String> get_wholesaleCommodity_serviceType(long userId, int type, int commodityType, String dateString,
			int releaseType, String serviceType, int welfareStatus);

}
