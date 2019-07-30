package com.dian.mmall.util;

import java.util.ArrayList;
import java.util.List;

import com.dian.mmall.pojo.tupian.Picture;

public class PictureUtil {

	public static String listToString(String pictureString) {
		
		List<Picture> pictures=JsonUtil.string2Obj(pictureString, List.class,Picture.class);
		List<Picture> returnPictures=new ArrayList<Picture>();
		for(int i=0;i<pictures.size();i++) {
			Picture picture=pictures.get(i);
			picture.setId(-1);
			picture.setPictureName(null);
			picture.setTocken(null);
			picture.setUserId(-1);
			returnPictures.add(picture);
		}
		return JsonUtil.obj2String(returnPictures);
	}
	
}
