package com.dian.mmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.user.UserAccount;

@Mapper
public interface UserAccountMapper {


	int admin_create_userAccount(UserAccount userAccount);

}
