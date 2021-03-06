package com.dian.mmall.common;

import lombok.Getter;

@Getter
public enum PictureNum {
	//GRAINANDOIL("粮油区",5),
	ShiMingRenZheng("实名认证",3),
	miechongzhuangxiu("灭虫装修广告",8),
	caishangpin("菜粮油等",5),
	dianqiheweixiu("电器维修二手",8),
	zufangang("租房",8),
	jiushui("酒水",8),
	gongfubaihuo("工服百货",5),
	shucaishili("蔬菜示例",1),
	guanggao("广告示例",1), //不能删
	pifashangpin("批发商品",5);
//	  LEASE("店面/窗口出租",14),
//	   RENTALBOOTH("摊位出租转让",15),
//	   JOBWANTED("求职专区",31);
	   
private Integer  num; //对应可以上传的图片数量
  private String pictureNum; //菜单名
  private  PictureNum(String pictureNum,Integer  num) {
		this.num=num;
		this.pictureNum=pictureNum;
	}
}
