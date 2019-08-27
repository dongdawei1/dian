package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.ServiceType;

@Mapper
public interface ServiceTypeMapper {

	int create_serviceType(ServiceType serviceType);

	List<String> get_serviceType(Integer releaseType,String serviceType,Long userId);

}
