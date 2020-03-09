package com.dian.mmall.controller.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI + "bunner/")
public class DibuBunnerController {
	@Autowired
	private BunnerService bunnerService;

	@RequestMapping(value = "getBunner", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getBunner(HttpServletRequest httpServletRequest, @RequestParam Integer permissionid,
			@RequestParam Integer bunnerType) {
		User user = (User) httpServletRequest.getAttribute("user");

		return bunnerService.getBunnerList(user.getRole(), permissionid, bunnerType, user.getId());

	}
	
	//用这个获取广告
	@RequestMapping(value = "getpguang", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getpguang(HttpServletRequest httpServletRequest, @RequestParam Integer permissionid,
			@RequestParam Integer bunnerType) {
		User user = (User) httpServletRequest.getAttribute("user");
        String appid= httpServletRequest.getHeader("appid");
		return bunnerService.getpguang(user, permissionid, bunnerType, appid);

	}
}
