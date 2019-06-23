package com.dian.mmall.common.zhiwei;

public enum Salary {
    liangqian("2000元以上"),
    sanqian("3000元以上"),
    siqian("4000元以上"),
    woqian("5000元以上"),
    liuqian("6000元以上"),
    baqian("8000元以上"),
    yiwan("10000元以上"),
    mianyi("面议");
	
	
	String salary;
	Salary(String salary){
		this.salary=salary;
	}
	public String getSalary() {
		return salary;
	}

}
