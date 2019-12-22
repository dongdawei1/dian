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
	private long id;
	private long userId;  //必填后端获取
	private String servicetypeId; //commodityPacking  包装方式,cations 商品规格,specifi 包装单位
}
