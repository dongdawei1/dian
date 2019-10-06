package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.user.UserAccount;

@Mapper
public interface UserAccountMapper {


	int admin_create_userAccount(UserAccount userAccount);

	int admin_select_userAccount_byId(long userId);

	UserAccount admin_select_userAccount(long userId);

	int update_userAccount(UserAccount userAccount ,int type);

}
