package com.dian.mmall.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
 //菜单表
	private Integer id;
	private String name;
	private String url;
	private Integer pid;
	private boolean open = true;
	private boolean checked = false;
	private String icon;
	private List<Permission> children = new ArrayList<Permission>();
	private Integer paixu;

	
}
