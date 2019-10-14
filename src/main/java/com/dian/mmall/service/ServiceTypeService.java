package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ServiceTypeService {





	ServerResponse<Object> get_serviceType(Integer releaseType, String serviceType,Long userId);








	ServerResponse<String> create_serviceType(User user, Map<String, Object> params);
	ServerResponse<String> admin_create_serviceType(User user, Map<String, Object> params);


	ServerResponse<Object> get_serviceTypeUrl(Integer releaseType, String serviceType, long id);

}
