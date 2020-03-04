package com.dian.mmall.service.release;

import java.util.List;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.user.User;

public interface WineAndTablewareService {

	ServerResponse<String> create_wineAndTableware(User user, Map<String, Object> params);

	ServerResponse<Object> get_myWineAndTableware_list(User user, Map<String, Object> params);

	ServerResponse<String> operation_userWineAndTableware(User user, Map<String, Object> params);

	ServerResponse<Object> get_userWineAndTableware_id(long userId, long id);

	ServerResponse<Object> getWineAndTablewareTitleList(Map<String, Object> params);

	ServerResponse<Object> getWineAndTablewarePublicList(Map<String, Object> params);

	ServerResponse<Object> getWineAndTablewareDetails(long id);

	ServerResponse<Object> adminWineAndTableware(Map<String, Object> params);

	List<WineAndTableware> adminGetWtall(long userId);

}
