package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TRolePermissionMapper {

	int isrole(long userId, int permissionid );

	

	
	
}
