package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface UserAccountService {
	ServerResponse<String> admin_create_userAccount( Map<String, Object> params);
	
	ServerResponse<String> create_liushui(Map<String, Object> params);
}
