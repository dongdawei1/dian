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
	private Integer specifi;//1g,2kg,3ML,4L'
	private double cations; // kg ,g, L ,ML
	private String pictureUrl; //图片地址
}
