package com.dian.mmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.PermissionMapper;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.User;
import com.dian.mmall.service.PermissionService;



@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionMapper permissionDao;



	//查询全部菜单
	public List<Permission> queryAll() {
		return permissionDao.queryAll();
	}

 //查询用户权限

	public List<Permission> queryPermissionsByUser(User dbUser) {
		return permissionDao.queryPermissionsByUser(dbUser);
	}
}
