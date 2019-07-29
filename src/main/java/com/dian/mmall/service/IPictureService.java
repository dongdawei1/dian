package com.dian.mmall.service;

import java.util.List;

import com.dian.mmall.pojo.tupian.Picture;

public interface IPictureService {
    //上传成功落库状态是1
	int createPicture(Picture picture1);
   //更新前检查是否有
	Picture selectPicture(Picture picture1);
	//更新为删除
	void updatePicture(long id);
	//更新为使用
	void updatePictureUse(long id);
	Picture selectPictureBYid(long id);

}
