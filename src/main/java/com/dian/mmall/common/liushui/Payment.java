package com.dian.mmall.common.liushui;

public enum Payment {
	chongzhi("转入"),
	dongjie("冻结"),
	jiedong("解冻" ),
	zhuanchu("转出");
	
  String payment;
  Payment(String payment){
		this.payment=payment;
	}
	public String getPayment() {
		return payment;
	}
}
