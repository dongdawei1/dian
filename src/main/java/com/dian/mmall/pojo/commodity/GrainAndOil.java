package com.dian.mmall.pojo.commodity;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrainAndOil {
	private Integer id;
	private Integer userId; 
	private Integer numberOfChecks; //交易次数
	private String  commodityName;  //商品名
	private String  placeOfOrigin; //产地
	private String	brand; //品牌
	private String	specifications;//规格
	private String	price; //价格
	private String	priceEffectiveStart;  //有效期开始日期
	private String	priceEffectiveEnd;  //有效期结束日期
	
	private String	type;  //商品类型
	
	private String	remarks;  //备注 10字以内
	private Map<String,String> pictureUrl; //图片地址
	
	private Integer isReceivingPurchase; //判断是否在价格有效期内，如果在就显示 一键发起采购信息键
	
	 private String createTime;  
	 private String updateTime;
} 
