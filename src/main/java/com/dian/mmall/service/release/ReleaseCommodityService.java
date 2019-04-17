package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ReleaseCommodityService {
	//所有发布商品的接口
	ServerResponse<String> commodity(User user, String loginToken,Map<String, Object> params);
 
}
