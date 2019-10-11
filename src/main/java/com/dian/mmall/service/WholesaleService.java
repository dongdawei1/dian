package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;

public interface WholesaleService {

	ServerResponse<Object> getwholesale(Map<String, Object> params);

}
