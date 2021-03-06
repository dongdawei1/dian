package com.dian.mmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.service.IPictureService;
@Service("iPictureService")
public class IPictureServiceImp implements IPictureService {

	 
	  @Autowired
	    private PictureMapper pictureMapper;

	@Override
	public int createPicture(Picture picture1) {
		// TODO Auto-generated method stub
		return  pictureMapper.createPicture(picture1);
	}

	@Override
	public Picture selectPicture(Picture picture1) {
		// TODO Auto-generated method stub
		return pictureMapper.selectPicture( picture1);
	}

	@Override
	public void updatePicture(long id) {
		pictureMapper.updatePicture(id);
		
	}

	@Override
	public void updatePictureUse(long id) {
		pictureMapper.updatePictureUse( id);
		
	}

	@Override
	public Picture selectPictureBYid(long id) {
		// TODO Auto-generated method stub
		return pictureMapper.selectPictureBYid(id);
	}
	  
	  
}
