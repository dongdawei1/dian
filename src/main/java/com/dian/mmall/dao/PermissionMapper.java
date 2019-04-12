package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.User;


@Mapper
public interface PermissionMapper {


	//查询全部菜单
	@Select("select * from t_permission")
	List<Permission> queryAll();

  
	
	//根据用户查询权限
	List<Permission> queryPermissionsByUser(User dbUser);

}
