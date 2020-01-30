package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.config.WeChatConfig;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.OrderMapper;
import com.dian.mmall.dao.PayOrderMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.goumaidingdan.OrderCommonOfferMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.PayOrder;
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
import com.dian.mmall.util.HttpUtils;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.RedisPoolUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;
import com.dian.mmall.util.WXPayUtil;

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
	@Autowired
	private WeChatConfig weChatConfig; // 读取配置文件中配置微信的字段
	@Autowired
	private PayOrderMapper payOrderMapper;

	@Override
	public synchronized ServerResponse<String> create_wholesaleCommodity_order(long userId,
			Map<String, Object> params) {
		// 暂时不做 批发订单
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

	/**
	 * 删除商品时查询有无交易的订单,前端下单入口已经屏蔽
	 */
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

	/**
	 * 商户创建采购订单
	 */
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
					// 限制死的 收货时间必须是两小时后
					serverResponseObject = DateTimeUtil.isPastDate2(giveTakeTime, 1);
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
//						String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + id;
//
//						RedisShardedPoolUtil.setEx(keyString, JsonUtil.obj2StringPretty(order),
//								Const.RedisCacheExtime.REDIS_ORDER_TIME);

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

	/**
	 * 商户采购订单 预估价格
	 */
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

	/**
	 * 用户手动改变, 倒计时结束改变 （倒计时结束改变后端没有校验） 改变订单状态
	 */

	@Override
	public synchronized ServerResponse<String> operation_purchase_order(User user, Map<String, Object> params) {
		// 先去redis中找 ，
//		String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + "*";
//		Set<String> keySet = RedisPoolUtil.keys(keyString);

		// boolean isredis = false;
		// orderCommonOfferId 选中的销售商id ，id 订单id ,commodityZongJiage 选中的报价元
		// saleUserIdDeng 登陆的userId
		int type = Integer.parseInt(params.get("type").toString().trim());
		long id = Long.parseLong(params.get("id").toString().trim());
//		if (keySet.size() > 0) {
//			System.out.println(isredis + "____________2222");
//			for (String key : keySet) {
//				PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
//				String orderJsonStr = RedisPoolUtil.get(key);
//				Order order = JsonUtil.string2Obj(orderJsonStr, Order.class);
//
//				if (id == order.getId() && user.getId() == order.getPurchaseUserId()) {
//					isredis = true;
//					// 这里处理
//					ServerResponse<String> serverResponseImpl = operation_purchase_order_impl(type, order, params,
//							isredis);
//					if (serverResponseImpl.getStatus() == 0) {
//
//						// 删除redis中的key
//						RedisPoolUtil.del(key);
//						return ServerResponse.createBySuccess();
//					} else {
//						return serverResponseImpl;
//					}
//
//				}
//			}
//		}

//		System.out.println(isredis + "____________3333333");
//		if (!isredis) {
//			System.out.println(isredis + "____________44444");
		// 找不到去数据库找，
		Order order = orderMapper.getOrderById(id, user.getId());

		if (order != null) {
			// 这里处理
			ServerResponse<String> serverResponseImpl = operation_purchase_order_impl(type, order, params);
			if (serverResponseImpl.getStatus() == 0) {
				return ServerResponse.createBySuccess();
			} else {
				return serverResponseImpl;
			}

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
		}
//		}
//		System.out.println(isredis + "____________555555");
		// return ServerResponse.createBySuccess();
	}

	/**
	 * 计算平均价格
	 */
	public int create_order_average(WholesaleCommodity wholesaleCommodity) {
		int commodityJiage = 0;
		List<Integer> commodityJiage_list = wholesaleCommodityService.getCommodityJiage(wholesaleCommodity);
		int length = commodityJiage_list.size();
		for (int a = 0; a < length; a++) {
			commodityJiage += commodityJiage_list.get(a);
		}
		return (int) commodityJiage / length;
	}

	/**
	 * 我的订单页 获取 3天内 创建的订单
	 */
	@Override
	public ServerResponse<Object> get_conduct_purchase_order(User user) {

//		String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + "*";
//		Set<String> keySet = RedisPoolUtil.keys(keyString);

		List<Order> orders = new ArrayList<Order>();

		String between = DateTimeUtil.betweenAnd(1);
		String and = DateTimeUtil.betweenAnd(2);

		// if (keySet.size() == 0) {
		// 查询数据库有无关单的数据

		orders = orderMapper.get_shut_orders(user.getId(), between, and, null, 0);

		int leng = orders.size();
		if (leng == 0) {
			return ServerResponse.createBySuccess();
		}

		PurchaseSeeOrderVo purchaseSeeOrderVo = new PurchaseSeeOrderVo();
		List<PurchaseSeeOrderVo> listPurchaseSeeOrderVo = new ArrayList<PurchaseSeeOrderVo>();
		for (int a = 0; a < leng; a++) {

			Order order = orders.get(a);
			PurchaseSeeOrderVo purchaseSeeOrderVo_sub = setPurchaseSeeOrderVo(order);
			if (purchaseSeeOrderVo_sub.getVoSocket() == 0) {
				purchaseSeeOrderVo.setVoSocket(0);
			}

			listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);
			purchaseSeeOrderVo_sub = null;
		}

//		} else {
//
//			purchaseSeeOrderVo = new PurchaseSeeOrderVo();
//			listPurchaseSeeOrderVo = new ArrayList<PurchaseSeeOrderVo>();
//
//			// 获取redis中存在的id
//			List<String> orderStatus_liStrings = new ArrayList<String>();
//			for (String key : keySet) {
//				PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
//
//				long pttl = RedisPoolUtil.pttl(key);
//				if (pttl > (15 * 60 - 20) * 1000) {
//					String orderJsonStr = RedisPoolUtil.get(key);
//					Order order = JsonUtil.string2Obj(orderJsonStr, Order.class);
//
//					orderStatus_liStrings.add(order.getId() + "");
//					// guanShanReason 这个字段临时返给前端抢单的order_common_offer列表
//					// 开启长连接
//					purchaseSeeOrderVo.setVoSocket(0);
//					purchaseSeeOrderVo_sub.setVoSocket(0);
//					purchaseSeeOrderVo_sub.setOrderStatuName("报价中");
//					purchaseSeeOrderVo_sub.setOrderStatu11(true);
//					purchaseSeeOrderVo_sub.setVoOrder(order);
//					purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(guanShanReason(order.getId()));
//					// 刚发布的没有抢单报价
//					listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);
//
//				}
//			}
//			// 添加不在redis中的订单
//			List<Order> orders_notin = new ArrayList<Order>();
//
//			if (orderStatus_liStrings.size() > 0) {
//				orders_notin = orderMapper.get_shut_orders(user.getId(), between, and, orderStatus_liStrings, 2);
//
//			} else {
//				orders_notin = orderMapper.get_shut_orders(user.getId(), between, and, orderStatus_liStrings, 1);
//			}
//
//			if (orders_notin.size() > 0) {
//				for (Order order : orders_notin) {
//					PurchaseSeeOrderVo purchaseSeeOrderVo_sub = setPurchaseSeeOrderVo(order);
//					if (purchaseSeeOrderVo_sub.getVoSocket() == 0) {
//						purchaseSeeOrderVo.setVoSocket(0);
//					}
//					listPurchaseSeeOrderVo.add(purchaseSeeOrderVo_sub);
//					purchaseSeeOrderVo_sub = null;
//
//				}
//			}
//
//		}

		purchaseSeeOrderVo.setListPurchaseSeeOrderVo(listPurchaseSeeOrderVo);
		return ServerResponse.createBySuccess(purchaseSeeOrderVo);
	}

	/**
	 * 根据订单id查询报价人员名单 ，返回setPurchaseSeeOrderVo
	 */
	private List<OrderCommonOfferEvaluateVo> guanShanReason(long orderId) {
		List<OrderCommonOfferEvaluateVo> listOrderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();
		// guanShanReason 把报价的集合放到这个字段中 ，要处理状态 commodStatus ==0 的 预创建的多个
		List<OrderCommonOffer> orderCommonOffer_list = orderCommonOfferMapper.getInitial(orderId);
		int leng = orderCommonOffer_list.size();
		if (leng > 0) {
			for (int a = 0; a < leng; a++) {
				OrderCommonOffer offer = orderCommonOffer_list.get(a);
				offer.setCommodityZongJiage(offer.getCommodityZongJiage() / 100);
//				if (offer.getOldOrNew() != 0 && offer.getRecommend() != 0) {
//					//设置手机号和用户名
//				}
//				offer.setConsigneeName("选择后可见");
//				offer.setContact("选择后可见");

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

	/**
	 * 查询抢单成功的 返回setPurchaseSeeOrderVo
	 */
	private List<OrderCommonOfferEvaluateVo> getOrderCommonOffer(long orderId, long saleUserId) {

		List<OrderCommonOfferEvaluateVo> listOrderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();
		// guanShanReason 给名字 地址即可 commodStatus ==1 的 成功
		OrderCommonOffer orderCommonOffer = orderCommonOfferMapper.getSuccess(orderId, saleUserId);
		orderCommonOffer.setContact(EncrypDES.decryptPhone(orderCommonOffer.getContact()));
		orderCommonOffer.setCommodityZongJiage(orderCommonOffer.getCommodityZongJiage() / 100);

		Evaluate evaluate = evaluateMapper.selectEvvaluateByUserId(saleUserId);

		OrderCommonOfferEvaluateVo orderCommonOfferEvaluateVo = new OrderCommonOfferEvaluateVo();
		orderCommonOfferEvaluateVo.setEvaluate(evaluate);
		orderCommonOfferEvaluateVo.setOrderCommonOffer(orderCommonOffer);
		listOrderCommonOfferEvaluateVo.add(orderCommonOfferEvaluateVo);
		return listOrderCommonOfferEvaluateVo;
	}

	/**
	 * 返回get_conduct_purchase_order方法 的订单+抢单人员的VO
	 */
	private PurchaseSeeOrderVo setPurchaseSeeOrderVo(Order order) {
		int orderStatus = order.getOrderStatus();
		PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
		List<OrderCommonOfferEvaluateVo> rderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();
		if (orderStatus == 11) {
			// 开启长连接
			purchaseSeeOrderVo_sub.setVoSocket(0);
			purchaseSeeOrderVo_sub.setOrderStatuName("报价中");
			purchaseSeeOrderVo_sub.setOrderStatu11(true);
			rderCommonOfferEvaluateVo = guanShanReason(order.getId());
			if (rderCommonOfferEvaluateVo.size() > 0) {
				for (int a = 0; a < rderCommonOfferEvaluateVo.size(); a++) {
					OrderCommonOfferEvaluateVo subVo = rderCommonOfferEvaluateVo.get(a);
					subVo.getOrderCommonOffer().setContact("选择后可见");
					subVo.getOrderCommonOffer().setConsigneeName("选择后可见");
					rderCommonOfferEvaluateVo.set(a, subVo);
				}

			}

			purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			// 刚发布的没有抢单报价
			// guanShanReason 这个字段临时返给前端抢单的order_common_offer列表
			// orders.set(a, guanShanReason(order));

		} else if (orderStatus == 18) {
			// 开启长连接
			purchaseSeeOrderVo_sub.setOrderStatuName("待选择商家");
			purchaseSeeOrderVo_sub.setOrderStatu18(true);
			rderCommonOfferEvaluateVo = guanShanReason(order.getId());
			if (rderCommonOfferEvaluateVo.size() > 0) {
				for (int a = 0; a < rderCommonOfferEvaluateVo.size(); a++) {
					OrderCommonOfferEvaluateVo subVo = rderCommonOfferEvaluateVo.get(a);
					subVo.getOrderCommonOffer().setContact("选择后可见");
					subVo.getOrderCommonOffer().setConsigneeName("选择后可见");
					rderCommonOfferEvaluateVo.set(a, subVo);
				}

			}

			purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
		} else {

			if (orderStatus == 3) {
				purchaseSeeOrderVo_sub.setOrderStatuName("关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else if (orderStatus == 17) {
				purchaseSeeOrderVo_sub.setOrderStatuName("无销售商接单关单");
				purchaseSeeOrderVo_sub.setOrderStatu17(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else if (orderStatus == 20) {
				purchaseSeeOrderVo_sub.setOrderStatuName("未支付质保金关单");
				purchaseSeeOrderVo_sub.setOrderStatu20(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else if (orderStatus == 19) {
				purchaseSeeOrderVo_sub.setOrderStatuName("未支付定金关单");
				purchaseSeeOrderVo_sub.setOrderStatu19(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else {
				if (orderStatus == 4) {
					purchaseSeeOrderVo_sub.setOrderStatuName("送货中");
					purchaseSeeOrderVo_sub.setOrderStatu4(true);
				} else if (orderStatus == 5) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待评价");
					purchaseSeeOrderVo_sub.setOrderStatu5(true);
				} else if (orderStatus == 6) {
					purchaseSeeOrderVo_sub.setOrderStatuName("已完成");
					purchaseSeeOrderVo_sub.setOrderStatu6(true);
				} else if (orderStatus == 12) {

					purchaseSeeOrderVo_sub.setOrderStatuName("待支付定金(报价的6%)");
					purchaseSeeOrderVo_sub.setOrderStatu12(true);
				} else if (orderStatus == 13) {
					// 开启长连接
					purchaseSeeOrderVo_sub.setVoSocket(0);
					purchaseSeeOrderVo_sub.setOrderStatuName("待销售商支付质保金(报价的20%)");
					purchaseSeeOrderVo_sub.setOrderStatu13(true);
				} else if (orderStatus == 16) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待确认收货");
					purchaseSeeOrderVo_sub.setOrderStatu16(true);
				} else if (orderStatus == 21) {
					purchaseSeeOrderVo_sub.setOrderStatuName("支付失败请重新支付");
					purchaseSeeOrderVo_sub.setOrderStatu21(true);
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

	/**
	 * 实现用户（抢单人员，购买商，定时任务） 操作和定时任务操作， 改变订单状态 operation_purchase_order
	 */
	private ServerResponse<String> operation_purchase_order_impl(int type, Order order, Map<String, Object> params) {
		// 更新数据库，订单表，抢单表，找到了操作 更新数据库，订单表，抢单表， 发通知给抢单人员
//		状态 =11 时 显示关单键  和 确认键  关单后端 传 3，确认传13 -->
//
//		关单后状态==3或者17   显示开启键   开启后端 传 11， 
//		确认后状态==13  显示待确认总价 按键  置灰    销售商点击确认价格
//      
//		支付成功状态==4  显示 待收货 不可点击   送货者状态4时显示 已送到 传 operationRow(scope.row,16)  点击后状态变成16-->
//      送货者再次确认  13-->转为12待支付           显示 支付键    支付后端  传 4  ， 和 显示关单键 -->
//		状态==16 显示确认收货   向后端传 5  状态更新为5--->
//
//		状态==5 显示待评价      向后端传 6  订单更新为 6 完成评价
		String updateTime = DateTimeUtil.dateToAll();
		long id = order.getId();

		order.setUpdateTime(updateTime);

		List<OrderCommonOffer> list_evaluates = orderCommonOfferMapper.getInitial(order.getId());

		try {
			if (type == 3) {
				if (order.getOrderStatus() == 11 || order.getOrderStatus() == 18) {
					// 关单 关单状态只能 是11 时 12 （），21时18时才能关
					order.setOrderStatus(type);
					orderMapper.operation_purchase_order(order);
					if (list_evaluates.size() > 0) {
						// TODO 去发送通知全部 接单失败
						// 全部更新抢单
						orderCommonOfferMapper.operation_purchase_evaluate_all(order.getId(), updateTime);
					}
				} else if (order.getOrderStatus() == 12 || order.getOrderStatus() == 21) {
					// TODO 通知接单成功的用户，关单了 order.getSaleUserId()发给这个user id

					order.setOrderStatus(type);
					// 更新订单表
					orderMapper.operation_purchase_order(order);
					// 更新抢单表
					orderCommonOfferMapper.operation_purchase_evaluate_id(order.getId(), updateTime,
							order.getSaleUserId());
					// 检查有无待支付的订单 删除

				}
			} else if (type == 11) {
				// 重新开启订单 只有3和17,19,20的才能重新开启
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				if (list_evaluates.size() > 0) {
					// 全部更新
					orderCommonOfferMapper.operation_purchase_evaluate_all(order.getId(), updateTime);
				}
				// TODO 通知给抢单人员
			} else if (type == 13) {
				// 确认订单
				long order_commodityZongJiage = Long.parseLong(params.get("commodityZongJiage").toString().trim());
				long orderCommonOfferId = Long.parseLong(params.get("orderCommonOfferId").toString().trim());
				if (list_evaluates.size() > 0) {
					boolean b = false;
					for (int a = 0; a < list_evaluates.size(); a++) {
						OrderCommonOffer orderCommonOffer = list_evaluates.get(a);
						if (orderCommonOffer.getId() == orderCommonOfferId) {
							b = true;

							long commodityZongJiage = orderCommonOffer.getCommodityZongJiage() / 100;

							if (commodityZongJiage == order_commodityZongJiage) {
								// 选中的更新 为成功
								long saleUserId = orderCommonOffer.getSaleUserId();
								orderCommonOfferMapper.operation_purchase_evaluate_selected(order.getId(), updateTime,
										saleUserId, orderCommonOffer.getId());
								// 更新订单 为抢单成功 和其他信息 报价金额，抢单用户id
								if (order.getOrderStatus() == 11) {
									order.setSaleUserId(saleUserId);
									order.setCommodityZongJiage(orderCommonOffer.getCommodityZongJiage());
									order.setOrderStatus(type);
									orderMapper.operation_purchase_order(order);
									// TODO 通知给抢单成功人员 userID
								} else {
									return ServerResponse
											.createByErrorMessage(ResponseMessage.dingdanzhuangtaicuowu.getMessage());
								}

							} else {
								return ServerResponse
										.createByErrorMessage(ResponseMessage.baojiajinebuyizhi.getMessage());
							}
						}
					}
					if (!b) {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.baojiashanghuchaxunshibai.getMessage());
					}

					// 未选中全部更新为失败
					orderCommonOfferMapper.operation_purchase_evaluate_noSelected(order.getId(), updateTime);

				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.baojiashanghuchaxunshibai.getMessage());
				}

			} else if (type == 12) {
				// 抢单人员操作
				long saleUserIdDeng = Long.parseLong(params.get("saleUserIdDeng").toString().trim());
				if (saleUserIdDeng != order.getSaleUserId()) {
					return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
				}
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				// TODO 通知给商户 ，抢单人员已经确认过价格

			} else if (type == 19) {
				PayOrder payOrder = payOrderMapper.getPayOrderByOrderId(order.getId(), 0);
				if (payOrder == null) {
					// 支付操作时处理 TODO 直接set支付的金额和待支付金额
					order.setOrderStatus(type);
					// 判断是否在支付中
					orderMapper.operation_purchase_order(order);
					// TODO 通知给抢单成功人员 userID 支付成功送货
					// 更新抢单表
					orderCommonOfferMapper.operation_purchase_evaluate_id(order.getId(), updateTime,
							order.getSaleUserId());
				} else {
					// 是不是要调微信查询 结果TODO

				}
			}

			else if (type == 16) {
				// 抢单人员操作
				long saleUserIdDeng = Long.parseLong(params.get("saleUserIdDeng").toString().trim());
				if (saleUserIdDeng != order.getSaleUserId()) {
					return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
				}
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				// TODO 通知给商户 ，抢单人员已经送货到了
			} else if (type == 5) {
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				// TODO 通知给抢单成功人员 userID 用户已经收货，订单完成
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}

			return ServerResponse.createBySuccess();
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		}
	}

	/**
	 * 改变 订单状态是11（下单）,12（待支付定金）,13（销售商支付质保金）,18（有人接单多+15分钟）,21(支付失败)状态的定时任务
	 */
	@Override
	public void timerOrderStatus() {

		List<Order> orders = orderMapper.timerOrderStatus();

		if (orders.size() > 0) {
			long nowDateLong = new Date().getTime();
			String updateTime = DateTimeUtil.dateToAll();

			for (Order o : orders) {
				int orderStatus = o.getOrderStatus();
				long createTimeLong = DateTimeUtil.strToDate(o.getCreateTime()).getTime();

				if (orderStatus == 11) {
					if ((nowDateLong - createTimeLong) >= 30 * 60 * 1000) {
						// 已经到报价时间
						int initialCount = orderCommonOfferMapper.getInitialCount(o.getId());
						if (initialCount > 0) {
							// 有报价更新为 orderStatus==18
							o.setOrderStatus(18);
							o.setUpdateTime(updateTime);
							orderMapper.operation_purchase_order(o);
						} else {
							// 没有报价的更新为 17
							o.setOrderStatus(17);
							orderMapper.operation_purchase_order(o);
						}
					}
				} else if (orderStatus == 13) {

					// 销售商到时见未确认 更新为 20 ，最后一次更新时间+15分钟 用 updateTime

					long oUpDateLong = DateTimeUtil.strToDate(o.getUpdateTime()).getTime();
					if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
						o.setUpdateTime(updateTime);
						o.setOrderStatus(20);
						orderMapper.operation_purchase_order(o);
						// TODO 记录下 这个销售商
						// 更新抢单表
						orderCommonOfferMapper.uptateGuanDan(o.getId(), o.getSaleUserId(), updateTime);
						// TODO 发消息给这个销售商
					}
				} else if (orderStatus == 12 || orderStatus == 21) {
					// 判断是否在支付中
					PayOrder payOrder = payOrderMapper.getPayOrderByOrderId(o.getId(), 0);
					if (payOrder == null) {
						// 检查有无支付成功的
						payOrder = payOrderMapper.getPayOrderByOrderId(o.getId(), 9);
						if (payOrder == null) {

							// 购买者未支付定金 19，最后一次更新时间+15分钟 用 updateTime
							long oUpDateLong = DateTimeUtil.strToDate(o.getUpdateTime()).getTime();
							if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
								o.setUpdateTime(updateTime);
								o.setOrderStatus(19);
								orderMapper.operation_purchase_order(o);
								// 更新抢单表
								orderCommonOfferMapper.uptateGuanDan(o.getId(), o.getSaleUserId(), updateTime);

							}
						}
					} else {
						// TODO 调微信 关单去

						// 购买者未支付定金 19，最后一次更新时间+15分钟 用 updateTime
						long oUpDateLong = DateTimeUtil.strToDate(o.getUpdateTime()).getTime();
						if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
							o.setUpdateTime(updateTime);
							o.setOrderStatus(19);
							orderMapper.operation_purchase_order(o);
							// 更新抢单表
							orderCommonOfferMapper.uptateGuanDan(o.getId(), o.getSaleUserId(), updateTime);

						}

					}
					// TODO 发消息给这个销售商,购买者未支付定金关单
				} else if (orderStatus == 18) {
					if ((nowDateLong - createTimeLong) >= 45 * 60 * 1000) {
						// 留15分钟给采购者选择销售商，超时关单为3
						o.setOrderStatus(3);
						orderMapper.operation_purchase_order(o);
						// TODO 发消息给全部 接单的销售商
						List<OrderCommonOffer> lists = orderCommonOfferMapper.getInitial(o.getId());
					}
				}
			}
		}
	}

	/**
	 * 处理微信支付创建时间大于=当前时间15分钟的 支付订单 定时任务查询微信 执行 成功的调用callback ,没有查到结果的 到时间 超时关单，
	 * 未到时不处理 关单前再查一次 支付表状态
	 */

	@Override
	public void timerSelsetPayOrder() {
		String createTime = DateTimeUtil.dateTimeToDateString(new Date().getTime() - 15 * 60 * 100);
		List<PayOrder> payOrders = payOrderMapper.timerSelsetPayOrder(createTime, 0, 0);
		if (payOrders.size() > 0) {
			for (PayOrder pay : payOrders) {
				ServerResponse<Object> serverResponse = orderqueryWX(pay);
				if (serverResponse.getStatus() == 0) {
					timerSelsetPayOrderImpl((Map<String, String>) serverResponse.getData(), pay);
				}
			}

		}

	}

	private void timerSelsetPayOrderImpl(Map<String, String> sortedMap, PayOrder payOrder) {

		// 支付结果
		String trade_state = sortedMap.get("trade_state");
//		SUCCESS—支付成功
//		REFUND—转入退款
//		NOTPAY—未支付
//		CLOSED—已关闭
//		REVOKED—已撤销（付款码支付）
//		USERPAYING--用户支付中（付款码支付）
//		PAYERROR--支付失败(其他原因，如银行返回失败)
		payOrder.setUpdateTime(DateTimeUtil.dateToAll());
		if (sortedMap.toString().length() > 300) {
			payOrder.setMeg(sortedMap.toString().substring(0, 298));
		} else {
			payOrder.setMeg(sortedMap.toString());
		}
		payOrder.setPayType("CX");
		payOrder.setBeiyong(trade_state);
		payOrder.setCostType("CX_ding");

		Order order = new Order();
		order.setId(payOrder.getOrderId());
		order.setGuanShanReason("CX");
		if ("SUCCESS".equals(trade_state)) {
			String transaction_id = sortedMap.get("transaction_id");// 微信支付订单号
			payOrder.setBeiyong(transaction_id);
			String time_end = sortedMap.get("time_end");// 支付时间
			payOrder.setPayTime(time_end);
			order.setPaymentTime(time_end);

			int total_fee = 0;
			try {
				total_fee = Integer.parseInt(sortedMap.get("total_fee"));
			} catch (Exception e) {
				System.out.print(payOrder.getOutTradeNo() + "转换金额异常");
			}
			if (total_fee != 0) {
				if (total_fee != payOrder.getTotalFee()) {
					payOrder.setState(1);
					order.setPayStatus(4);
				} else {
					payOrder.setCostType(total_fee + "");
					// 支付金额与应收金额不一致返回错误，业务进行不下去可以叫用户打客服电话
					payOrder.setState(3);
					createZhifuBaojing(payOrder);

					order.setPayStatus(5);
					order.setGuanShanTime(payOrder.getTotalFee() + "");
				}
				// 更新支付表
				payOrderMapper.callbackUpdate(payOrder);
				// 更新订单表 支付成功
				order.setOrderStatus(4);
				order.setGuaranteeMoney(total_fee + "");
				callbackUpDateOrder(order);
				//TODO 通知接单者 已支付完成
			}
		}else if(!"USERPAYING".equals(trade_state)) {
		// TODO 支付失败关单
			
			
		payOrder.setState(4);
		// 更新支付表
		payOrderMapper.callbackUpdate(payOrder);
		// 更新订单表 支付失败可以再次发起
		order.setOrderStatus(19);
		order.setPayStatus(0);
		callbackUpDateOrder(order);
		//TODO 通知接单者 关单了
		}
	}

	/**
	 * 生成二维码
	 */
	// 支付定金生成二维码（ 第一步统一下单生成二维码）
	@Override
	public ServerResponse<String> native_pay_order(User user, String spbillCreateIp, long id) {
		// 检查订单信息，获取支付金额
		Order order = orderMapper.getOrderById(id, user.getId());

		if (order != null) {
			if (order.getOrderStatus() != 12 && order.getOrderStatus() != 21) {
				return ServerResponse.createByErrorMessage(ResponseMessage.dingdanzhuangtaicuowu.getMessage());
			}
			long commodityZongJiage = order.getCommodityZongJiage();
			int totalFee = new Double(commodityZongJiage * 0.06).intValue();
			if (totalFee == 0) {
				totalFee = 1; // 如果金额小于1分将支付1分
			}
			// 若当前订单有 未支付的 把这个返链接返回
			PayOrder payOrder = payOrderMapper.getPayOrderByOrderId(id, 0);

			if (payOrder != null && payOrder.getTotalFee() == totalFee) {
				return ServerResponse.createBySuccessMessage(payOrder.getMeg());
			}
			// 查询是否已经支付过
			payOrder = payOrderMapper.getPayOrderByOrderId(id, 9);
			if (payOrder != null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.zhifuyiwancheng.getMessage());
			}

			// 生成支付订单
			payOrder = new PayOrder();
			payOrder.setUserId(user.getId());
			payOrder.setOrderId(id);
			payOrder.setBody("订单ID" + id + "支付定金");
			// 生成规则 订单表id+"_"+时间戳
			String outTradeNo = id + "_" + new Date().getTime();
			payOrder.setOutTradeNo(outTradeNo);
			payOrder.setSpbillCreateIp(spbillCreateIp);
			payOrder.setTotalFee(totalFee);
			payOrder.setTradeType("NATIVE"); // 暂时先写死
			payOrder.setState(0);
			payOrder.setDel(0);
			payOrder.setCreateTime(DateTimeUtil.dateToAll());

			// 落库payOrder
			payOrderMapper.createPyOrder(payOrder);

			payOrder = payOrderMapper.getPayOrderByOrderId(id, 0);

			return unifiedOrder(payOrder);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
		}

	}

	/**
	 * 微信支付 统一下单方法
	 * 
	 * @return
	 */
	private ServerResponse<String> unifiedOrder(PayOrder payOrder) {
		// 微信接口文档
		// https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_10&index=1
		// TODO 所有weChatConfig 配置文件均为测试文件
		// 生成签名
		SortedMap<String, String> params = new TreeMap<>();
		params.put("appid", weChatConfig.getAppId()); // 公众账号id
		params.put("mch_id", weChatConfig.getMchId()); // 商户号
		params.put("nonce_str", payOrder.getNonceStr()); // 随机字符串
		params.put("body", payOrder.getBody()); // 描述
		params.put("out_trade_no", payOrder.getOutTradeNo()); // 订单号，自己生成的订单号
//		//要求32个字符内，只能是数字大小写字母_—|*,并且自己内部是唯一
		params.put("total_fee", payOrder.getTotalFee().toString()); // 支付金额
//		 // APP和网页支付提交用户端ip，扫码Native支付填调用微信支付API的机器IP 待确认（调用时服务器的ip）。
		params.put("spbill_create_ip", payOrder.getSpbillCreateIp());
//		   // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
		params.put("notify_url", weChatConfig.getPayCallbackUrl());
		params.put("trade_type", payOrder.getTradeType()); // JSAPI--JSAPI支付（公众号或小程序支付）、NATIVE--扫码支付、APP--app支付，MWEB--H5支付
//       
//	   //网上找的开始-----
//		// 自定义参数, 可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
//        //params.put("device_info", "");
//        // 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
//       // params.put("attach", "");
//      //网上找的结束-----
//		
//        //sign签名 TODO 所有weChatConfig 配置文件均为测试文件
		String sign = WXPayUtil.createSign(params, weChatConfig.getKey());
		params.put("sign", sign);
		// map转xml
		String payXml = null;
		try {
			payXml = WXPayUtil.mapToXml(params);
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhuanghuanshujumapToXml.getMessage());
		}

		if (payXml == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhuanghuanshujumapToXml.getMessage() + "null");
		}
		// 获取codeurl
		String orderStr = HttpUtils.doPost(WeChatConfig.getUnifiedOrderUrl(), payXml, 4000);
		// 统一下单
		Map<String, String> unifiedOrderMap = null;
		try {
			unifiedOrderMap = WXPayUtil.xmlToMap(orderStr);
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhuanghuanshujuxmlToMap.getMessage());
		}
		System.out.println(unifiedOrderMap.toString());

		payOrder.setUpdateTime(DateTimeUtil.dateToAll());
		// {return_msg=appidä¸å­å¨, return_code=FAIL}
		if (unifiedOrderMap != null) {
			String return_code = unifiedOrderMap.get("return_code");
			if (return_code.equals("FAIL")) {

				payOrder.setDel(1);
				if (unifiedOrderMap.toString().length() > 300) {
					payOrder.setMeg(unifiedOrderMap.toString().substring(0, 298));
				} else {
					payOrder.setMeg(unifiedOrderMap.toString());
				}
				payOrderMapper.unifiedUptaePayOrder(payOrder);
				// TODO 暂时返回固定的链接
				// return
				// ServerResponse.createByErrorMessage(ResponseMessage.weixinxiaodanshibai.getMessage());
				return ServerResponse.createBySuccessMessage("https://www.baidu.com");

			}
			// 更新数据库
			payOrder.setMeg(unifiedOrderMap.get("code_url"));
			payOrderMapper.unifiedUptaePayOrder(payOrder);
			return ServerResponse.createBySuccessMessage(unifiedOrderMap.get("code_url"));
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.zhuanghuanshujuxmlToMap.getMessage() + "null");
	}

	/**
	 * 微信支付异步回调
	 */
	@Override
	public ServerResponse<String> callback(SortedMap<String, String> sortedMap) {
		String outTradeNo = sortedMap.get("out_trade_no");
		PayOrder payOrder = payOrderMapper.getCallbackPayOrder(outTradeNo, 0);
		if (payOrder != null) {
			payOrder.setUpdateTime(DateTimeUtil.dateToAll());
			if (sortedMap.toString().length() > 300) {
				payOrder.setMeg(sortedMap.toString().substring(0, 298));
			} else {
				payOrder.setMeg(sortedMap.toString());
			}
			// coller层自己加的的参数
			payOrder.setPayType(sortedMap.get("payType"));
			String transaction_id = sortedMap.get("transaction_id");// 微信支付订单号
			payOrder.setBeiyong(transaction_id);
			payOrder.setCostType("WX_diao");

			Order order = new Order();
			order.setId(payOrder.getOrderId());
			order.setGuanShanReason("HD");
			if ("SUCCESS".equals(sortedMap.get("result_code"))) {

				String time_end = sortedMap.get("time_end");// 支付时间
				payOrder.setPayTime(time_end);

				order.setPaymentTime(time_end);

				int total_fee = 0;
				try {
					total_fee = Integer.parseInt(sortedMap.get("total_fee"));
				} catch (Exception e) {
					System.out.print(outTradeNo + "转换金额异常");
				}
				if (total_fee != 0) {
					if (total_fee != payOrder.getTotalFee()) {
						payOrder.setState(1);
						order.setPayStatus(4);
					} else {
						payOrder.setCostType(total_fee + "");
						// 支付金额与应收金额不一致返回错误，业务进行不下去可以叫用户打客服电话
						payOrder.setState(3);
						createZhifuBaojing(payOrder);

						order.setPayStatus(5);
						order.setGuanShanTime(payOrder.getTotalFee() + "");
					}
					// 更新支付表
					payOrderMapper.callbackUpdate(payOrder);
					// 更新订单表 支付成功
					order.setOrderStatus(4);
					order.setGuaranteeMoney(total_fee + "");
					callbackUpDateOrder(order);
					//TODO 通知接单者 已支付完成
					return ServerResponse.createBySuccess();
				}
				// 支付金额为0返回错误
				return ServerResponse.createByError();

			}
			// 支付失败关单
			payOrder.setState(2);
			// 更新支付表
			payOrderMapper.callbackUpdate(payOrder);
			// 更新订单表 支付失败可以再次发起
			order.setOrderStatus(21);
			order.setPayStatus(0);
			callbackUpDateOrder(order);
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

	/**
	 * 微信支付回调、查询后 更新 订单状态
	 */

	private ServerResponse<String> callbackUpDateOrder(Order order) {

		order.setUpdateTime(DateTimeUtil.dateToAll());
		/**
		 * orderStatus=21 如果状态是关单==3，就不做更新 guanShanReason 此字段记录是哪个操作对应支付表payType 更新
		 * order表 guanShanTime==应该支付金额 orderStatus==4成功,5支付异常
		 */
		// 如果是 4 要计算待支付金额
		if (order.getOrderStatus() == 4) {
			Order selectOrder = orderMapper.getOrderById(order.getId(), 0);

			// 已支付金额元
			Float guaranteeMoney = Float.parseFloat(order.getGuaranteeMoney()) / 100;
			order.setGuaranteeMoney(guaranteeMoney + "");
			// 待支付金额 balanceMoney 字符串 待支付金额
			order.setBalanceMoney(
					Float.parseFloat(selectOrder.getCommodityZongJiage() + "") / 100 - guaranteeMoney + "");
			orderMapper.callbackUpDateOrder(order);
			return null;
		}
		orderMapper.callbackUpDateOrder(order);

		return null;
	}

	/**
	 * 主动查询后更新状态 微信文档 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_2
	 */
	private ServerResponse<Object> orderqueryWX(PayOrder pay) {
		SortedMap<String, String> params = new TreeMap<>();
		params.put("appid", weChatConfig.getAppId()); // 公众账号id
		params.put("mch_id", weChatConfig.getMchId()); // 商户号
		params.put("out_trade_no", pay.getOutTradeNo()); // 订单号，自己生成的订单号
		params.put("nonce_str", MD5Util.generateUUID()); // 随机字符串
		String sign = WXPayUtil.createSign(params, weChatConfig.getKey());
		params.put("sign", sign);
		// map转xml
		String payXml = null;
		try {
			payXml = WXPayUtil.mapToXml(params);
		} catch (Exception e) {
			// TODO 内部邮件报警
			return ServerResponse.createByError();
		}

		if (payXml == null) {
			// TODO 内部邮件报警
			return ServerResponse.createByError();
		}

		// 获取 查询结果
		String orderStr = HttpUtils.doPost(WeChatConfig.getWxOrderUrl(), payXml, 4000);

		Map<String, String> unifiedOrderMap = null;
		try {
			unifiedOrderMap = WXPayUtil.xmlToMap(orderStr);
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhuanghuanshujuxmlToMap.getMessage());
		}
		System.out.println(unifiedOrderMap.toString());

		if (unifiedOrderMap != null) {
			String return_code = unifiedOrderMap.get("return_code");

			if (return_code.equals("FAIL")) {
				// TODO 内部报警
				return ServerResponse.createByError();
			}
			String result_code = unifiedOrderMap.get("result_code");
			if (return_code.equals("SUCCESS") && result_code.equals("SUCCESS")) {
// 支付结果		String	trade_state	=unifiedOrderMap.get("trade_state");	
//				SUCCESS—支付成功
//				REFUND—转入退款
//				NOTPAY—未支付
//				CLOSED—已关闭
//				REVOKED—已撤销（付款码支付）
//				USERPAYING--用户支付中（付款码支付）
//				PAYERROR--支付失败(其他原因，如银行返回失败)
				return ServerResponse.createBySuccess(unifiedOrderMap);
			}
		}
		return ServerResponse.createByError();
	}

	/**
	 * 主动关单，更新订单表状态为超时未支付，更新支付表为 设置 del=1
	 */

	/**
	 * 支付报警 state==3 TODO 暂未实现
	 */
	private void createZhifuBaojing(PayOrder payOrder) {
		// TODO 发邮件给技术
		payOrder.getCostType();// 实际支付金额

		System.out.println("支付订单ID" + payOrder.getId() + "支付异常" + ",应该支付金额" + payOrder.getTotalFee() + "(分)，实际支付："
				+ payOrder.getCostType() + "(分)");

	}

	/**
	 * TODO guanShanTime==应该支付金额 orderStatus==5支付异常 暂未实现
	 */
	private ServerResponse<Order> getZhifuBaojing(PayOrder payOrder) {
		// 管理员业务查询接口
		return null;
	}

}
