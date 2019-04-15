package com.dian.mmall.service.release;

public interface TRolePermissionService {

	int isrole(long userId, int permissionid );

	int isrelease(int role, int permissionid);

	

}
