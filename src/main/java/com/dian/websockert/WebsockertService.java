package com.dian.websockert;

import java.util.Map;

import com.dian.mmall.pojo.Liushui;
import com.dian.mmall.pojo.Order;

public interface WebsockertService {
	String remote(String message, String vmcNo);

	void remote1(String string, Object object);
	
	String fadingdan(String detailed,Order order);

	void fayourenjiedan(int type ,long purchaseUserId);

	void fajiedong(Liushui liushui2);


	void faxuanzhong(Order order);

	
	
	
}
