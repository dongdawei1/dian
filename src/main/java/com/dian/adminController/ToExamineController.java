//package com.dian.adminController;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.dian.mmall.common.Const;
//import com.dian.mmall.common.ResponseCode;
//import com.dian.mmall.common.ResponseMessage;
//import com.dian.mmall.common.ServerResponse;
//import com.dian.mmall.pojo.CreateGanggaoVo;
//import com.dian.mmall.pojo.user.RealName;
//import com.dian.mmall.pojo.user.User;
//import com.dian.mmall.service.BunnerService;
//import com.dian.mmall.service.IUserService;
//import com.dian.mmall.service.RealNameService;
//import com.dian.mmall.service.ServiceTypeService;
//import com.dian.mmall.service.ToExamineService;
//import com.dian.mmall.service.release.ReleaseWelfareService;
//
//import com.dian.mmall.util.CookieUtil;
//import com.dian.mmall.util.JsonUtil;
//import com.dian.mmall.util.LegalCheck;
//import com.dian.mmall.util.RedisPoolUtil;
//import com.dian.mmall.util.RedisShardedPoolUtil;
//import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
//
//@Controller
//@RequestMapping(Const.APIV5 + "toExamine/")
//public class ToExamineController {
//	// 管理员接口审批
//
//	@Autowired
//	private RealNameService realNameService;
//	@Autowired
//	private ServiceTypeService serviceTypeService;
//	@Autowired
//	private IUserService iUserService;
//	@Autowired
//	private BunnerService bunnerService;
//	// 获取待实名
//	@RequestMapping(value = "getRealNameAll", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getRealNameAll(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return realNameService.getRealNameAll(params);
//	}
//
//	// 实名审核
//	@RequestMapping(value = "examineRealName", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> examineRealName(@RequestBody Map<String, Object> params,
//			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session) {
//
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		// 检查是否有管理员权限
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//
//		serverResponse = realNameService.examineRealName(user, params, loginToken);
//		if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
//			User shenheUser = (User) serverResponse.getData();
//			int result = RedisPoolUtil.checkeKey(shenheUser);
//			if (result == 0) {
//				return ServerResponse.createBySuccessMessage("成功用户登录");
//			} else if (result == 1) {
//				return ServerResponse.createBySuccessMessage("成功用户未登录");
//			}
//			return ServerResponse.createBySuccessMessage("更新登录信息失败，请联系用户重新登陆");
//		}
//		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//
//	}
//
//	@Autowired
//	private ReleaseWelfareService releaseWelfareService;
//
//	// 获取待审核招聘
//	@RequestMapping(value = "getReleaseWelfareAll", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getReleaseWelfareAll(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return releaseWelfareService.getReleaseWelfareAll(params);
//
//	}
//
//	@Autowired
//	private ResumeService resumeService;
//	// 获取待审核简历
//
//	@RequestMapping(value = "getTrialResumeAll", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getTrialResumeAll(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return resumeService.getTrialResumeAll(params);
//
//	}
//
//	@Autowired
//	private MenuAndRenovationAndPestControlService menuAndRenovationAndPestControlService;
//	// 获取待审核装修灭虫
//
//	@RequestMapping(value = "getmrpAll", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getmrpAll(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return menuAndRenovationAndPestControlService.getmrpAll(params);
//
//	}
//
//	@Autowired
//	private RentService rentService;
//
//	// 获取待审核出租
//	@RequestMapping(value = "adminMent", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminMent(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return rentService.adminMent(params);
//
//	}
//
//	@Autowired
//	private EquipmentService equipmentService;
//
//	// 获取待审核二手电器
//	@RequestMapping(value = "adminEquipment", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminEquipment(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return equipmentService.adminEquipment(params);
//
//	}
//
//	@Autowired
//	private WineAndTablewareService wineAndTablewareService;
//
//	// 获取待审核二手电器
//	@RequestMapping(value = "adminWineAndTableware", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminWineAndTableware(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return wineAndTablewareService.adminWineAndTableware(params);
//
//	}
//
//	@Autowired
//	private FoodAndGrainService foodAndGrainService;
//
//	// 获取待审核米菜
//	@RequestMapping(value = "adminFoodAndGrain", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminFoodAndGrain(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return foodAndGrainService.adminFoodAndGrain(params);
//
//	}
//
//	@Autowired
//	private DepartmentStoreService departmentStoreService;
//
//	// 获取待审核米菜 零售
//	@RequestMapping(value = "adminDepartmentStore", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminDepartmentStore(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return departmentStoreService.adminDepartmentStore(params);
//
//	}
//
//	@Autowired
//	private WholesaleCommodityService wholesaleCommodityService;
//
//	// 获取待审核米菜 批发
//	@RequestMapping(value = "adminWholesaleCommodity", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> adminWholesaleCommodity(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//
//		return wholesaleCommodityService.adminWholesaleCommodity(params);
//
//	}
//
//	@Autowired
//	private ToExamineService toExamineService;
//
//	// 除实名外的全部审核
//	@RequestMapping(value = "examineAll", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> examineAll(@RequestBody Map<String, Object> params,
//			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session) {
//
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		// 检查是否有管理员权限
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		return toExamineService.examineAll(user, params);
//
//	}
//
//	// 查询签约地址
//	@RequestMapping(value = "getAddressDetailed", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getAddressDetailed(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return toExamineService.getAddressDetailed(params);
//	}
//
//	// 查询签约地址 条件和上边的冲突
//	@RequestMapping(value = "getAccurateressDetailed", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getAccurateressDetailed(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return toExamineService.getAccurateressDetailed(params);
//	}
//
//	// 创建培训地址
//	@RequestMapping(value = "createAddressDetailed", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> createAddressDetailed(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		// 检查是否有管理员权限
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		return toExamineService.createAddressDetailed(user.getUsername(), params);
//
//	}
//
//	// 创建服务名称
//	@RequestMapping(value = "admin_create_serviceType", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> admin_create_serviceType(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		// 检查是否有管理员权限
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		return serviceTypeService.admin_create_serviceType(user, params);
//
//	}
//
//	// 创建批发市场
//	@RequestMapping(value = "admin_create_wholesale", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> admin_create_wholesale(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		// 检查是否有管理员权限
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		return serviceTypeService.admin_create_serviceType(user, params);
//
//	}
//
//	// 获取待提交接单人员名单
//	@RequestMapping(value = "admin_select_addOrder", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> admin_select_addOrder(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return realNameService.admin_select_addOrder(params);
//
//	}
//
//	// 获取待签约接单人员名单
//	@RequestMapping(value = "admin_select_signingOrder", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> admin_select_signingOrder(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return realNameService.admin_select_signingOrder(params);
//
//	}
//
//	// 获取待签约接单人员名单根据id
//	@RequestMapping(value = "admin_select_signingOrderById", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> admin_select_signingOrderById(HttpServletRequest httpServletRequest,
//			@RequestParam long id) {
//		// TODO只有管理员才能调用
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (id < 0) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.shimingidbunengweikong.getMessage());
//		}
//
//		return realNameService.admin_select_signingOrderById(id);
//
//	}
//
//	// 更新待提交接单人员名单状态
//	@RequestMapping(value = "admin_update_addOrder", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> admin_update_addOrder(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		return realNameService.admin_update_addOrder(user.getUsername(), params);
//
//	}
//
//	// 更新待提交接单人员名单状态
//	@RequestMapping(value = "admin_create_orderUser", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> admin_create_orderUser(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//
//		User user = (User) serverResponse.getData();
//		return toExamineService.admin_create_orderUser(user.getUsername(), params);
//
//	}
//
//	// 创建广告前查询实名信息
//	@RequestMapping(value = "admin_guangggao_realName", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> admin_guangggao_realName(HttpServletRequest httpServletRequest,
//			@RequestParam String userName) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//		serverResponse = realNameService.admin_guangggao_realName(userName.trim());
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//		RealName realName = (RealName) serverResponse.getData();
//
//		serverResponse = iUserService.selectUserById(realName.getUserId());
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//		User user = (User) serverResponse.getData();
//		serverResponse = toExamineService.getUserCreate(user);
//		System.out.println(serverResponse);
//		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//		List<CreateGanggaoVo> vos = (List<CreateGanggaoVo>) serverResponse.getData();
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		map.put("realName", realName);
//		map.put("user", user);
//		map.put("vos", vos);
//		return ServerResponse.createBySuccess(map);
//
//	}
//
//	// 管理删除 发布
//	@RequestMapping(value = "adminupall", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> adminupall(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		// TODO只有管理员才能调用
//		ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);		if (serverResponse.getStatus() != 0) {
//			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//		}
//		User user = (User) serverResponse.getData();
//		ServerResponse<String> serverResponse1 = toExamineService.adminupall(user.getUsername(), params);
//		if (serverResponse1.getStatus() == 0) {
//			return ServerResponse.createBySuccessMessage(serverResponse1.getMsg());
//		}
//		return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
//
//	}
//	// 创建广告前查询实名信息
//	@RequestMapping(value = "isguanggao", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> isguanggao(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		if (!LegalCheck.checke_role(httpServletRequest)) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		return bunnerService.isguanggao(params);
//
//	}
//	
//	// 创建广告
//		@RequestMapping(value = "crguanggao", method = RequestMethod.POST)
//		@ResponseBody
//		public ServerResponse<String> crguanggao(HttpServletRequest httpServletRequest,
//				@RequestBody Map<String, Object> params) {
//			ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);			if (serverResponse.getStatus() != 0) {
//				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//			}
//			User user = (User) serverResponse.getData();
//			return bunnerService.crguanggao(user,params);
//
//		}
//		
//		// 获取全部广告
//		@RequestMapping(value = "agetguangaoAll", method = RequestMethod.POST)
//		@ResponseBody
//		public ServerResponse<Object> agetguangaoAll(HttpServletRequest httpServletRequest,
//				@RequestBody Map<String, Object> params) {
//			if (!LegalCheck.checke_role(httpServletRequest)) {
//				return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//			}
//			return bunnerService.agetguangaoAll(params);
//
//		}
//		// 获取全部广告
//		@RequestMapping(value = "aupguangao", method = RequestMethod.POST)
//		@ResponseBody
//		public ServerResponse<String> aupguangao(HttpServletRequest httpServletRequest,
//				@RequestBody Map<String, Object> params) {
//			ServerResponse<Object> serverResponse = LegalCheck.checke_guanli_user(httpServletRequest);
//			if (serverResponse.getStatus() != 0) {
//				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
//			}
//			User user = (User) serverResponse.getData();
//			return bunnerService.aupguangao(user,params);
//
//		}
//}
