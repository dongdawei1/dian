package com.dian.dingshi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.OrderExampleTimerMapper;
import com.dian.mmall.service.impl.BunnerServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("orderExampleTimerService")
public class OrderExampleTimerServiceImpl implements OrderExampleTimerService{
	@Autowired
	private OrderExampleTimerMapper omap;
	
	public List<Long> getall(int a, String dateString) {
		return omap.getall( a,dateString);
	}

	@Override
	public void upall(int a, String dateString) {
		omap.upall(a,dateString);
		
	}

	@Override
	public void delall(int a, String dateString, String termOfValidity) {
		omap.delall( a,  dateString,  termOfValidity) ;
		
	}

}
