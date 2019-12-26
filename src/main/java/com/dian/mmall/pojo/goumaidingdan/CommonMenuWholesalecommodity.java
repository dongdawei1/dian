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
	private Integer releaseType;//p商品类型不能为空4蔬菜出售5粮油出售6调料/副食出售29水产/禽蛋出售9清洁用品
	private String  serviceType;//p商品名称不能为空
	private Integer commodityPacking;//p包装方式3瓶, 2袋 ，1散装
	private String  commodityPackingName;//p购买单位  1散装 ,2袋  ,3瓶/桶
	private String  specifiName;//p 购买单位   kg ,袋  ,瓶/桶
	private Integer specifi;//p 1g,2kg,3ML,4L'
	private String cations; // p  code 1kg ,2g, 3L ,4ML
	private String  specifi_cations; //p包装规格
	private String number; //p购买数量小于100  String 再转吧
	private String  remarks;//p备注
	private String pictureUrl; //p图片地址
	private Integer type=0;//p 0是全部  1是我的常用订单
	private  boolean placing=false;//p是否置灰
}
