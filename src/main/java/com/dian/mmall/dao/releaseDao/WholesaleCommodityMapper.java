package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;

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


	int examineWholesaleCommodity(WholesaleCommodity wholesaleCommodity);

	int checkout_count(long id, long userId);

	int operation_userWholesaleCommodity(long id, int type, String updateTime);

	WholesaleCommodity get_userWholesaleCommodity_id(long userId, long id);

	int update_wholesaleCommodity(WholesaleCommodity wholesaleCommodity_create);

	List<String> wholesaleCommodity_serviceType(int releaseType, String selectedOptions, String serviceType,
			String companyName, String createTime);

	long getWholesaleCommodityPublicListNo(int releaseType, String selectedOptions, String serviceType,
			String companyName, String createTime);

	List<WholesaleCommodity> getWholesaleCommodityPublicList(int pageLength, int pageSize, int releaseType,
			String selectedOptions, String serviceType, String companyName, String createTime);

	WholesaleCommodity getWholesaleCommodityPublicId(long id);

	int getWholesaleCommodityBoolean(WholesaleCommodity wholesaleCommodity);

	List<WholesaleCommodity> getWholesalecommodity(String selectedOptions,int releaseType);

	List<Integer> getCommodityJiage(WholesaleCommodity wholesaleCommodity);

}
