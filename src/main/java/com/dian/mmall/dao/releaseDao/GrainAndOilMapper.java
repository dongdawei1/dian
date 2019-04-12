package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.commodity.GrainAndOil;

@Mapper
public interface GrainAndOilMapper {

	void caeateGrainAndOil(GrainAndOil grainAndOil);

}
