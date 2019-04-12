package com.dian.mmall.service.impl.releaseimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.releaseDao.GrainAndOilMapper;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.service.release.GrainAndOilService;
@Service("grainAndOilService")
public class GrainAndOilServiceImpl implements GrainAndOilService {
	@Autowired  
	  private	GrainAndOilMapper grainAndOilMapper;

	@Override
	public void caeateGrainAndOil(GrainAndOil grainAndOil) {
		grainAndOilMapper.caeateGrainAndOil(grainAndOil) ;
		
	}
}
