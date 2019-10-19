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

	long get_myWholesaleCommodityNo(long userId, int type, int commodityType, String dateString, int releaseType,
			String serviceType, int welfareStatus);

	List<WholesaleCommodity> get_myWholesaleCommodity_list(int pageLength, int pageSize, long userId, int type,
			int commodityType, String dateString, int releaseType, String serviceType, int welfareStatus);


	long adminWholesaleCommodity_no(long userId, Integer releaseType);

	List<WholesaleCommodity> adminWholesaleCommodity(int pageLength, int pageSize, long userId, Integer releaseType);

	long adminWholesaleCommodity_no_realName(String contact, String companyName, String detailed, Integer releaseType);

	List<WholesaleCommodity> adminWholesaleCommodity_realName(int pageLength, int pageSize, String contact, String companyName,
			String detailed, Integer releaseType);

}
