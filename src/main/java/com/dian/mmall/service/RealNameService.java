package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;

public interface RealNameService {

	ServerResponse<String> newRealName(long l, Map<String, Object> params);

}
