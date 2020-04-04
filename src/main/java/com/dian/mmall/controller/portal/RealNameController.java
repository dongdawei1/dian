package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LogUtil;
import com.dian.mmall.util.RedisPoolUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping(Const.PCAPI + "realName/")
public class RealNameController {

	@Autowired
	private RealNameService realNameService;

	// 用户实名
	@RequestMapping(value = "newRealName", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> newRealName(@RequestBody Map<String, Object> params,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session) {
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		User user = (User) httpServletRequest.getAttribute("user");

		ServerResponse<Object> serverResponse = realNameService.newRealName(user, loginToken, params);

		if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {

			RedisPoolUtil.checkeKey((User) serverResponse.getData());
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		}

		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	}

	// 重新用户实名
	@RequestMapping(value = "updateRealName", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> updateRealName(@RequestBody Map<String, Object> params,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session) {
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		User user = (User) httpServletRequest.getAttribute("user");
		ServerResponse<Object> serverResponse = realNameService.updateRealName(user, loginToken, params);

		if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
			RedisPoolUtil.checkeKey((User) serverResponse.getData());
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		}
		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
	}



	// 获取实名信息
	@RequestMapping(value = "getUserRealName", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getUserRealName(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return realNameService.getUserRealName((User) httpServletRequest.getAttribute("user"));

	}

	// 获取实名信息
	@RequestMapping(value = "getRealNameById", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getRealNameById(HttpServletRequest httpServletRequest, @RequestParam long id) {
		return realNameService.getRealNameById(id);

	}

	// 获取实名信息
	@RequestMapping(value = "getRealNameByuserId", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getRealNameByuserId(HttpServletRequest httpServletRequest, @RequestParam long id) {

		return realNameService.getRealNameByuserId(id);

	}

	// 重新用户实名
	@RequestMapping(value = "addOrder", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> addOrder(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2 || (user.getRole() != 4 && user.getRole() != 1)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhiyoushiming.getMessage());
		}

		return realNameService.addOrder(user, params);
	}

	
	
	
//	public  String getDetailed(	User user) {
//		
//		return realNameService.getDetailed(user.getId());
//	}

}
