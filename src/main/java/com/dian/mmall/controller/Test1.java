package com.dian.mmall.controller;

import com.dian.mmall.common.ServerResponse;

public class Test1 {
public static void main(String[] args) {
	String password="deleete";
	String username="delete";
	//校验是否有特殊字符
    if(password.toLowerCase().indexOf("delete")>=0 || password.toLowerCase().indexOf("update")>=0 
 		   || username.toLowerCase().indexOf("delete")>=0 || username.toLowerCase().indexOf("update")>=0 ) {
 	  System.out.println("Test1.main()" );
    }
	  System.out.println(4444444 );
}
}
