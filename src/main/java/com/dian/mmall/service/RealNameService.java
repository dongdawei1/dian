package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;

public interface RealNameService {

	ServerResponse<String> newRealName(long l,String loginToken, Map<String, Object> params);

	ServerResponse<Object> getRealName(long userId);

	ServerResponse<String> updateRealName(long user_id, String loginToken, Map<String, Object> params);

}
