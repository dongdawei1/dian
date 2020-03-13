package com.dian.mmall.util;

import java.util.Set;
import java.util.regex.Pattern;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.RedisPool;
import com.dian.mmall.pojo.user.User;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created by geely
 */
@Slf4j
public class RedisPoolUtil {

	/**
	 * 设置key的有效期，单位是秒
	 * 
	 * @param key
	 * @param exTime
	 * @return
	 */
	public static Long expire(String key, int exTime) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.expire(key, exTime);
		} catch (Exception e) {
			log.error("expire key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	// exTime的单位是秒
	public static String setEx(String key, String value, int exTime) {
		Jedis jedis = null;
		String result = null;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.setex(key, exTime, value);
		} catch (Exception e) {
			log.error("setex key:{} value:{} error", key, value, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static String set(String key, String value) {
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.set(key, value);
		} catch (Exception e) {
			log.error("set key:{} value:{} error", key, value, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static String get(String key) {
		Jedis jedis = null;
		String result = null;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.get(key);
		} catch (Exception e) {
			log.error("get key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	// 查看key是否存在
	public static Boolean exists(String key) {
		Jedis jedis = null;
		Boolean result = false;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.exists(key);
		} catch (Exception e) {
			log.error("get key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}

		return result;
	}

	public static Long del(String key) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.del(key);
		} catch (Exception e) {
			log.error("del key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static int checkeKey(User user) {
		Jedis jedis = null;
		// 0有成功替换，1没有，2异常
		
		try {
			int result=2;
			jedis = RedisPool.getJedis();
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				String pattern = ".*" + "\"username\"" + ":\"" + user.getUsername() + "\"," + ".*";
				String userJsonStr = RedisShardedPoolUtil.get(key);
				if (userJsonStr.indexOf("username") != -1) {
					User userJedis = JsonUtil.string2Obj(userJsonStr, User.class);
					if (userJedis.getUsername().equals(user.getUsername()) && userJedis.getId() == user.getId()) {
						// 在这里替换tocken
						RedisShardedPoolUtil.del(key);

						if (key.indexOf(Const.PCAPPID) == 0) {
							RedisShardedPoolUtil.setEx(key, JsonUtil.obj2String(user),
									Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
							result=0;
							System.out.println("tihuan3331appp"+userJsonStr);
						} else if (key.indexOf(Const.APPAPPID) == 0) {
							RedisShardedPoolUtil.setEx(key, JsonUtil.obj2String(user),
									Const.RedisCacheExtime.REDIS_SESSION_EXTIMEAPP);
							result=0;
							System.out.println("tihuan3331aaaaa"+userJsonStr);
						}

						
					}
				}

			}
			RedisPool.returnResource(jedis);

		} catch (Exception e) {
			RedisPool.returnBrokenResource(jedis);
			return 2;
		}

		return 1;
	}

	public static void delectKeyP(User user) {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				String pattern = ".*" + "\"username\"" + ":\"" + user.getUsername() + "\"," + ".*";
				String userJsonStr = RedisShardedPoolUtil.get(key);
				if (userJsonStr.indexOf("username") != -1) {
					User userJedis = JsonUtil.string2Obj(userJsonStr, User.class);
					// log.info(userJedis.getUsername() + " - " + user.getUsername());
					if (userJedis.getUsername().equals(user.getUsername()) && userJedis.getId() == user.getId()) {
						// 在这 aString.indexOf(Const.PCAPPID)==0
						// RedisShardedPoolUtil.del(key);
						// P,DIANTOP
						if (key.indexOf(Const.PCAPPID) == 0) {
							RedisShardedPoolUtil.del(key);
						}
					}
				}

			}
			RedisPool.returnResource(jedis);

		} catch (Exception e) {
			RedisPool.returnBrokenResource(jedis);
		}

	}

	public static void delectKeyA(User user) {
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				String pattern = ".*" + "\"username\"" + ":\"" + user.getUsername() + "\"," + ".*";
				String userJsonStr = RedisShardedPoolUtil.get(key);
				if (userJsonStr.indexOf("username") != -1) {
					User userJedis = JsonUtil.string2Obj(userJsonStr, User.class);
					// log.info(userJedis.getUsername() + " - " + user.getUsername());
					if (userJedis.getUsername().equals(user.getUsername()) && userJedis.getId() == user.getId()) {
						// 在这 aString.indexOf(Const.PCAPPID)==0
						// RedisShardedPoolUtil.del(key);
						// P,DIANTOP
						if (key.indexOf(Const.APPAPPID) == 0) {
							RedisShardedPoolUtil.del(key);
						}
					}
				}

			}
			RedisPool.returnResource(jedis);

		} catch (Exception e) {
			RedisPool.returnBrokenResource(jedis);
		}

	}

	public static void delectKeyAll(User user, String appid) {

		if (appid.equals(Const.APPAPPIDP)) {
			delectKeyP(user); // 删除pc端登陆的已有账户

		} else if (appid.equals(Const.APPAPPIDA)) {
			delectKeyA(user);
		}

	}

	public static Set<String> keys(String key) {
		Jedis jedis = null;
		Set<String> result = null;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.keys(key);
			;
		} catch (Exception e) {
			log.error("del key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;

	}

	public static long pttl(String key) {
		// 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以毫秒为单位，返回 key 的剩余生存时间。
		Jedis jedis = null;
		long result = -5;
		try {
			jedis = RedisPool.getJedis();
			result = jedis.pttl(key);
			;
		} catch (Exception e) {
			log.error("del key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;

	}

	public static void main(String[] args) {
		Jedis jedis = RedisPool.getJedis();

		// DIANTOP3C38516C11C9CF959D0BCCC08057
//
//		// PCAPPID = "DIANTOP";
//		// RedisPoolUtil.main()DIANTOP

		Set<String> keyes = jedis.keys("*");
		for (String key : keyes) {
			System.out.println(key + "<--ddd");
			String userJsonStr = RedisShardedPoolUtil.get(key);
			System.out.println("userJsonStr-->" + userJsonStr);
			// System.out.println( "jedis.pttl(key)-->"+ jedis.pttl(key));
		}

//        System.out.println( "jedis.pttl(key)-->"+keyes.size());
//        System.out.println( "jedis.pttl(key)-->");

		// RedisShardedPoolUtil.del("keyTest");

//        String aaa = RedisShardedPoolUtil.get(null);
//        System.out.println(aaa);

	}

}
