package com.dian.mmall.controller.portal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.PurchaseCreateOrderVoService;
import com.dian.mmall.util.CheckLand;

@Controller
@RequestMapping("/api/orderVoAddCommonMenu/")
public class PurchaseCreateOrderVoController {
	@Autowired
	private PurchaseCreateOrderVoService purchaseCreateOrderVoService;
	@RequestMapping(value = "getPurchaseCreateOrderVo", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getPurchaseCreateOrderVo(HttpServletRequest httpServletRequest) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
     if(user.getRole()!=1 && user.getRole()!=2) {
    	 return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
     }
     if(user.getIsAuthentication()!=2) {
    	 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
     }
		return purchaseCreateOrderVoService.getPurchaseCreateOrderVo(user);

	}
}
