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
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.pojo.banner.DibuBunner;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.impl.releaseimpl.MenuAndRenovationAndPestControlServiceImpl;
import com.dian.mmall.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("bunnerService")
public class BunnerServiceImpl implements BunnerService {
	@Autowired
	private BunnerMapper bunnerMapper;
    
	@Autowired
	private RealNameMapper realNameMapper;
	
	@Autowired 
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;
	@Override
	public ServerResponse<Object> getBunnerList(Integer role, Integer permissionid, Integer bunnerType,long userId) {
		
		String detailed="%"+realNameMapper.getDetailed(userId)+"%";
		String date=DateTimeUtil.dateToAll();
		 List<DibuBunner> listBunner=bunnerMapper.getBunnerList(role,permissionid,bunnerType,detailed,date);
		 int size =listBunner.size();

		 if(size==0) {
			 listBunner=bunnerMapper.getBunnerList(role,null,bunnerType,detailed,date);	
			 size =listBunner.size();
			 if(size==0 && detailed!=null) {
		
			        //获得第一个点的位置
			        int index=detailed.indexOf("/");
			        //根据第一个点的位置 获得第二个点的位置
			        index=detailed.indexOf("/", index+1);
			        //根据第二个点的位置，截取 字符串。得到结果 result
			        String result=detailed.substring(0,index)+"%";
				 
				 listBunner=bunnerMapper.getBunnerList(role,null,bunnerType,result,date);	 
			 }
			 return ServerResponse.createBySuccess(listBunner); 
		 }else {
			 return ServerResponse.createBySuccess(listBunner);
		 }
		 
		 
	}
	@Override
	public int getguanggaocount(long tableId, int tableName) {
		return bunnerMapper.getguanggaocount(tableId, tableName);
	}

}
