package com.dian.mmall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ReleaseCount {
 //商品对应的类型id
	 
	GRAINANDOIL("shang_grainandoil",30);   
	//OIL("oil",52);  //油
		
    private Integer count; //类型编号
    private String tabuleName; //类型名
	private  ReleaseCount(String tabuleName,Integer count ) {
		this.count=count;
		this.tabuleName=tabuleName;
	}
	
}
