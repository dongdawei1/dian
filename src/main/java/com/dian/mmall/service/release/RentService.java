package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface RentService {

	ServerResponse<String> create_rent(User user, Map<String, Object> params);

	ServerResponse<Object> get_myRent_list(User user, Map<String, Object> params);

	ServerResponse<Object> adminMent(Map<String, Object> params);

	ServerResponse<String> operation_userment(User user, Map<String, Object> params);

	ServerResponse<Object> get_userrent_id(long userId, long id);

	ServerResponse<Object> getRentTitleList(Map<String, Object> params);

	ServerResponse<Object> getrentList(Map<String, Object> params);

}
