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
	private String  username;  //用户名
	 private String password; 
	 private String mobilePhone; //手机号
	 private String city;      //城市
	 private String address;    //地址
	 private String license;   //营业执照图片url
	 private String contact;  //收货联系方式
	  private String email;  
     private String consignee_name; //收货人姓名  
     private Integer integral;  //积分
	    private Integer evaluate;  //评价

	    private String region; //城市-区街道

	    private Integer role; //角色
	    private String createTime;
	     
	    private String updateTime;

	
	
}
