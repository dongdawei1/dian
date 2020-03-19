//package com.dian.mmall.controller.portal;
//
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
//import com.dian.mmall.service.release.WholesaleCommodityService;
//
//@Controller
//@RequestMapping(Const.PCAPI + "wholesaleCommodity/")
//public class WholesaleCommodityController {
//	@Autowired
//	private WholesaleCommodityService wholesaleCommodityService;
//
//	// 批发创建
//	@RequestMapping(value = "create_wholesaleCommodity", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> create_wholesaleCommodity(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 13) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return wholesaleCommodityService.create_wholesaleCommodity(user, params);
//
//	}
//
//	// 获取商品名
//	@RequestMapping(value = "get_wholesaleCommodity_serviceType", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> get_wholesaleCommodity_serviceType(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return wholesaleCommodityService.get_wholesaleCommodity_serviceType(user.getId(), params);
//
//	}
//
//	// 获取商品名
//	@RequestMapping(value = "wholesaleCommodity_serviceType", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> wholesaleCommodity_serviceType(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return wholesaleCommodityService.wholesaleCommodity_serviceType(params);
//
//	}
//
//	@RequestMapping(value = "get_myWholesaleCommodity_list", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> get_myWholesaleCommodity_list(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 13) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return wholesaleCommodityService.get_myWholesaleCommodity_list(user.getId(), params);
//
//	}
//
//	// 批发操作列
//	@RequestMapping(value = "operation_userWholesaleCommodity", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<String> operation_userWholesaleCommodity(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 13) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//		return wholesaleCommodityService.operation_userWholesaleCommodity(user.getId(), params);
//	}
//
////商户根据id获取详请编辑
//
//	@RequestMapping(value = "get_userWholesaleCommodity_id", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> get_userWholesaleCommodity_id(HttpServletRequest httpServletRequest,
//			@RequestParam long id) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() != 1 && user.getRole() != 13) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//		return wholesaleCommodityService.get_userWholesaleCommodity_id(user.getId(), id);
//
//	}
//
//	// 公开列表
//
//	@RequestMapping(value = "getWholesaleCommodityPublicList", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getWholesaleCommodityPublicList(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//		User user = (User) httpServletRequest.getAttribute("user");
//		if (user.getRole() == 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//
//		return wholesaleCommodityService.getWholesaleCommodityPublicList(params);
//
//	}
//	// 公开展示灭虫装修等列表
//
//	@RequestMapping(value = "getWholesaleCommodityPublicId", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> getWholesaleCommodityPublicId(HttpServletRequest httpServletRequest,
//			@RequestParam long id) {
//
//		User user = (User) httpServletRequest.getAttribute("user");
//		// 检查权限
//		if (user.getRole() == 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
//		}
//		if (user.getIsAuthentication() != 2) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
//		}
//		return wholesaleCommodityService.getWholesaleCommodityPublicId(id);
//
//	}
//
//}
