//package com.dian.mmall.controller.portal;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.dian.mmall.common.Const;
//import com.dian.mmall.common.ResponseMessage;
//import com.dian.mmall.common.ServerResponse;
//import com.dian.mmall.pojo.user.User;
//import com.dian.mmall.service.release.DepartmentStoreService;
//import com.dian.mmall.service.release.FoodAndGrainService;
//
//@Controller
//@RequestMapping(Const.PCAPI + "departmentStore/")
//public class DepartmentStoreController {
//
//	@Autowired
//	private DepartmentStoreService departmentStoreService;
//
//	// 百货
//	@RequestMapping(value = "create_departmentStore", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> create_departmentStore(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 12) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return departmentStoreService.create_departmentStore(user, params);
//
//	}
//
//	// 商户获取获取自己发布的除删除外的全部信息
//
//	@RequestMapping(value = "get_myDepartmentStore_list", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> get_myDepartmentStore_list(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 12) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return departmentStoreService.get_myDepartmentStore_list(user, params);
//
//	}
//
//	// 操作列
//
//	@RequestMapping(value = "operation_userDepartmentStore", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> operation_userDepartmentStore(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 12) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return departmentStoreService.operation_userDepartmentStore(user, params);
//
//	}
//
////商户根据id获取详请编辑
//
//	@RequestMapping(value = "get_userDepartmentStore_id", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> get_userDepartmentStore_id(HttpServletRequest httpServletRequest,
//			@RequestParam long id) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		int role = user.getRole();
//		if (role != 1 && role != 4) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
//		}
//		return departmentStoreService.get_userDepartmentStore_id(user.getId(), id);
//
//	}
//
////根据类型获取全部标题
//
//	@RequestMapping(value = "getDepartmentStoreTitleList", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getDepartmentStoreTitleList(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		return departmentStoreService.getDepartmentStoreTitleList(params);
//
//	}
//
//	// 公开列表
//
//	@RequestMapping(value = "getDepartmentStorePublicList", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getDepartmentStorePublicList(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		return departmentStoreService.getDepartmentStorePublicList(params);
//
//	}
//	// 公开获取id
//
//	@RequestMapping(value = "getDepartmentStoreDetails", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> getDepartmentStoreDetails(HttpServletRequest httpServletRequest,
//			@RequestParam long id) {
//
//		return departmentStoreService.getDepartmentStoreDetails(id);
//
////	}
//
//}
