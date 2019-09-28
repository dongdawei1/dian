package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.qianyue.Peixun;

@Mapper
public interface PeixunMapper {


	List<String> getAddressDetailed(String detailed, String addressDetailed);

	int createAddressDetailed(Peixun peixun);

}
