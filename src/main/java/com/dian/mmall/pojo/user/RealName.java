package com.dian.mmall.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealName {
	
	//实名信息  ,
	private long id;
	private long userId;
    private Integer provincesId;      //省
    private Integer cityId;    //市
	 private Integer districtCountyId; //区，
	 private String detailed;//省市区
	 private String  addressDetailed; //详细地址，
	 private String licenseUrl;   //营业执照图片url
	 private String contact;  //收/送货联系方式
	 private String consigneeName; //收/送货人姓名 
	 private Integer authentiCationStatus;  //状态审批状态 1 审批中 ，2通过，3审核不通过
	 private String authentiCationFailure; //审核失败原因
	 private String email;  
	 private String createTime;
	 private String updateTime;

}
