package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.PayOrder;

@Mapper
public interface PayOrderMapper {

	PayOrder getPayOrderByOrderId(long orderId,int state, String tradeType);

	int createPyOrder(PayOrder payOrder);

	void unifiedUptaePayOrder(PayOrder payOrder);


	PayOrder getCallbackPayOrder(String outTradeNo, int state);

	int callbackUpdate(PayOrder payOrder);

	List<PayOrder> timerSelsetPayOrder(String createTime, int state, int del);

	int get_pay_order_all(long userId);

	PayOrder get_pay_order_byOrderId(long userId, long orderId, int del, String tradeType);

	List<PayOrder> pcAadAppAll(long orderId, int state);

}
