package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.TUserRole;

@Mapper
public interface TUserRoleMapper {
	  //注册返回id
	   int createTUserRole(TUserRole userRole);
}
