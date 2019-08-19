package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.City;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IPictureService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("realNameService")
public class RealNameServiceImpl implements RealNameService{
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;
	
	    private  String path=Const.PATH_E_IMG;
	    @Autowired
		private PictureMapper pictureMapper;
	 @Autowired
	  private UserMapper userMapper;
	 //创建实名认证
	@Override
	public ServerResponse<String> newRealName(User user,String loginToken, Map<String, Object> params) {
		String isbusiness_string = params.get("isbusiness").toString().trim() ;
//		if(!isbusiness_string.equals("true") && !isbusiness_string.equals("false") ) {
//		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
//		}
		int isbusiness=Integer.valueOf(isbusiness_string);
		
		
		
		//校验是否有特殊字符
		Map<String, Object>  params_map=(Map<String, Object>) params.get("ruleForm");
    	ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params_map);
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return serverResponse;			
			}	
	 	
	 	
	 	long user_id=user.getId();
		if(isbusiness==2 || isbusiness==6 ) {
		 //所有商户实名
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
				
				if(list_size>0) {
					int count=0;
					for(int a=0;a<list_size;a++) {
						Picture picture=listObj3.get(a);
						if(listObj3.get(a).getUseStatus()==1) {
							   picture.setUserId(user_id);
							   picture.setUseStatus(3);
							
							Picture picture1=pictureMapper.selectPictureBYid(picture.getId());
							if(!picture.getPictureUrl().equals(picture1.getPictureUrl())) {
								return ServerResponse.createByErrorMessage("图片地址不一致");
							}
							pictureMapper.updatePictureUse(picture.getId());
							listObj4.add(picture);
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
				 licenseUrl=JsonUtil.obj2StringPretty(listObj4);
		 		 
				 address_detailed = params_map.get("address_detailed").toString().trim() ;
		 		 contact= params_map.get("contact").toString().trim() ;
		 		 consignee_name=params_map.get("consignee_name").toString().trim() ;
		 		 email=params_map.get("email").toString().trim() ;
		 	String	companyName=params_map.get("companyName").toString().trim() ;
		 	if(companyName==null ||companyName.equals("")) {
		 		return ServerResponse.createByErrorMessage(ResponseMessage.gongsimingchengkong.getMessage());
		 	}
		 	
		 		//判断手机号是否合法
			    	serverResponse=LegalCheck.legalCheckMobilePhone(contact);
			    	if(serverResponse.getStatus()!=0) {	
						return serverResponse;			
					}
		 		 //判断必传字段
		 		 if( address_detailed.length()<101 && address_detailed.length()>0 
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
		 			realName.setContact(EncrypDES.encryptPhone(contact));
		 			realName.setConsigneeName(consignee_name);
		 			realName.setCityId(city_id);
		 			realName.setDistrictCountyId(district_county_id);
		 			realName.setProvincesId(provinces_id);
		 			realName.setAuthentiCationStatus(1);
		 			realName.setDetailed(city);
		 			realName.setUserName(user.getUsername());
		 			realName.setCompanyName(companyName);
		 			//添加
		 			serverResponse=SetBean.setRole(user.getRole());
		 			//检查是否有非法输入
		 		 	if(serverResponse.getStatus()!=0) {	
		 					return serverResponse;			
		 			}	
		 		 	realName.setUserType(serverResponse.getMsg());
		 			//检查id是否已经存在
		 			if(realNameMapper.isNewRealName(user_id)>0) {
		 				return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		 			}
		 			int resultCount=  realNameMapper.newRealName(realName);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
	                 }
	     			user.setIsAuthentication(1);
	     			resultCount= userMapper.update_newRealName(user);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
	                 }
	     			if(listObj4.size()>0) {
	     		 		for(int a=0;a<listObj4.size();a++) {
	     		 			Picture picture=listObj4.get(a);	     		 			
	     		 			pictureMapper.updatePictureUse(picture.getId());
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

		}else if(isbusiness==11){
		  //求职者实名
		    Integer eag=0;   //求职年龄
		    String gender=null; //性别
		 	String address_detailed =null;
		    int	provinces_id=0;
		    int	city_id=0;
		    int	district_county_id=0;	
			String contact=null;  //联系方式
		    String consignee_name=null; //姓名 
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

		 		 contact= params_map.get("contact").toString().trim() ;
		 		 consignee_name=params_map.get("consignee_name").toString().trim() ;
		 		 email=params_map.get("email").toString().trim() ;
		 		//判断手机号是否合法
			    	serverResponse=LegalCheck.legalCheckMobilePhone(contact);
			    	if(serverResponse.getStatus()!=0) {	
						return serverResponse;			
					}
		 		 //判断必传字段
		 		 if(  consignee_name.length()<13 && consignee_name.length()>1) {
		 			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		            
					RealName realName = new RealName();   		
					realName.setCreateTime(formatter.format(new Date()));
					realName.setUserId(user_id);
					
		 			 if(email!=null) {
		 				realName.setEmail(email); 
		 					 }
		 			realName.setContact(EncrypDES.encryptPhone(contact));
		 			realName.setConsigneeName(consignee_name);
		 			realName.setCityId(city_id);
		 			realName.setDistrictCountyId(district_county_id);
		 			realName.setProvincesId(provinces_id);
		 			realName.setAuthentiCationStatus(1);
		 			realName.setDetailed(city);
		 			realName.setUserName(user.getUsername());
		 			//添加
		 			serverResponse=SetBean.setRole(user.getRole());
		 			//检查是否有非法输入
		 		 	if(serverResponse.getStatus()!=0) {	
		 					return serverResponse;			
		 			}	
		 		 	realName.setUserType(serverResponse.getMsg());
		 			
		 			//检查id是否已经存在
		 			if(realNameMapper.isNewRealName(user_id)>0) {
		 				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuIdYiJingCunZai.getMessage());
		 			}
		 			int resultCount=  realNameMapper.newRealName(realName);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
	                 }
	     			user.setIsAuthentication(1);
	     			resultCount= userMapper.update_newRealName(user);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
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
		}
		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
	}

	
	//查询实名信息
	@Override
	public ServerResponse<Object> getRealName(User user) {
		RealName realName=realNameMapper.getRealName(user.getId());
		if(realName!=null) {
		realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
		realName.setExamineName(null);
		realName.setExamineTime(null);
		return ServerResponse.createBySuccess(realName);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouchaxundaoshimingxinxi.getMessage());
	}
	
	
	
	//重新实名updateRealName
	
	@Override
	public ServerResponse<String> updateRealName(User user,String loginToken, Map<String, Object> params) {
		
		
		String isbusinessString = params.get("isbusiness").toString().trim() ;
//		if(!isbusiness_string.equals("true") && !isbusiness_string.equals("false") ) {
//		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
//		}
		int isbusiness=Integer.valueOf(isbusinessString);
		long userId=user.getId();
		//校验是否有特殊字符
		Map<String, Object>  params_map=(Map<String, Object>) params.get("ruleForm");
    	ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params_map);
	 	//检查是否有非法输入
	 	if(serverResponse.getStatus()!=0) {	
				return serverResponse;			
			}	
	 	User currentUser1 = userMapper.selectUserById(userId);
	 	if(currentUser1 != null ) {
	 	if(currentUser1.getIsAuthentication()!=3) {
	 		log.info("updateRealName   ",currentUser1.getIsAuthentication());
	 		return	ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
	 	}}else {
	 		return	ServerResponse.createByErrorMessage(ResponseMessage.huoquxinxishibai.getMessage());
	 	}
	 	String addressDetailed =null;
	    int	provincesId=0;
	    int	cityId=0;
	    int	districtCountyId=0;	 	
		String contact=null;  //收/送货联系方式
	    String consigneeName=null; //收/送货人姓名 
	    String email=null; 
	    String city=null;
	    Integer eag=0;   //求职年龄
	    String gender=null; //性别
	   
	    
		if(isbusiness==2 || isbusiness==6 ) {
			//商家的 实名认证
		   try {
		 		provincesId=Integer.valueOf(params_map.get("provincesId").toString().trim());
		 		cityId=Integer.valueOf(params_map.get("cityId").toString().trim());
			    districtCountyId=Integer.valueOf(params_map.get("districtCountyId").toString().trim());
			   //判断省市区id是否正确
			    if(provincesId>10000 &&  cityId>10000 && districtCountyId>10000) {
			    	 city=cityMapper.checkeCity(provincesId,cityId,districtCountyId);
			    	 if( city==null) {
		 				return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 			}
		 		}else {
		 			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 		}
			    		    
				 addressDetailed = params_map.get("addressDetailed").toString().trim() ;
		 		 contact= params_map.get("contact").toString().trim() ;
		 		 consigneeName=params_map.get("consigneeName").toString().trim() ;
		 		 email=params_map.get("email").toString().trim() ;
		 		//判断手机号是否合法
			    	serverResponse=LegalCheck.legalCheckMobilePhone(contact);
			    	if(serverResponse.getStatus()!=0) {	
						return serverResponse;			
					}
			    	
				 	String	companyName=params_map.get("companyName").toString().trim() ;
				 	if(companyName==null ||companyName.equals("")) {
				 		return ServerResponse.createByErrorMessage(ResponseMessage.gongsimingchengkong.getMessage());
				 	}
		 		 //判断必传字段
		 		 if( addressDetailed.length()<101 && addressDetailed.length()>0 
		 				&& consigneeName.length()<13 && consigneeName.length()>1) {
		 			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		            
					RealName realName = new RealName();   		
					realName.setUpdateTime(formatter.format(new Date()));
					realName.setUserId(userId);
					
		 			 if(email!=null) {
		 				realName.setEmail(email); 
		 					 }
		 			 realName.setCompanyName(companyName);
		 			realName.setAddressDetailed(addressDetailed);
		 			realName.setContact(EncrypDES.encryptPhone(contact));
		 			realName.setConsigneeName(consigneeName);
		 			realName.setCityId(cityId);
		 			realName.setDistrictCountyId(districtCountyId);
		 			realName.setProvincesId(provincesId);
		 			realName.setAuthentiCationStatus(1);
		 			realName.setDetailed(city);
		 			realName.setUserName(user.getUsername());
		 			//检查id是否已经存在
		 			RealName realName1=realNameMapper.getRealName(userId);
		 			int resultCount=0;
		 			
				
		 			if(realName1!=null) {
		 				//检查图片
		 				ServerResponse<String> serverResponseString=setPictureUrl((ArrayList<Picture>) params_map.get("licenseUrl"), realName1.getLicenseUrl());
		 				   if(serverResponseString.getStatus()!=0) {
		 					  return  serverResponseString;
		 				   }
		 				realName.setLicenseUrl(serverResponseString.getMsg());
		 				realName.setId(realName1.getId());
		 				 resultCount=  realNameMapper.updateRealName(realName);
		 			
		 				if(resultCount == 0){
		     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
		                 }
		 				
		 				//删除图片
							try {
								List<Picture> listObj4	= JsonUtil.list2Obj((ArrayList<Picture>)params.get("licenseUrl"),List.class,Picture.class);
						        for(int a=0;a<listObj4.size();a++) {
						        Picture	picture=listObj4.get(a);
						        	if(picture.getUseStatus()==2) {
											   FileControl.deleteFile(path+picture.getUserName());
											   pictureMapper.updatePicture(picture.getId());
										
						        	}
						        }
							} catch (Exception e) {
								// TODO: handle exception
							}
						
		 				
		 				
		 			}else {
		 				return	ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());	
	     			}
		 			user.setIsAuthentication(1);
	     			resultCount= userMapper.update_newRealName(user);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
	                 }
	
	     			User currentUser = userMapper.selectUserById(userId);
                    currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
                    currentUser.setPassword(null);
                    return ServerResponse.createBySuccessMessage(JsonUtil.obj2String(currentUser));
		 		 }
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		 	} catch (Exception e) {
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		}else if(isbusiness==11) {
			//找工作
		   try {
		 		provincesId=Integer.valueOf(params_map.get("provincesId").toString().trim());
		 		cityId=Integer.valueOf(params_map.get("cityId").toString().trim());
			    districtCountyId=Integer.valueOf(params_map.get("districtCountyId").toString().trim());
			   //判断省市区id是否正确
			    if(provincesId>10000 &&  cityId>10000 && districtCountyId>10000) {
			    	 city=cityMapper.checkeCity(provincesId,cityId,districtCountyId);
			    	 if( city==null) {
		 				return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 			}
		 		}else {
		 			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		 		}
			    		    
			    eag=Integer.valueOf(params_map.get("eag").toString().trim());
			     //判断年龄
			     if( eag<18 || eag>60) {
			    	 return	ServerResponse.createByErrorMessage(ResponseMessage.NianLiFanWei.getMessage()); 
			     }
			     gender=params_map.get("gender").toString().trim() ;
			     //判断性别
			     if(!gender.equals("男") && !gender.equals("女") ) {
			    	 return	ServerResponse.createByErrorMessage(ResponseMessage.XinBieYouWu.getMessage()); 
			     }
		 		 
				 addressDetailed = params_map.get("addressDetailed").toString().trim() ;
		 		 contact= params_map.get("contact").toString().trim() ;
		 		 consigneeName=params_map.get("consigneeName").toString().trim() ;
		 		 email=params_map.get("email").toString().trim() ;
		 		//判断手机号是否合法
			    	serverResponse=LegalCheck.legalCheckMobilePhone(contact);
			    	if(serverResponse.getStatus()!=0) {	
						return serverResponse;			
					}
		 		 //判断必传字段
		 		 if( addressDetailed.length()<101 && addressDetailed.length()>0 
		 				&& consigneeName.length()<13 && consigneeName.length()>1) {
		 			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		            
					RealName realName = new RealName();   		
					realName.setUpdateTime(formatter.format(new Date()));
					realName.setUserId(userId);
					
		 			 if(email!=null) {
		 				realName.setEmail(email); 
		 					 }
		 			realName.setAddressDetailed(addressDetailed);
		 			
		 			realName.setContact(EncrypDES.encryptPhone(contact));
		 			realName.setConsigneeName(consigneeName);
		 			realName.setCityId(cityId);
		 			realName.setDistrictCountyId(districtCountyId);
		 			realName.setProvincesId(provincesId);
		 			realName.setAuthentiCationStatus(1);
		 			realName.setDetailed(city);
		 			realName.setEag(eag);
		 			realName.setGender(gender);
		 			realName.setUserName(user.getUsername());
		 			//检查id是否已经存在
		 			RealName realName1=realNameMapper.getRealName(userId);
		 			int resultCount=0;
		 			if(realName1!=null) {
		 			//不等于null 更新
		 				realName.setId(realName1.getId());
		 				 resultCount=  realNameMapper.updateRealName(realName);
			     			if(resultCount == 0){
			     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
			                 }		
		 			}else {
		 				return	ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());	
		 			}
		 			
		 			user.setIsAuthentication(1);
	     			resultCount= userMapper.update_newRealName(user);
	     			if(resultCount == 0){
	     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
	                 }
	     			
	     			User currentUser = userMapper.selectUserById(userId);
                    currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
                    currentUser.setPassword(null);
                    return ServerResponse.createBySuccessMessage(JsonUtil.obj2String(currentUser));
		 		 }
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		 	} catch (Exception e) {
		 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
		}
		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
	}


	
	//管理员审核获取全部待审批的
	@Override
	public ServerResponse<Object> getRealNameAll(Map<String, Object> params) {
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
	 	if(contact.length()!=11 && contact!=null && !contact.equals("")) {
	 		 return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
	 	}	
	 	if(contact.length()==11) {
			contact = EncrypDES.encryptPhone(contact);
		}
	 	Page<RealName> realName_pagePage=new Page<RealName>();
		
	 	long zongtiaoshu=realNameMapper.getRealNamePageno(userName,contact);
		int totalno=(int) Math.ceil((float)zongtiaoshu/pageSize);
		
//		System.out.println(grainAndOilMapper.getGrainAndOilPageno()+"   "+pageSize+"    "+
//				 Math.ceil(grainAndOilMapper.getGrainAndOilPageno()/pageSize)	);
		
		realName_pagePage.setTotalno(zongtiaoshu);
		realName_pagePage.setPageSize(pageSize);
		realName_pagePage.setCurrentPage(currentPage); //当前页
		
	    List<RealName> list_realname  =	new ArrayList();
	    List<RealName> list_srelnameall= realNameMapper.getRealNameAll((currentPage-1)*pageSize,pageSize,userName,contact);
		for(RealName realName :list_srelnameall) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			list_realname.add(realName);
		}

		realName_pagePage.setDatas(list_realname );
		
		return ServerResponse.createBySuccess(realName_pagePage);
	}

    //实名审核和添加实名
	@Override
	public ServerResponse<Object> examineRealName(User user, Map<String, Object> params,String loginToken) {
	   String isArtificialString=params.get("isArtificial").toString().trim();	
	   if(isArtificialString==null ||isArtificialString.contentEquals("")) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
			}
	   int isArtificial=Integer.valueOf(isArtificialString);
		if(isArtificial==1) { //审批
		String	user_beishenhe =params.get("userId").toString().trim();	
		if(user_beishenhe==null ||user_beishenhe.contentEquals("")) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
			}
	    
		long user_id=Long.valueOf(user_beishenhe);
		User user_shenhe= userMapper.selectUserById(user_id);
	  if(user_shenhe==null) {
		  return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
	  }	
	  
		RealName realName = new RealName(); 
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		realName.setExamineTime(formatter.format(new Date()));
		realName.setExamineName(user.getUsername());
		realName.setUserId(user_id);
		int authentiCationStatus=Integer.valueOf(params.get("authentiCationStatus").toString().trim());
		if(authentiCationStatus!=2 && authentiCationStatus!=3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		realName.setAuthentiCationStatus(authentiCationStatus);
		int resultCount=0;
		if(authentiCationStatus==2) {
			
			resultCount=realNameMapper.examineRealName(realName);
			if(resultCount==0) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
			
		}else if(authentiCationStatus==3) {
			String authentiCationFailure= params.get("authentiCationFailure").toString().trim();
			if(authentiCationFailure==null ||authentiCationFailure.contentEquals("")) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
			}
			realName.setAuthentiCationFailure(authentiCationFailure);
			resultCount=realNameMapper.examineRealName(realName);
			if(resultCount==0) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
					
		}else {
			return	ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		}
		user_shenhe.setIsAuthentication(authentiCationStatus);
		resultCount= userMapper.update_newRealName(user_shenhe);
		if(resultCount==0) {
			return	ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		}
		
		//user_shenhe = userMapper.selectUserById(user_shenhe.getId());
		user_shenhe.setMobilePhone(EncrypDES.decryptPhone(user_shenhe.getMobilePhone()));
		user_shenhe.setPassword(null);
        return ServerResponse.createBySuccess(user_shenhe);
	   }
	else if(isArtificial==2) {  //添加
		String username=params.get("userName").toString().trim();	
		   if(username==null ||username.contentEquals("")) {
				return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
				}	
	    User user2=	userMapper.checkUsername(username);
	    if(user2==null) {
			  return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuIdBuJingCunZai.getMessage());
		  }	
	    String mobilePhone=params.get("mobilePhone").toString().trim();
	    if(!mobilePhone.equals(EncrypDES.decryptPhone(user2.getMobilePhone()))) {
	    	return	ServerResponse.createByErrorMessage(ResponseMessage.ZhuoCeShouJiCuoWu.getMessage());
	    }
	   
	    
	    
		//校验是否有特殊字符
		    	ServerResponse<String> serverResponse= LegalCheck.legalCheckFrom(params);
			 	//检查是否有非法输入
			 	if(serverResponse.getStatus()!=0) {	
						return ServerResponse.createByErrorMessage(serverResponse.getMsg());			
					}	
			 	
			 	//所有商户实名
			 	String address_detailed =null;
			    int	provinces_id=0;
			    int	city_id=0;
			    int	district_county_id=0;
			 	String licenseUrl=null;   //营业执照图片url
				String contact=null;  //收/送货联系方式
			    String email=null; 
			    String city=null;
			    Integer eag=0;   //求职年龄
			    String gender=null; //性别
			   try {
			 		provinces_id=Integer.valueOf(params.get("provinces_id").toString().trim());
			 		city_id=Integer.valueOf(params.get("city_id").toString().trim());
				    district_county_id=Integer.valueOf(params.get("district_county_id").toString().trim());
				   //判断省市区id是否正确
				    if(provinces_id>10000 &&  city_id>10000 && district_county_id>10000) {
				    	 city=cityMapper.checkeCity(provinces_id,city_id,district_county_id);
				    	 if( city==null) {
			 				return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
			 			}
			 		}else {
			 			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
			 		}
				    eag=Integer.valueOf(params.get("eag").toString().trim());
				     //判断年龄
				     if( eag<18 || eag>60) {
				    	 return	ServerResponse.createByErrorMessage(ResponseMessage.NianLiFanWei.getMessage()); 
				     }
				    
				     gender=params.get("gender").toString().trim() ;
				     //判断性别
				     if(!gender.equals("男") && !gender.equals("女") ) {
				    	 return	ServerResponse.createByErrorMessage(ResponseMessage.XinBieYouWu.getMessage()); 
				     }	 
				   //图片
						List<Picture> listObj3	= JsonUtil.list2Obj((ArrayList<Picture>) params.get("licenseUrl"),List.class,Picture.class);
						int list_size=listObj3.size();
						//把getUse_status()==1 放到这个集合中
						List<Picture> listObj4=new ArrayList<Picture>();
						int getNum=PictureNum.ShiMingRenZheng.getNum();
						
						if(list_size>0) {
							int count=0;
							for(int a=0;a<list_size;a++) {
								Picture picture=listObj3.get(a);
								if(listObj3.get(a).getUseStatus()==1) {
									   picture.setUserId(user2.getId());
									   picture.setUseStatus(3);
									
									Picture picture1=pictureMapper.selectPictureBYid(picture.getId());
									if(!picture.getPictureUrl().equals(picture1.getPictureUrl())) {
										return ServerResponse.createByErrorMessage("图片地址不一致");
									}
									pictureMapper.updatePictureAdmin(picture);
									listObj4.add(picture);
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
						 licenseUrl=JsonUtil.obj2StringPretty(listObj4);

					 address_detailed = params.get("address_detailed").toString().trim() ;
			 		 contact= params.get("contact").toString().trim() ;
			 		 email=params.get("email").toString().trim() ;
			 		
			 		//判断手机号是否合法
				    	serverResponse=LegalCheck.legalCheckMobilePhone(contact);
				    	if(serverResponse.getStatus()!=0) {	
							return ServerResponse.createByErrorMessage(serverResponse.getMsg());			
						}
			 		 //判断必传字段
			 		 if( address_detailed.length()<101 && address_detailed.length()>0 ) {
			 			RealName realName = new RealName(); 
			 			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			 			realName.setExamineTime(formatter.format(new Date()));
			 			realName.setExamineName(user.getUsername());
			 			realName.setUserId(user2.getId());
			 			realName.setUserName(user2.getUsername());
			 			realName.setAuthentiCationStatus(2);
						realName.setCreateTime(formatter.format(new Date()));

			 			 if(email!=null) {
			 				realName.setEmail(email); 
			 					 }
			 			realName.setAddressDetailed(address_detailed);
			 			realName.setLicenseUrl(licenseUrl);
			 			realName.setContact(EncrypDES.encryptPhone(contact));
			 			realName.setConsigneeName(username);
			 			realName.setCityId(city_id);
			 			realName.setDistrictCountyId(district_county_id);
			 			realName.setProvincesId(provinces_id);
			 			realName.setDetailed(city);
			 		    realName.setEag(eag);
					    realName.setGender(gender);
			 			//添加
			 			serverResponse=SetBean.setRole(user2.getRole());
			 			//检查用户类型
			 		 	if(serverResponse.getStatus()!=0) {	
			 				return ServerResponse.createByErrorMessage(serverResponse.getMsg());		
			 			}	
			 		 	realName.setUserType(serverResponse.getMsg());
			 			//检查id是否已经存在
			 			if(realNameMapper.isNewRealName(user2.getId())>0) {
			 				return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
			 			}
			 			int resultCount=  realNameMapper.newRealName(realName);
		     			if(resultCount == 0){
		     				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage()); 
		                 }
		     			user2.setIsAuthentication(2);
		     			resultCount= userMapper.update_newRealName(user2);
		     			if(resultCount == 0){
		     				return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage()); 
		                 }
		     			user2.setPassword(null);
		     			user2.setMobilePhone(mobilePhone);
		     			 return ServerResponse.createBySuccess(user2);
			 		 }
			 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			 	} catch (Exception e) {
			 		return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
				}
		
	   }
	return	ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
    }
	
	//编辑时检查图片并重新赋值
	public ServerResponse<String> setPictureUrl(Object object,String PictureUrl){
		 //前端传入
		List<Picture> listObj3	= JsonUtil.list2Obj((ArrayList<Picture>) object,List.class,Picture.class);
	    //数据库查询
		List<Picture> listObj4	= JsonUtil.string2Obj(PictureUrl,List.class,Picture.class);
		if(listObj3.size()==0 || listObj4.size()==0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.tupianbunnegkong.getMessage());	
		}
		List<Picture> listObjCun=new ArrayList<Picture>();

		//处理编辑后
		for(int a3=0;a3<listObj3.size();a3++) {
			Picture picture=listObj3.get(a3);
			long id3=picture.getId();
			int useStatus=picture.getUseStatus();
			if(useStatus==1) {
				//1上传更新为3使用 
				pictureMapper.updatePictureUse(id3);
				picture.setUseStatus(3);
				listObjCun.add(picture);
			}else if(useStatus==3){	
				
				boolean  fanduanshifouweiyijingchunli=false;
			
					
				for(int a4=0;a4<listObj4.size();a4++) {
					if(id3==listObj4.get(a4).getId()) {
						listObjCun.add(picture);
						fanduanshifouweiyijingchunli=true;
						break;
					}	
				}
				if(fanduanshifouweiyijingchunli==false) {
					return ServerResponse.createByErrorMessage(ResponseMessage.benditupianbucunzai.getMessage());	
				}			
			}
		}
		if(listObjCun.size()>0) {
			return ServerResponse.createBySuccessMessage(JsonUtil.obj2StringPretty(listObjCun));
		}	
		return ServerResponse.createByErrorMessage(ResponseMessage.tupianbunnegkong.getMessage());	
		
	}



	
	@Override
	public ServerResponse<Object> getRealNameById(long id) {
		if(id>0) {
			RealName realName=realNameMapper.getRealNameById(id);
			if(realName!=null) {
			return 	ServerResponse.createBySuccess(realName);
			}else {
			return 	ServerResponse.createByError()	;
			}
		}else {
			return 	ServerResponse.createByError()	;
			}
	}
}
