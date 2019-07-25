package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.service.release.MenuAndRenovationAndPestControlService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("menuAndRenovationAndPestControlService")
public class MenuAndRenovationAndPestControlServiceImpl implements MenuAndRenovationAndPestControlService {
	@Autowired 
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	//创建
	public ServerResponse<String> create_menuAndRenovationAndPestControl(User user, Map<String, Object> params) {
	

	ServerResponse<Object> serverResponse=check_evaluate(user, params,1);
	   if(serverResponse.getStatus()!=0) {
		   return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	   }
	   Map<String,Object> map=(Map<String, Object>) serverResponse.getData();
       map.put("authentiCationStatus", 1);
       map.put("welfareStatus", 4);
  
       MenuAndRenovationAndPestControl menuAndRenovationAndPestControl=(MenuAndRenovationAndPestControl) BeanMapConvertUtil.convertMap(MenuAndRenovationAndPestControl.class,map);
	 	//{result=true, message=验证通过} 返回结果
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(menuAndRenovationAndPestControl);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		int	count=menuAndRenovationAndPestControlMapper.create_menuAndRenovationAndPestControl(menuAndRenovationAndPestControl);
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
public  ServerResponse<Object> check_evaluate(User currentUser, Map<String, Object> params,int type) {
	//type 1 新建，2为编辑
	//判断用户id与	tocken是否一致
	long userId=Long.valueOf(params.get("userId").toString().trim());
	if(userId!=currentUser.getId()) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
	}
	Map<String, Object> map=new HashMap<String, Object>();
	map.put("userId", userId);
	
	String createTime=DateTimeUtil.dateToAll();
	//判断是否超过可以发布的总数
		 
			String releaseTypeString=params.get("releaseType").toString().trim();	
			if(releaseTypeString==null || releaseTypeString.equals("")) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
			}
			int releaseType=Integer.valueOf(releaseTypeString);
			if(releaseType!=13 &&releaseType!=17 &&releaseType!=19) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
			}
			int count=0;
			if(type==1) {  //TODO新建时才检查总数
			 count =menuAndRenovationAndPestControlMapper.countNum(releaseType,userId);
		      if(count>5) {
		    	 return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
		    }}
		    map.put("releaseType",releaseType);
		    map.put("updateTime",createTime);
		    map.put("createTime",createTime);

	 
  //判断是否实名
		if(currentUser.getIsAuthentication()!=2) {
			 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		
		//图片
		List<Picture> listObj3	= JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"),List.class,Picture.class);
		int list_size=listObj3.size();
		//把getUse_status()==1 放到这个集合中
		List<Picture> listObj4=new ArrayList<Picture>();
		
		int getNum=PictureNum.miechongzhuangxiu.getNum();	
		if(list_size>0) {
			 count=0;
			for(int a=0;a<list_size;a++) {
				if(listObj3.get(a).getUse_status()==1) {
					listObj4.add(listObj3.get(a));
					count+=1;
				}
				}
				if(count>getNum) {
					//判断没有删除的图片是否大于规定
					return ServerResponse.createByErrorMessage("图片数量不能超过 "+getNum+"个");
				}
				if(count==0) {
					return ServerResponse.createByErrorMessage("图片不能为空");
				}	
			}else {
				return ServerResponse.createByErrorMessage("图片不能为空");
			}
		
		 map.put("pictureUrl",JsonUtil.obj2StringPretty(listObj4));
		 
	//判断非法输入
		ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
	
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
	String companyName= realName.getCompanyName();
	if(!companyName.equals(params.get("companyName").toString().trim())) {
	 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());	
	 	}		
	map.put("companyName", companyName);
	
	String detailed=realName.getDetailed();
	if(!detailed.equals(params.get("detailed").toString().trim())) {
	 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());	
	 	}		
	map.put("detailed", detailed);
	
   String addressDetailed=realName.getAddressDetailed();
	if(!addressDetailed.equals(params.get("addressDetailed").toString().trim())) {
	 	return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());	
	 	}		
	map.put("addressDetailed", addressDetailed);
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
	
	
     map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
     String serviceDetailed=params.get("serviceDetailed").toString().trim();
      if(!serviceDetailed.equals("全市") &&!serviceDetailed.equals("来电确认")) {
    	  return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());			  
      }
	 map.put("serviceDetailed", serviceDetailed);
	 try {
		Integer.parseInt(params.get("startPrice").toString().trim());
	} catch (Exception e) {
		return ServerResponse.createByErrorMessage(ResponseMessage.qibujiagebuhefa.getMessage());			  
	}
	 
	 map.put("startPrice", params.get("startPrice").toString().trim());
	 map.put("remarks", params.get("remarks").toString().trim());	 
	 map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
	 map.put("releaseTitle", params.get("releaseTitle").toString().trim()); 
	return ServerResponse.createBySuccess(map);

}

//用户获取自己发布
@Override
public ServerResponse<Object> get_usermrp_list(User user, Map<String, Object> params) {
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
	
    
 	String releaseTypeString = params.get("releaseType").toString().trim();
 	Integer releaseType=null;
 	if(releaseTypeString!=null && !releaseTypeString.equals("")) {
 		releaseType =	Integer.valueOf(releaseTypeString);
 	}
 	
 	
 	String getwelfareStatusString=params.get("welfareStatus").toString().trim();
 	Integer welfareStatus = null;
 	if(getwelfareStatusString!=null && !getwelfareStatusString.equals("")) {
 		welfareStatus=	Integer.valueOf(getwelfareStatusString);
 	}
 	
 	
 	
 	Page<MenuAndRenovationAndPestControl> releaseWelfare_pagePage=new Page<MenuAndRenovationAndPestControl>();
	
 	long zongtiaoshu=menuAndRenovationAndPestControlMapper.get_usermrp_list_no(releaseType,welfareStatus,user.getId());
	
	releaseWelfare_pagePage.setTotalno(zongtiaoshu);
	releaseWelfare_pagePage.setPageSize(pageSize);
	releaseWelfare_pagePage.setCurrentPage(currentPage); //当前页
	
    List<MenuAndRenovationAndPestControl> list_releaseWelfare  =	new ArrayList();
    List<MenuAndRenovationAndPestControl> list_releaseWelfareall=  menuAndRenovationAndPestControlMapper.get_usermpr_list_all((currentPage-1)*pageSize,pageSize,releaseType,welfareStatus,user.getId());
    
	for(MenuAndRenovationAndPestControl releaseWelfare :list_releaseWelfareall) {
		releaseWelfare.setContact(EncrypDES.decryptPhone(releaseWelfare.getContact()));
		list_releaseWelfare.add(releaseWelfare);
	}

	releaseWelfare_pagePage.setDatas(list_releaseWelfare );
	return ServerResponse.createBySuccess(releaseWelfare_pagePage);
}

//审核分页
@Override
public ServerResponse<Object> getmrpAll(Map<String, Object> params) {
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
	
    
 	String releaseTypeString = params.get("releaseType").toString().trim();
 	Integer releaseType=null;
 	if(releaseTypeString!=null && !releaseTypeString.equals("")) {
 		releaseType =	Integer.valueOf(releaseTypeString);
 	}
 	
 	String contact = params.get("contact").toString().trim();
	if(contact.length()!=11 && contact!=null && !contact.equals("")) {
 		 return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
 	}
	if(contact.length()==11) {
		contact = EncrypDES.encryptPhone(contact);
	}
 	
 	
 	
 	Page<MenuAndRenovationAndPestControl> releaseWelfare_pagePage=new Page<MenuAndRenovationAndPestControl>();
	
 	long zongtiaoshu=menuAndRenovationAndPestControlMapper.get_mrp_list_no(releaseType,contact);
	
	releaseWelfare_pagePage.setTotalno(zongtiaoshu);
	releaseWelfare_pagePage.setPageSize(pageSize);
	releaseWelfare_pagePage.setCurrentPage(currentPage); //当前页
	
    List<MenuAndRenovationAndPestControl> list_releaseWelfare  =	new ArrayList();
    List<MenuAndRenovationAndPestControl> list_releaseWelfareall=  menuAndRenovationAndPestControlMapper.get_mpr_list_all((currentPage-1)*pageSize,pageSize,releaseType,contact);
    
	for(MenuAndRenovationAndPestControl releaseWelfare :list_releaseWelfareall) {
		releaseWelfare.setContact(EncrypDES.decryptPhone(releaseWelfare.getContact()));
		list_releaseWelfare.add(releaseWelfare);
	}

	releaseWelfare_pagePage.setDatas(list_releaseWelfare );
	return ServerResponse.createBySuccess(releaseWelfare_pagePage);
}
//操作
@Override
public ServerResponse<String> operation_usermrp(User user, Map<String, Object> params) {
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
			result=menuAndRenovationAndPestControlMapper.operation_usermrp(userIdLong,idLong,type_int,timeString);
		}else if(type_int==3 || type_int==4 || type_int==5) {
			timeString=formatter.format(new Date());
			result=menuAndRenovationAndPestControlMapper.operation_usermrp(userIdLong,idLong,type_int,timeString);
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
			//result=releaseWelfareMapper.position_operation_edit(releaseWelfare);
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
@Override
public ServerResponse<Object> get_usermrp_id(User user, long id) {
    if(id<=0) {
    	 return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());	
    }
	MenuAndRenovationAndPestControl menuAndRenovationAndPestControl=menuAndRenovationAndPestControlMapper.get_usermrp_id(user.getId(), id);
	if(menuAndRenovationAndPestControl==null) {
		 return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());	
	}
	menuAndRenovationAndPestControl.setContact(EncrypDES.decryptPhone(menuAndRenovationAndPestControl.getContact()));
	return ServerResponse.createBySuccess(menuAndRenovationAndPestControl);
}

}
