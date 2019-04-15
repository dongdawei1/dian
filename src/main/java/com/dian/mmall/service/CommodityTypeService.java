package com.dian.mmall.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.dian.mmall.dao.PermissionMapper;
//准备删除
public interface CommodityTypeService {

	int getcommodityType(int permissionId, String commoditytype);
	
}
