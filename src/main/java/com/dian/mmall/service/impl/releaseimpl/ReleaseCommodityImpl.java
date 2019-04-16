package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CommodityTypeMapper;
import com.dian.mmall.dao.releaseDao.GrainAndOilMapper;
import com.dian.mmall.dao.releaseDao.TRolePermissionMapper;
import com.dian.mmall.enums.PermissionCode;
import com.dian.mmall.enums.ReleaseCount;
import com.dian.mmall.pojo.User;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.service.release.ReleaseCommodityService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.LegalCheck;
/**
 * 所有发布商品的接口
 */
@Service("releaseCommodityService")
public class ReleaseCommodityImpl implements ReleaseCommodityService {

	@Autowired  
	  private	GrainAndOilMapper grainAndOilMapper;
	
	@Autowired
    private TRolePermissionMapper tRolePermissionMapper;
	
	@Autowired
	private CommodityTypeMapper commodityTypeMapper;
	
	@Override
	public ServerResponse<String> commodity(User user, Map<String, Object> params) {
		

	 	//检查是否有非法输入
	 	if(!LegalCheck.LegalCheckFrom(params)) {	
				return ServerResponse.createByErrorMessage("内容中有非法输入不能包含delete,uptate");
			
			}	
	 	
	     long userId=user.getId();   
	     int role=user.getRole();

	     
	 	 int permissionid=0;	
	 	String permissionid_string= params.get("permissionid").toString().trim() ;    
	 	String commoditytype=params.get("commoditytype").toString().trim() ;  
	 	
	 	//Map<String,String> pictureUrl=(Map<String, String>) params.get("pictureUrl");
	 	
	 	
	 	if(permissionid_string!=null && permissionid_string!="") {
	    		permissionid=Integer.parseInt(permissionid_string);
	   	     //检查是否实名
//	   	     LEASE("店面/窗口出租",14),
//	   		   RENTALBOOTH("摊位出租转让",15),
//	   		   JOBWANTED("求职专区",31);
//	   	   getIsAuthentication()=1已实名，  role=6商铺出租/求职，
	    		//TODO此处还需要优化
	    	if(permissionid!=PermissionCode.LEASE.getCode() && permissionid!=PermissionCode.RENTALBOOTH.getCode()
	    			&& permissionid!=PermissionCode.JOBWANTED.getCode() ) {	
	   	    	 if(user.getIsAuthentication()!=1) {
	   	    		 return ServerResponse.createByErrorMessage("发布该项内容需要完善用户信息，请先到用户中心进行信息完善"); 
	   	    	 }
	   	    	 
	   	    	 
	   	     }
	    		
	    		//粮油code需要在PermissionCode中和数据库id保持一致
	    		if(permissionid ==PermissionCode.GRAINANDOIL.getCode() ) {
	    		return 	releaseGrainAndOil(permissionid,commoditytype, userId,role
	    					, params);
		
	    		}else {
	    			//如果不等于所有就不是此菜单
	    		 return ServerResponse.createByErrorMessage("不存在的菜单");
	    		}
			
	    	}	
	 	
	 	return ServerResponse.createByErrorMessage("系统异常稍后重试");
	}
  
	
	
//菜单五已经实现 （更新图片）	
	public ServerResponse<String> releaseGrainAndOil(Integer permissionid,String commoditytype, long userId,Integer role
			, Map<String, Object> params){
		System.out.println("___________________________________"+params.get("pictureUrl"));
		System.out.println(params.get("pictureUrl").getClass());
		
		ArrayList<String> list= (ArrayList<String>) params.get("pictureUrl");
		
		System.out.println(list.size());
		
		
		ServerResponse<String> checkroleString=checkRoleAndcommodityType(permissionid, commoditytype, userId,role);
		if(!checkroleString.getMsg().equals("success")) {
			return checkroleString;
		}
      //在这里查看总数在枚举ReleaseCount中定义

		int count= ReleaseCount.GRAINANDOIL.getCount() ;		
		if(grainAndOilMapper.checkReleaseCount(userId)>=count)
		{
			return ServerResponse.createByErrorMessage("发布总数已经超过 "+count+"个,请删除一部分同类型发布再试");
		}

		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	
	 	params.put("userId", userId); 	
	 	params.put("numberOfChecks", 0L);
	 	params.put("approval_status", 1);
	 	params.put("isReceivingPurchase", 1);//初始化都是再有效期
	 	params.put("approval_status", 1);//初始化审核中
	 	params.put("createTime", formatter.format(new Date()));
	 	
	 	params.put("pictureUrl", params.get("pictureUrl").toString());
	 	params.put("isDelete", 1);
	 	
	 	GrainAndOil grainAndOil=(GrainAndOil) BeanMapConvertUtil.convertMap(GrainAndOil.class,params);
			
	 	
	 	//{result=true, message=验证通过} 返回结果
	 	System.out.println(AnnotationDealUtil.validate(grainAndOil).toString());
	 	Map<String, Object> checknullMap=AnnotationDealUtil.validate(grainAndOil);
	 	if((boolean)checknullMap.get("result")==true && ((String)checknullMap.get("message")).equals("验证通过")) {
	 		
	 		//落库
	 		grainAndOilMapper.caeateGrainAndOil(grainAndOil) ;
	 		//更新图片  TODO
	 		return ServerResponse.createBySuccess("创建成功");
	 		
	 	}else if((boolean)checknullMap.get("result")==false) {
	 		return ServerResponse.createByErrorMessage((String)checknullMap.get("message"));
	 	}else {
	 		return ServerResponse.createByErrorMessage("系统异常稍后重试");
	 	}
		
		
	}
	
	
	
	
	
	//校验
	public  ServerResponse<String> checkRoleAndcommodityType(int permissionid ,String commoditytype,long userId,int role) {
		  int isroleAndtype=0; 
			//取得是总条数，后期可能会有一个用户多个角色的情况
		
		
		 isroleAndtype=tRolePermissionMapper.isrole(userId,permissionid);
		 if(isroleAndtype<1) {
			//检查用户有没有此菜单权限,role查 t_role_permission表 	
			 return ServerResponse.createByErrorMessage("没有此菜单权限");  
		   }	
		 
		 //检查有没有发布权限是否有发布权限1有2没有
		 isroleAndtype= tRolePermissionMapper.isrelease(role,permissionid);
		 if(isroleAndtype!=1) {
			 return ServerResponse.createByErrorMessage("没有此菜单发布信息权限"); 
		   
		 }
		//检查菜单下有无此类型商品
		   isroleAndtype=commodityTypeMapper.getcommodityType(permissionid ,commoditytype);
		   if(isroleAndtype<1) {
  				
			   return ServerResponse.createByErrorMessage("该菜单下无此类型商品"); 
  		   }
 
		   return ServerResponse.createBySuccessMessage("success");	  		  
	  }
	
	  
	
	
	
	
	  //更新图片库
	
}
