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
	public ServerResponse<Object> getBunnerList(Integer permissionid, Integer bunnerType) {
		 List<DibuBunner> listBunner=bunnerMapper.getBunnerList(permissionid,bunnerType);
		 int size =listBunner.size();
	//	
		List<Object> list=new ArrayList<Object>();
		 if(size>0) {
			
			 for(int a=0;a<size;a++) {
				 DibuBunner dibuBunner=listBunner.get(a);
				 Map<String, Object> map=new HashMap<String, Object>();
				 int tableName=dibuBunner.getTableName();
				 if(tableName==13) {
					 MenuAndRenovationAndPestControl mrp=menuAndRenovationAndPestControlMapper.getMrpBunner(dibuBunner.getTableId()); 
					 map.put("object", mrp);	
					 map.put("url", dibuBunner.getUrl()+"/"+mrp.getId());
					 list.add(map); 
				 }				 
				if(list.size()==2) {
				return ServerResponse.createBySuccess(list); 
				}
			 }
			 return ServerResponse.createBySuccess(list); 
		 }else {
			 return ServerResponse.createBySuccess(null);
		 }
		 
		 
	}

}
