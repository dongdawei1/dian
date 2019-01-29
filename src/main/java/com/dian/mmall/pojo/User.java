package com.dian.mmall.pojo;

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
 
	private Integer id;
	private String  username;
	
	private String loginacct; 
	
	 private String password;
	  private String email;

	    private String phone;

	    private String question;

	    private String answer;

	    private Integer role;

	    private String createTime;

	    private String updateTime;

	
	
}
