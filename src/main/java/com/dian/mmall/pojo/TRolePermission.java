package com.dian.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TRolePermission {
	private Integer id;
	private Integer permissionid;
	private Integer roleid;
	private Integer isrelease;//是否有发布权限1有2没有
}
