package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.Liushui;

@Mapper
public interface LiushuiMapper {

	int create_liushui(Liushui liushui);

	List<Liushui>  getjinxingliushui(long dingdanId, int liushuiStatus);

	void tongbu_jiedong(Liushui liushui);

}
