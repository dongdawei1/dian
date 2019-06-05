package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.City;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IPictureService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
@Service("realNameService")
public class RealNameServiceImpl implements RealNameService{
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;
	 @Autowired
	    private IPictureService ipics;  //图片库更新

	 @Autowired
	  private UserMapper userMapper;
	 //实名认证
	@Override
	public ServerResponse<String> newRealName(long user_id,String loginToken, Map<String, Object> params) {
		String isbusiness_string = params.get("isbusiness").toString().trim() ;
		if(!isbusiness_string.equals("true") && !isbusiness_string.equals("false") ) {
		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
		}
		boolean isbusiness=Boolean.valueOf(isbusiness_string);
		
		if(isbusiness==true) {
			//商家的 实名认证
			//校验是否有特殊字符
			Map<String, Object>  params_map=(Map<String, Object>) params.get("ruleForm");
	    	ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params_map);
		 	//检查是否有非法输入
		 	if(serverResponse.getStatus()!=0) {	
					return serverResponse;			
				}	
			
		 	
		 	
		 
		 	String address_detailed =null;
		    int	provinces_id=0;
		    int	city_id=0;
		    int	district_county_id=0;
		 	String licenseUrl=null;   //营业执照图片url
			String contact=null;  //收/送货联系方式
		    String consignee_name=null; //收/送货人姓名 
		    String email=null; 
		    String city=null;
		   try {
		 		provinces_id=Integer.valueOf(params_map.get("provinces_id").toString().trim());
		 		city_id=Integer.valueOf(params_map.get("city_id").toString().trim());
			    district_county_id=Integer.valueOf(params_map.get("district_county_id").toString().trim());
			   //判断省市区id是否正确
			    if(provinces_id>10000 &&  city_id>10000 && district_county_id>10000) {
			    	 city=cityMapper.checkeCity(provinces_id,city_id,district_county_id);
			    	System.out.println( city);
			    	 if( city==null) {
		 				return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 			}
		 		}else {
		 			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 		}
			    		    
			    //图片
				List<Picture> listObj3	= JsonUtil.list2Obj((ArrayList<Picture>) params_map.get("licenseUrl"),List.class,Picture.class);
				int list_size=listObj3.size();
				//把getUse_status()==1 放到这个集合中
				List<Picture> listObj4=new ArrayList<Picture>();
				
				int getNum=PictureNum.ShiMingRenZheng.getNum();
				//如果大于3要判断没删除的是否超过总数
				if(list_size>0) {
					int count=0;
					for(int a=0;a<list_size;a++) {
						if(listObj3.get(a).getUse_status()==1) {
							listObj4.add(listObj3.get(a));
							count+=1;
						}
						if(count>getNum) {
							return ServerResponse.createByErrorMessage("图片数量不能超过 "+getNum+"个");
						}
						
					}}
				 licenseUrl= params_map.get("licenseUrl").toString().trim() ;
		 		 
				 address_detailed = params_map.get("address_detailed").toString().trim() ;
		 		 contact= params_map.get("contact").toString().trim() ;
		 		 consignee_name=params_map.get("consignee_name").toString().trim() ;
		 		 email=params_map.get("email").toString().trim() ;
		 		//判断必传字段
		 		 if( address_detailed.length()<101 && address_detailed.length()>0 
		 				&& contact.length()<13 && contact.length()>5 	
		 				&& consignee_name.length()<13 && consignee_name.length()>1) {
		 			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		            
					RealName realName = new RealName();   		
					realName.setCreateTime(formatter.format(new Date()));
					realName.setUserId(user_id);
					
		 			 if(email!=null) {
		 				realName.setEmail(email); 
		 					 }
		 			realName.setAddressDetailed(address_detailed);
		 			realName.setLicenseUrl(licenseUrl);
		 			realName.setContact(contact);
		 			realName.setConsigneeName(consignee_name);
		 			realName.setCityId(city_id);
		 			realName.setDistrictCountyId(district_county_id);
		 			realName.setProvincesId(provinces_id);
		 			realName.setAuthentiCationStatus(1);
		 			realName.setDetailed(city);
		 			//检查id是否已经存在
		 			if(realNameMapper.isNewRealName(user_id)>0) {
		 				return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		 			}
		 			int resultCount=  realNameMapper.newRealName(realName);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
	                 }
	     			resultCount= userMapper.update_newRealName(user_id);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
	                 }
	     			if(listObj4.size()>0) {
	     		 		for(int a=0;a<listObj4.size();a++) {
	     		 			Picture picture=listObj4.get(a);
	     		 			picture.setTocken(loginToken);
	     		 			picture.setUser_id(user_id);
	     		 			
	     		 			ipics.updatePictureUse(picture);
	     		 		}
	     		 	}
	     			User currentUser = userMapper.selectUserById(user_id);
                    currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
                    currentUser.setPassword(null);
                    return ServerResponse.createBySuccessMessage(JsonUtil.obj2String(currentUser));
		 		 }
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		 	} catch (Exception e) {
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		}else {
			//找工作和租房实名认证
			return null;
		}
			
		
	}

	
	//查询实名信息
	@Override
	public ServerResponse<Object> getRealName(long id) {
		RealName realName=realNameMapper.getRealName(id);
		if(realName!=null) {
		realName.setLicenseUrl(null);
		return ServerResponse.createBySuccess(realName);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
	}

}
