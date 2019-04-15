package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TRolePermissionMapper {
     //检查有无该菜单权限因为可能有用户拥有多个角色返回总数
	int isrole(long userId, int permissionid );

	int isrelease(int roleid, int permissionid);

	

	
	
}
