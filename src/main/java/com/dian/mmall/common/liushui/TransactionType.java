package com.dian.mmall.common.liushui;

public enum TransactionType {
	zhibaojin("质保金");
	
  String transactionType;
  TransactionType(String transactionType){
		this.transactionType=transactionType;
	}
	public String getTransactionType() {
		return transactionType;
	}
}
