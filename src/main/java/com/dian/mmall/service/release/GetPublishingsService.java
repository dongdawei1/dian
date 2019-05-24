package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface GetPublishingsService {



	ServerResponse getMenuList(User user, Map<String, Object> params);
}
