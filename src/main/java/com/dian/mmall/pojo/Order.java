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
public class Order {
	private long id;

	private long saleUserId; //销售商户ID
	private String saleUserName; //销售商户ID

	private long purchaseUserId; //购买商户ID
	
	private long wholesaleCommodityId;
	
	
	private long evaluateid;//评价ID
	
	
	private Integer releaseType;

	private Integer permissionId;
	
	@MaxSize(max=12, message="商品原单价不能大于12位")
	private long  commodityJiage; // 
	
	@MaxSize(max=12, message="订单数量不能大于12字")
	@IsEmptyAnnotation(message = "订单数量不能为空")
	private long  commodityCountNo;  
	
	@MaxSize(max=20, message="商品总价不能大于20位")
	private long  commodityZongJiage; // 
	
	
	@IsEmptyAnnotation(message = "购买方式不能为空")
	private Integer reserve;  //是否接受预定 1预定 2 在线支付 3采购订单
	
	@IsEmptyAnnotation(message="送货方式不能为空") 
	private Integer deliveryType;//1自取  ,2送货,  3自取+送货  4满免
	
	private long deliveryCollect; //运费
	@MaxSize(max=100, message="送货地址不能大于100字")
	private String  addressDetailed; //送货地址
	
	@IsEmptyAnnotation(message="送/取货时间不能为空") 
	private String giveTakeTime;//送货取货时间
	
	@MaxSize(max=30, message="备注不能大于30字")
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message = "下单时间不能为空")
	private String createTime;
	
	private String confirmTime;   //确认时间
	private String paymentTime;  //支付定金时间，
	private String guanShanTime; //支付异常时正常应该支付的金额
	private String guanShanReason; //此字段记录是哪个操作对应支付表payType 
	private String collectTime;//收货时间
	
	private String updateTime; //更新时间
	@IsEmptyAnnotation(message="订单状态不能为空") 
	private Integer orderStatus;
	//1购买者下单,2批发者确认，3用户关单，4取/送货 ，5待评价，6评价完成 ，11发布采购订单 ，12待支付， 13发布者确认  ，
	//16 确认收货，17超时无人接单关单，18 三十分钟已过有人接单 确认期，19 超时未支付，20接单者未支付定金，21支付失败
	private Integer payStatus; //初始0,4支付成功，5支付异常，
	@IsEmptyAnnotation(message="商品快照不能为空")
	private String commoditySnapshot;   //下单时的商品详情除地址
	
	private String  guaranteeMoney;//保障金
	private Integer  yesGuaranteeMoney;//是否支付了保证金 0未支付，1已支付
	private String  balanceMoney;//待支付金额 
	
}
