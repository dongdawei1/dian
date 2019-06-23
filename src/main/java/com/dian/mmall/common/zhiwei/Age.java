package com.dian.mmall.common.zhiwei;

public enum Age {
	shibaisanshi("18-30岁"),
	shibaisishi("18-40岁"),
	shibaiwushi("18-50岁" ),
	sanshisishi("30-40岁"),
	sanshiliushi("30-60岁");
	
  String age;
	Age(String age){
		this.age=age;
	}
	public String getAge() {
		return age;
	}
	
}
