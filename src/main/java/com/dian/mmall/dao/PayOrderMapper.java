package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.PayOrder;

@Mapper
public interface PayOrderMapper {

	PayOrder getPayOrderByOrderId(long orderId,int state);

	int createPyOrder(PayOrder payOrder);

	void unifiedUptaePayOrder(PayOrder payOrder);


	PayOrder getCallbackPayOrder(String outTradeNo, int state);

	int callbackUpdate(PayOrder payOrder);

}
