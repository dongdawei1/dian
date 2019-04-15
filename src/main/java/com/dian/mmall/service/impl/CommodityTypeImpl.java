package com.dian.mmall.service.impl;


//准备删除

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.CommodityTypeMapper;
import com.dian.mmall.dao.PermissionMapper;
import com.dian.mmall.service.CommodityTypeService;
@Service("commoditytype")
public class CommodityTypeImpl implements CommodityTypeService {
	@Autowired
	private CommodityTypeMapper commodityTypeMapper;

	@Override
	public int getcommodityType(int permissionId, String commoditytype) {
		
		return commodityTypeMapper.getcommodityType(permissionId, commoditytype);
	}
	
	
	
}
