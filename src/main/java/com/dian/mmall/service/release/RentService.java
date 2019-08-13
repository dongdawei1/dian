package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface RentService {

	ServerResponse<String> create_rent(User user, Map<String, Object> params);

}
