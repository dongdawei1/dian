package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.zhiwei.Position;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping(Const.PCAPI + "releaseWelfare/")
public class ReleaseWelfareController {
	private String recruitWorkers = "/home/recruitWorkers";

	@Autowired
	private ReleaseWelfareService releaseWelfareService;

	// 商户创建职位信息
	@RequestMapping(value = "create_position", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_position(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getRole() != 1 && user.getRole() != 5 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return releaseWelfareService.create_position(user, params);

	}

	// 商户获取获取自己发布的除删除外的全部信息

	@RequestMapping(value = "get_position_list", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_position_list(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getRole() != 1 && user.getRole() != 5 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return releaseWelfareService.get_position_list(user, params);

	}

	// 职位分页

	@RequestMapping(value = "get_position_all", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> get_position_all(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return releaseWelfareService.get_position_all(user, params);

	}

	// 职位操作列

	@RequestMapping(value = "position_operation", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> position_operation(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {

		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限
		if (user.getRole() != 1 && user.getRole() != 5 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return releaseWelfareService.position_operation(user, params);

	}

	// 获取职位类型

	@RequestMapping(value = "get_position", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getPosition(HttpServletRequest httpServletRequest) {

		User user = (User) httpServletRequest.getAttribute("user");
		int role = user.getRole();
		List<String> list = new ArrayList<String>();

		Position[] positions = Position.values();

		if (role == 2) {
			for (int i = 0; i < positions.length; i++) {
				if (positions[i].getRoleId() == 2) {
					list.add(positions[i].getPositionType());
				}
			}
		} else if (role == 5) {  //暂时未做
			for (int i = 0; i < positions.length; i++) {
				if (positions[i].getRoleId() == 5) {
					list.add(positions[i].getPositionType());
				}
			}
		} else {
			for (int i = 0; i < positions.length; i++) {
				list.add(positions[i].getPositionType());
			}
		}
		return ServerResponse.createBySuccess(list);

	}

}
