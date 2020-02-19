package com.dian.comController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dian.config.WeChatConfig;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.util.WXPayUtil;

@Controller
@RequestMapping(Const.PCAPI+"vx/")
public class WxPayController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private WeChatConfig weChatConfig;


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
}
