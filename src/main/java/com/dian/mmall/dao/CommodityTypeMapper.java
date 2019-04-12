package com.dian.mmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface CommodityTypeMapper {
	@Select("select count(*) from commodity_type  where permissionid=#{permissionid} and commoditytype=#{commoditytype}")
	int getcommodityType(Integer permissionid,String commoditytype);
}
