package com.dian.mmall.service.impl.releaseimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.EquipmentMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.service.release.EquipmentService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("equipmentService")
public class EquipmentServiceImpl implements EquipmentService {
	@Autowired
	private EquipmentMapper equipmentMapper ;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	
	@Override
	public ServerResponse<String> create_equipment(User user, Map<String, Object> params) {

		ServerResponse<Object> serverResponse=check_evaluate(user, params,1);
		   if(serverResponse.getStatus()!=0) {
			   return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		   }
		   Map<String,Object> map=(Map<String, Object>) serverResponse.getData();
	       map.put("authentiCationStatus", 1);
	       map.put("welfareStatus", 4);
	  
	       Equipment equipment=(Equipment) BeanMapConvertUtil.convertMap(Equipment.class,map);
		 	//{result=true, message=验证通过} 返回结果
		 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(equipment);
		 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
		 		int	count=equipmentMapper.create_equipment(equipment);
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
		
			 
				String releaseTypeString=params.get("releaseType").toString().trim();	
				if(releaseTypeString==null || releaseTypeString.equals("")) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
				}
				int releaseType=Integer.valueOf(releaseTypeString);
				if(releaseType!=18 &&releaseType!=33 &&releaseType!=34) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
				}
				int count=0;
				if(type==1) {  //TODO新建时才检查总数
				//判断是否超过可以发布的总数
				 count =equipmentMapper.countNum(releaseType,userId);
			      if(count>20) {
			    	 return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			       }
			    map.put("createTime",createTime);
			    }
			    map.put("releaseType",releaseType);
			    map.put("updateTime",createTime);
			    map.put("termOfValidity",DateTimeUtil.a_few_days_later(90));

		 
	  //判断是否实名
			if(currentUser.getIsAuthentication()!=2) {
				 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
			}
			
			if(type==1) {  //新建
			//图片
			List<Picture> listObj3	= JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"),List.class,Picture.class);
			int list_size=listObj3.size();
			//把getUse_status()==1 放到这个集合中
			List<Picture> listObj4=new ArrayList<Picture>();	
			int getNum=PictureNum.dianqiheweixiu.getNum();	
			if(list_size>0) {
				 count=0;
				for(int a=0;a<list_size;a++) {
					Picture picture=listObj3.get(a);
					if(picture.getUseStatus()==1) {
					   picture.setUserId(userId);
					   picture.setUseStatus(3);
						
						Picture picture1=pictureMapper.selectPictureBYid(picture.getId());
						if(!picture.getPictureUrl().equals(picture1.getPictureUrl())) {
							return ServerResponse.createByErrorMessage("图片地址不一致");
						}
						pictureMapper.updatePictureUse(picture.getId());
						listObj4.add(picture);
						count+=1;				
					}}
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
			} 
			
			
			params.put("serviceAndprice", JsonUtil.obj2String(params.get("serviceAndprice")));
			
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
			  //判断手机号是否合法
		       ServerResponse<String>	serverContact=LegalCheck.legalCheckMobilePhone(contact);
		    	if(serverResponse.getStatus()!=0) {	
					return ServerResponse.createByErrorMessage(serverContact.getMsg());			
				}
		map.put("contact", EncrypDES.encryptPhone(contact));	
		map.put("consigneeName", params.get("consigneeName").toString().trim());
		map.put("detailed", realName.getDetailed());
		map.put("realNameId", realName.getId());
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
		
		
	     String serviceDetailed=params.get("serviceDetailed").toString().trim();
	      if(!serviceDetailed.equals("全市") &&!serviceDetailed.equals("来电确认")) {
	    	  return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());			  
	      }
		 map.put("serviceDetailed", serviceDetailed);
		 
		 map.put("serviceType", params.get("serviceType").toString().trim());
		 map.put("serviceAndprice", params.get("serviceAndprice").toString().trim());
		 map.put("remarks", params.get("remarks").toString().trim());	 
		 map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
		 map.put("releaseTitle", params.get("releaseTitle").toString().trim()); 
		return ServerResponse.createBySuccess(map);

	}



	@Override
	public ServerResponse<Object> adminEquipment(Map<String, Object> params) {
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
		
	 	String contact = params.get("contact").toString().trim();
		if(contact.length()!=11 && contact!=null && !contact.equals("")) {
	 		 return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
	 	}
		if(contact.length()==11) {
			contact = EncrypDES.encryptPhone(contact);
		}
	 	
		
	 	
	 	Page<Equipment> equipment_pagePage=new Page<Equipment>();
	                                           	
	 	long zongtiaoshu=equipmentMapper.adminEquipment_no(contact);
		
		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); //当前页
		
	    List<Equipment> list_equipment  =	new ArrayList();
	    List<Equipment> list_equipmentall=  equipmentMapper.adminEquipment((currentPage-1)*pageSize,pageSize,contact);
	    
	    List<ServiceType> serviceTypeList=serviceTypeMapper.get_serviceTypeAll(18);
	    
		for(Equipment re :list_equipmentall) {
			re.setContact(EncrypDES.decryptPhone(re.getContact()));
			String serviceTypeString=re.getServiceType();
			long userId=re.getUserId();
			boolean bo=false;
			for(int a=0;a<serviceTypeList.size();a++) {		
				ServiceType serviceType=serviceTypeList.get(a);
				String serviceTypeName=serviceType.getServiceTypeName();
				if(serviceTypeString.equals(serviceTypeName) && userId==serviceType.getCreateUserId() &&  serviceType.getAuthentiCationStatus()==1) {
					re.setServiceType("(待审批的商品类型: )"+serviceTypeString);
					re.setEvaluateid(serviceType.getId());
					bo=true;
				}else if(serviceTypeString.equals(serviceTypeName) &&  serviceType.getAuthentiCationStatus()==2) {
					re.setEvaluateid(serviceType.getId());
					bo=true;
				}	
				
			}
			if(bo==false) {
				re.setEvaluateid(-1);
				re.setServiceType("(不通过,商品类型不合规: )"+serviceTypeString);
			}
			list_equipment.add(re);
		}

		equipment_pagePage.setDatas(list_equipment );
		return ServerResponse.createBySuccess(equipment_pagePage);
	}



	@Override
	public ServerResponse<Object> get_myEquipment_list(User user, Map<String, Object> params) {
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
	 	
	 	
	 	
	 	Page<Equipment> equipment_pagePage=new Page<Equipment>();
		
	 	long zongtiaoshu=equipmentMapper.get_userEquipment_list_no(releaseType,welfareStatus,user.getId());
		
		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); //当前页
		
	    List<Equipment> list_equipment  =	new ArrayList();
	    List<Equipment> list_equipmentall=  equipmentMapper.get_myEquipment_list((currentPage-1)*pageSize,pageSize,releaseType,welfareStatus,user.getId());
	    
		for(Equipment equipment :list_equipmentall) {
			equipment.setContact(EncrypDES.decryptPhone(equipment.getContact()));
			list_equipment.add(equipment);
		}

		equipment_pagePage.setDatas(list_equipment );
		return ServerResponse.createBySuccess(equipment_pagePage);
	}
}
