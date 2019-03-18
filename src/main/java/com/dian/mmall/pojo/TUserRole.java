package com.dian.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TUserRole {
	//用户对应的角色
		private Integer id;
		private Integer userid;
		private Integer roleid;
}
