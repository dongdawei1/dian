package com.dian.mmall.service.impl;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.config.WeChatConfig;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.LiushuiMapper;
import com.dian.mmall.dao.OrderMapper;
import com.dian.mmall.dao.OrderUserMapper;
import com.dian.mmall.dao.PayOrderMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.goumaidingdan.OrderCommonOfferMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.pojo.Liushui;
import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.OrderFanhui;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.PayOrder;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.goumaidingdan.OrderCommonOffer;
import com.dian.mmall.pojo.goumaidingdan.OrderCommonOfferEvaluateVo;
import com.dian.mmall.pojo.goumaidingdan.PurchaseSeeOrderVo;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.OrderUser;
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
import com.dian.websockert.WebsockertService;
import com.jhlabs.image.WoodFilter;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RealNameMapper realNameMapper;
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
	@Autowired
	private WebsockertService websockertService;

	@Autowired
	private OrderUserMapper orderUserMapper;

	@Autowired
	private LiushuiMapper liushuiMapper;
	@Autowired
	private WholesaleCommodityService wholesaleCommodityService;

	@Override
	public synchronized ServerResponse<String> create_wholesaleCommodity_order(long userId,
			Map<String, Object> params) {
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
						if (!realName.getAddressDetailed().equals(params.get("addressDetailed").toString().trim())) {
							return ServerResponse.createByErrorMessage(ResponseMessage.dizhixiangqcuowu.getMessage());
						}

						order.setAddressDetailed(realName.getAddressDetailed());
						order.setGiveTakeTime(giveTakeTime);
						order.setRemarks(params.get("remarks").toString().trim());
						order.setCreateTime(newdateString);
						order.setUpdateTime(newdateString);
						order.setOrderStatus(11);
						order.setPayStatus(0);
						order.setReleaseType(4);
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

						order.setAddressDetailed(realName.getUserName());
						order.setId(id);
//						
						// 调用push给接单人员或者 管理
						if (websockertService.fadingdan(realName.getDetailed(), order).equals("false")) {
							websockertService.fadingdan(realName.getDetailed(), order);
						}

//						order.setId(id);
//						order.setCommodityJiage(commodityJiage_int);
						// 把订单存放在redis中
//						String keyString = Const.ORDER_REDIS + "_" + user.getId() + "_" + user.getUsername() + "_" + id;
//
//						RedisShardedPoolUtil.setEx(keyString, JsonUtil.obj2StringPretty(order),
//								Const.RedisCacheExtime.REDIS_ORDER_TIME);

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
		return ServerResponse.createBySuccess((int) Math.ceil((count / 100)) + "");
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
		if (length == 0) {
			return 0;
		}
		return (int) commodityJiage / length;
	}

	/**
	 * 根据订单id查询报价人员名单 ，返回setPurchaseSeeOrderVo
	 */
	private List<OrderCommonOfferEvaluateVo> guanShanReason1(long orderId, int type) {
		List<OrderCommonOfferEvaluateVo> listOrderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();
		// guanShanReason 把报价的集合放到这个字段中 ，要处理状态 commodStatus ==0 的 预创建的多个
		List<OrderCommonOffer> orderCommonOffer_list = orderCommonOfferMapper.getInitial(orderId, 3);
		int leng = orderCommonOffer_list.size();
		if (leng > 0) {
			for (int a = 0; a < leng; a++) {
				OrderCommonOffer offer = orderCommonOffer_list.get(a);
				if (type == 18) {
					offer.setContact("选择后可见");
					offer.setConsigneeName("选择后可见");
				}

				offer.setCommodityZongJiage(offer.getCommodityZongJiage() / 100);

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
	 * 查询报价成功的 返回setPurchaseSeeOrderVo
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
	 * 我的订单页 获取 3天内 创建的订单
	 */
	@Override
	public ServerResponse<Object> get_conduct_purchase_order(User user) {

		List<Order> orders = new ArrayList<Order>();

		String between = DateTimeUtil.betweenAnd(1);
		String and = DateTimeUtil.betweenAnd(2);

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

		purchaseSeeOrderVo.setListPurchaseSeeOrderVo(listPurchaseSeeOrderVo);
		return ServerResponse.createBySuccess(purchaseSeeOrderVo);
	}

	/**
	 * 实现我的订单页 获取 3天内 创建的订单
	 */
	private PurchaseSeeOrderVo setPurchaseSeeOrderVo(Order order) {
		int orderStatus = order.getOrderStatus();
		PurchaseSeeOrderVo purchaseSeeOrderVo_sub = new PurchaseSeeOrderVo();
		List<OrderCommonOfferEvaluateVo> rderCommonOfferEvaluateVo = new ArrayList<OrderCommonOfferEvaluateVo>();

		long nowDateLong = new Date().getTime();

		String updateTime = DateTimeUtil.dateToAll();
		if (orderStatus == 11) {
			// 开启长连接 confirmTime
			long createTimeLong = DateTimeUtil.strToDate(order.getCreateTime()).getTime();

			// 超过报价时间有报价更新为 18 无报价更新为17
			if ((nowDateLong - createTimeLong) >= 30 * 60 * 1000 && (nowDateLong - createTimeLong) < 45 * 60 * 1000) {
				// 已经到报价时间
				int initialCount = orderCommonOfferMapper.getInitialCount(order.getId());
				if (initialCount > 0) {
					// 有报价更新为 orderStatus==18
					order.setOrderStatus(18);
					order.setUpdateTime(updateTime);
					String confirmTime = DateTimeUtil.dateTimeToDateString((new Date()).getTime() + 15 * 60 * 1000);
					order.setConfirmTime(confirmTime);

					tongbu_gengxin_uporder(order);
					// 开启长连接
					purchaseSeeOrderVo_sub.setVoSocket(0);
					purchaseSeeOrderVo_sub.setOrderStatu18(true);
					purchaseSeeOrderVo_sub.setOrderStatuName("待选择供货商");
					rderCommonOfferEvaluateVo = guanShanReason1(order.getId(), 18);

				} else {
					// 没有报价的更新为 17 无销售商报价重新发布
					order.setOrderStatus(17);
					order.setUpdateTime(updateTime);
					tongbu_gengxin_uporder(order);
					purchaseSeeOrderVo_sub.setOrderStatu17(true);
					purchaseSeeOrderVo_sub.setOrderStatuName("无销售商报价超时关闭");
				}
			}
			// 大于45分钟直接更新为失败 (防止定时任务 有问题)
			else if ((nowDateLong - createTimeLong) >= 45 * 60 * 1000) {
				order.setUpdateTime(updateTime);
				order.setOrderStatus(3);
				purchaseSeeOrderVo_sub.setOrderStatuName("超时关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);
				tongbu_gengxin_uporder(order);
				// 检查有无报价者有解冻保证金，没有更新成失效
				int initialCount = orderCommonOfferMapper.getInitialCount(order.getId());
				if (initialCount > 0) {
					tongbu_jiedong_baozhengjin(order.getId(), 3);
				}

			} else {
				// 小于30分钟
				String confirmTime = DateTimeUtil
						.dateTimeToDateString(DateTimeUtil.strToDate(order.getCreateTime()).getTime() + 30 * 60 * 1000);
				order.setConfirmTime(confirmTime);
				purchaseSeeOrderVo_sub.setOrderStatu11(true);
				purchaseSeeOrderVo_sub.setVoSocket(0);
				purchaseSeeOrderVo_sub.setOrderStatuName("报价中");
				order.setOrderStatus(11);
				rderCommonOfferEvaluateVo = guanShanReason1(order.getId(), 18);
			}

			purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			// 刚发布的没有抢单报价
			// guanShanReason 这个字段临时返给前端抢单的order_common_offer列表
			// orders.set(a, guanShanReason(order));

		} else if (orderStatus == 18) {
			// 开启长连接
			long oUpDateLong = DateTimeUtil.strToDate(order.getUpdateTime()).getTime();
			if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
				order.setUpdateTime(updateTime);
				order.setOrderStatus(3);
				tongbu_gengxin_uporder(order);
				tongbu_jiedong_baozhengjin(order.getId(), 3);
				purchaseSeeOrderVo_sub.setOrderStatuName("未支付定金关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);

			} else {
				purchaseSeeOrderVo_sub.setVoSocket(0);
				purchaseSeeOrderVo_sub.setOrderStatuName("待选择商家");
				purchaseSeeOrderVo_sub.setOrderStatu18(true);
				rderCommonOfferEvaluateVo = guanShanReason1(order.getId(), 18);
			}
			purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
		} else if (orderStatus == 12) {
			long oUpDateLong = (DateTimeUtil.strToDate(order.getUpdateTime())).getTime();
			if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
				order.setUpdateTime(updateTime);
				order.setOrderStatus(19);
				tongbu_gengxin_uporder(order);
				tongbu_jiedong_baozhengjin(order.getId(), 2);
				purchaseSeeOrderVo_sub.setOrderStatuName("未支付质保金关单");

			} else {
				purchaseSeeOrderVo_sub.setOrderStatuName("待支付定金(报价的6%)");
				purchaseSeeOrderVo_sub.setOrderStatu12(true);
				order.setCommodityZongJiage(order.getCommodityZongJiage() / 100);
				String confirmTime = DateTimeUtil
						.dateTimeToDateString(DateTimeUtil.strToDate(order.getUpdateTime()).getTime() + 15 * 60 * 1000);
				order.setConfirmTime(confirmTime);

				purchaseSeeOrderVo_sub
						.setListOrderCommonOfferEvaluateVo(getOrderCommonOffer(order.getId(), order.getSaleUserId()));
			}
		} else {

			if (orderStatus == 3) {
				purchaseSeeOrderVo_sub.setOrderStatuName("关单");
				purchaseSeeOrderVo_sub.setOrderStatu3(true);
				purchaseSeeOrderVo_sub.setListOrderCommonOfferEvaluateVo(rderCommonOfferEvaluateVo);
			} else if (orderStatus == 17) {
				purchaseSeeOrderVo_sub.setOrderStatuName("无销售商接单关单");
				purchaseSeeOrderVo_sub.setOrderStatu17(true);
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

	/** _______________根据时间处理订单开始__________________ */

	private synchronized void tongbu_gengxin_uporder(Order order) {
		orderMapper.operation_purchase_order(order);
	}

	private synchronized void tongbu_jiedong_baozhengjin(long orderId, int type) {
		// 3是关单全部退
		// 1 初始，2 失败，3成功,4投诉不解冻，5无投诉解冻成功，6抢单失败退款

		List<OrderCommonOffer> ol = orderCommonOfferMapper.getInitial(orderId, type);
		List<Liushui> liushui = liushuiMapper.getjinxingliushui(orderId, 3);
		for (int a = 0; a < ol.size(); a++) {
			OrderCommonOffer orderCommonOffer = ol.get(a);
			for (int liu = 0; liu < liushui.size(); liu++) {
				Liushui liushui2 = liushui.get(liu);
				if (orderCommonOffer.getSaleUserId() == liushui2.getUserId()) {
					tongbu_jiedong_gengxin_yue(liushui2);
					continue;
				}
			}
		}

	}

	// 解冻和退款
	private synchronized void tongbu_jiedong_gengxin_yue(Liushui liushui2) {

		OrderUser orderUser = orderUserMapper.getOrderUserById(liushui2.getUserId());
		orderUser.setShengyuAmount(orderUser.getShengyuAmount() + liushui2.getAmount());
		orderUser.setDongjieAmount(orderUser.getDongjieAmount() - liushui2.getAmount());
		int re = tongbu_gengxin_yue(orderUser);

		if (re > 0) {
			liushui2.setLiushuiStatus(6);
			liushui2.setPayment("解冻");
			tongbu_jiedong(liushui2);
		} else {
			liushui2.setLiushuiStatus(7);
			liushui2.setPayment("解冻失败");
			tongbu_jiedong(liushui2);
		}
		// 通知解冻情况 userId,amount,dingdanId liushui2.getAmount()/100
		websockertService.fajiedong(liushui2);
		uptate_guandan(liushui2.getDingdanId(), liushui2.getUserId());
	}

	// 解冻
	private synchronized void tongbu_jiedong(Liushui liushui) {
		liushui.setUpdateTime(DateTimeUtil.dateToAll());
		liushuiMapper.tongbu_jiedong(liushui);

	}

	// 更新余额
	private synchronized int tongbu_gengxin_yue(OrderUser orderUser) {
		orderUser.setUpdateTime(DateTimeUtil.dateToAll());
		return orderUserMapper.upyue(orderUser);

	}

	// 更新抢单表
	private void uptate_guandan(long id, long saleUserId) {
		orderCommonOfferMapper.uptateGuanDan(id, saleUserId, DateTimeUtil.dateToAll());

	}

	// 调微信 关单去,并更新支付表

	/** _______________根据时间处理订单结束__________________ */

	/**
	 * 用户手动改变, 改变订单状态
	 */

	@Override
	public synchronized ServerResponse<String> operation_purchase_order(User user, Map<String, Object> params) {
		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(user.getId());
		int type = Integer.parseInt(params.get("type").toString().trim());
		long id = Long.parseLong(params.get("id").toString().trim());
		Order order = orderMapper.getOrderById(id, user.getId());
		if (order != null) {
			String updateTime = DateTimeUtil.dateToAll();
			order.setUpdateTime(updateTime);
			// 更新数据库，订单表，抢单表，找到了操作 更新数据库，订单表，抢单表， 发通知给抢单人员
//			状态 =11 时 显示关单键  和 确认键  关单后端 传 3，确认传12 -->
			//
//			关单后状态==3或者17 （超时无人接单）  显示开启键   
			// 开启后端 传 11，
//			确认后状态==12  显示待确认总价 按键  置灰    销售商点击确认价格
//	      
//			支付成功状态==4  显示 待收货 不可点击   送货者状态4时显示 已送到 传 operationRow(scope.row,16)  点击后状态变成16-->
//	      送货者再次确认  13-->转为12待支付           显示 支付键    支付后端  传 4  ， 和 显示关单键 -->
//			状态==16 显示确认收货   向后端传 5  状态更新为5--->
			//
//			状态==5 显示待评价      向后端传 6  订单更新为 6 完成评价
			if (type == 3) {
				if (order.getOrderStatus() == 11 || order.getOrderStatus() == 18) {
					// 关单 关单状态只能 是11 时 12 （），21时18时才能关
					order.setOrderStatus(type);
					tongbu_gengxin_uporder(order);
					tongbu_jiedong_baozhengjin(order.getId(), 3);
				} else if (order.getOrderStatus() == 12 || order.getOrderStatus() == 21) {
					order.setOrderStatus(type);
					tongbu_gengxin_uporder(order);
					// 更新抢单表
					tongbu_jiedong_baozhengjin(order.getId(), 2);
				}
			} else if (type == 11) {
				// 送货时间不能小于3小时后
				if ((new Date().getTime() + 3 * 1000 * 60 * 60) > (DateTimeUtil.strToDate(order.getGiveTakeTime())
						.getTime())) {
					return ServerResponse.createByErrorMessage(ResponseMessage.julisonghuo.getMessage());
				}

				// 重新开启订单 只有3和17,19,的才能重新开启
				order.setOrderStatus(type);
				tongbu_gengxin_uporder(order);
				// realName.getUserName() 是商户街道名后期优化
				order.setAddressDetailed(realName.getUserName());
				// 调用push给接单人员或者 管理
				order.setCreateTime(updateTime);
				if (websockertService.fadingdan(realName.getDetailed(), order).equals("false")) {
					websockertService.fadingdan(realName.getDetailed(), order);
				}

			} else if (type == 12) {
				// 支付
				long order_commodityZongJiage = Long.parseLong(params.get("commodityZongJiage").toString().trim());
				//报价表id
				long orderCommonOfferId = Long.parseLong(params.get("orderCommonOfferId").toString().trim());
				OrderCommonOffer orderCommonOffer = orderCommonOfferMapper.getbysucc(orderCommonOfferId, 0);
				long commodityZongJiage = orderCommonOffer.getCommodityZongJiage() / 100;
				if (commodityZongJiage == order_commodityZongJiage) {
					// 选中的更新 为成功
					long saleUserId = orderCommonOffer.getSaleUserId();
					if (order.getOrderStatus() == 11 || order.getOrderStatus() == 18) {
						orderCommonOfferMapper.operation_purchase_evaluate_selected(order.getId(), updateTime,
								saleUserId, orderCommonOffer.getId());
						
						
						// 更新订单 为抢单成功 和其他信息 报价金额，抢单用户id
						order.setSaleUserId(saleUserId);
						order.setCommodityZongJiage(orderCommonOffer.getCommodityZongJiage());
						order.setOrderStatus(type);
						order.setSaleUserName(orderCommonOffer.getSaleCompanyName());
						tongbu_gengxin_uporder(order);
						
						// push 信息 报价已经被选中
						websockertService.faxuanzhong(order);
					} else {
						return ServerResponse.createByErrorMessage(ResponseMessage.dingdanzhuangtaicuowu.getMessage());
					}

				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.baojiajinebuyizhi.getMessage());
				}

				// 未选中全部更新为失败
				tongbu_jiedong_baozhengjin(order.getId(), 3);

			} else if (type == 5) {
				order.setOrderStatus(type);
				orderMapper.operation_purchase_order(order);
				//  通知给抢单成功人员 userID 用户已经收货，订单完成
				websockertService.fayourenjiedan(5 ,order.getPurchaseUserId());
			}

			else {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}

			return ServerResponse.createBySuccess();

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
		}

	}

	/**
	 * 定时任务改变 订单状态是11（下单）,12（待支付定金）,13（销售商支付质保金）,18（有人接单多+15分钟）,21(支付失败)状态的定时任务
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
							// 有报价更新为 orderStatus==18,延迟15分钟
							o.setOrderStatus(18);
							o.setUpdateTime(updateTime);
							tongbu_gengxin_uporder(o);
						} else {
							// 没有报价的更新为 17
							o.setOrderStatus(17);
							o.setUpdateTime(updateTime);
							tongbu_gengxin_uporder(o);
						}
					}
				} else if (orderStatus == 18) {
					if ((nowDateLong - createTimeLong) >= 45 * 60 * 1000) {
						// 留15分钟给采购者选择销售商，超时关单为3
						o.setOrderStatus(3);
						o.setUpdateTime(updateTime);
						tongbu_gengxin_uporder(o);
						// 有报价才会是18直接去退保证金
						tongbu_jiedong_baozhengjin(o.getId(), 3);
					}
				} else if (orderStatus == 12) {
					long updateTimeLong = DateTimeUtil.strToDate(o.getUpdateTime()).getTime();
					if ((nowDateLong - updateTimeLong) >= 15 * 60 * 1000) {

						// 判断是否在支付中 0表示未支付，1表示已经支付，2支付失败关单，3支付金额与实际金额不一致，4超时关单
						List<PayOrder> pcAadAppAll = payOrderMapper.pcAadAppAll(o.getId(), 9);

						if (pcAadAppAll.size() == 0) {
							o.setUpdateTime(updateTime);
							o.setOrderStatus(19);
							tongbu_gengxin_uporder(o);
							tongbu_jiedong_baozhengjin(o.getId(), 2);
						} else {
							for (int a = 0; a < pcAadAppAll.size(); a++) {
								PayOrder payOrder = pcAadAppAll.get(a);
								if (payOrder.getState() == 0) {
									// 购买者未支付定金 19，最后一次更新时间+15分钟 用 updateTime
									long oUpDateLong = DateTimeUtil.strToDate(o.getUpdateTime()).getTime();
									if ((nowDateLong - oUpDateLong) >= 15 * 60 * 1000) {
										o.setUpdateTime(updateTime);
										o.setOrderStatus(19);
										tongbu_gengxin_uporder(o);
										tongbu_jiedong_baozhengjin(o.getId(), 2);
										// 调用微信关单
										closeorderWX(payOrder);
									}
								} else if (payOrder.getState() == 1 || payOrder.getState() == 3) {
									// 先去查询一下支付结果再根据支付结果处理
									ServerResponse<Object> serverResponse = orderqueryWX(payOrder);
									if (serverResponse.getStatus() == 0) {
										timerSelsetPayOrderImpl((Map<String, String>) serverResponse.getData(),
												payOrder);
									}
								}
							}
						}

					}
				}
			}
		}
	}

	/**
	 * 处理微信支付创建时间大于=当前时间10分钟的 支付订单 定时任务查询微信 执行 成功的调用callback ,没有查到结果的 到时间 超时关单，
	 * 未到时不处理 关单前再查一次 支付表状态
	 */

	@Override
	public void timerSelsetPayOrder() {
//		String createTime = DateTimeUtil.dateTimeToDateString(new Date().getTime() - 10 * 60 * 1000);
//		List<PayOrder> payOrders = payOrderMapper.timerSelsetPayOrder(createTime, 0, 0);
//		if (payOrders.size() > 0) {
//			for (PayOrder pay : payOrders) {
//				ServerResponse<Object> serverResponse = orderqueryWX(pay);
//				if (serverResponse.getStatus() == 0) {
//					timerSelsetPayOrderImpl((Map<String, String>) serverResponse.getData(), pay);
//				}
//			}
//
//		}

	}

	/**
	 * 定时任务根据主动查询结果做更新支付表和订单表
	 */
	private void timerSelsetPayOrderImpl(Map<String, String> sortedMap, PayOrder payOrder) {
		Order order = orderMapper.getOrderByIdyichang(payOrder.getOrderId());
		if (order != null) {
			if (order.getPayStatus() != 4) {

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
						if (total_fee == payOrder.getTotalFee()) {
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
					}
				} else if (!"USERPAYING".equals(trade_state)) {
					// 支付超时关单,关单成功更新数据库
					if (closeorderWX(payOrder).getStatus() == 0) {

						payOrder.setState(4);
						payOrder.setDel(1);
						// 更新支付表
						payOrderMapper.callbackUpdate(payOrder);
						// 更新订单表 支付失败可以再次发起
						order.setOrderStatus(19);
						order.setPayStatus(0);
						callbackUpDateOrder(order);
					}
				}
			}
		}
	}

	/**
	 * 主动关单，更新订单表状态为超时未支付，更新支付表为 设置
	 * https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_3
	 */

	private ServerResponse<String> closeorderWX(PayOrder payOrder) {
		SortedMap<String, String> params = new TreeMap<>();
		params.put("appid", weChatConfig.getAppId()); // 公众账号id
		params.put("mch_id", weChatConfig.getMchId()); // 商户号
		params.put("out_trade_no", payOrder.getOutTradeNo()); // 订单号，自己生成的订单号
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
		String orderStr = HttpUtils.doPost(WeChatConfig.getWxCloseOrderUrl(), payXml, 4000);

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
				payOrder.setState(4);
				payOrder.setDel(1);
				// 更新支付表
				payOrderMapper.callbackUpdate(payOrder);
				return ServerResponse.createBySuccess();
			}
		}

		return ServerResponse.createByError();
	}

	/**
	 * pc生成二维码
	 */
	// 支付定金生成二维码（ 第一步统一下单生成二维码）
	@Override
	public ServerResponse<String> native_pay_order(User user, String spbillCreateIp, long id) {
		// 检查订单信息，获取支付金额
		ServerResponse<Object> response = native_pay_order_imp(user, spbillCreateIp, id, NATIVE);
		if (response.getStatus() == 0) {
			return ServerResponse.createBySuccess(((HashMap<String, Object>) response.getData()).get("url").toString());
		}
		return ServerResponse.createBySuccessMessage(response.getMsg());
	}

	/**
	 * 微信生成账单
	 **/
	@Override
	public ServerResponse<Object> native_pay_order_app(User user, String spbillCreateIp, long id) {
		// 检查订单信息，获取支付金额
		ServerResponse<Object> response = native_pay_order_imp(user, spbillCreateIp, id, APP);
		if (response.getStatus() == 0) {
			return ServerResponse.createBySuccess(response.getData());
		}
		return ServerResponse.createBySuccessMessage(response.getMsg());
	}

	private String NATIVE = "NATIVE";
	private String APP = "APP";

	/**
	 * pc生成二维码,app生成链接
	 */
	public synchronized ServerResponse<Object> native_pay_order_imp(User user, String spbillCreateIp, long id,
			String tradeType) {
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
			PayOrder payOrder = payOrderMapper.getPayOrderByOrderId(id, 0, tradeType);

			if (payOrder != null && payOrder.getTotalFee() == totalFee) {
				return ServerResponse.createBySuccessMessage(payOrder.getMeg());
			}
			// 查询是否已经支付过
			payOrder = payOrderMapper.getPayOrderByOrderId(id, 9, tradeType);
			if (payOrder != null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.zhifuyiwancheng.getMessage());
			}

			// 生成支付订单
			payOrder = new PayOrder();
			payOrder.setUserId(user.getId());
			payOrder.setOrderId(id);
			payOrder.setBody("订单ID" + id + "支付定金");
			// 生成规则 订单表id+"_"+时间戳
			String outTradeNo = id + "_" + new Date().getTime() + tradeType;
			payOrder.setOutTradeNo(outTradeNo);
			payOrder.setSpbillCreateIp(spbillCreateIp);
			payOrder.setTotalFee(totalFee);
			payOrder.setTradeType(tradeType); // 暂时先写死 pc NATIVE
			payOrder.setState(0);
			payOrder.setDel(0);
			payOrder.setCreateTime(DateTimeUtil.dateToAll());

			// 落库payOrder
			payOrderMapper.createPyOrder(payOrder);

			payOrder = payOrderMapper.getPayOrderByOrderId(id, 0, tradeType);
			ServerResponse<String> response = unifiedOrder(payOrder);
			if (response.getStatus() == 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				System.out.println(response.getData());
				map.put("url", response.getData());
				map.put("payOrder", payOrder);
				return ServerResponse.createBySuccess(map);
			}
			return ServerResponse.createByErrorMessage(response.getMsg());

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
		}

	}

	/**
	 * 微信支付 统一下单方法 https://api.mch.weixin.qq.com/pay/unifiedorder
	 * https://api.mch.weixin.qq.com/pay/unifiedorder
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
				return ServerResponse.createBySuccess("https://www.baidu.com");

			}
			// 更新数据库

			if (payOrder.getTradeType().equals(NATIVE)) {
				payOrder.setMeg(unifiedOrderMap.get("code_url"));
				payOrderMapper.unifiedUptaePayOrder(payOrder);
				return ServerResponse.createBySuccess(unifiedOrderMap.get("code_url"));
			} else if (payOrder.getTradeType().equals(APP)) {
				payOrder.setMeg(unifiedOrderMap.get("prepay_id"));
				payOrderMapper.unifiedUptaePayOrder(payOrder);
				return ServerResponse.createBySuccess(unifiedOrderMap.get("prepay_id"));
			}

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
					if (total_fee == payOrder.getTotalFee()) {
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
					
					return ServerResponse.createBySuccess();
				}
				// 支付金额为0返回错误
				return ServerResponse.createByError();

			}
			// 支付失败关单
			payOrder.setState(2);
			payOrder.setDel(1);
			// 更新支付表
			payOrderMapper.callbackUpdate(payOrder);
			// 更新订单表 支付失败可以再次发起
//			order.setOrderStatus(21);
//			order.setPayStatus(0);
//			callbackUpDateOrder(order);
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
			// 通知发单者 已支付完成
			websockertService.fayourenjiedan(12 ,order.getPurchaseUserId());
			// 通知发单者 已支付完成
			websockertService.fayourenjiedan(5 ,order.getSaleUserId());
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
		String orderStr = HttpUtils.doPost(WeChatConfig.getWxQueryOrderUrl(), payXml, 4000);

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

	/**
	 * 查询是否有待支付的payOrder
	 */
	@Override
	public ServerResponse<String> get_pay_order_all(long userId) {
		int count = payOrderMapper.get_pay_order_all(userId);
		if (count > 0) {
			return ServerResponse.createBySuccess("YES");
		}
		return ServerResponse.createBySuccess("NO");
	}

	/**
	 * 查询支付状态
	 */

	@Override
	public ServerResponse<String> get_pay_order_byOrderId(long userId, long orderId, String appid) {
		PayOrder payOrder = null;
		if (appid.equals(Const.APPAPPIDP)) {
			payOrder = payOrderMapper.get_pay_order_byOrderId(userId, orderId, 0, NATIVE);
		} else {
			payOrder = payOrderMapper.get_pay_order_byOrderId(userId, orderId, 0, APP);
		}

		if (payOrder == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
		}

		if (payOrder.getState() == 1 || payOrder.getState() == 3) {
			return ServerResponse.createBySuccess("YES");
		} else if (payOrder.getState() == 2 || payOrder.getState() == 4) {
			return ServerResponse.createBySuccess("FAIL");
		}
		return ServerResponse.createBySuccess("NO");
	}

	@Override
	public ServerResponse<Object> peceiptGetPendingOrders(long userId, Map<String, Object> params) {

//		provincesIdint(20) NULL省
//		cityIdint(21) NULL市
//		districtCountyIdint(20) NULL区
		//
		String currentPage_string = params.get("currentPage").toString().trim();
		String pageSize_string = params.get("pageSize").toString().trim();
		int currentPage = 0;
		int pageSize = 0;

		if (currentPage_string != null && !currentPage_string.equals("")) {
			currentPage = Integer.parseInt(currentPage_string);
			if (currentPage <= 0) {
				return ServerResponse.createByErrorMessage("页数不能小于0");
			}

		} else {
			return ServerResponse.createByErrorMessage("请正确输入页数");
		}

		if (pageSize_string != null && !pageSize_string.equals("")) {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		// 类型
		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = 4;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
		}

		// selectedOptions 城市id

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		Integer provincesId = 0;
		Integer cityId = 0;
		Integer districtCountyId = 0;
		RealName realName = realNameMapper.getRealName(userId);
		if (selectedOptions_list.size() == 3) {
			provincesId = selectedOptions_list.get(0);
			cityId = selectedOptions_list.get(1);
			districtCountyId = selectedOptions_list.get(2);
		} else {

			provincesId = realName.getProvincesId();
			cityId = realName.getCityId();
			districtCountyId = realName.getDistrictCountyId();
		}

		// 送貨日期
		String giveTakeTime = params.get("giveTakeTime").toString().trim();
		if (giveTakeTime != null && !giveTakeTime.equals("")) {
			giveTakeTime = "%" + giveTakeTime + "%";
		}

		Page<Order> order_pagePage = new Page<Order>();

		long zongtiaoshu = orderMapper.peceiptGetPendingOrdersZongtiaoshu(provincesId, cityId, districtCountyId,
				releaseType, giveTakeTime);

		order_pagePage.setTotalno(zongtiaoshu);
		order_pagePage.setPageSize(pageSize);
		order_pagePage.setCurrentPage(currentPage); // 当前页
		if (zongtiaoshu == 0) {
			if (realName.getIsReceipt() == 2) {
				return ServerResponse.createBySuccess("YES", order_pagePage);
			}
			return ServerResponse.createBySuccess(order_pagePage);
		}

		// 查询list
		List<Order> orderList = orderMapper.peceiptGetPendingOrders((currentPage - 1) * pageSize, pageSize, provincesId,
				cityId, districtCountyId, releaseType, giveTakeTime);
		for (int i = 0; i < orderList.size(); i++) {
			Order o = orderList.get(i);
			String addressDetailed = realNameMapper.getRealNameAddressDetailed(o.getPurchaseUserId());
			o.setAddressDetailed(addressDetailed);
			o.setPurchaseUserId(0);
			orderList.set(i, o);
		}
		order_pagePage.setDatas(orderList);
		if (realName.getIsReceipt() == 2) {
			// 接单用户开启轮询
			return ServerResponse.createBySuccess("YES", order_pagePage);
		}
		return ServerResponse.createBySuccess(order_pagePage);
	}

	/** 发布企业用户中心 全部发布的订单 */
	@Override
	public ServerResponse<Object> myPurchaseOrder(long userId, Map<String, Object> params) {
		String currentPage_string = params.get("currentPage").toString().trim();
		String pageSize_string = params.get("pageSize").toString().trim();
		int currentPage = 0;
		int pageSize = 0;

		if (currentPage_string != null && !currentPage_string.equals("")) {
			currentPage = Integer.parseInt(currentPage_string);
			if (currentPage <= 0) {
				return ServerResponse.createByErrorMessage("页数不能小于0");
			}

		} else {
			return ServerResponse.createByErrorMessage("请正确输入页数");
		}

		if (pageSize_string != null && !pageSize_string.equals("")) {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		// 类型
		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = 4;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
		}

		// 送貨日期
		String createTime = params.get("createTime").toString().trim();
		if (createTime != null && !createTime.equals("")) {
			createTime = "%" + createTime + "%";
		}

		Page<Order> order_pagePage = new Page<Order>();

		long zongtiaoshu = orderMapper.myPurchaseOrderZongtiaoshu(releaseType, createTime, userId);

		order_pagePage.setTotalno(zongtiaoshu);
		order_pagePage.setPageSize(pageSize);
		order_pagePage.setCurrentPage(currentPage); // 当前页
		if (zongtiaoshu == 0) {
			return ServerResponse.createBySuccess(order_pagePage);
		}

		// 查询list
		List<Order> orderList = orderMapper.myPurchaseOrder((currentPage - 1) * pageSize, pageSize, releaseType,
				createTime, userId);
		for (int i = 0; i < orderList.size(); i++) {
			Order o = orderList.get(i);
//			购买者下单,2批发者确认，3用户关单，4取/送货 ，5待评价，6评价完成 
//			，11发布采购订单 ，12待支付， ，16 确认收货，17超时无人接单关单，
//			18 三十分钟已过有人接单 确认期，19 超时未支付，20待支付定金，21支付失败
			if (o.getReleaseType() == 4) {
				o.setPaymentTime("实时采购信息");
			}

			int orderStatus=o.getOrderStatus();
			if (orderStatus == 5 || orderStatus == 6) {

				o.setGuanShanTime(o.getCommodityZongJiage() / 100 + "");
				o.setGuanShanReason("已完成");
			} else if(orderStatus == 4 || orderStatus == 16 ) {
				o.setGuanShanTime(o.getCommodityZongJiage() / 100 + "");
				o.setGuanShanReason("进行中");
				o.setCollectTime("--");
			}else {
				o.setSaleUserName("--");
				o.setGuanShanTime("--");
				o.setCollectTime("--");
				if (orderStatus == 3 ||orderStatus == 17 || orderStatus == 19 || orderStatus == 20
						|| orderStatus == 21) {
					o.setGuanShanReason("关单");
					
				} else {
					o.setGuanShanReason("进行中");
				}
			}

			orderList.set(i, o);
		}
		order_pagePage.setDatas(orderList);

		return ServerResponse.createBySuccess(order_pagePage);
	}

	/** 接单企业 首页 除抢单中的全部订单 */
	@Override
	public ServerResponse<Object> mySaleOrder(long userId, Map<String, Object> params) {
		Integer isReceipt = realNameMapper.getIsReceipt(userId);
		if (isReceipt == null || isReceipt != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiedanyonghukechanxun.getMessage());
		}
		String currentPage_string = params.get("currentPage").toString().trim();
		String pageSize_string = params.get("pageSize").toString().trim();
		int currentPage = 0;
		int pageSize = 0;
		if (currentPage_string != null && !currentPage_string.equals("")) {
			currentPage = Integer.parseInt(currentPage_string);
			if (currentPage <= 0) {
				return ServerResponse.createByErrorMessage("页数不能小于0");
			}

		} else {
			return ServerResponse.createByErrorMessage("请正确输入页数");
		}

		if (pageSize_string != null && !pageSize_string.equals("")) {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		// 类型
		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = 4;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
		}

		// 送貨日期
		String giveTakeTime = params.get("giveTakeTime").toString().trim();
		if (giveTakeTime != null && !giveTakeTime.equals("")) {
			giveTakeTime = "%" + giveTakeTime + "%";
		}

		String orderStatusString = params.get("orderStatus").toString().trim();
		int orderStatus = 0; // 2送货中 3已完成9全部
		try {
			orderStatus = Integer.parseInt(orderStatusString);
		} catch (Exception e) {

		}

		if (orderStatus != 2 && orderStatus != 3 && orderStatus != 9) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanzhuangtaicuowu.getMessage());
		}

		Page<Order> order_pagePage = new Page<Order>();

		long zongtiaoshu = orderMapper.mySaleOrderZongtiaoshu(releaseType, giveTakeTime, userId, orderStatus);

		order_pagePage.setTotalno(zongtiaoshu);
		order_pagePage.setPageSize(pageSize);
		order_pagePage.setCurrentPage(currentPage); // 当前页
		if (zongtiaoshu == 0) {
			return ServerResponse.createBySuccess(order_pagePage);
		}

		// 查询list
		List<Order> orderList = orderMapper.mySaleOrder((currentPage - 1) * pageSize, pageSize, releaseType,
				giveTakeTime, userId, orderStatus);
		for (int i = 0; i < orderList.size(); i++) {
			Order o = orderList.get(i);
//			购买者下单,2批发者确认，3用户关单，4取/送货 ，5待评价，6评价完成 
//			，11发布采购订单 ，12待支付，  ，16 确认收货，17超时无人接单关单，
//			18 三十分钟已过有人接单 确认期，19 超时未支付，，21支付失败
			if (o.getReleaseType() == 4) {
				o.setPaymentTime("实时采购信息");
			}
			o.setGuanShanTime(o.getCommodityZongJiage() / 100 + "");

			if (o.getOrderStatus() == 5 || o.getOrderStatus() == 6) {
				o.setGuanShanReason("已完成");
			} else {
				o.setCollectTime("--");
				o.setGuanShanReason("进行中");
			}

			orderList.set(i, o);
		}
		order_pagePage.setDatas(orderList);

		return ServerResponse.createBySuccess(order_pagePage);
	}

	/* _______________________________接单开始___________________________ */

	/**
	 * 报价
	 */

	@Override
	public synchronized ServerResponse<Object> createjiedan(long userId, Map<String, Object> params) {
		// 判断是否是接单人员
		OrderUser orderUser = orderUserMapper.getOrderUserById(userId);
		if (orderUser == null || orderUser.getAuthentiCationStatus() != 1) {
			return ServerResponse.createByErrorMessage(ResponseMessage.feijiedanyonghu.getMessage());
		}
		// 判断订单是否存在
		String idString = params.get("id").toString().trim();

		if (idString == null || idString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanxinxyouwu.getMessage());
		}
		long id = Long.parseLong(idString);
		Order order = orderMapper.getOrderByIdyichang(id);
		if (order == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanxinxyouwu.getMessage());
		}
		// 判断是否是可报价
		if (order.getOrderStatus() != 11 && order.getOrderStatus() != 18) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dingdanzhuanggaibian.getMessage());
		}
		// 判断是否报价过 5==(0,1)
		int initialCount = orderCommonOfferMapper.getmybaojia(id, userId, 5);
		if (initialCount > 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chongfubaojia.getMessage());
		}
		// 报价转成分
		long commodityZongJiage = Long.parseLong(params.get("commodityZongJiage").toString().trim()) * 100;
		// 剩余可冻结金额分
		long shengyuAmount = orderUser.getShengyuAmount();
		// 质保金
		long baojiadongjie = new Double(commodityZongJiage * Const.BAOZHANGJIN).longValue();

		if (shengyuAmount < baojiadongjie) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chongfubaojia.getMessage());
		}

		OrderCommonOffer offer = new OrderCommonOffer();
		offer.setCommodityZongJiage(commodityZongJiage);
		offer.setSaleUserId(userId);

		offer.setOrderFormId(id);
		String createTime = DateTimeUtil.dateToAll();
		offer.setCreateTime(createTime);
		offer.setUpdateTime(createTime);
		offer.setCommodStatus(0);
		RealName realName = realNameMapper.getRealName(userId);
		offer.setSaleCompanyName(realName.getCompanyName());
		offer.setContact(realName.getContact());
		offer.setConsigneeName(realName.getConsigneeName());
		offer.setSaleUserAddressDetailed(realName.getAddressDetailed());

		int re = orderCommonOfferMapper.creoffer(offer);
		if (re > 0) {
			// 更新余额
			OrderUser orderUser1 = new OrderUser();
			orderUser1.setId(orderUser.getId());
			orderUser1.setShengyuAmount(shengyuAmount - baojiadongjie);
			orderUser1.setDongjieAmount(baojiadongjie + orderUser.getDongjieAmount());
			orderUser1.setUpdateTime(createTime);
			re = orderUserMapper.upyue(orderUser1);
			if (re > 0) {
				Liushui liushui = new Liushui();
				liushui.setUserId(userId);
				liushui.setReceivablesUserId(0);
				liushui.setAmount(baojiadongjie);
				liushui.setPayment("冻结");
				liushui.setTransactionType("质保金");
				liushui.setReceiptsAndPayments("余额");
				liushui.setAccountNo("系统冻结");
				liushui.setCreateTime(createTime);
				liushui.setUpdateTime(createTime);
				liushui.setDingdanId(id);
				liushui.setLiushuiStatus(3);

				re = liushuiMapper.create_liushui(liushui);
				if (re == 0) {
					orderUser.setUpdateTime(createTime);
					re = orderUserMapper.upyue(orderUser);
					long idorderCommonOfferMapper = orderCommonOfferMapper.getId(offer);

					orderCommonOfferMapper.uptateGuanDanById(idorderCommonOfferMapper, createTime);
					return ServerResponse.createByErrorMessage(ResponseMessage.qiandangshibai.getMessage());
				}
				orderUser1.setShengyuAmount(new Double(orderUser1.getShengyuAmount() / 100).longValue());
				orderUser1.setDongjieAmount(new Double(baojiadongjie / 100).longValue());
				// 通知 发布商户push
				websockertService.fayourenjiedan(12,order.getPurchaseUserId());
				return ServerResponse.createBySuccess(orderUser1);
			} else {
				orderUser.setUpdateTime(createTime);
				re = orderUserMapper.upyue(orderUser);
			}
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.qiandangshibai.getMessage());
	}

	/* 获取订单 */
	@Override
	public ServerResponse<Object> getdaibaojia(User user, int releaseType, int orderStatus) {
		// 判断是否是接单人员
		OrderUser orderUser = orderUserMapper.getOrderUserById(user.getId());
		if (orderUser == null || orderUser.getAuthentiCationStatus() != 1) {
			return ServerResponse.createByErrorMessage(ResponseMessage.feijiedanyonghu.getMessage());
		}

		List<OrderFanhui> ofanhuiList = null;
		if (orderStatus == 11) {
			// 待报价
			ofanhuiList = orderMapper.getdaibaojia(user.getId(), user.getDetailed(), releaseType);
			for (int a = 0; a < ofanhuiList.size(); a++) {
				OrderFanhui orderFanhui = ofanhuiList.get(a);
				orderFanhui.setGuanShanTime(DateTimeUtil.dateTimeToDateString(
						(DateTimeUtil.strToDate(orderFanhui.getCreateTime()).getTime()) + 30 * 60 * 1000));
				List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.string2Obj(
						(String) orderFanhui.getCommoditySnapshot(), List.class, CommonMenuWholesalecommodity.class);

				orderFanhui.setCommoditySnapshot(listObj4);
				ofanhuiList.set(a, orderFanhui);
			}
			return ServerResponse.createBySuccess(ofanhuiList);
		}else if (orderStatus == 12) {
			Map<String, List<OrderFanhui>> map =new HashMap<String, List<OrderFanhui>>();
			
			// 待报价
			ofanhuiList = orderMapper.getbaojiazhong(user.getId(),  releaseType);
			for (int a = 0; a < ofanhuiList.size(); a++) {
				OrderFanhui orderFanhui = ofanhuiList.get(a);
				orderFanhui.setGuanShanTime(DateTimeUtil.dateTimeToDateString(
						(DateTimeUtil.strToDate(orderFanhui.getCreateTime()).getTime()) + 45 * 60 * 1000));
				List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.string2Obj(
						(String) orderFanhui.getCommoditySnapshot(), List.class, CommonMenuWholesalecommodity.class);

				orderFanhui.setCommoditySnapshot(listObj4);
				orderFanhui.setCommodityZongJiage(liushuiMapper.getAmount(user.getId(),orderFanhui.getId(),3,0)/100);
				ofanhuiList.set(a, orderFanhui);
			}
			
			map.put("baojia", ofanhuiList);
			String between = DateTimeUtil.betweenAnd(1);
			String and = DateTimeUtil.betweenAnd(2);
			ofanhuiList = orderMapper.getsonghuo(user.getId(),between,and,  releaseType);
			for (int a = 0; a < ofanhuiList.size(); a++) {
				OrderFanhui orderFanhui = ofanhuiList.get(a);
        
				RealName realName=realNameMapper.getRealName(orderFanhui.getPurchaseUserId());
				
				List<CommonMenuWholesalecommodity> listObj4 = JsonUtil.string2Obj(
						(String) orderFanhui.getCommoditySnapshot(), List.class, CommonMenuWholesalecommodity.class);

				orderFanhui.setCommoditySnapshot(listObj4);
				orderFanhui.setPaymentTime(realName.getConsigneeName());
				orderFanhui.setGuanShanTime(EncrypDES.decryptPhone(realName.getContact()));
				orderFanhui.setGuanShanReason(realName.getCompanyName());
				
				orderFanhui.setYesGuaranteeMoney(liushuiMapper.getAmount(user.getId(),orderFanhui.getId(),3,0)/100);
				ofanhuiList.set(a, orderFanhui);
			}
			map.put("songhuo", ofanhuiList);
			return ServerResponse.createBySuccess(map);
		}
		return ServerResponse.createBySuccess(ofanhuiList);

	}
	
	



  //请求收货
	@Override
	public ServerResponse<String> shouhuo(long userId, long id, int orderStatus,int releaseType) {
		Order order = orderMapper.getShouhuo(id, userId,orderStatus,releaseType);
		
		if(order!=null) {
			order.setOrderStatus(orderStatus);
			order.setUpdateTime(DateTimeUtil.dateToAll());
			orderMapper.operation_purchase_order(order);
			// 通知 发布商户push
			websockertService.fayourenjiedan(12 ,order.getPurchaseUserId());
			return ServerResponse.createBySuccess(ResponseMessage.caozuochenggong.getMessage());
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.dingdanchaxunshibai.getMessage());
	} 
}
