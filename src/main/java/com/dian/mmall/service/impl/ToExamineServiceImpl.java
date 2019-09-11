package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.EquipmentMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.dao.releaseDao.WineAndTablewareMapper;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
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
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	@Autowired
	private EquipmentMapper equipmentMapper;
	@Autowired
	private WineAndTablewareMapper wineAndTablewareMapper;
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
	
	String tabuleTypeString=params.get("tabuleType").toString().trim();	
	if(tabuleTypeString==null ||tabuleTypeString.contentEquals("")) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
	
	int tabuleType=Integer.valueOf(tabuleTypeString);
	params.remove("tabuleType");
	//先判断是否需要审批审批类型
	if(tabuleType==18 || tabuleType==7) {	
		String isServiceTypeString=params.get("isServiceType").toString().trim();	
		if(isServiceTypeString==null ||isServiceTypeString.contentEquals("")) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.fabuleixinbixuan.getMessage());
			}    
		int isServiceType=Integer.valueOf(isServiceTypeString);
		
		String serviceTypeIdString=params.get("serviceTypeId").toString().trim();
		if(serviceTypeIdString==null ||serviceTypeIdString.contentEquals("")) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.shangpinfuwuleixingidnull.getMessage());
			} 
		long serviceTypeId=Integer.valueOf(serviceTypeIdString);
		if(authentiCationStatus==2) {
			if(isServiceType==3) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.shenhefuwuleixin.getMessage());
			}
		}	
		
		
		
		int result=0;
		//不管发布状态是否通过，只要服务类型通过就去更新服务类型库
		if(serviceTypeId!=-1) {
		if( (isServiceType==2 || isServiceType==3) ) {	   
			ServiceType serviceType=new ServiceType();
			serviceType.setId(serviceTypeId);
			serviceType.setAuthentiCationStatus(isServiceType);
			serviceType.setExamineName(user.getUsername());
			serviceType.setExamineTime(formatter.format(new Date()));
			result=serviceTypeMapper.updatebyId(serviceType);			
			if(result==0) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.shangpinfuwuleixluokushibai.getMessage());
			}else {
				serviceTypeMapper.deletebyId(serviceType);
			}
		
		}else {
			result=serviceTypeMapper.selectbyId(serviceTypeId);
			if(result==0) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.shangpinleixinchaxunshibai.getMessage());
			}
		}
		}else {
			if( isServiceType==1 ||isServiceType==2 || authentiCationStatus==2 ) {
					return	ServerResponse.createByErrorMessage(ResponseMessage.fuwuleixinIdcuowu.getMessage());	
			}
		}
	}
	
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
	
	
	params.remove("serviceTypeId");
	params.remove("isServiceType");
	
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
	}else if(tabuleType==18) {	
		//正常落库即可  和菜 共用一个
		Equipment equipment=(Equipment) BeanMapConvertUtil.convertMap(Equipment.class, params);
		resultCount=equipmentMapper.examineEquipment(equipment);
	}else if(tabuleType==7) {	
		//正常落库即可  和菜 共用一个
		WineAndTableware wineAndTableware=(WineAndTableware) BeanMapConvertUtil.convertMap(WineAndTableware.class, params);
		resultCount=wineAndTablewareMapper.examineEquipment(wineAndTableware);
	}
	
	
	
	if(resultCount==0) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
	}
		return	ServerResponse.createBySuccessMessage(ResponseMessage.shenpishenggong.getMessage());
	}

}


