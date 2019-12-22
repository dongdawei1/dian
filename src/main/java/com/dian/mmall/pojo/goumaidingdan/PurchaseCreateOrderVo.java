package com.dian.mmall.pojo.goumaidingdan;

import java.util.List;

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
public class PurchaseCreateOrderVo {
	private int isCommonMenu=0;  //0是没有创建过常用订单，1是创建过
	private List<CommonMenuWholesalecommodity> myCommonMenu;
	private List<CommonMenuWholesalecommodity> allCommonMenu;	
	
	/* shichang_wholesalecommodity  此表查询出    serviceType 商品名
	releaseType=4,   该城市下的全部 的 以商品名和 
	已  commodityPacking  包装方式,cations 商品规格,specifi 包装单位  去重
	
	*/
}
