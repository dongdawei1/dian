package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PermissionCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.releaseDao.GrainAndOilMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.TRolePermissionMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.JsonUtil;
@Service("getPublishingsService")
public class GetPublishingsServiceImpl implements GetPublishingsService {
	
	@Autowired
    private TRolePermissionMapper tRolePermissionMapper;
	@Autowired  
	private	GrainAndOilMapper grainAndOilMapper;
	@Autowired
	private ReleaseWelfareMapper releaseWelfareMapper;
	
	@Autowired
	private CityMapper cityMapper;
	
	@Override
	public ServerResponse getMenuList(User user, Map<String,Object> params) {
		long userId=user.getId(); 
		String permissionid_string= params.get("permissionid").toString().trim() ;    
		String currentPage_string= params.get("currentPage").toString().trim() ;    
		String pageSize_string= params.get("pageSize").toString().trim() ;    
	 	
		int permissionid=0;
		int currentPage=0;
		int  pageSize=0;
		
	 	if(permissionid_string!=null && permissionid_string!="") {
	    		permissionid=Integer.parseInt(permissionid_string);
	 	}else {
	 		 return ServerResponse.createByErrorMessage("菜单id输入有误");
	 	}
	 	
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
	 	
		//粮油code需要在PermissionCode中和数据库id保持一致
	
		if(permissionid ==PermissionCode.GRAINANDOIL.getCode() ) {
		
			return   	getGrainAndOilList(permissionid, userId,params,currentPage,pageSize);

		}else if(permissionid ==PermissionCode.ZHIWEI.getCode()) {
			//招聘查询
			return   	getPositionList(permissionid, userId,params,currentPage,pageSize);
		}
		else {
			//如果不等于所有就不是此菜单
		 return ServerResponse.createByErrorMessage("不存在的菜单");
		}
		
	}

	//菜单五已经实现 	
	public ServerResponse getGrainAndOilList(Integer permissionid, long userId,Map<String,Object> params,int currentPage,
		int  pageSize){

	     
	     
			ServerResponse checkroleString=checkRoleAndcommodityType(permissionid,  userId);
		
			if(checkroleString.getMsg().equals("success")) {
				
				Page<GrainAndOil> grainAndOil_pagePage=new Page<GrainAndOil>();
				grainAndOil_pagePage.setTotalno(grainAndOilMapper.getGrainAndOilPageno());
				grainAndOil_pagePage.setPageSize(pageSize);
				grainAndOil_pagePage.setCurrentPage(currentPage); //当前页
				grainAndOil_pagePage.setDatas(grainAndOilMapper.getGrainAndOilList((currentPage-1)*pageSize,pageSize));
				//在这里查询 TODO
				return ServerResponse.createBySuccess(grainAndOil_pagePage);
			}else {
				
				 return checkroleString;  
			}
	     
			
			
		}
		
		
		//招聘
    public ServerResponse getPositionList(Integer permissionid, long userId,Map<String,Object> params,int currentPage,
				int  pageSize){
					ServerResponse checkroleString=checkRoleAndcommodityType(permissionid,  userId);
					if(checkroleString.getMsg().equals("success")) {
						
					
				 	String	provinces_id=params.get("provincesId").toString().trim();
				 	String	city_id=params.get("cityId").toString().trim();
					String   district_county_id=params.get("districtCountyId").toString().trim();
					

					
					String detailed="%"+ctiy(provinces_id,city_id,district_county_id)+"%";
					System.out.println(detailed);
					String position=params.get("position").toString().trim();
						Page<ReleaseWelfare> releaseWelfare_pagePage=new Page<ReleaseWelfare>();
						long count =releaseWelfareMapper.getUserReleaseWelfarePageno(detailed,position);
						if(count==0) {
							detailed="%"+ctiy(provinces_id,city_id,null)+"%";
							count =releaseWelfareMapper.getUserReleaseWelfarePageno(detailed,position);
						}
						releaseWelfare_pagePage.setTotalno(count);
						releaseWelfare_pagePage.setPageSize(pageSize);
						releaseWelfare_pagePage.setCurrentPage(currentPage); //当前页
						releaseWelfare_pagePage.setDatas(releaseWelfareMapper.getUserReleaseWelfareList((currentPage-1)*pageSize,pageSize,detailed,position));
						return ServerResponse.createBySuccess(releaseWelfare_pagePage);
					}else {
						
						 return checkroleString;  
					}					
				}	
		
		
		//校验
	public  ServerResponse<String> checkRoleAndcommodityType(int permissionid ,long userId) {
			  int isroleAndtype=0; 
				//取得是总条数，后期可能会有一个用户多个角色的情况
			
			 isroleAndtype=tRolePermissionMapper.isrole(userId,permissionid);
			 if(isroleAndtype<1) {
				//检查用户有没有此菜单权限,role查 t_role_permission表 	
				 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());  
			   }	
	 
			   return ServerResponse.createBySuccessMessage("success");	  		  
		  }
	
	

	 public String ctiy(String provinces_Id,String city_Id,String districtCounty_Id) {
		
		 if(provinces_Id==null || provinces_Id.equals("") || city_Id==null || city_Id.equals("")) {
			 return null; 
		 }
		 
		 int	provincesId=Integer.valueOf(provinces_Id);
		 int	cityId=Integer.valueOf(city_Id);
		 int	districtCountyId=0;
		 if(districtCounty_Id!=null) {
			 districtCountyId=Integer.valueOf(districtCounty_Id);
		 }	
		 if(districtCountyId!=0) {
			return cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		 }else {
			return cityMapper.checkeCityTuo(provincesId, cityId);
		 }
		 
	 }
}
