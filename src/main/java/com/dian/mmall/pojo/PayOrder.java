package com.dian.mmall.pojo;

import java.io.Serializable;

import com.dian.mmall.util.MD5Util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 支付表
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayOrder  {

	private long id;
	private long userId;
	private long orderId;
	
	
	private String nonceStr=MD5Util.generateUUID();
	private String body;
	private String outTradeNo; //订单id
	private String spbillCreateIp; //客户端或者服务器ip
	private String tradeType; //JSAPI--JSAPI支付（公众号或小程序支付）、NATIVE--扫码支付、APP--app支付，MWEB--H5支付
	
	/**
	 * 分为单位
	 */
	private Integer totalFee;
	/**
	 * 0表示未支付，1表示已经支付
	 */
	private Integer state;
	private String createTime;
	private String payTime;
	private String updateTime;

	private String meg; //微信返回信息
	private String payType="CJ";//操作类型CJ 创建，HD 微信回调，CX 主动查询
	private String costType="dingjin";//暂时先写死
	
	private String beiyong; //数据库设计长度1 使用时修改
	private Integer del;//0正常，1已删除

}
