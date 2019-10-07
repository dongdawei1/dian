package com.dian.mmall.common.liushui;

public enum ReceiptsAndPayments {
	yue("余额"),
	weixin("微信"),
	zhifubao("支付宝" ),
	yinhangka("银行卡"),
	xianxia("线下"),
	jizhang("记账");
	
  String receiptsAndPaymentsType;
  ReceiptsAndPayments(String receiptsAndPaymentsType){
		this.receiptsAndPaymentsType=receiptsAndPaymentsType;
	}
	public String getReceiptsAndPayments() {
		return receiptsAndPaymentsType;
	}
}
