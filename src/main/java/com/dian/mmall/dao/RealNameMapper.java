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


	long admin_select_addOrderNo(String userName, String contact, String statTimeString, String endTimeString, String detailed, Integer isReceipt);


	List<RealName> admin_select_addOrder(int pageLength, int pageSize, String userName, String contact, String statTimeString,
			String endTimeString, String detailed, Integer isReceipt);


	int admin_update_addOrder(long id, String addReceiptTime, String qianyueDetailed, String qianyueTime, Integer isReceipt, String commitAddReceiptName);


	long admin_select_signingOrderNo(String userName, String contact, String statTimeString, String endTimeString,
			String detailed, String addressDetailed, Integer isReceipt);


	List<RealName> admin_select_signingOrder(int pageLength, int pageSize, String userName, String contact,
			String statTimeString, String endTimeString, String detailed, String addressDetailed, Integer isReceipt);


	RealName admin_select_signingOrderById(long id);


	int admin_set_addOrder(RealName realName);


	RealName admin_select_realNameByContact(String contact);

}
