package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ReleaseWelfareService {

	ServerResponse<String> create_position(User currentUser, Map<String, Object> params);

}
