package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.TUserRole;

@Mapper
public interface TUserRoleMapper {
	  //创建用户是插入角色表
	   int createTUserRole(TUserRole userRole);
}
