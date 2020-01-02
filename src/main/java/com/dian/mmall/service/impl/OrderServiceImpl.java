package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.OrderMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.goumaidingdan.OrderCommonOfferMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.goumaidingdan.OrderCommonOffer;
import com.dian.mmall.pojo.goumaidingdan.OrderCommonOfferEvaluateVo;
import com.dian.mmall.pojo.goumaidingdan.PurchaseSeeOrderVo;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.service.PurchaseCreateOrderVoService;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisPoolUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private WholesaleCommodityService wholesaleCommodityService;
	@Autowired
	private PurchaseCreateOrderVoService purchaseCreateOrderVoService;
	@Autowired
	private OrderCommonOfferMapper orderCommonOfferMapper;

	@Autowired
	private EvaluateMapper evaluateMapper;

	@Override
	public synchronized ServerResponse<String> create_wholesaleCommodity_order(long userId,
			Map<String, Object> params) {
		// TODO 暂时不做 批发订单
//    	commoditySurplusNo &lt;  #{commodityReserveNo} and
//    	startTime &lt;   #{updateTime} and
//    	endTime   &gt;   #{updateTime} 
//    	params.put("commodityJiage", (int)params.get("commodityJiage")*100);
//    	params.put("updateTime", DateTimeUtil.dateToAll());
//    	WholesaleCommodity wholesaleCommodity=(WholesaleCommodity) BeanMapConvertUtil
//				.convertMap(WholesaleCommodity.class, params);
//    	
//    	ServerResponse<Object> serverResponse=wholesaleCommodityService.getWholesaleCommodityBoolean(wholesaleCommodity);
//    	
//    	if(serverResponse.getStatus()==0) {
//    		long zongJiage=wholesaleCommodity.getCommodityJiage()*wholesaleCommodity.getCommodityReserveNo();
//    		Order order=new Order();
//    		order.setSaleUserId(wholesaleCommodity.getUserId());
//    		order.setPurchaseUserId(userId);
//    		order.setWholesaleCommodityId(wholesaleCommodity.getId());
//    		order.setEvaluateid(wholesaleCommodity.getEvaluateid());
//    		order.setCommodityJiage(wholesaleCommodity.getCommodityJiage());
//    		order.setCommodityCountNo(wholesaleCommodity.getCommodityReserveNo());
//    		order.setCommodityZongJiage(zongJiage);
//    		order.setReserve(2);
//    		order.setDeliveryType(1);
//    		order.setDeliveryCollect(0);
//    		
//    	
//    	}

		// 上边是批发订单

		return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshangpinshibai.getMessage());
	}

	@Override
	public ServerResponse<Object> get_conduct_order(long wholesaleCommodityId, int orderStatus) {
		if (wholesaleCommodityId < 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinidcuowo.getMessage());
		}

		if (orderStatus < 0 || orderStatus > 10) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunzhuangtcuowo.getMessage());
		}
		return ServerResponse.createBySuccess(orderMapper.get_conduct_order(wholesaleCommodityId, orderStatus));
	}

	// 商户创建采购订单
	@Override
	public ServerResponse<String> create_purchase_order(User user, Map<String, Object> params) {

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(user.getId());

		if (realName != null) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			if (!realName.getContact().equals(params.get("contact").toString().trim())
					|| !realName.getConsigneeName().equals(params.get("consigneeName").toString().trim())
					|| !realName.getAddressDetailed().equals(params.get("addressDetailed").toString().trim())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}

			String giveTakeTime = params.get("giveTakeTime").toString().trim();
			String newdateString = DateTimeUtil.dateToAll();
			ServerResponse<Object> serverResponseObject = DateTimeUtil.dateCompare(giveTakeTime, 3);
			if (serverResponseObject.getStatus() == 0) {
				if ((boolean) serverResponseObject.getData()) {
					serverResponseObject = DateTimeUtil.isPastDate(giveTakeTime, 1);
					if ((boolean) serverResponseObject.getData()) {

						List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.list2Obj(
								(ArrayList<CommonMenuWholesalecommodity>) params.get("fromData"), List.class,
								CommonMenuWholesalecommodity.class);
						Order order = new Order();
						order.setPurchaseUserId(user.getId());
						order.setCommodityCountNo(1);
						order.setReserve(3);
						order.setDeliveryType(2);
						order.setAddressDetailed(params.get("addressDetailed").toString().trim());
						order.setGiveTakeTime(giveTakeTime);
						order.setRemarks(params.get("remarks").toString().trim());
						order.setCreateTime(newdateString);
						order.setOrderStatus(11);
						order.setPayStatus(0);
						order.setCommoditySnapshot(JsonUtil.obj2StringPretty(listObj4));
						String commodityJiage = params.get("commodityJiage").toString().trim();
						int commodityJiage_int = 0;
						if (commodityJiage != null && !commodityJiage.equals("")) {
							try {
								commodityJiage_int = Integer.parseInt(commodityJiage);
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
						order.setCommodityJiage(commodityJiage_int * 100);
						order.setYesGuaranteeMoney(0);
						int resout = orderMapper.create_order(order);

						if (resout == 0) {
							return ServerResponse
									.createByErrorMessage(ResponseMessage.chuangjiandingdanshibai.getMessage());
						}

						long id = orderMapper.getId(order);
						order.setId(id);
						order.setCommodityJiage(commodityJiage_int);
						// 把订单存放在redis中
						String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + id;

						RedisShardedPoolUtil.setEx(keyString, JsonUtil.obj2StringPretty(order),
								Const.RedisCacheExtime.REDIS_ORDER_TIME);
						// TODO 成功 调用push接口发送站内信

						// 调vo接口创建 /更新 redis的常用菜单
						int isCommonMenu = Integer.parseInt(params.get("isCommonMenu").toString().trim());
						purchaseCreateOrderVoService.createMyCommonMenu(user, listObj4, isCommonMenu);
						return ServerResponse.createBySuccess();
					} else {
						// 送货日期小于今天
						return ServerResponse
								.createByErrorMessage(ResponseMessage.songhuoriqibunnegxiaoyujint.getMessage());
					}
				} else {
					// 送货日期不在三天内
					return ServerResponse
							.createByErrorMessage(ResponseMessage.songhuoriqizhinnegsantiannei.getMessage());
				}
			} else {
				// 送货日期格式错误
				return ServerResponse.createByErrorMessage(ResponseMessage.songhuoriqiyouwu.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}

	}

	// 商户采购订单预估价格
	@Override
	public ServerResponse<String> create_order_evaluation(User user, Map<String, Object> params) {
		List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.list2Obj(
				(ArrayList<CommonMenuWholesalecommodity>) params.get("fromData"), List.class,
				CommonMenuWholesalecommodity.class);
		if (listObj4.size() == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanbunnegweikong.getMessage());
		}
		RealName realName = realNameMapper.getRealName(user.getId());
		if (realName == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}
		String detailed = "%" + realName.getDetailed() + "%";
		int commodityPacking = 1;
		int commodityJiage = 0;
		float count = 0;
		for (int a = 0; a < listObj4.size(); a++) {
			CommonMenuWholesalecommodity commonMenuWholesalecommodity = listObj4.get(a);
			WholesaleCommodity wholesaleCommodity = new WholesaleCommodity();
			wholesaleCommodity.setReleaseType(commonMenuWholesalecommodity.getReleaseType());
			wholesaleCommodity.setServiceType(commonMenuWholesalecommodity.getServiceType());
			commodityPacking = commonMenuWholesalecommodity.getCommodityPacking();
			wholesaleCommodity.setCommodityPacking(commodityPacking);
			if (commodityPacking != 1) {
				wholesaleCommodity.setSpecifi(commonMenuWholesalecommodity.getSpecifi());
				wholesaleCommodity.setCations(commonMenuWholesalecommodity.getCations());
			}
			wholesaleCommodity.setServiceDetailed(detailed);
			// 获取平均价格
			commodityJiage = create_order_average(wholesaleCommodity);
			count += commodityJiage * Float.parseFloat(commonMenuWholesalecommodity.getNumber());
		}
		return ServerResponse.createBySuccessMessage((int) Math.ceil((count / 100)) + "");
	}

	// 计算平均价格
	public int create_order_average(WholesaleCommodity wholesaleCommodity) {
		int commodityJiage = 0;
		List<Integer> commodityJiage_list = wholesaleCommodityService.getCommodityJiage(wholesaleCommodity);
		int length = commodityJiage_list.size();
		for (int a = 0; a < length; a++) {
			commodityJiage += commodityJiage_list.get(a);
		}
		return (int) commodityJiage / length;
	}

	@Override
	public ServerResponse<Object> get_conduct_purchase_order(User user) {

		String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + "*";

		Set<String> keySet = RedisPoolUtil.keys(keyString);

		List<Order> orders = new ArrayList<Order>();

		String between = DateTimeUtil.betweenAnd(1);
		String and = DateTimeUtil.betweenAnd(2);

		PurchaseSeeOrderVo purchaseSeeOrderVo = null;

		List<PurchaseSeeOrderVo> listPurchaseSeeOrderVo = null;

		if (keySet.size() == 0) {
			// 查询数据库有无关单的数据

			orders = orderMapper.get_shut_orders(user.getId(), between, and, null, 0);

			int leng = orders.size();
			if (leng == 0) {
				return ServerResponse.createBySuccess();
			}

			purchaseSeeOrderVo = new PurchaseSeeOrderVo();
			listPurchaseSeeOrderVo = new ArrayList<PurchaseSeeOrderVo>();
			for (int a = 0; a < leng; a++) {

				Order order = orders.get(a);
				PurchaseSeeOrderVo purchaseSeeOrderVo_sub = setPurchaseSeeOrderVo(order);
				if (purchaseSeeOrderVo_sub.getVoSocket() == 0) {
					purchaseSeeOrderVo.setVoSocket(0);
				}

				listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);
				purchaseSeeOrderVo_sub = null;
			}

		} else {

			purchaseSeeOrderVo = new PurchaseSeeOrderVo();
			listPurchaseSeeOrderVo = new ArrayList<PurchaseSeeOrderVo>();

			// 获取redis中存在的id
			List<String> orderStatus_liStrings = new ArrayList<String>();
			for (String key : keySet) {
				PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();

				long pttl = RedisPoolUtil.pttl(key);
				System.out.println("pttl_____" + pttl);
				System.out.println("pttl_____" + (15 * 60 - 20) * 1000);
				if (pttl > (15 * 60 - 20) * 1000) {
					String orderJsonStr = RedisPoolUtil.get(key);
					Order order = JsonUtil.string2Obj(orderJsonStr, Order.class);

					orderStatus_liStrings.add(order.getId() + "");
					// guanShanReason 这个字段临时返给前端抢单的order_common_offer列表
					// 开启长连接
					purchaseSeeOrderVo.setVoSocket(0);
					purchaseSeeOrderVo_sub.setVoSocket(0);
					purchaseSeeOrderVo_sub.setOrderStatuName("报价中");
					purchaseSeeOrderVo_sub.setOrderStatu11(true);
					purchaseSeeOrderVo_sub.setVoOrder(order);
					purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(guanShanReason(order.getId()));
					// 刚发布的没有抢单报价
					listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);

				}
			}
			// 添加不在redis中的订单
			List<Order> orders_notin = new ArrayList<Order>();

			if (orderStatus_liStrings.size() > 0) {
				orders_notin = orderMapper.get_shut_orders(user.getId(), between, and, orderStatus_liStrings, 2);

			} else {
				orders_notin = orderMapper.get_shut_orders(user.getId(), between, and, orderStatus_liStrings, 1);
			}

			if (orders_notin.size() > 0) {
				for (Order order : orders_notin) {
					PurchaseSeeOrderVo purchaseSeeOrderVo_sub = setPurchaseSeeOrderVo(order);
					if (purchaseSeeOrderVo_sub.getVoSocket() == 0) {
						purchaseSeeOrderVo.setVoSocket(0);
					}
					listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);
					purchaseSeeOrderVo_sub = null;

				}
			}

		}

		purchaseSeeOrderVo.setListPurchaseSeeOrderVo(listPurchaseSeeOrderVo);
		return ServerResponse.createBySuccess(purchaseSeeOrderVo);
	}

	public List<OrderCommonOfferEvaluateVo> guanShanReason(long orderId) {
		List<OrderCommonOfferEvaluateVo> listOrderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();

		// guanShanReason 把报价的集合放到这个字段中 ，要处理状态 commodStatus ==0 的 预创建的多个
		List<OrderCommonOffer> orderCommonOffer_list = orderCommonOfferMapper.getInitial(orderId);
		int leng = orderCommonOffer_list.size();
		if (leng > 0) {
			for (int a = 0; a < leng; a++) {
				OrderCommonOffer offer = orderCommonOffer_list.get(a);
				offer.setCommodityZongJiage(offer.getCommodityZongJiage() / 100);
				if (offer.getOldOrNew() != 0 && offer.getRecommend() != 0) {
					offer.setSaleCompanyName("--");
				}

				Evaluate evaluate = evaluateMapper.selectEvvaluateByUserId(offer.getSaleUserId());
				// orderCommonOffer_list.set(a, offer);
				OrderCommonOfferEvaluateVo orderCommonOfferEvaluateVo = new OrderCommonOfferEvaluateVo();
				orderCommonOfferEvaluateVo.setEvaluate(evaluate);
				orderCommonOfferEvaluateVo.setOrderCommonOffer(offer);
				listOrderCommonOfferEvaluateVo.add(orderCommonOfferEvaluateVo);
			}
		}
		return listOrderCommonOfferEvaluateVo;
	}

	public List<OrderCommonOfferEvaluateVo> getOrderCommonOffer(long orderId, long saleUserId) {

		List<OrderCommonOfferEvaluateVo> listOrderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();
		// guanShanReason 给名字 地址即可 commodStatus ==1 的 成功
		OrderCommonOffer orderCommonOffer = orderCommonOfferMapper.getSuccess(orderId, saleUserId);
		Evaluate evaluate = evaluateMapper.selectEvvaluateByUserId(saleUserId);

		OrderCommonOfferEvaluateVo orderCommonOfferEvaluateVo = new OrderCommonOfferEvaluateVo();
		orderCommonOfferEvaluateVo.setEvaluate(evaluate);
		orderCommonOfferEvaluateVo.setOrderCommonOffer(orderCommonOffer);
		listOrderCommonOfferEvaluateVo.add(orderCommonOfferEvaluateVo);
		return listOrderCommonOfferEvaluateVo;
	}

	public PurchaseSeeOrderVo setPurchaseSeeOrderVo(Order order) {
		int orderStatus = order.getOrderStatus();
		PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
		if (orderStatus == 11) {
			// 开启长连接
			purchaseSeeOrderVo_sub.setVoSocket(0);
			purchaseSeeOrderVo_sub.setOrderStatuName("报价中");
			purchaseSeeOrderVo_sub.setOrderStatu11(true);
			purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(guanShanReason(order.getId()));
			// 刚发布的没有抢单报价
			// guanShanReason 这个字段临时返给前端抢单的order_common_offer列表
			// orders.set(a, guanShanReason(order));

		} else {

			if (orderStatus == 3) {
				purchaseSeeOrderVo_sub.setOrderStatuName("手动关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(null);
			} else if (orderStatus == 17) {
				purchaseSeeOrderVo_sub.setOrderStatuName("无接单关单");
				purchaseSeeOrderVo_sub.setOrderStatu17(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(null);
			} else {
				if (orderStatus == 4) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待收货");
					purchaseSeeOrderVo_sub.setOrderStatu4(true);
				} else if (orderStatus == 5) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待评价");
					purchaseSeeOrderVo_sub.setOrderStatu5(true);
				} else if (orderStatus == 6) {
					purchaseSeeOrderVo_sub.setOrderStatuName("已完成");
					purchaseSeeOrderVo_sub.setOrderStatu6(true);
				} else if (orderStatus == 12) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待支付保障金");
					purchaseSeeOrderVo_sub.setOrderStatu12(true);
				} else if (orderStatus == 13) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待送货");
					purchaseSeeOrderVo_sub.setOrderStatu13(true);
				} else if (orderStatus == 16) {
					purchaseSeeOrderVo_sub.setOrderStatuName("确认收货");
					purchaseSeeOrderVo_sub.setOrderStatu16(true);
				}
				order.setCommodityZongJiage(order.getCommodityZongJiage() / 100);
				purchaseSeeOrderVo_sub
						.setListOrderCommonOfferEvaluateVo(getOrderCommonOffer(order.getId(), order.getSaleUserId()));
			}
		}

		order.setCommodityJiage(order.getCommodityJiage() / 100);
		purchaseSeeOrderVo_sub.setVoOrder(order);
		return purchaseSeeOrderVo_sub;
	}
}
