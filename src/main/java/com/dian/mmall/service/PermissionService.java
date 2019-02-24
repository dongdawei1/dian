package com.dian.mmall.service;

import java.util.List;

import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.User;



public interface PermissionService {

	
 
	//查询全部菜单
	List<Permission> queryAll();

  //查询用户权限

	List<Permission> queryPermissionsByUser(User dbUser);

}
