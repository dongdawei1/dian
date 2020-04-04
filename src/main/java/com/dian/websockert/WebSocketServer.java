package com.dian.websockert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.controller.portal.RealNameController;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(Const.PCAPI + "so/{vmcNo}") // 客户端URI访问的路径
@Component
@Slf4j
@Getter
public class WebSocketServer {

	/**
	 * 保存所有连接的webSocket实体 CopyOnWriteArrayList使用了一种叫写时复制的方法，
	 * 当有新元素添加到CopyOnWriteArrayList时， 先从原有的数组中拷贝一份出来，然后在新的数组做写操作，
	 * 写完之后，再将原来的数组引用指向到新数组。 具备线程安全，并且适用于高并发场景
	 */
	private static CopyOnWriteArrayList<WebSocketServer> sWebSocketServers = new CopyOnWriteArrayList<>();
	private Session mSession; // 与客户端连接的会话，用于发送数据
	private String mVmcNo; // 客户端的标识(这里以机器编号)
	private String detailed;

	@OnOpen
	public void onOpen(Session session, @PathParam("vmcNo") String vmcNo) {

		mSession = session;
		mVmcNo = vmcNo;
		String userJsonStr = RedisShardedPoolUtil.get(vmcNo);
		User user = JsonUtil.string2Obj(userJsonStr, User.class);

		if (user != null) {
			detailed = user.getDetailed();
			if (detailed == null) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("type", 1); // 其他业务type等于别的
				map.put("msg", 0);
				this.sendMessage(JsonUtil.obj2String(map));
			} else {

				boolean success = false;
				for (int a = 0; a < sWebSocketServers.size(); a++) {

					if (sWebSocketServers.get(a).mVmcNo.equals(vmcNo)) {
						sWebSocketServers.set(a, this);
						success = true;
						break;
					}
				}
				if (!success) {
					sWebSocketServers.add(this); // 将回话保存
				}
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("type", 1); // 其他业务type等于别的
				map.put("msg", 1);
				this.sendMessage(JsonUtil.obj2String(map));
			}
		} else {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("type", 1); // 其他业务type等于别的
			map.put("msg", 0);
			this.sendMessage(JsonUtil.obj2String(map));
		}

	}

	@OnClose
	public void onClose() {		
		sWebSocketServers.remove(this);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
// 这里选择的是让其他客户端都知道消息，类似于转发的聊天室，可根据使用场景使用
		for (WebSocketServer socketServer : sWebSocketServers) {
			// socketServer.sendMessage("i have rcv you message");
		}
	}

	/**
	 * 对外发送消息
	 * 
	 * @param message
	 */
	public boolean sendMessage(String message) {
		try {
			mSession.getBasicRemote().sendText(message);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * 对某个机器发送消息
	 * 
	 * @param message
	 * @param vmcNo   机器编号
	 * @return true,返回发送的消息,false，返回failed字符串
	 */
	public static String sendMessage(String message, WebSocketServer server) {
//		boolean success = false;
//		System.out.println("WebSocketServer.sendMessage()");
//		System.out.println(sWebSocketServers.size());
//		for (WebSocketServer server : sWebSocketServers) {
//			System.out.println(server.mVmcNo);
//			if (server.mVmcNo.equals(vmcNo)) {
//				System.out.println("eee12"+vmcNo);
//				success = server.sendMessage(message);
//				break;
//			}
//		}
		boolean success = server.sendMessage(message);
		return success ? "true" : "failed";
	}

	public static CopyOnWriteArrayList<WebSocketServer> getsockall() {
		return sWebSocketServers;
	}

}
