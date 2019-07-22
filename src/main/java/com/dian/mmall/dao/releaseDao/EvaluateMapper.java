package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.pingjia.Evaluate;

@Mapper
public interface EvaluateMapper {
   //计算发布过的总数   5条以下
	int countNum(int releaseType, long userId);

	int create_evaluate(Evaluate evaluate);

}
