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
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	private UserAccountMapper userAccountMapper;

	@Override
	public ServerResponse<String> admin_create_userAccount(Map<String, Object> params) {
		// 管理员创建接单人员 账户
		UserAccount userAccount = (UserAccount) BeanMapConvertUtil.convertMap(UserAccount.class, params);
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

	@Override
	public int admin_select_userAccount_byId(long userId) {
		// 查询有无创建过账户
		return userAccountMapper.admin_select_userAccount_byId(userId);
	}

	@Override
	public synchronized ServerResponse<String> admin_update_userAccount(Map<String, Object> userAccount) {
		// 管理员更新重新加入的或者修改接单用户
		UserAccount userAccount1 = (UserAccount) BeanMapConvertUtil.convertMap(UserAccount.class, userAccount);
		return update_userAccount(userAccount1, 1);
	}

	private synchronized ServerResponse<String> update_userAccount(UserAccount userAccount, int type) {
		long userId = userAccount.getUserId();
		if (userId == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}

		ServerResponse<Object> serverResponse = admin_select_userAccount(userId);
		if (serverResponse.getStatus() == 0) {
			UserAccount userAccountSelect = (UserAccount) serverResponse.getData();
			// type 1 充值
			int result = 0;
			if (type == 1) {
				System.out.println("UserAccountServiceImpl.update_userAccount()"+type);
				// 管理员更新重新加入的或者修改接单用户 ,增加不用考虑余额
				userAccount.setBalance(userAccount.getBalance() + userAccountSelect.getBalance());
				userAccount
						.setAvailableAmount(userAccount.getAvailableAmount() + userAccountSelect.getAvailableAmount());
			
				result = userAccountMapper.update_userAccount(userAccount, type);
				if (result != 0) {
					return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
				}else {
					return ServerResponse.createBySuccessMessage(ResponseMessage.gengxinyue.getMessage());
				}
			}

			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
		}

		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	}

	@Override
	public ServerResponse<Object> admin_select_userAccount(long userId) {
		UserAccount userAccount = userAccountMapper.admin_select_userAccount(userId);

		if (userAccount == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		return ServerResponse.createBySuccess(userAccount);
	}

}
