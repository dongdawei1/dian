package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;

public interface ServiceTypeService {

	ServerResponse<String> create_serviceType(long userId, Map<String, Object> params);




	ServerResponse<Object> get_serviceType(Integer releaseType, String serviceType,Long userId);

}
