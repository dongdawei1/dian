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
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.ResumeService;

@Controller
@RequestMapping(Const.PCAPI + "resume/")
public class ResumeController {
	@Autowired
	private ResumeService resumeService;

	// 创建简历
	@RequestMapping(value = "create_resume", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_resume(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 11 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return resumeService.create_resume(user, params);

	}

	// 用户获取自己创建的简历
	@RequestMapping(value = "select_resume_by_id", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> select_resume_by_id(HttpServletRequest httpServletRequest) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getRole() != 1 && user.getRole() != 11 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return resumeService.select_resume_by_id(user.getId());

	}

	// 编辑简历
	@RequestMapping(value = "operation_resume", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> operation_resume(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 11 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return resumeService.operation_resume(user, params);

	}
	// 查看简历列表

	@RequestMapping(value = "get_resume_all", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_resume_all(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		return resumeService.get_resume_all(user, params);

	}
}
