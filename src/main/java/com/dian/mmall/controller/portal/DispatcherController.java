package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.PermissionService;

@Controller
@RequestMapping(Const.PCAPI + "permission")
public class DispatcherController {

	@Autowired
	private PermissionService permissionService;
	// 根据角色过去菜单

	@ResponseBody
	@RequestMapping(value = "/loadData")
	public ServerResponse<Object> loadData(HttpServletRequest httpServletRequest) {

		User user = (User) httpServletRequest.getAttribute("user");

		// 获取用户权限信息
		List<Permission> ps = permissionService.queryPermissionsByUser(user);
		return ServerResponse.createBySuccess(ps);

	}

}
