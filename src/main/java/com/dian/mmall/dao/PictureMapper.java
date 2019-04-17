package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.tupian.Picture;
@Mapper
public interface PictureMapper {

	int createPicture(Picture picture1);

	Picture selectPicture(Picture picture1);
   //删除更新
	void updatePicture(Picture picture1);
//使用更新
	void updatePictureUse(Picture picture);

}
