package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.commodity.GrainAndOil;

@Mapper
public interface GrainAndOilMapper {

	void caeateGrainAndOil(GrainAndOil grainAndOil);
   //检查发帖总数
	int checkReleaseCount(long userId);
    
	//分页查询全部  发布  code=5
	
	List<GrainAndOil>  getGrainAndOilList(int currentPage, int pageSize);
	//查询总数
	int getGrainAndOilPageno();
	
	
}
