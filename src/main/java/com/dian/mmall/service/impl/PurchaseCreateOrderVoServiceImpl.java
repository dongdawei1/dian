package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.goumaidingdan.CommonMenuMapper;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenu;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.goumaidingdan.PurchaseCreateOrderVo;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.PurchaseCreateOrderVoService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("purchaseCreateOrderVoService")
public class PurchaseCreateOrderVoServiceImpl implements PurchaseCreateOrderVoService {
	@Autowired
	private CommonMenuMapper commonMenuMapper;
	@Autowired
	private RealNameService realNameService;
	@Autowired
	private WholesaleCommodityService wholesaleCommodityService;

	@Override
	public ServerResponse<Object> getPurchaseCreateOrderVo(User user) {
		int releaseType = 401;
		String keyString = Const.MY_C0MMONMENU + "_" + user.getUsername() + "_" + user.getId();
		String myCommonMenuJsonStr = RedisShardedPoolUtil.get(keyString);
		List<CommonMenuWholesalecommodity> myCommonMenu_list = null;

		// 返回的vo
		PurchaseCreateOrderVo purchaseCreateOrderVo = new PurchaseCreateOrderVo();

		if (myCommonMenuJsonStr == null) {

			CommonMenu commonMenu = commonMenuMapper.getCommonMenu(user.getId());
			if (commonMenu != null) {
				String servicetypeId = commonMenu.getServicetypeId();
				myCommonMenu_list = JsonUtil.string2Obj(servicetypeId, List.class, CommonMenuWholesalecommodity.class);

				// 存到redis中，时长是 3600分钟
				RedisShardedPoolUtil.setEx(keyString, JsonUtil.obj2StringPretty(myCommonMenu_list),
						Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
				// 设置有已创建 个人常用菜单
				purchaseCreateOrderVo.setIsCommonMenu(1);
				purchaseCreateOrderVo.setMyCommonMenu(myCommonMenu_list);

			}
		} else {
			myCommonMenu_list = JsonUtil.string2Obj(myCommonMenuJsonStr, List.class,
					CommonMenuWholesalecommodity.class);
			purchaseCreateOrderVo.setIsCommonMenu(1);
			purchaseCreateOrderVo.setMyCommonMenu(myCommonMenu_list);
			RedisShardedPoolUtil.expire(keyString, Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
		}
		// 根据用户 省市，默认北京， 查询redis中是否有保存
		RealName realName = null;
		String provinces_city = Const.MY_C0MMONMENU + "110000_110100";
//		ServerResponse<Object> serverResponse = realNameService.getRealName(user);
//		if (serverResponse.getStatus() == 0) {
//			realName = (RealName) serverResponse.getData();
//			provinces_city = Const.MY_C0MMONMENU + realName.getProvincesId() + "_" + realName.getCityId();
//		}
//
//		if (provinces_city == null) {
//			provinces_city = Const.MY_C0MMONMENU + "110000_110100"; // 默认北京
//		}
		List<CommonMenuWholesalecommodity> allCommonMenu = null;
		// 查找redis中有没有
		String redis_all_commonMenu = RedisShardedPoolUtil.get(provinces_city);

		System.out.println("PurchaseCreateOrderVo4444444()" + redis_all_commonMenu);
		if (redis_all_commonMenu != null) {
			// redis中有，转为list
			allCommonMenu = JsonUtil.string2Obj(redis_all_commonMenu, List.class, CommonMenuWholesalecommodity.class);

		}

		if (allCommonMenu== null) {
			// redis中没有，数据库查找，并存到redis中

			// 查询该城市下全部菜单,如果空返回北京%110000, 110100%' ,暂时把城市
			String selectedOptions = "%110000, 110100%";
			// TODO selectedOptions此字段没有用目前是在 jerdis中写死的 全部查询北京
			ServerResponse<Object> onbResponse = wholesaleCommodityService.getWholesalecommodity(selectedOptions,
					releaseType);

			allCommonMenu = new ArrayList<CommonMenuWholesalecommodity>();

			List<WholesaleCommodity> wholesaleCommodity_list = (List<WholesaleCommodity>) onbResponse.getData();

			for (int i = 0; i < wholesaleCommodity_list.size(); i++) {
				CommonMenuWholesalecommodity commonMenuWholesalecommodity = new CommonMenuWholesalecommodity();
				commonMenuWholesalecommodity.setReleaseType(releaseType);
				commonMenuWholesalecommodity.setServiceType(wholesaleCommodity_list.get(i).getServiceType());
				commonMenuWholesalecommodity.setCations(wholesaleCommodity_list.get(i).getCations());
				commonMenuWholesalecommodity.setSpecifi(wholesaleCommodity_list.get(i).getSpecifi());
				commonMenuWholesalecommodity.setCommodityPacking(wholesaleCommodity_list.get(i).getCommodityPacking());

				if (wholesaleCommodity_list.get(i).getCommodityPacking() == 1) {
					commonMenuWholesalecommodity.setCommodityPackingName("散装");
					commonMenuWholesalecommodity.setSpecifi_cations("--");
					commonMenuWholesalecommodity.setSpecifiName("kg");
				} else if (wholesaleCommodity_list.get(i).getCommodityPacking() == 2) {
					commonMenuWholesalecommodity.setCommodityPackingName("袋装");
					commonMenuWholesalecommodity.setSpecifiName("袋");
					if (wholesaleCommodity_list.get(i).getSpecifi() == 1) {
						commonMenuWholesalecommodity
								.setSpecifi_cations(wholesaleCommodity_list.get(i).getCations() + "g/袋");
					} else if (wholesaleCommodity_list.get(i).getSpecifi() == 2) {
						commonMenuWholesalecommodity
								.setSpecifi_cations(wholesaleCommodity_list.get(i).getCations() + "Kg/袋");
					}
				} else if (wholesaleCommodity_list.get(i).getCommodityPacking() == 3) {
					commonMenuWholesalecommodity.setCommodityPackingName("瓶/桶装");
					commonMenuWholesalecommodity.setSpecifiName("瓶/桶");
					if (wholesaleCommodity_list.get(i).getSpecifi() == 3) {
						commonMenuWholesalecommodity
								.setSpecifi_cations(wholesaleCommodity_list.get(i).getCations() + "ML/瓶/桶");
					} else if (wholesaleCommodity_list.get(i).getSpecifi() == 4) {
						commonMenuWholesalecommodity
								.setSpecifi_cations(wholesaleCommodity_list.get(i).getCations() + "L/瓶/桶");
					}
				}

				allCommonMenu.add(commonMenuWholesalecommodity);
			}

			// 数据中没有就在这里存 操作
			RedisShardedPoolUtil.setEx(provinces_city, JsonUtil.obj2StringPretty(allCommonMenu),
					Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
			// 存改城市下的全部

		}

		purchaseCreateOrderVo.setAllCommonMenu(allCommonMenu);
		return ServerResponse.createBySuccess(purchaseCreateOrderVo);
	}

	// 我的常用菜单 存贮或更新
	@Override
	public void createMyCommonMenu(User user, List<CommonMenuWholesalecommodity> listObj4, int isCommonMenu) {
		// TODO Auto-generated method stub 0是redis中没有 1是有
		String keyString = Const.MY_C0MMONMENU + "_" + user.getUsername() + "_" + user.getId();
		CommonMenu commonMenu = new CommonMenu();
		commonMenu.setUserId(user.getId());
		String servicetypeId = null;
		if (isCommonMenu == 0) {
			// 创建
			for (int a = 0; a < listObj4.size(); a++) {
				CommonMenuWholesalecommodity commonMenuWholesalecommodity = listObj4.get(a);
				commonMenuWholesalecommodity.setNumber(null);
				commonMenuWholesalecommodity.setType(1);
				commonMenuWholesalecommodity.setPlacing(false);
				listObj4.set(a, commonMenuWholesalecommodity);
			}
			servicetypeId = JsonUtil.obj2StringPretty(listObj4);
			commonMenu.setServicetypeId(servicetypeId);
			// 落库
			commonMenuMapper.createCommonMenu(commonMenu);
			RedisShardedPoolUtil.setEx(keyString, servicetypeId, Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
		} else {
			// 更新
			String myCommonMenuJsonStr = RedisShardedPoolUtil.get(keyString);
			List<CommonMenuWholesalecommodity> redismyCommonMenu = JsonUtil.string2Obj(myCommonMenuJsonStr, List.class,
					CommonMenuWholesalecommodity.class);
			boolean bo = false;
			for (int a = 0; a < listObj4.size(); a++) {
				CommonMenuWholesalecommodity my = listObj4.get(a);

				for (int rs = 0; rs < redismyCommonMenu.size(); rs++) {
					CommonMenuWholesalecommodity redis = redismyCommonMenu.get(rs);

					if (my.getCommodityPacking() == 1) {
						if (redis.getReleaseType() == my.getReleaseType()
								&& redis.getServiceType().equals(my.getServiceType())
								&& redis.getCommodityPacking() == my.getCommodityPacking()) {
							bo = true;
						}
					} else {
						if (redis.getReleaseType() == my.getReleaseType()
								&& redis.getServiceType().equals(my.getServiceType())
								&& redis.getCommodityPacking() == my.getCommodityPacking()
								&& redis.getSpecifi() == my.getSpecifi()
								&& redis.getCations().equals(my.getCations())) {
							bo = true;
						}
					}
				}
				if (bo == false) {
					my.setNumber(null);
					my.setType(1);
					my.setPlacing(false);
					redismyCommonMenu.add(my);
				}
				bo = false;
			}

			servicetypeId = JsonUtil.obj2StringPretty(redismyCommonMenu);
			commonMenu.setServicetypeId(servicetypeId);
			RedisShardedPoolUtil.getSet(keyString, servicetypeId);
			RedisShardedPoolUtil.expire(keyString, Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
			// 落库
			commonMenuMapper.updateCommonMenu(commonMenu);
		}

	}

}
