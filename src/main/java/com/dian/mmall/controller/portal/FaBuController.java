package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.FabuService;

@Controller
@RequestMapping(Const.PCAPI + "fabu/")
public class FaBuController {

	@Autowired
	private FabuService fabuService;

	
	@RequestMapping(value = "createfabu", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> createfabu(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return fabuService.createfabu(user, params);

	}

	
	
	
	
	
//	
//	
	// 商户获取获取自己发布的除删除外的全部信息

	@RequestMapping(value = "getmyfabu", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getmyfabu(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		
		
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return fabuService.getmyfabu(user, params);

	}
//
	// 操作列

	@RequestMapping(value = "upfabu", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> upfabu(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return fabuService.upfabu(user, params);

	}
//
//商户根据id获取详请编辑

	@RequestMapping(value = "getmyfabubyid", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getmyfabubyid(HttpServletRequest httpServletRequest,
			@RequestParam long id) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return fabuService.getmyfabubyid(user.getId(), id);

	}

//
////根据类型获取全部标题
//
//	@RequestMapping(value = "getFoodAndGrainTitleList", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getFoodAndGrainTitleList(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {
//
//		User user = (User) httpServletRequest.getAttribute("user");
//		int role = user.getRole();
//		if (role != 1 && role != 4) {
//			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
//		}
//
//		return foodAndGrainService.getFoodAndGrainTitleList(params);
//
//	}
//
//	// 公开列表
//
//	@RequestMapping(value = "getFoodAndGrainPublicList", method = RequestMethod.POST)
//	@ResponseBody
//	public ServerResponse<Object> getFoodAndGrainPublicList(HttpServletRequest httpServletRequest,
//			@RequestBody Map<String, Object> params) {	
//
//		return foodAndGrainService.getFoodAndGrainPublicList(params);
//
//	}
//	// 公开获取id
//
//	@RequestMapping(value = "getFoodAndGrainDetails", method = RequestMethod.GET)
//	@ResponseBody
//	public ServerResponse<Object> getFoodAndGrainDetails(HttpServletRequest httpServletRequest, @RequestParam long id) {
//
//		return foodAndGrainService.getFoodAndGrainDetails(id);
//
//	}
}
