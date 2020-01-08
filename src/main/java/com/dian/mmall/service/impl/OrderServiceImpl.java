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
					//限制死的 收货时间必须是两小时后
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

	// 改变订单状态

	@Override
	public synchronized ServerResponse<String> operation_purchase_order(User user, Map<String, Object> params) {
		// 先去redis中找 ，
		String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + "*";
		Set<String> keySet = RedisPoolUtil.keys(keyString);

		boolean isredis = false;
		// orderCommonOfferId 选中的销售商id ，id 订单id ,commodityZongJiage 选中的报价元
		// saleUserIdDeng 登陆的userId
		int type = Integer.parseInt(params.get("type").toString().trim());
		long id = Long.parseLong(params.get("id").toString().trim());
		if (keySet.size() > 0) {
			System.out.println(isredis + "____________2222");
			for (String key : keySet) {
				PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
				String orderJsonStr = RedisPoolUtil.get(key);
				Order order = JsonUtil.string2Obj(orderJsonStr, Order.class);

				if (id == order.getId() && user.getId() == order.getPurchaseUserId()) {
					isredis = true;
					// 这里处理
					ServerResponse<String> serverResponseImpl = operation_purchase_order_impl(type, order, params,
							isredis);
					if (serverResponseImpl.getStatus() == 0) {

						// 删除redis中的key
						RedisPoolUtil.del(key);
						return ServerResponse.createBySuccess();
					} else {
						return serverResponseImpl;
					}

				}
			}
		}

		System.out.println(isredis + "____________3333333");
		if (!isredis) {
			System.out.println(isredis + "____________44444");
			// 找不到去数据库找，
			Order order = orderMapper.getOrderById(id, user.getId());
			if (order != null) {
				// 这里处理
				ServerResponse<String> serverResponseImpl = operation_purchase_order_impl(type, order, params, isredis);
				if (serverResponseImpl.getStatus() == 0) {
					return ServerResponse.createBySuccess();
				} else {
					return serverResponseImpl;
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
			}
		}
		System.out.println(isredis + "____________555555");
		return ServerResponse.createBySuccess();
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

	// 获取创建的订单
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
//				if (offer.getOldOrNew() != 0 && offer.getRecommend() != 0) {
//					//设置手机号和用户名
//				}
				offer.setConsigneeName("选择后可见");
				offer.setContact("选择后可见");

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
		orderCommonOffer.setContact(EncrypDES.decryptPhone(orderCommonOffer.getContact()));
		orderCommonOffer.setCommodityZongJiage(orderCommonOffer.getCommodityZongJiage() / 100);

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

		} else {

			if (orderStatus == 3) {
				purchaseSeeOrderVo_sub.setOrderStatuName("手动关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else if (orderStatus == 17) {
				purchaseSeeOrderVo_sub.setOrderStatuName("无接单关单");
				purchaseSeeOrderVo_sub.setOrderStatu17(true);
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
					purchaseSeeOrderVo_sub.setOrderStatuName("支付处理中");
					purchaseSeeOrderVo_sub.setOrderStatu12(true);
				} else if (orderStatus == 13) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待支付保障金");
					purchaseSeeOrderVo_sub.setOrderStatu13(true);
				} else if (orderStatus == 16) {
					purchaseSeeOrderVo_sub.setOrderStatuName("待确认收货");
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

	public ServerResponse<String> operation_purchase_order_impl(int type, Order order, Map<String, Object> params,
			boolean isredis) {
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
				if (order.getOrderStatus() == 11) {
					// 关单 关单状态只能 是11 时 12 时才能关
					order.setOrderStatus(type);
					orderMapper.operation_purchase_order(order);
					if (list_evaluates.size() > 0) {
						// 全部更新
						orderCommonOfferMapper.operation_purchase_evaluate_all(order.getId(), updateTime);

						// TODO 去发送通知全部 接单失败
					}
				} else if (order.getOrderStatus() == 12) {
					// 关单 关单状态只能 是11 时 12 时才能关
					order.setOrderStatus(type);
					orderMapper.operation_purchase_order(order);
					orderCommonOfferMapper.operation_purchase_evaluate_id(order.getId(), updateTime,
							order.getSaleUserId());

					// TODO 通知接单成功的用户，关单了 order.getSaleUserId()发给这个user id

				}
			} else if (type == 11) {
				// 重新开启订单 只有3和17的才能重新开启
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
			} else if (type == 4) {
				// 支付操作时处理 TODO 直接set支付的金额和待支付金额
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				// TODO 通知给抢单成功人员 userID 支付成功送货
			} else if (type == 16) {
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
			System.out.println("______0000" + e.toString());
			return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
		}
	}

}
