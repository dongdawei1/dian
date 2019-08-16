package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.ToExamineService;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("toExamineService")
public class ToExamineServiceImpl implements ToExamineService {
	@Autowired 
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;
	@Autowired
	private ReleaseWelfareMapper releaseWelfareMapper;
	@Autowired
	private ResumeMapper resumeMapper;
	@Autowired
	private RentMapper rentMapper;
	//全部审核
	public ServerResponse<String> examineAll(User user, Map<String, Object> params) {
	String	userId =params.get("userId").toString().trim();	
	String  id=params.get("id").toString().trim();	
	if(userId==null ||userId.contentEquals("") ||id==null ||id.contentEquals("") ) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.yonghuidhuoshenpixiangbucunzi.getMessage());
		}
	int authentiCationStatus=Integer.valueOf(params.get("authentiCationStatus").toString().trim());
	if(authentiCationStatus!=2 && authentiCationStatus!=3) {
		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
	}
	
	long user_id=Long.valueOf(userId);
	long releaseWelfareId=Long.valueOf(id);
	params.put("userId", user_id);
	params.put("id", releaseWelfareId);
	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
	params.put("examineTime", formatter.format(new Date()));
	params.put("examineName", user.getUsername());

	int resultCount=0;
	if(authentiCationStatus==2) {
		params.put("welfareStatus", 1);
		params.put("authentiCationStatus", 2);
	
		
	}else if(authentiCationStatus==3) {
		String authentiCationFailure= params.get("authentiCationFailure").toString().trim();
		if(authentiCationFailure==null ||authentiCationFailure.contentEquals("")) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
		}
		params.put("authentiCationStatus", 3);
		params.put("welfareStatus", 4);
		params.put("authentiCationFailure", authentiCationFailure);				
	}
	
	String tabuleTypeString=params.get("tabuleType").toString().trim();	
	if(tabuleTypeString==null ||tabuleTypeString.contentEquals("")) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
	
	int tabuleType=Integer.valueOf(tabuleTypeString);
	params.remove("tabuleType");
	
	
	if(tabuleType==13) {
	MenuAndRenovationAndPestControl releaseWelfare=(MenuAndRenovationAndPestControl) BeanMapConvertUtil.convertMap(MenuAndRenovationAndPestControl.class, params);
	resultCount=menuAndRenovationAndPestControlMapper.examineMrp(releaseWelfare);	
	}else if(tabuleType==14){
		Rent rent=(Rent) BeanMapConvertUtil.convertMap(Rent.class, params);
		resultCount=rentMapper.examineResume(rent);
	}
	else if(tabuleType==30){
	ReleaseWelfare releaseWelfare=(ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class, params);
	resultCount=releaseWelfareMapper.examineReleaseWelfare(releaseWelfare);
	}else if(tabuleType==31){
		Resume resume=(Resume) BeanMapConvertUtil.convertMap(Resume.class, params);
		resultCount=resumeMapper.examineResume(resume);
	}
	
	
	
	if(resultCount==0) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
	}
		return	ServerResponse.createBySuccessMessage(ResponseMessage.shenpishenggong.getMessage());
	}

}


