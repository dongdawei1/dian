package com.dian.mmall.pojo;

import com.dian.mmall.common.liushui.Payment;
import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Liushui {
	private long id;
	@IsEmptyAnnotation(message = "用户ID不能为空")
	private long userId;
	
	@IsEmptyAnnotation(message = "收款用户ID不能为空")
	private long receivablesUserId;
	
	@IsEmptyAnnotation(message = "金额不能为空")
	private long amount;//金额分
	
	@IsEmptyAnnotation(message = "操作类型不能为空")
	private String payment;	
	
	@IsEmptyAnnotation(message = "交易类型不能为空")
	private String transactionType;
	
	@IsEmptyAnnotation(message = "支付方式不能为空")
	private String receiptsAndPayments;
	
	@IsEmptyAnnotation(message = "收款账户不能为空")
	private String accountNo;
	
	@IsEmptyAnnotation(message = "创建时间不能为空")
	private String createTime;
	
	private long dingdanId; //订单ID
	
	private Integer liushuiStatus; // 1 初始，2 失败，3成功
	
	private String updateTime; // 非必填
	
	
}
