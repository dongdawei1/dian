package com.dian.mmall.pojo.pingjia;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluate {
	private long id;
	@IsEmptyAnnotation(message="发布id不能为空") //发布的id，接单固定评价 固定-1
	
	private long permissionid;
	private long userId; 
	@IsEmptyAnnotation(message="菜单id不能为空") //13和17和19 的综合评价  ,接单固定评价 菜单-1
	private Integer releaseid;
	//灭虫，装修，菜谱，广告
	private Integer	fuwutaiduhao=0;
	private Integer	fuwutaiduzhong=0;
	private Integer	fuwutaiducha=0; //服务态度
	private Integer	jiagegao=0;
	private Integer	jiagezhong=0;
	private Integer	jiagedi=0; //价格
	private Integer	zhuanyehao=0; 
	private Integer	zhuanyemanyi=0;
	private Integer	zhuayeyiban=0; //专业
	private Integer	zhunshihao=0;
	private Integer	zhunshijiaohao=0; 
	private Integer	zhunshijiaocha=0;   //准时
	
	private Integer	shouhouhao=0;
	private Integer	shouhouzhong=0;
	private Integer	shouhoucha=0;//售后
	
	private Integer	zhilianghao=0;
	private Integer	zhiliangyiban=0;
	private Integer	zhiliangcha=0;  //质量

	
	
}
