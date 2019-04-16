package com.dian.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
	
	//实名信息  ,
	//  TODO  实名完成后需要   修改  tocken中的信息否则不生效
	private long id;
	private long userId;
	 private String city;      //城市
	 private String address;    //地址
	 private String license;   //营业执照图片url
	 private String contact;  //收货联系方式
	 private String consignee_name; //收货人姓名 
	 private Integer evaluate;  //评价
	 private String region; //城市-区街道
	 private Integer integral;  //积分
	 private String email;  
	 private String createTime;
	 private String updateTime;
}
