package com.dian.mmall.controller.portal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.config.WeChatConfig;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.service.release.WineAndTablewareService;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.IpUtils;
import com.dian.mmall.util.WXPayUtil;

@Controller
@RequestMapping("/api/order/")
public class OrderController {
	private String StringPath = "/home/order";
	@Autowired
	private OrderService orderService;
	@Autowired
	private WeChatConfig weChatConfig;

	// 零售商创建批发订单
	@RequestMapping(value = "create_wholesaleCommodity_order", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_wholesaleCommodity_order(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限
		params.put("StringPath", StringPath);
		ServerResponse<String> serverResponse1 = CheckLand.getCreateRole(user, params);
		if (serverResponse1.getStatus() != 0) {
			return serverResponse1;
		}

		return orderService.create_wholesaleCommodity_order(user.getId(), params);

	}

	// 商户发布采购订单 预估价格
	@RequestMapping(value = "create_order_evaluation", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_order_evaluation(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		return orderService.create_order_evaluation(user, params);

	}

	// 商户发布采购订单
	@RequestMapping(value = "create_purchase_order", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_purchase_order(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		return orderService.create_purchase_order(user, params);

	}

	// 商户今天发布的采购订单
	@RequestMapping(value = "get_conduct_purchase_order", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_conduct_purchase_order(HttpServletRequest httpServletRequest,
			@RequestParam String uuid) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		return orderService.get_conduct_purchase_order(user);

	}

	// 商户编辑采购订单
	@RequestMapping(value = "operation_purchase_order", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> operation_purchase_order(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		return orderService.operation_purchase_order(user, params);

	}

	// 商户扫码支付
	@RequestMapping(value = "native_pay_order", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> native_pay_order(HttpServletRequest httpServletRequest, @RequestParam long id) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		String spbillCreateIp = IpUtils.getIpAddr(httpServletRequest);
		return orderService.native_pay_order(user, spbillCreateIp, id);

	}
	
	/**
	 * 获取是否有待支付的支付订单
	 * */ 
	@RequestMapping(value = "get_pay_order_all", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> get_pay_order_all(HttpServletRequest httpServletRequest,@RequestParam String uuid) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		String spbillCreateIp = IpUtils.getIpAddr(httpServletRequest);
		return orderService.get_pay_order_all(user.getId());

	}

	
	/**
	 * 根据orderId查询支付状态
	 * */ 
	@RequestMapping(value = "get_pay_order_byOrderId", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> get_pay_order_byOrderId(HttpServletRequest httpServletRequest, @RequestParam long orderId,@RequestParam String uuid) {
		// 检查登陆
		ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User user = (User) serverResponse.getData();
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		String spbillCreateIp = IpUtils.getIpAddr(httpServletRequest);
		return orderService.get_pay_order_byOrderId(user.getId(),orderId);

	}
	
	/**
	 * 微信支付回调
	 */
	@RequestMapping("callback")
	public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {

		InputStream inputStream = request.getInputStream();

		// BufferedReader是包装设计模式，性能更搞
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		inputStream.close();
		Map<String, String> callbackMap = WXPayUtil.xmlToMap(sb.toString());

		System.out.println(callbackMap.toString());

		SortedMap<String, String> sortedMap = WXPayUtil.getSortedMap(callbackMap);
		// 判断签名是否正确
		if (WXPayUtil.isCorrectSign(sortedMap, weChatConfig.getKey())) {
			sortedMap.remove("appid");
			sortedMap.remove("mch_id");
			sortedMap.remove("device_info");
			sortedMap.remove("openid");
			sortedMap.remove("is_subscribe");
			sortedMap.put("payType", "HD");
			ServerResponse<String> serverResponse = orderService.callback(sortedMap);

			if (serverResponse.getStatus() == 0) {
				response.setContentType("text/xml");
				response.getWriter().println("success");
				return;
			} else {
				// 都处理失败
				response.setContentType("text/xml");
				response.getWriter().println("fail");
			}
		}
		// 都处理失败
		response.setContentType("text/xml");
		response.getWriter().println("fail");

	}



    /**
     * 接单用户获取待处理订单
     * */
	@RequestMapping(value = "peceiptGetPendingOrders", method = RequestMethod.POST)
	@ResponseBody
public ServerResponse<Object> peceiptGetPendingOrders(HttpServletRequest httpServletRequest,
		@RequestBody Map<String, Object> params){
	// 检查登陆
			ServerResponse<Object> serverResponse = CheckLand.checke_land(httpServletRequest);
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
			User user = (User) serverResponse.getData();
			if(user.getIsAuthentication()!=2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
			}

			return orderService.peceiptGetPendingOrders(user.getId(),params);

}

}
