package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderExampleTimerMapper {

//	List<Long> getall(int a, String dateString);

	void upall(int a, String dateString);

	void delall(int a, String dateString, String termOfValidity);

	int adminupall(long id, int tablenameid, String username, String updateTime);

	String getcoutn(int tablenameid, long tableId, long userId);

	void timer_guanggaoguoqi(String dateString);
	void timer_guanggaoshengxiao(String dateString);
	
}
