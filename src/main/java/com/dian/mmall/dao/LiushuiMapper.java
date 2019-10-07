package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.Liushui;

@Mapper
public interface LiushuiMapper {

	int create_liushui(Liushui liushui);

}
