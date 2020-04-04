package com.dian.websockert;

import java.util.Map;

import com.dian.mmall.pojo.Order;

public interface WebsockertService {
	String remote(String message, String vmcNo);

	void remote1(String string, Object object);
	
	String fadingdan(String detailed,Order order);

	
	
	
}
