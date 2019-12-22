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
		int releaseType = 4;
		String keyString = Const.MY_C0MMONMENU + user.getId() + "_" + user.getUsername();
		String myCommonMenuJsonStr = RedisShardedPoolUtil.get(keyString);
		List<CommonMenuWholesalecommodity> myCommonMenu_list = null;
				
				
		

		// 返回的vo
		PurchaseCreateOrderVo purchaseCreateOrderVo = new PurchaseCreateOrderVo();
     
		if (myCommonMenuJsonStr == null) {

			CommonMenu commonMenu = commonMenuMapper.getCommonMenu(user.getId());
			if (commonMenu != null) {
			    String	servicetypeId=commonMenu.getServicetypeId();
			    myCommonMenu_list= JsonUtil.string2Obj(servicetypeId, List.class,
						CommonMenuWholesalecommodity.class);
			    
			
				// 存到redis中，时长是 3600分钟
				RedisShardedPoolUtil.setEx(keyString, JsonUtil.obj2StringPretty(myCommonMenu_list),
						Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
				//设置有已创建 个人常用菜单
				purchaseCreateOrderVo.setIsCommonMenu(1);
				purchaseCreateOrderVo.setMyCommonMenu(myCommonMenu_list);
				
				
			}
		} else {
			myCommonMenu_list =JsonUtil.string2Obj(myCommonMenuJsonStr, List.class,
					CommonMenuWholesalecommodity.class);
			purchaseCreateOrderVo.setIsCommonMenu(1);
			purchaseCreateOrderVo.setMyCommonMenu(myCommonMenu_list);
			RedisShardedPoolUtil.expire(keyString,Const.RedisCacheExtime.REDIS_SESSION_CommonMenu);
		}
		// 根据用户 省市，默认北京， 查询redis中是否有保存
		RealName realName = null;
		String provinces_city = null;
		ServerResponse<Object> serverResponse = realNameService.getRealName(user);
		if (serverResponse.getStatus() == 0) {
			realName = (RealName) serverResponse.getData();
			provinces_city = Const.MY_C0MMONMENU + realName.getProvincesId() + "_" + realName.getCityId();
		}

		if (provinces_city == null) {
			provinces_city = Const.MY_C0MMONMENU + "110000_110100"; // 默认北京
		}
		List<CommonMenuWholesalecommodity> allCommonMenu = null;
		// 查找redis中有没有
		String redis_all_commonMenu = RedisShardedPoolUtil.get(provinces_city);
		if (redis_all_commonMenu != null) {
			// redis中有，转为list
			allCommonMenu = JsonUtil.string2Obj(redis_all_commonMenu, List.class, CommonMenuWholesalecommodity.class);

		} else {
			// redis中没有，数据库查找，并存到redis中
			
			// 查询该城市下全部菜单,如果空返回北京%110000, 110100%'
			String selectedOptions = "%" + realName.getProvincesId() + ", " + realName.getCityId() + "%";
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

}
