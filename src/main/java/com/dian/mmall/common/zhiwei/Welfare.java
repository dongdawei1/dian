package com.dian.mmall.common.zhiwei;

public enum Welfare {
	wuxian("五险"),
	baozhu("包住"),
	baochi("包吃"),
	niandishaungxi("年底双薪"),
	fangbu("房补");	
	String welfare;
	Welfare( String welfare){
		   this.welfare=welfare;
	   }
	   public String getWelfare(){
	       return welfare;
	   }
}
