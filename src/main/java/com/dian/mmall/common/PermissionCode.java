package com.dian.mmall.common;

import lombok.Getter;

@Getter
public enum PermissionCode {

	GRAINANDOIL("粮油区",5),
	  LEASE("店面/窗口出租",14),
	   RENTALBOOTH("摊位出租转让",15),
	   JOBWANTED("求职专区",31),
	   ZHIWEI("职位",30);
	   
  private Integer  code; //菜单数据库id编号
    private String permissionName; //菜单名
    private  PermissionCode(String permissionName,Integer  code ) {
		this.code=code;
		this.permissionName=permissionName;
	}
	
}
