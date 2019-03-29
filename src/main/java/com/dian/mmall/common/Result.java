package com.dian.mmall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
	//判断结果
	private boolean success;
	//返回信息
	private String message;
	 
	
	 
	public boolean isSuccess() {
	return success;
	}
	 
	
}
