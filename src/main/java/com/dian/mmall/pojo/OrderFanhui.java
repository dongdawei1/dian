package com.dian.mmall.pojo;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderFanhui {
	private long id;

	private String addressDetailed; //待报价订单不查这个;

	
	@IsEmptyAnnotation(message="送/取货时间不能为空") 
	private String giveTakeTime;//送货取货时间
	
	@MaxSize(max=30, message="备注不能大于30字")
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message = "下单时间不能为空")
	private String createTime;
	
	private Integer releaseType;
	private String paymentTime;  //支付定金时间，
	private String guanShanTime; //支付异常时正常应该支付的金额
	private String guanShanReason; //此字段记录是哪个操作对应支付表payType 
	private String collectTime;//收货时间
	
	@IsEmptyAnnotation(message="订单状态不能为空") 
	private Integer orderStatus;
	//1购买者下单,2批发者确认，3用户关单，4取/送货 ，5待评价，6评价完成 ，11发布采购订单 ，12待支付， 13发布者确认  ，
	//16 确认收货，17超时无人接单关单，18 三十分钟已过有人接单 确认期，19 超时未支付，20接单者未支付定金，21支付失败
	
	@IsEmptyAnnotation(message="商品快照不能为空")
	private String commoditySnapshot;   //下单时的商品详情除地址
	
	private String  guaranteeMoney;//保障金
	private Integer  yesGuaranteeMoney;//是否支付了保证金 0未支付，1已支付
	private String  balanceMoney;//待支付金额 
	private long saleUserId;
	private long  commodityZongJiage; // 	
}
