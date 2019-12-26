package com.dian.mmall.pojo.goumaidingdan;

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
public class CommonMenu {
	//常用订单表  表名common_menu
	private long id;
	private long userId;  //销售商id
	private String servicetypeId; //commodityPacking  包装方式,cations 商品规格,specifi 包装单位
}
