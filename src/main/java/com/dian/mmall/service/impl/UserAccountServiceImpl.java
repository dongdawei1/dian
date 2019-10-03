package com.dian.mmall.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.UserAccountMapper;
import com.dian.mmall.pojo.user.OrderUser;
import com.dian.mmall.pojo.user.UserAccount;
import com.dian.mmall.service.UserAccountService;
import com.dian.mmall.util.BeanMapConvertUtil;

@Service("userAccountService")
public class UserAccountServiceImpl  implements UserAccountService{
  
	@Autowired
	private UserAccountMapper userAccountMapper;
	
	@Override
	public ServerResponse<String> admin_create_userAccount(Map<String, Object> params) {
		
		long balance=(long)params.get("availableAmount")*100;
		params.put("balance",balance );
		params.put("availableAmount",balance);
		
		UserAccount   userAccount= (UserAccount) BeanMapConvertUtil.convertMap(UserAccount.class, params);
		int count = userAccountMapper.admin_create_userAccount(userAccount);
		if (count == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		}
		return ServerResponse.createBySuccess();
		
	}

	@Override
	public ServerResponse<String> create_liushui(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
