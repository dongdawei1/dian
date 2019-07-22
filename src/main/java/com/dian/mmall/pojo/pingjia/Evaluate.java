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
	@IsEmptyAnnotation(message="菜单id不能为空") //13和17和19 的综合评价
	private Integer permissionid;
	
	@IsEmptyAnnotation(message="发布id不能为空") //发布的id
	private Integer releaseid;
	//灭虫，装修，菜谱，广告
	private Integer	fuwutaiduhao;
	private Integer	fuwutaiduzhong;
	private Integer	fuwutaiducha; //服务态度
	private Integer	jiagegao;
	private Integer	jiagezhong;
	private Integer	jiagedi; //价格
	private Integer	zhuanye; 
	private Integer	zhuayeyiban; //专业
	private Integer	zhunshihao;
	private Integer	zhunshijiaohao; 
	private Integer	zhunshijiaocha;   //准时
	
	private Integer	shouhouhao;
	private Integer	shouhouzhong;
	private Integer	shouhoucha;//售后
	
	private Integer	zhilianghao;
	private Integer	zhiliangyiban;
	private Integer	zhiliangcha;  //质量
	
}
