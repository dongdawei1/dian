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
import com.dian.mmall.service.WholesaleService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping(Const.PCAPI+"wholesale/")
public class WholesaleController {
	
	@Autowired
	private WholesaleService wholesaleService;
	
	/**市场名*/
	@RequestMapping(value = "getwholesale", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> getwholesale(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user =	(User) httpServletRequest.getAttribute("user"); 
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return wholesaleService.getwholesale(params);

	}
	
	
	
}
