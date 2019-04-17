package com.dian.mmall.enums;

import lombok.Getter;

@Getter
public enum PictureNum {
	GRAINANDOIL("粮油区",5);
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
