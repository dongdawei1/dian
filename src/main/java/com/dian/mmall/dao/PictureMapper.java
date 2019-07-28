package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.tupian.Picture;
@Mapper
public interface PictureMapper {

	int createPicture(Picture picture1);

	Picture selectPicture(Picture picture1);
   //删除更新
	void updatePicture(long id);
//使用更新
	void updatePictureUse(long id);

	Picture selectPictureBYid(long id);
   //管理员添加自由送货的实名
	void updatePictureAdmin(Picture picture);

}
