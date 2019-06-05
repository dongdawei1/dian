package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.user.RealName;

@Mapper
public interface RealNameMapper {

	int newRealName(RealName realName);
    
	
	RealName getRealName(long userId);


	int isNewRealName(long userId);

}
