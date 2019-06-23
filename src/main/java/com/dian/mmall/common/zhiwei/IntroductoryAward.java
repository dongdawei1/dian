package com.dian.mmall.common.zhiwei;

public enum IntroductoryAward {
	wu("无"),
	yibai("每人100元入职一个月后奖励"),
	erbai("每人200元入职一个月后奖励"),
	sanbai("每人300元入职一个月后奖励"),
	sibai("每人400元入职一个月后奖励"),
	wubai("每人500元入职一个月后奖励");
	
String introductoryAward;
	IntroductoryAward(String introductoryAward){
		this.introductoryAward=introductoryAward;
	}
	public String getIntroductoryAward() {
		return introductoryAward;
	}
	
	
}
