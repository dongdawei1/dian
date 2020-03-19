package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.fabu.Fabu;

@Mapper
public interface FabuMapper {
 
	int getFabuCount(int releaseType, long userId);

	int createfabu(Fabu fabu);

}
