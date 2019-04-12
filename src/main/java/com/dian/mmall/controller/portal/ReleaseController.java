package com.dian.mmall.controller.portal;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CommodityTypeMapper;
import com.dian.mmall.pojo.User;
import com.dian.mmall.pojo.commodity.GrainAndOil;
import com.dian.mmall.service.CommodityTypeService;
import com.dian.mmall.service.release.TRolePermissionService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/release/")
public class ReleaseController {
	
	  @Autowired
	    private TRolePermissionService tPermissionService;
	  @Autowired
	    private CommodityTypeService commodityTypeService;
	
	  
	@RequestMapping(value = "grainAndOil",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> grainAndOil(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest){
		
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage("用户登陆已过期");
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	if(user == null){
    		return ServerResponse.createByErrorMessage("用户登陆已过期");
    	}	
    	
    	if(!LegalCheck.LegalCheckFrom(params)) {	
			return ServerResponse.createByErrorMessage("内容中有非法输入不能包含delete,uptate");
		
		}
    	
    /*	
    	//private Integer userId;  //必填后端获取
    	private Integer numberOfChecks; //交易次数
    //	private String  commodityName;  //商品名  前端必传
    	private String  placeOfOrigin; //产地  前端必传
    	private String	brand; //品牌  前端必传
    	private String	specifications;//规格 前端必传
    	private String	price; //价格 // 前端必填
    	private String	priceEffectiveStart;   //必填//有效期开始日期
    	private String	priceEffectiveEnd;  //必填 //有效期结束日期
    	private String	remarks;  //备注 10字以内
    	private Map<String,String> pictureUrl; //图片地址	
    	private Integer isReceivingPurchase;  //必填//判断是否在价格有效期内，1在2 不在 如果在就显示 一键发起采购信息键
    	//permissionId
    	//private String	type;  //商品类型	 //必填 前端传
    	
    	private Integer approval_status;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过
    	private String approval_failure; //审核失败原因
    	
        private String createTime;   //必填
        private String updateTime;
        
        */
    	
    	
        long userId=user.getId();   
    	int permissionid=0;	
    	
    	String permissionid_string= params.get("permissionid").toString().trim() ;    
    	String commoditytype=params.get("commoditytype").toString().trim() ;  
    	
    	//Map<String,String> pictureUrl=(Map<String, String>) params.get("pictureUrl");
    	
    	System.out.println(params.get("pictureUrl").toString());
    	
    	if(permissionid_string!=null && permissionid_string!="") {
       		permissionid=Integer.parseInt(permissionid_string);
  
       		if(permissionid ==5 ) {
       		
       			String checkroleString=checkRoleAndcommodityType(permissionid, commoditytype, userId);
       			
       			if(checkroleString.equals("没有此权限")) {
       				return ServerResponse.createByErrorMessage("没有此权限");
       			}
       			else	if(checkroleString.equals("没有此菜单")) {
       				return ServerResponse.createByErrorMessage("没有此菜单");
       			}
       			else if(checkroleString.equals("success")) {
       			
       								
       			}else {
       			 return ServerResponse.createByErrorMessage("系统异常稍后重试");
       			}
  		
       		}else {
       			//如果不等于5 就不是此菜单
       		 return ServerResponse.createByErrorMessage("不存在的菜单");
       		}
		
       	}	
    	
    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	params.put("userId", userId); 	
    	params.put("numberOfChecks", 0L);
    	params.put("approval_status", 1);
    	params.put("isReceivingPurchase", 1);//初始化都是再有效期
    	params.put("approval_status", 1);//初始化审核中
    	params.put("createTime", formatter.format(new Date()));
    	
    	params.put("pictureUrl", params.get("pictureUrl").toString());
    	
    	
    	GrainAndOil grainAndOil=(GrainAndOil) BeanMapConvertUtil.convertMap(GrainAndOil.class,params);
		
    	
    	//{result=true, message=验证通过} 返回结果
    	System.out.println(AnnotationDealUtil.validate(grainAndOil).toString());
    	
    	System.out.println(grainAndOil.toString());
		return null;
		
		
		
	}
	
	
	
	  public  String checkRoleAndcommodityType(int permissionid ,String commoditytype,long userId) {
		  int isroleAndtype=0; 
			//取得是总条数，后期可能会有一个用户多个角色的情况
		  System.out.println(permissionid+",String"+ commoditytype+",long"+ userId);
    		 isroleAndtype=tPermissionService.isrole(userId,permissionid);
    		 if(isroleAndtype<1) {
    			//检查用户有没有此菜单权限,role查 t_role_permission表 	
    			return "没有此权限";  
    		   }		   
    		   isroleAndtype=commodityTypeService.getcommodityType(permissionid ,commoditytype);
    		   if(isroleAndtype<1) {
       			//检查菜单下有无此类型商品	
       			return "没有此菜单";  
       		   }
    		   
		  return "success";		  		  
	  }
	  
	
	
}
