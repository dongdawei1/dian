package com.dian.mmall.common.zhiwei;

public enum Experience {
   
	buxian("不限"),
	yinian("1年以上"),
	liangnian("2年以上"),
	sannian("3年以上"),
	sinian("4年以上"),
	wu("无"),
	yizhinian("1-2年"),
	erzhinian("2-5年"),
	wunian("5-10年"),
	shinian("10年以上");
	String experience;
	Experience(String experience){
		this.experience=experience;
	}
	public String getExperience() {
		return experience;
	}
	
}
