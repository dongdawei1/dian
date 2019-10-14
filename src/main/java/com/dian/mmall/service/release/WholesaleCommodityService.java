package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface WholesaleCommodityService {

	ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params);

}
