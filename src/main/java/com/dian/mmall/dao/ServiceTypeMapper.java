package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.ServiceType;

@Mapper
public interface ServiceTypeMapper {

	int create_serviceType(ServiceType serviceType);

	List<String> get_serviceType(Integer releaseType,String serviceType,Long userId);
	
	List<ServiceType> get_serviceTypeAll(Integer releaseType);

	int updatebyId(ServiceType serviceType);

	void deletebyId(ServiceType serviceType);

	int selectbyId(long id);

	List<ServiceType> get_serviceTypeUrl(Integer releaseType, String serviceType);
	
	int getserviceTypeNameCount(Integer releaseType, String serviceType, Integer authentiCationStatus);

}
