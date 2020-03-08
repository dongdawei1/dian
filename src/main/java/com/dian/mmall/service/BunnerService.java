package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface BunnerService {


	ServerResponse<Object> getBunnerList(Integer role, Integer permissionid, Integer bunnerType, long userId);


	int getguanggaocount(long tableId, int permissionid);




	ServerResponse<Object> isguanggao(Map<String, Object> params);


	ServerResponse<String> crguanggao(User user, Map<String, Object> params);


	ServerResponse<Object> agetguangaoAll(Map<String, Object> params);


	ServerResponse<String> aupguangao(User user, Map<String, Object> params);

}
