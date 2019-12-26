package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.OrderMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.Order;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
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
						System.out.println(JsonUtil.obj2StringPretty(listObj4));
						order.setCommoditySnapshot(JsonUtil.obj2StringPretty(listObj4));
						String commodityJiage=params.get("commodityJiage").toString().trim();
						int commodityJiage_int=0;
						if(commodityJiage!=null && !commodityJiage.equals("")) {
							try {
								commodityJiage_int=Integer.parseInt(commodityJiage);
							} catch (Exception e) {
								// TODO: handle exception
							}
							
						}
						order.setCommodityJiage(commodityJiage_int*100);
						order.setYesGuaranteeMoney(0);
						int resout = orderMapper.create_order(order);

						if (resout == 0) {
							return ServerResponse
									.createByErrorMessage(ResponseMessage.chuangjiandingdanshibai.getMessage());
						}
						// TODO 成功 调用push接口发送站内信

						// TODO 调vo接口创建 /更新 redis的常用菜单
						int isCommonMenu=Integer.parseInt(params.get("isCommonMenu").toString().trim());
						purchaseCreateOrderVoService.createMyCommonMenu(user, listObj4,isCommonMenu);
						return null;
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
		return ServerResponse.createBySuccessMessage((int)Math.ceil((count / 100)) + "");
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

}
