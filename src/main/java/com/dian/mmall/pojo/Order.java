package com.dian.mmall.pojo;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	private long id;
	@IsEmptyAnnotation(message = "销售商户ID不能为空")
	private long saleUserId; 
	@IsEmptyAnnotation(message = "购买者ID不能为空")
	private long purchaseUserId; 	
	@IsEmptyAnnotation(message = "商品ID不能为空")
	private long wholesaleCommodityId;
	
	
	private long evaluateid;//评价ID
	
	
	@MaxSize(max=12, message="商品原单价不能大于12位")
	@IsEmptyAnnotation(message = "商品原单价不能为空")
	private long  commodityJiage; // 
	
	@MaxSize(max=12, message="订单数量不能大于12字")
	@IsEmptyAnnotation(message = "订单数量不能为空")
	private long  commodityCountNo;  
	
	@MaxSize(max=20, message="商品总价不能大于20位")
	@IsEmptyAnnotation(message = "商品总价不能为空")
	private long  commodityZongJiage; // 
	
	
	@IsEmptyAnnotation(message = "购买方式不能为空")
	private Integer reserve;  //是否接受预定 1预定 2 在线支付
	
	@IsEmptyAnnotation(message="送货方式不能为空") 
	private Integer deliveryType;//1自取  ,2送货,  3自取+送货  4满免
	
	@IsEmptyAnnotation(message="运费收取方式不能为空") 
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
	private String paymentTime;  //3代付款，4支付成功，5,取消支付，
	private String guanShanTime; //关单时间
	private String guanShanReason; //关单原因 
	private String collectTime;//收货时间
	
	
	@IsEmptyAnnotation(message="订单状态不能为空") 
	private Integer orderStatus;
	private Integer payStatus; //3代付款，4支付成功，5,取消支付，初始0
	//1购买者下单,2批发者确认，3关单，4取/送货 ，5待评价，6评价完成
	
	

}
