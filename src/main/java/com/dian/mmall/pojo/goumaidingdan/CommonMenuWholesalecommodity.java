package com.dian.mmall.pojo.goumaidingdan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonMenuWholesalecommodity {
	private Integer releaseType;//商品类型不能为空4蔬菜出售5粮油出售6调料/副食出售29水产/禽蛋出售9清洁用品
	private String  serviceType;//商品名称不能为空
	private Integer commodityPacking;//包装方式3瓶, 2袋 ，1散装
	private String  commodityPackingName;//购买单位  1散装 ,2袋  ,3瓶/桶
	private String  specifiName;// 购买单位   kg ,袋  ,瓶/桶
	private Integer specifi;//1g,2kg,3ML,4L'
	private String cations; //code 1kg ,2g, 3L ,4ML
	private String  specifi_cations; //包装规格
	private String number; //购买数量小于100  String 再转吧
	private String  remarks;//备注
	private String pictureUrl; //图片地址
	private Integer type=0;//0是全部  1是我的常用订单
	private  boolean isPlacing=false;//是否置灰
}
