package com.dian.mmall.enums;

import lombok.Getter;

@Getter
public enum PermissionCode {

	GRAINANDOIL("粮油区",5);
  private Integer  code; //菜单数据库id编号
    private String permissionName; //菜单名
    private  PermissionCode(String permissionName,Integer  code ) {
		this.code=code;
		this.permissionName=permissionName;
	}
	
}
