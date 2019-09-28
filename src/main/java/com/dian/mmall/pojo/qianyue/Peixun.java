package com.dian.mmall.pojo.qianyue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Peixun {
	 private long id;
	 private String detailed;//省市区
	 private String  addressDetailed; //详细地址，
	 private String contact;  //收/送货联系方式
	 private String consigneeName; //联系人
	 
	 private String examineName;
	 private String examineTime;
}
