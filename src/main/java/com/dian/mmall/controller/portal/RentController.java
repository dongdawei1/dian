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
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI + "rent/")
public class RentController {

	@Autowired
	private RentService rentService;

	// 创建出租
	@RequestMapping(value = "create_rent", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_rent(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");

		String releaseType = params.get("releaseType").toString().trim();
		if (releaseType == null || releaseType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int createType = Integer.valueOf(releaseType);
		if (createType == 14) {
			params.put("StringPath", "/home/lease");
		} else if (createType == 15) {
			params.put("StringPath", "/home/rentalBooth");
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		// 检查权限
		ServerResponse<String> serverResponse1 = CheckLand.getCreateRole(user, params);
		if (serverResponse1.getStatus() != 0) {
			return serverResponse1;
		}
		return rentService.create_rent(user, params);

	}
	// 商户获取获取自己发布的除删除外的全部信息

	@RequestMapping(value = "get_myRent_list", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_myRent_list(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		int role = user.getRole();
		if (role == 2 || role == 3) {
			params.put("StringPath", "/home/lease");
		} else {
			params.put("StringPath", "/home/rentalBooth");
		}

		// 检查权限

		ServerResponse<String> serverResponse1 = CheckLand.getCreateRole(user, params);
		if (serverResponse1.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
		}

		return rentService.get_myRent_list(user, params);

	}

	// 出租操作列

	@RequestMapping(value = "operation_userment", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> operation_userment(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		String releaseType = params.get("releaseType").toString().trim();
		if (releaseType == null || releaseType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int createType = Integer.valueOf(releaseType);
		if (createType == 14) {
			params.put("StringPath", "/home/lease");
		} else if (createType == 15) {
			params.put("StringPath", "/home/rentalBooth");
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		ServerResponse<String> serverResponse1 = CheckLand.getCreateRole(user, params);
		if (serverResponse1.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
		}

		return rentService.operation_userment(user, params);

	}

	// 商户根据id获取详请编辑

	@RequestMapping(value = "get_userrent_id", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_userrent_id(HttpServletRequest httpServletRequest, @RequestParam long id) {
		User user = (User) httpServletRequest.getAttribute("user");
		int role = user.getRole();
		if (role != 1 && role != 2 && role != 3 && role != 4 && role != 5 && role != 6) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
		return rentService.get_userrent_id(user.getId(), id);

	}

	// 获取title
	@RequestMapping(value = "getServiceDetailedList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getServiceDetailedList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		String releaseType = params.get("releaseType").toString().trim();
		if (releaseType == null || releaseType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int createType = Integer.valueOf(releaseType);
		if (createType == 14) {
			params.put("StringPath", "/home/lease");
		} else if (createType == 15) {
			params.put("StringPath", "/home/rentalBooth");
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}

		ServerResponse<String> serverResponse1 = CheckLand.checke_see(user, params);
		if (serverResponse1.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
		}

		return rentService.getServiceDetailedList(params);

	}

	// 公开展示灭虫装修等列表

	@RequestMapping(value = "getrentList", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getrentList(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		String releaseType = params.get("releaseType").toString().trim();
		if (releaseType == null || releaseType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int createType = Integer.valueOf(releaseType);
		if (createType == 14) {
			params.put("StringPath", "/home/lease");
		} else if (createType == 15) {
			params.put("StringPath", "/home/rentalBooth");
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		ServerResponse<String> serverResponse1 = CheckLand.checke_see(user, params);
		if (serverResponse1.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse1.getMsg());
		}

		return rentService.getrentList(params);

	}
	// 根据id获取详请编辑

	@RequestMapping(value = "get_rent_id", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_rent_id(HttpServletRequest httpServletRequest, @RequestParam long id) {

		int role = ((User) httpServletRequest.getAttribute("user")).getRole();
		if (role != 1 && role != 2 && role != 3 && role != 4 && role != 5 && role != 6 && role != 11) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
		return rentService.get_rent_id(id);

	}

}
