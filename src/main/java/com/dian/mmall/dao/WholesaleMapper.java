package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.Wholesale;

@Mapper
public interface WholesaleMapper {

	List<String> getwholesale(String detailed, String companyName);

}
