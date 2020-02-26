package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.EquipmentService;

@Controller
@RequestMapping(Const.PCAPI + "equipment/")
public class EquipmentController {

	@Autowired
	private EquipmentService equipmentService;

	// 创建电器/维修
	@RequestMapping(value = "create_equipment", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_equipment(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.create_equipment(user, params);

	}

	// 商户获取获取自己发布的除删除外的全部信息

	@RequestMapping(value = "get_myEquipment_list", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_myEquipment_list(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.get_myEquipment_list(user, params);

	}

	// 电器操作列

	@RequestMapping(value = "operation_userequipment", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> operation_userequipment(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.operation_userequipment(user, params);

	}

//商户根据id获取详请编辑

	@RequestMapping(value = "get_userequipment_id", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_userequipment_id(HttpServletRequest httpServletRequest, @RequestParam long id) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.get_userequipment_id(user.getId(), id);

	}
	// 公开展示电器维修等列表

	@RequestMapping(value = "getequipmentList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getequipmentList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");

		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.getequipmentList(params);

	}

//根据类型获取全部标题

	@RequestMapping(value = "getEquipmentReleaseTitleList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getEquipmentReleaseTitleList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return equipmentService.getEquipmentReleaseTitleList(params);

	}

	// 公开电器

	@RequestMapping(value = "getEquipmentPublicList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getEquipmentPublicList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return equipmentService.getEquipmentPublicList(params);

	}
	// 公开获取id

	@RequestMapping(value = "getEquipmentDetails", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getEquipmentDetails(HttpServletRequest httpServletRequest, @RequestParam long id) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return equipmentService.getEquipmentDetails(id);

	}
}
