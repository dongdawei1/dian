package com.dian.mmall.controller.portal;

import java.util.HashMap;
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
import com.dian.mmall.service.release.FoodAndGrainService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI + "foodAndGrain/")
public class FoodAndGrainController {
	@Autowired
	private FoodAndGrainService foodAndGrainService;

	/** 零售市场 */
	@RequestMapping(value = "create_foodAndGrain", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_foodAndGrain(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return foodAndGrainService.create_foodAndGrain(user, params);

	}

	// 商户获取获取自己发布的除删除外的全部信息

	@RequestMapping(value = "get_myFoodAndGrain_list", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_FoodAndGrain_list(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return foodAndGrainService.get_myFoodAndGrain_list(user, params);

	}

	// 操作列

	@RequestMapping(value = "operation_userFoodAndGrain", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> operation_userFoodAndGrain(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return foodAndGrainService.operation_userFoodAndGrain(user, params);

	}

//商户根据id获取详请编辑

	@RequestMapping(value = "get_userFoodAndGrain_id", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_userFoodAndGrain_id(HttpServletRequest httpServletRequest,
			@RequestParam long id) {
		User user = (User) httpServletRequest.getAttribute("user");
		int role = user.getRole();
		if (role != 1 && role != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
		return foodAndGrainService.get_userFoodAndGrain_id(user.getId(), id);

	}
	// 没有用

	@RequestMapping(value = "getFoodAndGrainList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getFoodAndGrainList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		

		ServerResponse<String> serverResponse1 = CheckLand.checke_see((User) httpServletRequest.getAttribute("user"),
				params);
		if (serverResponse1.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
		}

		return foodAndGrainService.getFoodAndGrainList(params);

	}

//根据类型获取全部标题

	@RequestMapping(value = "getFoodAndGrainTitleList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getFoodAndGrainTitleList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		int role = user.getRole();
		if (role != 1 && role != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}

		return foodAndGrainService.getFoodAndGrainTitleList(params);

	}

	// 公开列表

	@RequestMapping(value = "getFoodAndGrainPublicList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getFoodAndGrainPublicList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {	

		return foodAndGrainService.getFoodAndGrainPublicList(params);

	}
	// 公开获取id

	@RequestMapping(value = "getFoodAndGrainDetails", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getFoodAndGrainDetails(HttpServletRequest httpServletRequest, @RequestParam long id) {

		return foodAndGrainService.getFoodAndGrainDetails(id);

	}
}
