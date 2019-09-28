package com.dian.mmall.controller.portal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.service.ServiceTypeService;
import com.dian.mmall.service.ToExamineService;
import com.dian.mmall.service.release.DepartmentStoreService;
import com.dian.mmall.service.release.EquipmentService;
import com.dian.mmall.service.release.FoodAndGrainService;
import com.dian.mmall.service.release.MenuAndRenovationAndPestControlService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.service.release.WineAndTablewareService;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisPoolUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/toExamine/")
public class ToExamineController {
	//管理员接口审批
	
	@Autowired  
	private RealNameService realNameService;
	@Autowired
	private ServiceTypeService serviceTypeService;
 
	//获取待实名
    @RequestMapping(value = "getRealNameAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getRealNameAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
 	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
 	if(serverResponse.getStatus()!=0 ) {
 		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
 	}
    	return realNameService.getRealNameAll(params);
    }
	//实名审核 
    @RequestMapping(value = "examineRealName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> examineRealName(@RequestBody Map<String,Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
		
     	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
     User user=	(User) serverResponse.getData();
     String loginToken = CookieUtil.readLoginToken(httpServletRequest);
 	 serverResponse= realNameService.examineRealName(user,params, loginToken);
	if(serverResponse.getStatus()==ResponseCode.SUCCESS.getCode()) {
		User shenheUser=(User) serverResponse.getData();
		int result=RedisPoolUtil.checkeKey(shenheUser);
		if(result==0) {
			return ServerResponse.createBySuccessMessage("成功用户登录");
		}else if(result==1) {
			return ServerResponse.createBySuccessMessage("成功用户未登录");
		}
			return ServerResponse.createBySuccessMessage("更新登录信息失败，请联系用户重新登陆");
	}
	 return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	
    }
    
    
    
    @Autowired  
    private ReleaseWelfareService releaseWelfareService;
  //获取待审核招聘
    @RequestMapping(value = "getReleaseWelfareAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getReleaseWelfareAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return releaseWelfareService.getReleaseWelfareAll(params);
    	
    }
    
 
    
    @Autowired
    private ResumeService resumeService;
    //获取待审核简历
    
    @RequestMapping(value = "getTrialResumeAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getTrialResumeAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return resumeService.getTrialResumeAll(params);
    	
    }
 
    
    @Autowired
    private MenuAndRenovationAndPestControlService menuAndRenovationAndPestControlService;
    //获取待审核装修灭虫
    
    @RequestMapping(value = "getmrpAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getmrpAll(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return menuAndRenovationAndPestControlService.getmrpAll(params);
    	
    }
    
    @Autowired
    private RentService rentService;
    //获取待审核出租
    @RequestMapping(value = "adminMent",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> adminMent(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return rentService.adminMent(params);
    	
    }
    
    @Autowired
    private EquipmentService equipmentService;
    //获取待审核二手电器
    @RequestMapping(value = "adminEquipment",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> adminEquipment(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return equipmentService.adminEquipment(params);
    	
    }
    
    @Autowired
    private WineAndTablewareService wineAndTablewareService;
    //获取待审核二手电器
    @RequestMapping(value = "adminWineAndTableware",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> adminWineAndTableware(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return wineAndTablewareService.adminWineAndTableware(params);
    	
    }
    
    @Autowired
    private FoodAndGrainService foodAndGrainService;
    //获取待审核米菜
    @RequestMapping(value = "adminFoodAndGrain",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> adminFoodAndGrain(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return foodAndGrainService.adminFoodAndGrain(params);
    	
    }
    
    @Autowired
	private DepartmentStoreService  departmentStoreService;
    //获取待审核米菜
    @RequestMapping(value = "adminDepartmentStore",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> adminDepartmentStore(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	
    	
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	
    	return departmentStoreService.adminDepartmentStore(params);
    	
    }
    
    @Autowired
    private ToExamineService  toExamineService;
    //除实名外的全部审核
    @RequestMapping(value = "examineAll",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> examineAll(@RequestBody Map<String,Object> params, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpSession session){
		
     	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
     User user=	(User) serverResponse.getData();
 	 return toExamineService.examineAll(user,params);

    } 
    
    //查询签约地址
    @RequestMapping(value = "getAddressDetailed",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getAddressDetailed( HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
		
     	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
 	 return toExamineService.getAddressDetailed(params);
    } 
    
    //创建培训地址
    @RequestMapping(value = "createAddressDetailed",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> createAddressDetailed( HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
		
     	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
     User user=	(User) serverResponse.getData(); 
 	 return toExamineService.createAddressDetailed(user.getUsername(),params);

    } 
    
	//创建服务名称
    @RequestMapping(value = "admin_create_serviceType",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> admin_create_serviceType(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//TODO只有管理员才能调用
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
    	
     	User user = (User) serverResponse.getData();
     	//检查是否有管理员权限
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
        
        return serviceTypeService.admin_create_serviceType(user,params);
    
    } 
    
  //获取待提交接单人员名单
    @RequestMapping(value = "admin_select_addOrder",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> admin_select_addOrder(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//TODO只有管理员才能调用
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
        return realNameService.admin_select_addOrder(params);
        
    } 
    
  //更新待提交接单人员名单状态
    @RequestMapping(value = "admin_update_addOrder",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> admin_update_addOrder(HttpServletRequest httpServletRequest,@RequestBody Map<String, Object> params){
    	//TODO只有管理员才能调用
    	ServerResponse<Object> serverResponse=CheckLand.checke_role(httpServletRequest);
     	if(serverResponse.getStatus()!=0 ) {
     		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
     	}
     	
        User user=	(User) serverResponse.getData(); 
        return realNameService.admin_update_addOrder(user.getUsername(),params);
        
    } 
}
