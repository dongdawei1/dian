package com.dian.mmall.common.zhiwei;

public enum Welfare {
	wuxian("五险"),
	baozhu("包住"),
	baochi("包吃"),
	niandishaungxi("13薪"),
	fangbu("房补"),	
	qita("其他");
	String welfare;
	Welfare( String welfare){
		   this.welfare=welfare;
	   }
	   public String getWelfare(){
	       return welfare;
	   }
	   
}
