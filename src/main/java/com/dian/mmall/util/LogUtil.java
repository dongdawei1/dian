package com.dian.mmall.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {
	public static ServerResponse<String> setTocken(String appId, HttpServletResponse httpServletResponse,
			String userString) {

		User user = JsonUtil.string2Obj(userString, User.class);
		if (appId != null && !appId.equals("")) {
			if (appId.equals("p")) {
				// 定期更换加密方式
				String tockenString = MD5Util.setTocken(user.getId(), user.getCreateTime(), Const.PCAPPID);
				CookieUtil.writeLoginToken(httpServletResponse, tockenString);
				// 把用户session当做key存到数据库中，时长是 30分钟
				RedisShardedPoolUtil.setEx(tockenString, userString, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				return ServerResponse.createBySuccess();
			} else if (appId.equals("a")) {
				String tockenString = MD5Util.setTocken(user.getId(), user.getCreateTime(), Const.APPAPPID);
				// 把用户session当做key存到数据库中，时长是 30分钟
				RedisShardedPoolUtil.setEx(tockenString, userString, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				Map<String, String>  map=new HashMap<String, String>();
				map.put("dian_token", tockenString);
				map.put("user", userString);
				System.out.println(userString);
				System.out.println(JsonUtil.obj2String(map));
				return ServerResponse.createBySuccess(JsonUtil.obj2String(map));//);
			}
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.qingqiuxinxiyouwu.getMessage());
	}
}
