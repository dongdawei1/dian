package com.dian.mmall.pojo.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
 
	private long id;
	private String  username;  //用户名
	 private String password; 
	 private String mobilePhone; //手机号
	  private Integer role; //角色
	  private Integer isAuthentication; //是否实名1已实名 2未实名
	  
	  private String createTime;
	  private String updateTime;
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", mobilePhone=" + mobilePhone
				+ ", role=" + role + ", isAuthentication=" + isAuthentication + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}

	 
	
}
