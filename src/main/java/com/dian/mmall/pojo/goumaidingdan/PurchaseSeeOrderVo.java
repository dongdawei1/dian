package com.dian.mmall.pojo.goumaidingdan;

import java.util.List;
import java.util.Map;

import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.pingjia.Evaluate;

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
public class PurchaseSeeOrderVo {
	//1购买者下单,2批发者确认，3用户关单，4取/送货 送到后 送货者点击到地方 ，
	//5待评价，6评价完成 ，11发布采购订单 ，12抢单成功更新销售商户ID ,待确认
	//13发布已者确认，待发货， ，16 确认收货，17超时关单
	
	private Order voOrder;
	private String orderStatuName;  //状态
	// 11 报价中  ，12 待确认价格 ，13  待送货  4 待收货  16 已收货  3 用户关单 17 无接单关单，5待评价   6 已完成
	private boolean orderTime=false;
	private boolean orderStatu3=false;
	private boolean orderStatu4=false;
	private boolean orderStatu5=false;
	private boolean orderStatu6=false;
	private boolean orderStatu11=false;  //==true时  接单商户 列表 操作显示，并且显示倒计时
	private boolean orderStatu12=false;
	private boolean orderStatu13=false;
	private boolean orderStatu16=false;
	private boolean orderStatu17=false;
	
	private List<OrderCommonOfferEvaluateVo>     listOrderCommonOfferEvaluateVo;  
	//11是抢单列表，其他只有一个或没有 ,一个抢单，对应一条  评价	
	private Integer voSocket=1 ; //0开启长连接  ，
	
	private List<PurchaseSeeOrderVo> listPurchaseSeeOrderVo;
	
}
