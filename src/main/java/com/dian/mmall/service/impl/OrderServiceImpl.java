package com.dian.mmall.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.OrderMapper;
import com.dian.mmall.service.OrderService;
@Service
public class OrderServiceImpl implements OrderService {
   @Autowired
   private OrderMapper orderMapper;
	@Override
	public ServerResponse<String> create_wholesaleCommodity_order(long userId, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<Object> get_conduct_order(long userId, int releaseType, String serviceType,int orderStatus) {
		if(userId<0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshicuowo.getMessage());
		}
		if(releaseType<0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixinkong.getMessage());
		}
		if(serviceType==null ||serviceType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinmingkong.getMessage());
		}
		if(orderStatus<0 || orderStatus>10) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunzhuangtcuowo.getMessage());
		}
		return  ServerResponse.createBySuccess(orderMapper.get_conduct_order(userId,releaseType,serviceType,orderStatus));
	}

}
