package com.dian.mmall.pojo.banner;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DibuBunner {
	private long id;
	
	private Integer permissionid; //在哪个菜单下展示
	
	private Integer bunnerType; //1是底部
	private Integer tableName; //至能配置有查看权限有交集的类型
	private long tableId;
	private long createId;
	private String updateTime;
	private String 	url;//路由连接
}
