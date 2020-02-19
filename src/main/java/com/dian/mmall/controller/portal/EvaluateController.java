package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.EvaluateService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI + "evaluate/")
public class EvaluateController {
	@Autowired
	private EvaluateService evaluateService;

	// 评价
	@RequestMapping(value = "create_evaluate", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_evaluate(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		ServerResponse<String> serverResponse1 = CheckLand.getCreateRole(user, params);
		if (serverResponse1.getStatus() != 0) {
			return serverResponse1;
		}

		return evaluateService.create_evaluate(user, params);

	}
}
