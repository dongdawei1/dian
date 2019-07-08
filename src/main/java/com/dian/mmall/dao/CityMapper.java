package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.dian.mmall.pojo.City;
@Mapper
public interface CityMapper {
	
	String checkeCity(int provincesId, int cityId, int districtCountyId);
	String checkeCityTuo(int provincesId, int cityId);
	String checkeCityOne(int provincesId);

}
