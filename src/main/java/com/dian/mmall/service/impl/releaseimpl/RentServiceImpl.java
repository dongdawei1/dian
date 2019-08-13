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
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("rentService")
public class RentServiceImpl implements RentService {
	@Autowired
	private RentMapper rentMapper;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Override
	public ServerResponse<String> create_rent(User user, Map<String, Object> params) {

		ServerResponse<Object> serverResponse=check_evaluate(user, params,1);
		   if(serverResponse.getStatus()!=0) {
			   return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		   }
		   
		   Map<String,Object> map=(Map<String, Object>) serverResponse.getData();
	       map.put("authentiCationStatus", 1);
	       map.put("welfareStatus", 4);
	       System.out.println(map.toString());
	    Rent rent=(Rent) BeanMapConvertUtil.convertMap(Rent.class,map);
		 	//{result=true, message=验证通过} 返回结果
		 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(rent);
		 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
		 		int	count=rentMapper.create_rent(rent);
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
				if(releaseType!=14 &&releaseType!=15 ) {
					 return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
				}
				int count=0;
				if(type==1) {  //TODO新建时才检查总数
				//判断是否超过可以发布的总数
				 count =rentMapper.countNum(releaseType,userId);
			      if(count>5) {
			    	 return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			       }
			    map.put("createTime",createTime);
			    map.put("termOfValidity",DateTimeUtil.a_few_days_later(90));
			    }
			    map.put("releaseType",releaseType);
			    map.put("updateTime",createTime);
			   

		 
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
			int getNum=PictureNum.zufangang.getNum();	
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
		map.put("serviceDetailed",params.get("serviceDetailed").toString().trim());
		map.put("userType", serverResponse.getMsg());
		 map.put("remarks", params.get("remarks").toString().trim());	 
		 map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
		 map.put("releaseTitle", params.get("releaseTitle").toString().trim()); 
		return ServerResponse.createBySuccess(map);

	}
	
}
