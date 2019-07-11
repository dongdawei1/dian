package com.dian.mmall.dao.chaxuncishu;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.chaxuncishu.NumberOfQueries;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface NumberOfQueriesMapper {
	//@Select("select * from numberOfQueries where userId=#{userId} and queriesType=#{queriesType}")
	NumberOfQueries getNumberOfQueries(long userId, int queriesType);
    //创建查询次数
	int setNumberOfQueries(NumberOfQueries numberOfQueries);
	//更新查询次数
	int updateNumberOfQueries(NumberOfQueries numberOfQueries);

}
