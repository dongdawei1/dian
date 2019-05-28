package com.dian.mmall.pojo.user;

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
	 private String trading_area; //商圈 ，
	 private String contact;  //收/送货联系方式
	 private String consignee_name; //收/送货人姓名 
	 private Integer authentication_status;  //状态审批状态 1 审批中 ，2通过，3审核不通过
	 private String authentication_failure; //审核失败原因
	 private String email;  
	 private String createTime;
	 private String updateTime;
}
