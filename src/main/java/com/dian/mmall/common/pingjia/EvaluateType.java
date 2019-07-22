package com.dian.mmall.common.pingjia;

public enum EvaluateType {

	//灭虫,装修，广告，修理电器
	fuwutaiduhao(13,"好"),
	fuwutaiduzhong(13,"中"),
	fuwutaiducha(13,"差"), //服务态度
	jiagegao(13,"较高"),
	jiagezhong(13,"适中"),
	jiagedi(13,"满意"), //价格
	zhuanye(13,"专业"), 
	zhuayeyiban(13,"一般"), //专业
	zhunshihao(13,"准时"),
	zhunshijiaohao(13,"较好"),
	zhunshijiaocha(13,"不准时"),                 //准时
	
	shouhouhao(13,"售后好"),
	shouhouzhong(13,"售后中"),
	shouhoucha(13,"售后差"),  //售后
	
	//货物 质量
	zhilianghao(14,"质量好"),
	zhiliangyiban(14,"质量一般"),
	zhiliangcha(14,"质量差"),
	
	
	
	;
	
	String evaluateType;
    Integer releaseid;
    EvaluateType(Integer releaseid,String evaluateType){
    	this.evaluateType=evaluateType;
    	this.releaseid=releaseid;
    }
	public String getEvaluateType() {
		return evaluateType;
	}
	public Integer getReleaseid() {
		return releaseid;
	}
    
}
