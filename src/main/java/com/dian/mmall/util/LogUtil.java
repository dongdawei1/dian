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
	public static ServerResponse<Object> setTocken(String appId, HttpServletResponse httpServletResponse, User user) {
		if (appId != null && !appId.equals("")) {
			String userString = JsonUtil.obj2String(user);
			if (appId.equals(Const.APPAPPIDP)) {
				// 删除以前的登陆
				RedisPoolUtil.delectKeyP(user);
				// 定期更换加密方式
				String tockenString = MD5Util.setTocken(user.getId(), user.getCreateTime(), Const.PCAPPID);
				CookieUtil.writeLoginToken(httpServletResponse, tockenString);
				// 把用户session当做key存到数据库中，
				RedisShardedPoolUtil.setEx(tockenString, userString, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				return ServerResponse.createBySuccess(user);
			} else if (appId.equals(Const.APPAPPIDA)) {
				RedisPoolUtil.delectKeyA(user);
				String tockenString = MD5Util.setTocken(user.getId(), user.getCreateTime(), Const.APPAPPID);
				// 把用户session当做key存到数据库中，
				RedisShardedPoolUtil.setEx(tockenString, userString, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dian_token", tockenString);
				map.put("user", user);
				return ServerResponse.createBySuccess(map);// );
			}
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.qingqiuxinxiyouwu.getMessage());
	}
}
