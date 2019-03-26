package com.dian.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum CommodityType {
 //商品对应的类型id
	 
	GRAIN("grain",51),   //粮
	OIL("oil",52);  //油
		
    private Integer numbering; //类型编号
    private String type; //类型名
	private  CommodityType(String type,Integer numbering ) {
		this.numbering=numbering;
		this.type=type;
	}
	
}
