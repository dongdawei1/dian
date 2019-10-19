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
	public ServerResponse<Object> get_conduct_order(long wholesaleCommodityId, int orderStatus) {
		if(wholesaleCommodityId<0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinidcuowo.getMessage());
		}
		
		if(orderStatus<0 || orderStatus>10) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunzhuangtcuowo.getMessage());
		}
		return  ServerResponse.createBySuccess(orderMapper.get_conduct_order(wholesaleCommodityId,orderStatus));
	}

}
