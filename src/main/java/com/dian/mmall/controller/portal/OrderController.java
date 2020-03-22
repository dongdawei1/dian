package com.dian.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.util.IpUtils;

@Controller
@RequestMapping(Const.PCAPI+"order/")
public class OrderController {

	@Autowired
	private OrderService orderService;


	// 零售商创建批发订单  TODO  没有调用
	@RequestMapping(value = "create_wholesaleCommodity_order", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_wholesaleCommodity_order(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	//User user =	(User) httpServletRequest.getAttribute("user"); 
		// 检查权限
		return null;//orderService.create_wholesaleCommodity_order(user.getId(), params);

	}

	// 商户发布采购订单 预估价格
	@RequestMapping(value = "create_order_evaluation", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_order_evaluation(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		return orderService.create_order_evaluation(user, params);

	}
   //创建订单
	@RequestMapping(value = "create_purchase_order", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create_purchase_order(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
		User user = (User) httpServletRequest.getAttribute("user");
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
        if(user.getIsAuthentication()!=2) {
        	return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
        }
		return orderService.create_purchase_order(user, params);

	}

	// 商户今天发布的采购订单
	@RequestMapping(value = "get_conduct_purchase_order", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> get_conduct_purchase_order(HttpServletRequest httpServletRequest,
			@RequestParam String uuid) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
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
    	User user =	(User) httpServletRequest.getAttribute("user"); 
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
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		String spbillCreateIp = IpUtils.getIpAddr(httpServletRequest);
		return orderService.native_pay_order(user, spbillCreateIp, id);

	}
	// app生成支付单
		@RequestMapping(value = "native_pay_order_app", method = RequestMethod.GET)
		@ResponseBody
		public ServerResponse<Object> native_pay_order_app(HttpServletRequest httpServletRequest, @RequestParam long id) {
	    	User user =	(User) httpServletRequest.getAttribute("user"); 
			// 检查权限

			if (user.getRole() != 1 && user.getRole() != 2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
			}

			String spbillCreateIp = IpUtils.getIpAddr(httpServletRequest);
			return orderService.native_pay_order_app(user, spbillCreateIp, id);

		}
	
	
	
	
	/**
	 * 获取是否有待支付的支付订单
	 */
	@RequestMapping(value = "get_pay_order_all", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> get_pay_order_all(HttpServletRequest httpServletRequest, @RequestParam String uuid) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}

		
		return orderService.get_pay_order_all(user.getId());

	}

	/**
	 * 根据orderId查询支付状态
	 */
	@RequestMapping(value = "get_pay_order_byOrderId", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> get_pay_order_byOrderId(HttpServletRequest httpServletRequest,
			@RequestParam long orderId, @RequestParam String uuid) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		return orderService.get_pay_order_byOrderId(user.getId(), orderId,httpServletRequest.getHeader("appid"));

	}



	/**
	 * 接单用户获取待处理订单
	 */
	@RequestMapping(value = "peceiptGetPendingOrders", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> peceiptGetPendingOrders(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		return orderService.peceiptGetPendingOrders(user.getId(), params);

	}

	
	/**
	 * 发布企业查询所有订单
	 */
	@RequestMapping(value = "myPurchaseOrder", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> myPurchaseOrder(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		//检查实名
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		return orderService.myPurchaseOrder(user.getId(), params);

	}
	/**
	 * 接单企业查询 除抢单中的订单
	 */
	@RequestMapping(value = "mySaleOrder", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> mySaleOrder(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		//检查实名
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		// 检查权限

		if (user.getRole() != 1 && user.getRole() != 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiedaunyonghuocaikeyikan.getMessage());
		}
		return orderService.mySaleOrder(user.getId(), params);

	}

	

	/**
	 * app获取待报价订单
	 */
	@RequestMapping(value = "getdaibaojia", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Object> getdaibaojia(HttpServletRequest httpServletRequest,
			 @RequestParam String uuid,@RequestParam int   releaseType) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		
		return orderService.getdaibaojia(user.getId(),releaseType);
	}

	/**
	 * 创建接单
	 */
	@RequestMapping(value = "createjiedan", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Object> createjiedan(HttpServletRequest httpServletRequest,
			@RequestBody Map<String, Object> params) {
    	User user =	(User) httpServletRequest.getAttribute("user"); 
		//检查实名
		if (user.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}
		
		return orderService.createjiedan(user.getId(), params);

	}
}
