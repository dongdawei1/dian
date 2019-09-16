package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.ServiceTypeService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.LegalCheck;
@Service("serviceTypeServic")
public class ServiceTypeServiceImpl implements ServiceTypeService {
   @Autowired
   private ServiceTypeMapper serviceTypeMapper;
	
	@Override
	public ServerResponse<String> create_serviceType(User user, Map<String, Object> params) {
		return createServiceType(user, params,1);  //待审批
	}
	@Override
	public ServerResponse<String> admin_create_serviceType(User user, Map<String, Object> params) {
		
		return createServiceType(user, params,2); //管理员加入
	}
	@Override
	public ServerResponse<Object> get_serviceType(Integer releaseType,String serviceType,Long userId) {
		if(releaseType>0&& releaseType<200) {
			
			if(serviceType!=null && !serviceType.equals("")) {
				serviceType="%"+serviceType+"%";
			}
			
			return ServerResponse.createBySuccess(serviceTypeMapper.get_serviceType(releaseType,serviceType,userId));
		}else {
	 		  return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
	}

	
   
	
	public ServerResponse<String> createServiceType(User user, Map<String, Object> params,int authentiCationStatus) {
		
		//判断非法输入
		ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
	 	
	 	serverResponse= LegalCheck.legalCheckServiceTypeName(params.get("serviceTypeName").toString());
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
	 	
	 	String releaseTypeString=params.get("releaseType").toString().trim();
	 	if(releaseTypeString==null || releaseTypeString.equals("")) {
	 		return ServerResponse.createByErrorMessage(ResponseMessage.caidanIDweikong.getMessage());
	 	}
	 	Integer releaseType=0;
	 	try {
	 		releaseType=Integer.valueOf(releaseTypeString);	
	 		if(releaseType==18 || releaseType==33 ||releaseType==34) {
	 		releaseType=18;
	 		}else if(releaseType==4 || releaseType==5 ||releaseType==6||releaseType==29 ||releaseType==9 ||releaseType==11
	 				||releaseType==7 ||releaseType==8 ||releaseType==101||releaseType==102||releaseType==103||releaseType==104){
	 			releaseType=Integer.valueOf(releaseTypeString);	
	 		}
	 		
	 		//TODO 新加就在这里设置
	 		else {
	 		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
	 		}
	 		
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
	 	
	 	String serviceTypeName= serverResponse.getMsg();
	 	
	 	ServerResponse<Object> serverResponse2=get_serviceType(releaseType, null, user.getId());
	 	if(serverResponse.getStatus()!=0) {	
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunyiyouleixshibai.getMessage());
		}
	 	List<String> service_type_list=(List<String>) serverResponse2.getData();
	 	if(service_type_list.size()>0) {
	 		for(int a=0;a<service_type_list.size();a++) {
	 			if(serviceTypeName.equals(service_type_list.get(a))) {
	 				 return ServerResponse.createByErrorMessage(ResponseMessage.leixingyicunzai.getMessage());
	 			}
	 		}
	 	}
	 		 	
	 	ServiceType serviceType=new ServiceType();
	 	serviceType.setReleaseType(releaseType);
	 	serviceType.setServiceTypeName(serviceTypeName);
	 	serviceType.setAuthentiCationStatus(authentiCationStatus);
	 	serviceType.setCreateUserId(user.getId());
	 	
	 	if(authentiCationStatus==2) {
	 	serviceType.setExamineName(user.getUsername());
	 	 SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		 String timeString=formatter.format(new Date());
	 	serviceType.setExamineTime(timeString);
	 	}
		//{result=true, message=验证通过} 返回结果
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(serviceType);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		int	count=serviceTypeMapper.create_serviceType(serviceType);
	 		if(count==0) {
	 		  return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
	 	    }
	 		return ServerResponse.createBySuccess();
	 	}else if((boolean)checknullMap.get("result")==false) {
	 		return ServerResponse.createByErrorMessage((String)checknullMap.get("message"));
	 	}else {
	 		return ServerResponse.createByErrorMessage("系统异常稍后重试");
	 	}
	}
}
