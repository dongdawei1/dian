package com.dian.mmall.service.impl.releaseimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.releaseDao.TRolePermissionMapper;
import com.dian.mmall.service.release.TRolePermissionService;
@Service("tRolePermissionService")
public class TRolePermissionImpl implements TRolePermissionService {
	   @Autowired
	    private TRolePermissionMapper tRolePermissionMapper;

	@Override
	public int isrole( long userId, int permissionid ) {
		return tRolePermissionMapper.isrole(userId,  permissionid );
	}
}	

