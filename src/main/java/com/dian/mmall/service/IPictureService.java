package com.dian.mmall.service;

import java.util.List;

import com.dian.mmall.pojo.Picture;

public interface IPictureService {
    //上传成功落库状态是1
	int createPicture(Picture picture1);
   //更新前检查是否有
	Picture selectPicture(Picture picture1);
	void updatePicture(Picture picture1);

}
