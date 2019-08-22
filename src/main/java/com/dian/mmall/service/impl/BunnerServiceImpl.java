package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.BunnerMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.pojo.banner.DibuBunner;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.impl.releaseimpl.MenuAndRenovationAndPestControlServiceImpl;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("bunnerService")
public class BunnerServiceImpl implements BunnerService {
	@Autowired
	private BunnerMapper bunnerMapper;

	@Autowired 
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;
	@Override
	public ServerResponse<Object> getBunnerList(Integer role, Integer permissionid, Integer bunnerType) {
		 List<DibuBunner> listBunner=bunnerMapper.getBunnerList(role,permissionid,bunnerType);
		 int size =listBunner.size();

		 if(size==0) {
			 listBunner=bunnerMapper.getBunnerList(role,null,bunnerType);			
			 return ServerResponse.createBySuccess(listBunner); 
		 }else {
			 return ServerResponse.createBySuccess(listBunner);
		 }
		 
		 
	}

}
