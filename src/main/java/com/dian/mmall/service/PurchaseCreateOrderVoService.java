package com.dian.mmall.service;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface PurchaseCreateOrderVoService {

	ServerResponse<Object> getPurchaseCreateOrderVo(User user);

}
