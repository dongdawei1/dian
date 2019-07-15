package com.dian.mmall.service.impl.releaseimpl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("resumeService")
public class ResumeServiceImp implements ResumeService{
	@Autowired
	private ResumeMapper resumeMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;
	//创建简历
	public ServerResponse<String> create_resume(User user, Map<String, Object> params) {
	System.out.println(params.toString());
		String typeString=params.get("type").toString().trim();
		System.out.println(typeString);
		if(typeString==null || typeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		int type=Integer.valueOf(typeString);
		if(type!=1 && type!=2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		
		ServerResponse<Object> serverResponse=check_position(user, params,type);
	   if(serverResponse.getStatus()!=0) {
		   return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	   }
	   Map<String,Object> map=(Map<String, Object>) serverResponse.getData();
	   map.put("authentiCationStatus",1);
	   map.put("welfareStatus",4);
	 
	   
		  
	   Resume resume=(Resume) BeanMapConvertUtil.convertMap(Resume.class,map);
	 	//{result=true, message=验证通过} 返回结果
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(resume);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		int count=0;
	 		if(type==1) {
	 		count=resumeMapper.create_resume(resume); 
	 		}else {
	 		count=resumeMapper.update_resume(resume); 	
	 		}
	 		
	 		if(count==0) {
	 		  return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
	 	   }
	 		return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
	 	}else if((boolean)checknullMap.get("result")==false) {
	 		return ServerResponse.createByErrorMessage((String)checknullMap.get("message"));
	 	}else {
	 		return ServerResponse.createByErrorMessage("系统异常稍后重试");
	 	}
		
	}
//各种校验
public  ServerResponse<Object> check_position(User currentUser, Map<String, Object> params,int type) {
	
	//判断用户id与	tocken是否一致
	long userId=Long.valueOf(params.get("userId").toString().trim());
	if(userId!=currentUser.getId()) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
	}
	Map<String, Object> map=new HashMap<String, Object>();
	map.put("userId", userId);
	
	String createTime=DateTimeUtil.dateToAll();
	//判断是否超过可以发布的总数
	Resume resume= resumeMapper.selectResumeById(userId);
	 if(type==1) {
		if(resume!=null) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.yifabuguojianli.getMessage());
		}
		 map.put("updateTime",createTime);
		 map.put("createTime",createTime);
	 }else {
			if(resume==null) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.weifabuguojianli.getMessage());
			}
			 map.put("updateTime",createTime);
			 map.put("createTime",resume.getCreateTime());
			 map.put("id",resume.getId());
	 }
	 
    //判断是否实名
		if(currentUser.getIsAuthentication()!=2) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

	//判断非法输入
		ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
	//转男女code为字符串
	String genString=params.get("gender").toString().trim();
	if(genString.equals("")||genString==null) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
	}
	int  gender=Integer.valueOf(genString);
	if(gender==1) {
		map.put("gender", "男");
	}else if(gender==2) {
		map.put("gender", "女");
	}else {
		 return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
	}
	
	
	//判断是否公开电话
	String isPublishContactString=params.get("isPublishContact").toString().trim();
	if(isPublishContactString.equals("")||isPublishContactString==null) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
	}
	int isPublishContact=Integer.valueOf(isPublishContactString);
	if(isPublishContact!=1 &&  isPublishContact!=2) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
	}
	map.put("isPublishContact", isPublishContact);
	//判断邮箱和联系方式必须有一个公开email
	String email=params.get("email").toString().trim();
	if(isPublishContact==2 && (email.equals("")||email==null)) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.gongkaidianhuahuozheshuruyouxiang.getMessage());
	}
	

	//校验职位类型
	StringBuffer stringBuffer=new StringBuffer();
	Position[] position=Position.values();
	for(int a1=0;  a1< position.length;a1++) {
		stringBuffer.append(position[a1].getPositionType());
	}
	String string=stringBuffer.toString();
	String position_par=params.get("position").toString().trim() ;
	if( position_par==null || position_par.equals("")) {
		return ServerResponse.createByErrorMessage(ResponseMessage.zhiweikong.getMessage());
	}
	String position_check=".*"+position_par+".*";
    boolean isMatch = Pattern.matches(position_check, string);
	if(isMatch==false) {
		return ServerResponse.createByErrorMessage(ResponseMessage.zhiweibuhefa.getMessage());
	}
	map.put("position", position_par);
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
	map.put("salary", salary_par);
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
	map.put("education", education_par);
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
	map.put("experience", experience_par);
	//年龄范围	
	String age_par=params.get("age").toString().trim() ;
    if(age_par.equals("")||age_par==null) {
    	return ServerResponse.createByErrorMessage(ResponseMessage.nianlikong.getMessage());
    }
     int age=Integer.valueOf(age_par);
     if(age<18 ||age>60) {
    	 return ServerResponse.createByErrorMessage(ResponseMessage.nianlingbuhefa.getMessage());
     }
     map.put("age", age);
	//判断实名信息是否正确
	RealName realName=realNameMapper.getRealName(currentUser.getId());
	if(realName!=null) {
    
	//判断电话
    String contact= params.get("contact").toString().trim();
	if(!EncrypDES.decryptPhone(realName.getContact()).equals(contact)) {
    	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());		
	}
	map.put("contact", realName.getContact());
	//判断实名姓名
    String consigneeName=realName.getConsigneeName();
	if(!consigneeName.equals(params.get("consigneeName").toString().trim())) {
	 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());			
	}
	map.put("consigneeName", consigneeName);
	}else {
		return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());			
	}
	
	//获取用户类型
	serverResponse=SetBean.setRole(currentUser.getRole());
	//检查用户类型
 	if(serverResponse.getStatus()!=0) {	
		return ServerResponse.createByErrorMessage(serverResponse.getMsg());		
	}	
 	map.put("userType", serverResponse.getMsg());
 	
    int	provinces_id=0;
    int	city_id=0;
    int	district_county_id=0;
    String city=null;
    
    String provinces_idString=params.get("provinces_id").toString().trim();
    String city_idString=params.get("city_id").toString().trim();
    String district_county_idString=params.get("district_county_id").toString().trim();
  if(provinces_idString!=null && !provinces_idString.equals("") && city_idString!=null && !city_idString.equals("") && district_county_idString!=null && !district_county_idString.equals("")) {
	    provinces_id=Integer.valueOf(provinces_idString);
		city_id=Integer.valueOf(city_idString);
	    district_county_id=Integer.valueOf(district_county_idString); 
   	 city=cityMapper.checkeCity(provinces_id,city_id,district_county_id);	
   	 if( city==null) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
   	 map.put("detailed", city);
  }else {
	  return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
  }
  
   provinces_idString=params.get("provinces_id1").toString().trim();
   city_idString=params.get("city_id1").toString().trim();
   district_county_idString=params.get("district_county_id1").toString().trim();
if(provinces_idString!=null && !provinces_idString.equals("") && city_idString!=null && !city_idString.equals("") && district_county_idString!=null && !district_county_idString.equals("")) {
	    provinces_id=Integer.valueOf(provinces_idString);
		city_id=Integer.valueOf(city_idString);
	    district_county_id=Integer.valueOf(district_county_idString); 
 	 city=cityMapper.checkeCity(provinces_id,city_id,district_county_id);	
 	 if( city==null) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
 	 map.put("addressDetailed", city);
}else {
	  return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
}
   
    map.put("describeOne", params.get("describeOne").toString().trim());
    map.put("email", params.get("email").toString().trim());
    
	return ServerResponse.createBySuccess(map);

}
@Override
public ServerResponse<Object> select_resume_by_id(long userId) {
	//判断是否超过可以发布的总数
	Resume resume= resumeMapper.selectResumeById(userId);
		if(resume==null) {
			 return ServerResponse.createBySuccess();
		}
		resume.setContact(EncrypDES.decryptPhone(resume.getContact()));
		return ServerResponse.createBySuccess(resume);
}
@Override
public ServerResponse<String> operation_resume(User user, Map<String, Object> params) {
	String type=params.get("type").toString().trim();
	String userId=params.get("userId").toString().trim();
	String id=params.get("id").toString().trim();
	if(type!=null && !type.equals("") && userId!=null && !userId.equals("")
			&& id!=null && !id.equals("") ) {
		int type_int=Integer.valueOf(type);
		if(type_int<1 || type_int>6) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		long userIdLong=Long.valueOf(userId);
		Resume resume= resumeMapper.selectResumeById(userIdLong);
		if(resume==null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		long idLong=Long.valueOf(id);
		if(userIdLong!=user.getId()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
		}
		String timeString=DateTimeUtil.dateToAll();
		String a_few_days_later=DateTimeUtil.a_few_days_later(90).getMsg();
		int result=0;
		if(type_int==1) {
			resume.setUpdateTime(timeString);
			resume.setTermOfValidity(a_few_days_later);
			result=resumeMapper.operation_resume(type_int,resume);
		}else if(type_int==2) {
			resume.setUpdateTime(timeString);
			resume.setWelfareStatus(2);
			result=resumeMapper.operation_resume(type_int,resume);
		}else if(type_int==3) {
			resume.setUpdateTime(timeString);
			resume.setWelfareStatus(3);
			result=resumeMapper.operation_resume(type_int,resume);
		}else if(type_int==4) {
			resume.setUpdateTime(timeString);
			resume.setTermOfValidity(a_few_days_later);
			resume.setWelfareStatus(1);
			result=resumeMapper.operation_resume(type_int,resume);
		}else {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
	if(result>0) {
		return ServerResponse.createBySuccess();
	}
	}
	return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
}
}
