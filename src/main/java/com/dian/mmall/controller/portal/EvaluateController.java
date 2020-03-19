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
		

		return evaluateService.create_evaluate(user, params);

	}
}
