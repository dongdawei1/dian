package com.dian.mmall.service;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ToExamineService {

	ServerResponse<String> examineAll(User user, Map<String, Object> params);

}
