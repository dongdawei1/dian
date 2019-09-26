package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.user.RealName;

@Mapper
public interface RealNameMapper {

	int newRealName(RealName realName);
    
	
	RealName getRealName(long userId);


	int isNewRealName(long userId);


	int updateRealName(RealName realName);

    //全部未实名
	long getRealNamePageno(String userName,String  contact);


	List<RealName> getRealNameAll(int pageLength, int pageSize, String userName,String  contact);

    //实名
	int examineRealName(RealName realName);


	RealName getRealNameById(long id);


	String getDetailed(long userId);


	RealName getUserRealName(long id);


	int addOrder(long userId,String addReceiptTime);


	long admin_select_addOrderNo(String userName, String contact, String statTimeString, String endTimeString, String detailed);


	List<RealName> admin_select_addOrder(int pageLength, int pageSize, String userName, String contact, String statTimeString,
			String endTimeString, String detailed);

}
