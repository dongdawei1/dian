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
	@Override
	public String toString() {
		return "Evaluate [id=" + id + ", permissionid=" + permissionid + ", releaseid=" + releaseid + ", fuwutaiduhao="
				+ fuwutaiduhao + ", fuwutaiduzhong=" + fuwutaiduzhong + ", fuwutaiducha=" + fuwutaiducha + ", jiagegao="
				+ jiagegao + ", jiagezhong=" + jiagezhong + ", jiagedi=" + jiagedi + ", zhuanye=" + zhuanye
				+ ", zhuayeyiban=" + zhuayeyiban + ", zhunshihao=" + zhunshihao + ", zhunshijiaohao=" + zhunshijiaohao
				+ ", zhunshijiaocha=" + zhunshijiaocha + ", shouhouhao=" + shouhouhao + ", shouhouzhong=" + shouhouzhong
				+ ", shouhoucha=" + shouhoucha + ", zhilianghao=" + zhilianghao + ", zhiliangyiban=" + zhiliangyiban
				+ ", zhiliangcha=" + zhiliangcha + "]";
	}
	
	
}
