package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.banner.DibuBunner;

@Mapper
public interface BunnerMapper {

	List<DibuBunner> getBunnerList(Integer permissionid,Integer bunnerType);

}
