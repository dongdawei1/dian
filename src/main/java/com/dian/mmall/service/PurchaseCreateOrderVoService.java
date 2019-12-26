package com.dian.mmall.service;

import java.util.List;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.user.User;

public interface PurchaseCreateOrderVoService {

	ServerResponse<Object> getPurchaseCreateOrderVo(User user);
	void createMyCommonMenu(User user, List<CommonMenuWholesalecommodity> listObj4, int isCommonMenu);

}
