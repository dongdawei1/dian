package com.dian.mmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.TUserRoleMapper;
import com.dian.mmall.pojo.TUserRole;
import com.dian.mmall.service.TUserRoleService;
@Service("tUserRoleService")
public class TUserRoleServiceImpl  implements TUserRoleService {
	@Autowired
	private TUserRoleMapper tUserRoleMapper;

	//创建用户插入对应的角色
	@Override
	public int createTUserRole(TUserRole tUserRole) {
		
		return tUserRoleMapper.createTUserRole(tUserRole);
	}
	
	
	
}
