package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface CityMapper {
	
	int checkeCity(int provinces_id, int city_id, int district_county_id);

}
