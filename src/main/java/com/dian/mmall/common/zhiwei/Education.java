package com.dian.mmall.common.zhiwei;

public enum Education {
	wu("无"),
   buxian("不限"),
   chuzhong("初中以上"),
   gaozhong("高中以上"),
   dazhuan("大专以上"),
   benke("本科以上");
	
	
	String education;
	Education(String education){
		this.education=education;
	}
	public String getEducation() {
		return education;
	}
	
}
