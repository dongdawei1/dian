package com.dian.mmall.service.impl.releaseimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.service.release.GetPublishingsService;

@Service("getPublishingsService")
public class GetPublishingsServiceImpl implements GetPublishingsService {
	
	
	@Autowired
	private CityMapper cityMapper;
	
	 public String ctiy(String provinces_Id,String city_Id,String districtCounty_Id) {
		
		 if(provinces_Id==null || provinces_Id.equals("") || city_Id==null || city_Id.equals("")) {
			 return null; 
		 }
		 
		 int	provincesId=Integer.valueOf(provinces_Id);
		 int	cityId=Integer.valueOf(city_Id);
		 int	districtCountyId=0;
		 if(districtCounty_Id!=null) {
			 districtCountyId=Integer.valueOf(districtCounty_Id);
		 }	
		 if(districtCountyId!=0) {
			return cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		 }else {
			return cityMapper.checkeCityTuo(provincesId, cityId);
		 }
		 
	 }
	 
	 
	
}
