package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ReleaseCount;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.zhiwei.Age;
import com.dian.mmall.common.zhiwei.Education;
import com.dian.mmall.common.zhiwei.Experience;
import com.dian.mmall.common.zhiwei.IntroductoryAward;
import com.dian.mmall.common.zhiwei.Position;
import com.dian.mmall.common.zhiwei.Salary;
import com.dian.mmall.common.zhiwei.Welfare;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.service.impl.UserServiceImpl;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("releaseWelfareService")
public class ReleaseWelfareServiceImpl implements ReleaseWelfareService{
	
	@Autowired
	private ReleaseWelfareMapper releaseWelfareMapper;
	@Autowired
	private RealNameMapper realNameMapper;

	public ServerResponse<String> create_position(User currentUser, Map<String, Object> params) {

		ServerResponse<Object> serverResponse=check_position(currentUser, params);
	   if(serverResponse.getStatus()!=0) {
		   return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	   }
	   params=(Map<String, Object>) serverResponse.getData();
	   

//		return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
//	   
	   params.put("authentiCationStatus",1);
	   params.put("welfareStatus",4);
	   SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
   	   String timeString=formatter.format(new Date());
	   params.put("createTime",timeString);
	   params.put("updateTime",timeString);
	  
	   ReleaseWelfare releaseWelfare=(ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class,params);
	   
	 	//{result=true, message=验证通过} 返回结果
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(releaseWelfare);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		  System.out.println(releaseWelfare.toString());
	 	}else if((boolean)checknullMap.get("result")==false) {
	 		return ServerResponse.createByErrorMessage((String)checknullMap.get("message"));
	 	}else {
	 		return ServerResponse.createByErrorMessage("系统异常稍后重试");
	 	}
	
	  return null;
	 
		
	}

	
	
	//各种校验
	public  ServerResponse<Object> check_position(User currentUser, Map<String, Object> params) {
		//判断用户id与	tocken是否一致
		long userId=Long.valueOf(params.get("userId").toString().trim());
		String newusernamr = params.get("userName").toString().trim() ;
		if(!newusernamr.equals(currentUser.getUsername())  || userId!=currentUser.getId()) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
		}
		//判断是否超过可以发布的总数
			if(releaseWelfareMapper.countReleaseWelfare(userId)>=ReleaseCount.create_position.getCount()) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.fabuzongshudayu.getMessage()+ReleaseCount.create_position.getCount());
			}
	    //判断是否实名
			if(currentUser.getIsAuthentication()!=2) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
			}
		//判断是否有权限
			int role=currentUser.getRole();
			if(role!=1 && role!=2 && role!=5) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
			}
		//判断非法输入
			ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
		 	//检查是否有非法输入
		 	if(serverResponse.getStatus()!=0) {	
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
		//转男女code为字符串
		int  gender=Integer.valueOf(params.get("gender").toString().trim());
		if(gender==1) {
			params.put("gender", "男");
		}else if(gender==2) {
			params.put("gender", "女");
		}else if(gender==3) {
			params.put("gender", "不限");
		}else {
			 return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
		}
		//检查福利输入是否合法
		List<String> listObj3	= JsonUtil.list2Obj((ArrayList<String>) params.get("welfare"),List.class,String.class);
		if(listObj3.isEmpty()) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.fulibunengweikong.getMessage());
		}
		
		StringBuffer stringBuffer=new StringBuffer();
		 Welfare[] strWelfare= Welfare.values();
			for(int a1=0;  a1< strWelfare.length;a1++) {
				stringBuffer.append(strWelfare[a1].getWelfare());
			}
			
		String string=stringBuffer.toString();
		for(int a=0;  a< listObj3.size();a++) {
			String welfare=".*"+listObj3.get(a)+".*";
		      boolean isMatch = Pattern.matches(welfare, string);
			 if(isMatch==false) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.fulibuhefa.getMessage());
			 }
		}
		//校验职位类型
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		Position[] position=Position.values();
		for(int a1=0;  a1< position.length;a1++) {
			stringBuffer.append(position[a1].getPositionType());
		}
		string=stringBuffer.toString();
		String position_par=params.get("position").toString().trim() ;
		if( position_par==null || position_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhiweikong.getMessage());
		}
		String position_check=".*"+position_par+".*";
	    boolean isMatch = Pattern.matches(position_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhiweibuhefa.getMessage());
		}
	    
		//校验工资
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		Salary[] salary=Salary.values();
		for(int a1=0;  a1< salary.length;a1++) {
			stringBuffer.append(salary[a1].getSalary());
		}
		string=stringBuffer.toString();
		
		String salary_par=params.get("salary").toString().trim() ;
		if( salary_par==null || salary_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongzikong.getMessage());
		}
		String salary_check=".*"+salary_par+".*";
	    isMatch = Pattern.matches(salary_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongzibuhefa.getMessage());
		}
		//学历
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		Education[] education=Education.values();
		for(int a1=0;  a1< education.length;a1++) {
			stringBuffer.append(education[a1].getEducation());
		}
		string=stringBuffer.toString();
		
		String education_par=params.get("education").toString().trim() ;
		if( education_par==null || education_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xuelibuhefa.getMessage());
		}
		String education_check=".*"+education_par+".*";
	    isMatch = Pattern.matches(education_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xuelibuhefa.getMessage());
		}
		//工作年限
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		Experience[] experience=Experience.values();
		for(int a1=0;  a1< experience.length;a1++) {
			stringBuffer.append(experience[a1].getExperience());
		}
		string=stringBuffer.toString();
		
		String experience_par=params.get("experience").toString().trim() ;
		if( experience_par==null || experience_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianxiankong.getMessage());
		}
		String experience_check=".*"+experience_par+".*";
	    isMatch = Pattern.matches(experience_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianxianbuhefa.getMessage());
		}
		//年龄范围
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		Age[] age=Age.values();
		for(int a1=0;  a1< age.length;a1++) {
			stringBuffer.append(age[a1].getAge());
		}
		string=stringBuffer.toString();
		
		String age_par=params.get("age").toString().trim() ;
		if( age_par==null || age_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlikong.getMessage());
		}
		String age_check=".*"+age_par+".*";
	    isMatch = Pattern.matches(age_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlingbuhefa.getMessage());
		}
		//介绍奖励
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());;
		IntroductoryAward[] introductoryAward=IntroductoryAward.values();
		for(int a1=0;  a1< introductoryAward.length;a1++) {
			stringBuffer.append(introductoryAward[a1].getIntroductoryAward());
		}
		string=stringBuffer.toString();
		
		String introductoryAward_par=params.get("introductoryAward").toString().trim() ;
		if( introductoryAward_par==null || introductoryAward_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jianglikong.getMessage());
		}
		String introductoryAward_check=".*"+introductoryAward_par+".*";
	    isMatch = Pattern.matches(introductoryAward_check, string);
		if(isMatch==false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jianglibuhefa.getMessage());
		}
		
		//判断实名信息是否正确
		RealName realName=realNameMapper.getRealName(currentUser.getId());
		if(realName!=null) {
		//判断实际工作地址和实名地址是否一致
		String addressDetailed=realName.getAddressDetailed();
		if(!addressDetailed.equals(params.get("workingAddress").toString().trim())) {
			params.put("addressConsistency",2); //2不一致1一致
		}
		params.put("addressConsistency",1);
		//判断实名地址
		if(!addressDetailed.equals(params.get("addressDetailed").toString().trim())) {
	    	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
	    }
		//	判断省市区
		String detailed=params.get("detailed").toString().trim();
	    if(!detailed.equals(realName.getDetailed())) {
	    	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
	    }
		//判断电话
	    String contact=EncrypDES.decryptPhone(realName.getContact());
		if(!contact.equals(params.get("contact").toString().trim())) {
	    	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());		
		}
		//判断实名姓名
	    String consigneeName=realName.getConsigneeName();
		if(!consigneeName.equals(params.get("consigneeName").toString().trim())) {
		 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());			
		}
		}
		
		return ServerResponse.createBySuccess(params);

	}
	
}
