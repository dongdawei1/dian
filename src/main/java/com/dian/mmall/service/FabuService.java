package com.dian.mmall.service;

import java.util.List;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.fabu.FanHui;
import com.dian.mmall.pojo.user.User;

public interface FabuService {

	ServerResponse<String> createfabu(User user, Map<String, Object> params);


	ServerResponse<Object> getfabuad(Map<String, Object> params);


	ServerResponse<Object> getmyfabu(User user, Map<String, Object> params);


	ServerResponse<String> upfabu(User user, Map<String, Object> params);


	ServerResponse<Object> getmyfabubyid(long userId, long id);


	ServerResponse<Object> getfabulist(Map<String, Object> params);


	ServerResponse<Object> getfabutiao(Map<String, Object> params);


	ServerResponse<Object> getfabubyid(long id);


	List<FanHui> adminGetWcall(long userId);


	ServerResponse<Object> getquxian(long id);


	ServerResponse<Object> getfabulista(Map<String, Object> params);

}
