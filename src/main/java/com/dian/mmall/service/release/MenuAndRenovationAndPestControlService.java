package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface MenuAndRenovationAndPestControlService {

	ServerResponse<String> create_menuAndRenovationAndPestControl(User user, Map<String, Object> params);
    //用户自己获取自己的发布
	ServerResponse<Object> get_usermrp_list(User user, Map<String, Object> params);

}
