package com.dian.mmall.service.impl.releaseimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.dao.releaseDao.WholesaleCommodityMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.gongyong.IsButten;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;
import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("wholesaleCommodity")
public class WholesaleCommodityServiceImpl implements WholesaleCommodityService {
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private EvaluateMapper evaluateMapper;
	@Autowired
	private WholesaleCommodityMapper wholesaleCommodityMapper;
	@Autowired
	private OrderService orderService;

	@Override
	public ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params) {
		ServerResponse<Object> serverResponse = check_evaluate(user, params, 1);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		WholesaleCommodity wholesaleCommodity = (WholesaleCommodity) BeanMapConvertUtil
				.convertMap(WholesaleCommodity.class, map);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(wholesaleCommodity);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = wholesaleCommodityMapper.create_wholesaleCommodity(wholesaleCommodity);
			if (count == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}
	}

	public ServerResponse<Object> check_evaluate(User currentUser, Map<String, Object> params, int type) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致
		// 判断是否实名
		if (currentUser.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		long userId = currentUser.getId();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);

		String createTime = DateTimeUtil.dateToAll();

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
		int count = 0;
		if (type == 1) { // TODO新建时才检查总数
			// 判断是否超过可以发布的总数
			count = wholesaleCommodityMapper.countNum(releaseType, userId);
			if (count > 50) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			}
			map.put("createTime", createTime);
		}
		map.put("releaseType", releaseType);
		map.put("updateTime", createTime);

		if (type == 1) { // 新建
			// 图片
			List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
					Picture.class);
			int list_size = listObj3.size();
			// 把getUse_status()==1 放到这个集合中
			List<Picture> listObj4 = new ArrayList<Picture>();
			int getNum = PictureNum.pifashangpin.getNum();
			if (list_size > 0) {
				count = 0;
				for (int a = 0; a < list_size; a++) {
					Picture picture = listObj3.get(a);
					if (picture.getUseStatus() == 1) {
						picture.setUserId(userId);
						picture.setUseStatus(3);

						Picture picture1 = pictureMapper.selectPictureBYid(picture.getId());
						if (!picture.getPictureUrl().equals(picture1.getPictureUrl())) {
							return ServerResponse.createByErrorMessage("图片地址不一致");
						}
						pictureMapper.updatePictureUse(picture.getId());
						listObj4.add(picture);
						count += 1;
					}
				}
				if (count > getNum) {
					// 判断没有删除的图片是否大于规定
					return ServerResponse.createByErrorMessage("图片数量不能超过 " + getNum + "个");
				}
				if (count == 0) {
					return ServerResponse.createByErrorMessage("图片不能为空");
				}
			} else {
				return ServerResponse.createByErrorMessage("图片不能为空");
			}

			map.put("pictureUrl", JsonUtil.obj2StringPretty(listObj4));
		}

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(currentUser.getId());
		if (realName != null) {
			map.put("realNameId", realName.getId());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}

		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType == null || serviceType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinmingkong.getMessage());
		}
		count = serviceTypeMapper.getserviceTypeNameCount(releaseType, serviceType, 2);
		if (count == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinchaxunshibai.getMessage());
		}
		map.put("serviceType", serviceType);
		map.put("releaseTitle", params.get("releaseTitle").toString().trim());

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确

			map.put("detailed", cityMapper.checkeCity(provincesId, cityId, districtCountyId));
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chengsshicuowo.getMessage());
		}
		map.put("addressDetailed", params.get("addressDetailed").toString().trim());

		String commodityPackingString = params.get("commodityPacking").toString().trim();
		if (commodityPackingString == null || commodityPackingString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshikong.getMessage());
		}
		int commodityPacking = Integer.valueOf(commodityPackingString);
		if (commodityPacking != 1 && commodityPacking != 2 && commodityPacking != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshicuowo.getMessage());
		}

		map.put("commodityPacking", commodityPacking);

//		 specifi:2,//包装/规格  单位 散装,1 g,  2 kg ,3 ML,4 L ,  commoditySpecifications:'散装',//产品规格
		String specifiString = params.get("specifi").toString().trim();
		if (specifiString == null || specifiString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danweikong.getMessage());
		}
		System.out.println(params.toString());
		int specifi = Integer.valueOf(specifiString);
		if (specifi == 1) {
			specifiString = "g";
		} else if (specifi == 2) {
			specifiString = "kg";
		} else if (specifi == 3) {
			specifiString = "ML";
		} else if (specifi == 4) {
			specifiString = "L";
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshicuowo.getMessage());
		}

		String commoditySpecifications = null;
		boolean b = false;
		if (commodityPacking != 1) {
			String cationsString = params.get("cations").toString().trim();
			if (cationsString == null || cationsString.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigekong.getMessage());
			}
			b = LegalCheck.isNumericInt(cationsString);
			if (b == false) {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigecuowo.getMessage());
			}
			int cations = Integer.valueOf(cationsString);
			commoditySpecifications = cations + specifiString;
		}

		// 判断 选择的包装方式与单位是否统一
		if (commodityPacking == 1) {
			if (specifi != 2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
			commoditySpecifications = "散装";
		} else if (commodityPacking == 2) {
			if (specifi != 2 && specifi != 1) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		} else if (commodityPacking == 3) {
			if (specifi != 3 && specifi != 4) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		}
		map.put("commoditySpecifications", commoditySpecifications);
		// 单价
		String commodityJiageString = params.get("commodityJiage").toString().trim();
		if (commodityJiageString == null || commodityJiageString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danjiakong.getMessage());
		}
		b = LegalCheck.isNumericFolse(commodityJiageString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danjiacuowo.getMessage());
		}
		// 转成分
		Float float1 = Float.valueOf(commodityJiageString) * 100;
		long commodityJiage = float1.longValue();
		map.put("commodityJiage", commodityJiage);
		// 总数
		String commodityCountNoString = params.get("commodityCountNo").toString().trim();
		if (commodityCountNoString == null || commodityCountNoString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shuliangkong.getMessage());
		}
		b = LegalCheck.isNumericInt(commodityCountNoString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shuliangcuowo.getMessage());
		}
		long commodityCountNo = Integer.valueOf(commodityCountNoString);
		map.put("commodityCountNo", commodityCountNo);
		map.put("commodityReserveNo", 0);
		map.put("commoditySurplusNo", commodityCountNo);
		map.put("remarks", params.get("remarks").toString().trim());
		map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());

		// 是否支持线上预定
		String reserveString = params.get("reserve").toString().trim();
		if (reserveString == null || reserveString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingkong.getMessage());
		}
		b = LegalCheck.isNumericInt(reserveString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingcuowo.getMessage());
		}
		int reserve = Integer.valueOf(reserveString);
		if (reserve != 1 && reserve != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingcuowo.getMessage());
		}
		map.put("reserve", reserve);
		// 判断非法输入
		ServerResponse<Object> serverResponseObject = null;
		if (reserve == 1) {

			String deliveryTypeString = params.get("deliveryType").toString().trim();
			if (deliveryTypeString == null || deliveryTypeString.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shuliangkong.getMessage());
			}
			b = LegalCheck.isNumericInt(deliveryTypeString);
			if (b == false) {
				return ServerResponse.createByErrorMessage(ResponseMessage.songhuokong.getMessage());
			}
			// 送货方式 //1自取 ,2送货, 3自取+送货 4满免
			int deliveryType = Integer.valueOf(deliveryTypeString);
			if (deliveryType == 1) {
				map.put("deliveryType", deliveryType); // 送货方式
				map.put("deliveryCollect", 0);// 运费
			} else {
				// 运费
				String deliveryCollectString = params.get("deliveryCollect").toString().trim();
				if (deliveryCollectString == null || deliveryCollectString.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.yunfeikong.getMessage());
				}
				serverResponseObject = LegalCheck.isNumericInthan0(deliveryCollectString);
				if (serverResponseObject.getStatus() == 0) {
					int deliveryCollect = (int) serverResponseObject.getData() * 100;
					map.put("deliveryType", deliveryType); // 送货方式
					map.put("deliveryCollect", deliveryCollect);// 运费

				} else {
					return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
				}
			}
		} else {
			// 不支持线上预定
			map.put("deliveryType", 1); // 送货方式
			map.put("deliveryCollect", 0);// 运费
		}

		// 开始日期
		List<String> startend_list = JsonUtil.list2Obj((List<String>) params.get("value1"), List.class, String.class);
		if (startend_list.size() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
		}
		String startTime = startend_list.get(0);

		serverResponseObject = DateTimeUtil.isPastDate(startTime, 1);
		if (serverResponseObject.getStatus() == 0) {
			if ((boolean) serverResponseObject.getData()) {
				// TODO 开始时间要在 蔬菜 2天内
				int length = 2;
				serverResponseObject = DateTimeUtil.dateCompare(startTime, length);
				if (serverResponseObject.getStatus() == 0) {
					if ((boolean) serverResponseObject.getData()) {
						map.put("startTime", startTime);
					} else {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.jiagekaishiyaozai.getMessage() + length + "天");
					}

				} else {
					return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiagekaishicuowo.getMessage());
			}

		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}
		// 结束日期
		String endTime = startend_list.get(1);
		serverResponseObject = DateTimeUtil.isPastDate(startTime, 1);
		if (serverResponseObject.getStatus() == 0) {
			if ((boolean) serverResponseObject.getData()) {
				// TODO 结束时间要在 蔬菜 3天内 其他15天
				int length = 15;
				if (releaseType == 4) {
					length = 4;
				}
				serverResponseObject = DateTimeUtil.dateCompare(endTime, length);
				if (serverResponseObject.getStatus() == 0) {
					if ((boolean) serverResponseObject.getData()) {
						map.put("endTime", endTime);
					} else {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.jiagejieshuyaozai.getMessage() + length + "天");
					}

				} else {
					return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiagejieshucuowo.getMessage());
			}

		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}
		return ServerResponse.createBySuccess(map);

	}

	@Override
	public ServerResponse<Object> get_wholesaleCommodity_serviceType(long userId, Map<String, Object> params) {
		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		// 类型
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String welfareStatusString = params.get("welfareStatus").toString().trim();
		// 发布状态
		int welfareStatus = 0;
		if (welfareStatusString != null && !welfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(welfareStatusString);
		}
		// 商品名
		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType != null && !serviceType.equals("")) {
			serviceType = "%" + serviceType + "%";
		}
		// 是否在价格有效期
//		  <el-option label="价格有效期内" value="1"></el-option>
//          <el-option label="价格有效期已结束" value="2"></el-option>
//          <el-option label="价格有效期未开始" value="3"></el-option>

		String commodityTypeString = params.get("commodityType").toString().trim();
		int commodityType = 0;
		if (commodityTypeString != null && !commodityTypeString.equals("")) {
			commodityType = Integer.valueOf(commodityTypeString);
			if (commodityType != 1 && commodityType != 2 && commodityType != 3) {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
			}
		}

		// 1公开，2自己发布过的
		String typeString = params.get("type").toString().trim();
		if (typeString == null || typeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}
		int type = Integer.valueOf(typeString);
		if (type != 1 && type != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweicuowo.getMessage());
		}

		String dateString = DateTimeUtil.dateToAll();

		return ServerResponse.createBySuccess(wholesaleCommodityMapper.get_wholesaleCommodity_serviceType(userId, type,
				commodityType, dateString, releaseType, serviceType, welfareStatus));
	}

	@Override
	public ServerResponse<Object> get_myWholesaleCommodity_list(long userId, Map<String, Object> params) {
		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		// 类型
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String welfareStatusString = params.get("welfareStatus").toString().trim();
		// 发布状态
		int welfareStatus = 0;
		if (welfareStatusString != null && !welfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(welfareStatusString);
		}
		// 商品名
		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType != null && !serviceType.equals("")) {
			serviceType = "%" + serviceType + "%";
		}
		// 是否在价格有效期
//		  <el-option label="价格有效期内" value="1"></el-option>
//          <el-option label="价格有效期已结束" value="2"></el-option>
//          <el-option label="价格有效期未开始" value="3"></el-option>

		String commodityTypeString = params.get("commodityType").toString().trim();
		int commodityType = 0;
		if (commodityTypeString != null && !commodityTypeString.equals("")) {
			commodityType = Integer.valueOf(commodityTypeString);
			if (commodityType != 1 && commodityType != 2 && commodityType != 3) {
				return ServerResponse.createByErrorMessage(ResponseMessage.jiageyouxiaoqicuowo.getMessage());
			}
		}

		// 1公开，2自己发布过的
		String typeString = params.get("type").toString().trim();
		if (typeString == null || typeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}
		int type = Integer.valueOf(typeString);
		if (type != 1 && type != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweicuowo.getMessage());
		}

		String dateString = DateTimeUtil.dateToAll();
		String currentPage_string = params.get("currentPage").toString().trim();
		String pageSize_string = params.get("pageSize").toString().trim();
		int currentPage = 0;
		int pageSize = 0;

		if (currentPage_string != null && currentPage_string != "") {
			currentPage = Integer.parseInt(currentPage_string);
			if (currentPage <= 0) {
				return ServerResponse.createByErrorMessage("页数不能小于0");
			}

		} else {
			return ServerResponse.createByErrorMessage("请正确输入页数");
		}

		if (pageSize_string != null && pageSize_string != "") {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		Page<WholesaleCommodity> equipment_pagePage = new Page<WholesaleCommodity>();

		long zongtiaoshu = wholesaleCommodityMapper.get_myWholesaleCommodityNo(userId, type, commodityType, dateString,
				releaseType, serviceType, welfareStatus);

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页
		if (zongtiaoshu == 0) {
			equipment_pagePage.setDatas(null);
			return ServerResponse.createBySuccess(equipment_pagePage);
		}
		// 查询list
		List<WholesaleCommodity> equipmentList = wholesaleCommodityMapper.get_myWholesaleCommodity_list(
				(currentPage - 1) * pageSize, pageSize, userId, type, commodityType, dateString, releaseType,
				serviceType, welfareStatus);

		// isValidity
//		  <el-option label="价格有效期内" value="1"></el-option>
//      <el-option label="价格有效期已结束" value="2"></el-option>
//      <el-option label="价格有效期未开始

		ServerResponse<Object> serverResponse = null;

		int orderStatus_count = 0;
		for (int a = 0; a < equipmentList.size(); a++) {
			IsButten isButten = new IsButten();
			WholesaleCommodity aaCommodity = equipmentList.get(a);
			int getWelfareStatus = aaCommodity.getWelfareStatus();
			if (getWelfareStatus == 1 || getWelfareStatus == 2) {
				if (commodityType != 0) {
					if (commodityType == 1) {
						aaCommodity.setIsValidity("显示中-价格有效期内");

						// 查询有无未送货的订单
//						 private boolean isDisplayEdit = false; //编辑键
//						 private boolean isDisplayHide = false; //隐藏键
//						 private boolean isDisplayRelease = false; //发布键
//						 private boolean isDisplayRefresh = false; //刷新键
//						 private boolean isDisplayDelete = false; //删除键
						serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 0);
						if (serverResponse.getStatus() == 0) {
							orderStatus_count = (int) serverResponse.getData();
							if (orderStatus_count != 0) {
								// 如果没有显示
								isButten.setRefresh(true);
								aaCommodity.setIsButten(isButten);
							} else {
								isButten.setEdit(true);
								if (aaCommodity.getWelfareStatus() == 1) {
									// WelfareStatus=2隐藏中 1发布中
									isButten.setHide(true);
								} else {
									// 显示发布键
									isButten.setRelease(true);
								}
								isButten.setRefresh(true);
								isButten.setDelete(true);
								aaCommodity.setIsButten(isButten);
							}
							equipmentList.set(a, aaCommodity);

						} else {
							return serverResponse;
						}

					} else if (commodityType == 2) {
						aaCommodity.setIsValidity("未显示-价格有效期已结束");

						// 只显示编辑键
						isButten.setEdit(true);
						aaCommodity.setIsButten(isButten);
						equipmentList.set(a, aaCommodity);
					} else if (commodityType == 3) {
						aaCommodity.setIsValidity("未显示-价格有效期未开始");

						isButten.setDelete(true);
						isButten.setEdit(true);
						aaCommodity.setIsButten(isButten);
						equipmentList.set(a, aaCommodity);
					}
				} else {
					String startTime = aaCommodity.getStartTime();
					String endTime = aaCommodity.getEndTime();

					ServerResponse<Object> serverResponseObject = DateTimeUtil.isPastDate(endTime, 0);
					if (serverResponseObject.getStatus() == 0) {
						if ((boolean) serverResponseObject.getData()) {
							// 结束时间晚于现在
							serverResponseObject = DateTimeUtil.isPastDate(startTime, 0);
							if ((boolean) serverResponseObject.getData()) {
								// 开始时间晚于现在 未开始
								aaCommodity.setIsValidity("未显示-价格有效期未开始");

								isButten.setDelete(true);
								isButten.setEdit(true);
								aaCommodity.setIsButten(isButten);
								equipmentList.set(a, aaCommodity);
							} else {
								// 开始时间早于现在 进行中
								aaCommodity.setIsValidity("显示中-价格有效期内");

								serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 0);
								if (serverResponse.getStatus() == 0) {
									orderStatus_count = (int) serverResponse.getData();
									if (orderStatus_count != 0) {
										// 如果没有显示
										isButten.setRefresh(true);
										aaCommodity.setIsButten(isButten);
									} else {
										isButten.setEdit(true);
										if (aaCommodity.getWelfareStatus() == 1) {
											// WelfareStatus=2隐藏中 1发布中
											isButten.setHide(true);
										} else {
											// 显示发布键
											isButten.setRelease(true);
										}
										isButten.setRefresh(true);
										isButten.setDelete(true);
										aaCommodity.setIsButten(isButten);
									}
									equipmentList.set(a, aaCommodity);
								} else {
									return serverResponse;
								}
							}
						} else {
							// 结束时间晚于现在 已结束
							aaCommodity.setIsValidity("未显示-价格有效期已结束");

							// 只显示编辑键
							isButten.setEdit(true);
							aaCommodity.setIsButten(isButten);
							equipmentList.set(a, aaCommodity);
						}

					}

				}
			} else if (getWelfareStatus == 4) {
				aaCommodity.setIsValidity("未显示-待审核未发布");
				isButten.setDelete(true);
				isButten.setEdit(true);
				aaCommodity.setIsButten(isButten);
				equipmentList.set(a, aaCommodity);
			} else if (getWelfareStatus == 5 || commodityType == 2) {
				aaCommodity.setIsValidity("未显示-价格有效期已结束");
				// 只显示编辑键
				isButten.setEdit(true);
				aaCommodity.setIsButten(isButten);

				equipmentList.set(a, aaCommodity);

			}
		}

		equipment_pagePage.setDatas(equipmentList);
		return ServerResponse.createBySuccess(equipment_pagePage);

	}

	@Override
	public ServerResponse<Object> adminWholesaleCommodity(Map<String, Object> params) {
		String currentPage_string = params.get("currentPage").toString().trim();
		String pageSize_string = params.get("pageSize").toString().trim();
		int currentPage = 0;
		int pageSize = 0;

		if (currentPage_string != null && currentPage_string != "") {
			currentPage = Integer.parseInt(currentPage_string);
			if (currentPage <= 0) {
				return ServerResponse.createByErrorMessage("页数不能小于0");
			}

		} else {
			return ServerResponse.createByErrorMessage("请正确输入页数");
		}

		if (pageSize_string != null && pageSize_string != "") {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
			if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 29 && releaseType != 9) {
				return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}

		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		String companyName = params.get("companyName").toString().trim();
		String detailed =null;
		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		}

		Page<WholesaleCommodity> equipment_pagePage = new Page<WholesaleCommodity>();
		long zongtiaoshu = 0;
		List<WholesaleCommodity> list_equipmentall = null;
		
		System.out.println("contact"+contact+"detailed"+detailed+"companyName"+companyName);
		if ((contact.equals("") || contact == null) && ( detailed == null || detailed.equals("") )
				&& (companyName.equals("") || companyName == null)) {
			zongtiaoshu = wholesaleCommodityMapper.adminWholesaleCommodity_no(0, releaseType);
			list_equipmentall = wholesaleCommodityMapper.adminWholesaleCommodity((currentPage - 1) * pageSize, pageSize,
					0, releaseType);
		} else {
			zongtiaoshu = wholesaleCommodityMapper.adminWholesaleCommodity_no_realName(contact,companyName,detailed , releaseType);
			list_equipmentall = wholesaleCommodityMapper.adminWholesaleCommodity_realName((currentPage - 1) * pageSize, pageSize,
					contact,companyName,detailed ,releaseType);
		}

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页
		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}
}
