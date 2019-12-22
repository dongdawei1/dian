package com.dian.mmall.dao.goumaidingdan;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.goumaidingdan.CommonMenu;

@Mapper
public interface CommonMenuMapper {

	CommonMenu getCommonMenu(long id);

}
