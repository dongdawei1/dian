package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.visitor.functions.If;
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
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.service.impl.UserServiceImpl;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

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
	   params.put("authentiCationStatus",1);
	   params.put("welfareStatus",4);
	   SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
   	   String timeString=formatter.format(new Date());
	   params.put("createTime",timeString);
	   params.put("updateTime",timeString);
	   params.put("termOfValidity",DateTimeUtil.a_few_days_later(30).getMsg());
	  System.out.print(params.toString());
	 // isPublishContact
	  
	   ReleaseWelfare releaseWelfare=(ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class,params);
	 	//{result=true, message=验证通过} 返回结果
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(releaseWelfare);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		int count=releaseWelfareMapper.create_position(releaseWelfare); 
	 		System.out.println(releaseWelfare.toString());
	 	}else if((boolean)checknullMap.get("result")==false) {
	 		return ServerResponse.createByErrorMessage((String)checknullMap.get("message"));
	 	}else {
	 		return ServerResponse.createByErrorMessage("系统异常稍后重试");
	 	}
	
	  return ServerResponse.createBySuccessMessage(ResponseMessage.BianJiChengGong.getMessage());
	 
		
	}

   //管理员获取全部待审批的发布职位
	@Override
	public ServerResponse<Object> getReleaseWelfareAll(Map<String, Object> params) {
		String currentPage_string= params.get("currentPage").toString().trim() ;    
		String pageSize_string= params.get("pageSize").toString().trim() ;    
		int currentPage=0;
		int  pageSize=0;
	 	
	 	if(currentPage_string!=null && currentPage_string!="") {
	 		currentPage=Integer.parseInt(currentPage_string);
	 		if(currentPage<=0) {
	 			 return ServerResponse.createByErrorMessage("页数不能小于0");
	 		}
	 		
 	    } else {
 	    	 return ServerResponse.createByErrorMessage("请正确输入页数");
 	    }
	 	
	 	if(pageSize_string!=null && pageSize_string!="") {
	 		pageSize=Integer.parseInt(pageSize_string);
	 		if(pageSize<=0) {
	 			 return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
	 		}
 	    } else {
 	    	 return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
 	    }
		
	 
	 	String userName = params.get("userName").toString().trim();
	 	String contact = params.get("contact").toString().trim();
	 	
	 	Page<ReleaseWelfare> releaseWelfare_pagePage=new Page<ReleaseWelfare>();
		
	 	long zongtiaoshu=releaseWelfareMapper.getReleaseWelfarePageno(userName,contact);
		int totalno=(int) Math.ceil((float)zongtiaoshu/pageSize);
		releaseWelfare_pagePage.setTotalno(zongtiaoshu);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); //当前页
		
	    List<ReleaseWelfare> list_releaseWelfare  =	new ArrayList();
	    List<ReleaseWelfare> list_releaseWelfareall=  releaseWelfareMapper.getReleaseWelfareAll((currentPage-1)*pageSize,pageSize,userName,contact);
	    
		for(ReleaseWelfare releaseWelfare :list_releaseWelfareall) {
			releaseWelfare.setContact(EncrypDES.decryptPhone(releaseWelfare.getContact()));
			list_releaseWelfare.add(releaseWelfare);
		}

		releaseWelfare_pagePage.setDatas(list_releaseWelfare );
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
	}
	
	
	 //审核招聘
		@Override
		public ServerResponse<String> examineReleaseWelfare(User user, Map<String, Object> params) {
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
				ReleaseWelfare releaseWelfare=(ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class, params);
				resultCount=releaseWelfareMapper.examineReleaseWelfare(releaseWelfare);
				if(resultCount==0) {
					return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
				
			}else if(authentiCationStatus==3) {
				String authentiCationFailure= params.get("authentiCationFailure").toString().trim();
				if(authentiCationFailure==null ||authentiCationFailure.contentEquals("")) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
				}
				params.put("authentiCationStatus", 3);
				params.put("welfareStatus", 4);
				params.put("authentiCationFailure", authentiCationFailure);
				ReleaseWelfare releaseWelfare=(ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class, params);
				resultCount=releaseWelfareMapper.examineReleaseWelfare(releaseWelfare);
				if(resultCount==0) {
					return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
						
			}
				return	ServerResponse.createBySuccessMessage(ResponseMessage.shenpishenggong.getMessage());
			

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
			params.put("gender", "男");
		}else if(gender==2) {
			params.put("gender", "女");
		}else if(gender==3) {
			params.put("gender", "不限");
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
		System.out.println(params.toString());
		//判断邮箱和联系方式必须有一个公开email
		String email=params.get("email").toString().trim();
		if(isPublishContact==2 && (email.equals("")||email==null)) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.gongkaidianhuahuozheshuruyouxiang.getMessage());
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
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());
		for(int a=0;  a< listObj3.size();a++) {
			String welfare=".*"+listObj3.get(a)+".*";
		      boolean isMatch = Pattern.matches(welfare, string);
			 if(isMatch==false) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.fulibuhefa.getMessage());
			 }
			 stringBuffer.append(listObj3.get(a)+"/");
		}
		params.put("welfare", stringBuffer.toString());
		//校验职位类型
		stringBuffer=stringBuffer.delete(0, stringBuffer.length());
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
		}else {
			params.put("addressConsistency",1);
		}
		
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
	    String contact= params.get("contact").toString().trim();
		if(!EncrypDES.decryptPhone(realName.getContact()).equals(contact)) {
	    	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());		
		}
		params.put("contact", realName.getContact());
		//判断实名姓名
	    String consigneeName=realName.getConsigneeName();
		if(!consigneeName.equals(params.get("consigneeName").toString().trim())) {
		 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());			
		}
		}
		
		//获取用户类型
		serverResponse=SetBean.setRole(currentUser.getRole());
		//检查用户类型
	 	if(serverResponse.getStatus()!=0) {	
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());		
		}	
	 	params.put("userType", serverResponse.getMsg());
		return ServerResponse.createBySuccess(params);

	}
   //用户获取发布的职位
	@Override
	public ServerResponse<Object> get_position_list(User user, Map<String, Object> params) {
		String currentPage_string= params.get("currentPage").toString().trim() ;    
		String pageSize_string= params.get("pageSize").toString().trim() ;    
		int currentPage=0;
		int  pageSize=0;
	 	
	 	if(currentPage_string!=null && currentPage_string!="") {
	 		currentPage=Integer.parseInt(currentPage_string);
	 		if(currentPage<=0) {
	 			 return ServerResponse.createByErrorMessage("页数不能小于0");
	 		}
	 		
 	    } else {
 	    	 return ServerResponse.createByErrorMessage("请正确输入页数");
 	    }
	 	
	 	if(pageSize_string!=null && pageSize_string!="") {
	 		pageSize=Integer.parseInt(pageSize_string);
	 		if(pageSize<=0) {
	 			 return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
	 		}
 	    } else {
 	    	 return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
 	    }
		
	 
	 	String position = params.get("position").toString().trim();
	 	String getwelfareStatusString=params.get("welfareStatus").toString().trim();
	 	Integer welfareStatus = null;
	 	if(getwelfareStatusString!=null && !getwelfareStatusString.equals("")) {
	 		welfareStatus=	Integer.valueOf(getwelfareStatusString);
	 	}
	 	
	 	Page<ReleaseWelfare> releaseWelfare_pagePage=new Page<ReleaseWelfare>();
		
	 	long zongtiaoshu=releaseWelfareMapper.get_position_list_no(position,welfareStatus,user.getId());
		int totalno=(int) Math.ceil((float)zongtiaoshu/pageSize);
		releaseWelfare_pagePage.setTotalno(zongtiaoshu);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); //当前页
		
	    List<ReleaseWelfare> list_releaseWelfare  =	new ArrayList();
	    List<ReleaseWelfare> list_releaseWelfareall=  releaseWelfareMapper.get_position_list_all((currentPage-1)*pageSize,pageSize,position,welfareStatus,user.getId());
	    
		for(ReleaseWelfare releaseWelfare :list_releaseWelfareall) {
			releaseWelfare.setContact(EncrypDES.decryptPhone(releaseWelfare.getContact()));
			list_releaseWelfare.add(releaseWelfare);
		}

		releaseWelfare_pagePage.setDatas(list_releaseWelfare );
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
	}

	
	//操作列
	@Override
	public ServerResponse<String> position_operation(User user, Map<String, Object> params) {
//        data.type=type;
//        data.userId= form.userId;
//        data.id=form.id;
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
			long idLong=Long.valueOf(id);
			if(userIdLong!=user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}
			 SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			 String timeString=null;
			 int  result=0;
			if(type_int==1) {
				 timeString=formatter.format(new Date());
				result=releaseWelfareMapper.position_operation(userIdLong,idLong,type_int,timeString);
			}else if(type_int==2) {
				 timeString=DateTimeUtil.a_few_days_later(30).getMsg();
				result=releaseWelfareMapper.position_operation(userIdLong,idLong,type_int,timeString);
			}else if(type_int==3 || type_int==4 || type_int==5) {
				timeString=formatter.format(new Date());
				result=releaseWelfareMapper.position_operation(userIdLong,idLong,type_int,timeString);
			}else if(type_int==6){
				//判断非法输入
				ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
			 	//检查是否有非法输入
			 	if(serverResponse.getStatus()!=0) {	
						return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
			 	ReleaseWelfare  releaseWelfare=new ReleaseWelfare();
			 	//判断是否公开电话
				String isPublishContactString=params.get("isPublishContact").toString().trim();
				if(isPublishContactString.equals("")||isPublishContactString==null) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
				}
				int isPublishContact=Integer.valueOf(isPublishContactString);
				if(isPublishContact!=1 &&  isPublishContact!=2) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
				}
				releaseWelfare.setIsPublishContact(isPublishContact);
				//判断邮箱和联系方式必须有一个公开
				String email=params.get("email").toString().trim();
				if(isPublishContact==2 && (email.equals("")||email==null)) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.gongkaidianhuahuozheshuruyouxiang.getMessage());
				}
				releaseWelfare.setEmail(email);
			 	
				//判断实名信息是否正确
				RealName realName=realNameMapper.getRealName(userIdLong);
				if(realName!=null) {
				//判断实际工作地址和实名地址是否一致
				String addressDetailed=realName.getAddressDetailed();
				if(!addressDetailed.equals(params.get("workingAddress").toString().trim())) {
					 //2不一致1一致
					releaseWelfare.setAddressConsistency(2);
				}else {
					releaseWelfare.setAddressConsistency(1);
				}
				releaseWelfare.setWorkingAddress(params.get("workingAddress").toString().trim());
				releaseWelfare.setDescribeOne(params.get("describeOne").toString().trim());
				releaseWelfare.setId(idLong);
				releaseWelfare.setUserId(userIdLong);
				timeString=formatter.format(new Date());
				releaseWelfare.setUpdateTime(timeString);
				result=releaseWelfareMapper.position_operation_edit(releaseWelfare);
				}
				}else {
				 return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
			}
			
			if(result==0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.caozuoshibai.getMessage());
			}
			
			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
			
			
		}else {
			 return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		
		
	}
   
	


}
