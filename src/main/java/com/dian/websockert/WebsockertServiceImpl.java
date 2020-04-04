package com.dian.websockert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.dingshi.OrderExampleTimerServiceImpl;
import com.dian.mmall.dao.OrderUserMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.OrderFanhui;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("websockertService")
public class WebsockertServiceImpl implements WebsockertService {

	@Autowired
	private OrderUserMapper orderUserMapper;

	@Override
	public String remote(String message, String vmcNo) {
		// return
		// WebSocketServer.sendMessage(message,"DIANTOP908468FCE8E7D066CB059F0B3FD3");
		return null;
	}

	@Override
	public void remote1(String string, Object object) {

		CopyOnWriteArrayList<WebSocketServer> arrayList = WebSocketServer.getsockall();
		System.out.println(arrayList.size());
		for (WebSocketServer server : arrayList) {
			System.out.println(111111);
			System.out.println(server.getMVmcNo());
			WebSocketServer.sendMessage("22", server);
		}
	}

	@Override
	public String fadingdan(String detailed, Order order) {
	
		Map<String, Object> map=new HashMap<String, Object>();
	//	map.put("addressDetailed", order.getAddressDetailed());
	//	map.put("createTime", order.getCreateTime());
//		map.put("giveTakeTime", order.getGiveTakeTime());
//		map.put("remarks", order.getRemarks());
//		List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.string2Obj(order.getCommoditySnapshot(), List.class,
//				CommonMenuWholesalecommodity.class);
//		map.put("orderStatus", listObj4);
	
		
		OrderFanhui orderFanhui=new OrderFanhui();
		orderFanhui.setId(order.getId());
		orderFanhui.setOrderStatus(order.getOrderStatus());
		orderFanhui.setCommoditySnapshot(order.getCommoditySnapshot());
		orderFanhui.setRemarks(order.getRemarks());
		orderFanhui.setGiveTakeTime(order.getGiveTakeTime());
		orderFanhui.setCreateTime(order.getCreateTime());
		orderFanhui.setAddressDetailed(order.getAddressDetailed());
		orderFanhui.setPaymentTime(detailed);
		map.put("getjinxin", orderFanhui);
		map.put("type", 2); // 2为新建订单
		return fasong(detailed, map);
	}

	public synchronized String fasong(String detailed, Map<String, Object> map) {
		CopyOnWriteArrayList<WebSocketServer> arrayList = WebSocketServer.getsockall();
		int a = 0;
		for (WebSocketServer server : arrayList) {
			// 判断是否是同一区域
			if (detailed.equals(server.getDetailed())) {
				String userJsonStr = RedisShardedPoolUtil.get(server.getMVmcNo());
				User user = JsonUtil.string2Obj(userJsonStr, User.class);
				// 先判断是否是 买菜商家
				if (user.getRole() == 4) {
					// 检查是否是 接单用户
					if (orderUserMapper.getisjiedan(user.getId()) > 0) {
						String reString = WebSocketServer.sendMessage(JsonUtil.obj2String(map), server);
						if (reString.equals("true")) {
							a++;
						}
					}
				}
			}

		}
		if (a > 0) {
			return "true";
		}
		// 发送给同区域管理员

		for (WebSocketServer server : arrayList) {
			if (detailed.equals(server.getDetailed())) {
				String userJsonStr = RedisShardedPoolUtil.get(server.getMVmcNo());
				User user = JsonUtil.string2Obj(userJsonStr, User.class);
				if (user.getRole() == 1) {
					map.put("mag", "该区域无接单用户");
					String reString = WebSocketServer.sendMessage(JsonUtil.obj2String(map), server);
					if (reString.equals("true")) {
						a++;
					}
				}
			}

		}

		if (a > 0) {
			return "guanli";
		}

		return "false";

	}

}
