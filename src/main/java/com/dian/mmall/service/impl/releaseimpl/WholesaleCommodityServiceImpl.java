package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.dian.mmall.pojo.goumaidingdan.CommonMenuWholesalecommodity;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.OrderService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.PictureUtil;
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

	
	@Autowired
	private BunnerService bunnerService;

	@Override
	public ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params) {
		ServerResponse<Object> serverResponse = check_evaluate(user.getId(), params, 1);
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

	public ServerResponse<Object> check_evaluate(long userId, Map<String, Object> params, int type) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

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
		RealName realName = realNameMapper.getRealName(userId);
		if (realName != null) {
			String contact = params.get("contact").toString().trim();
			if (!EncrypDES.encryptPhone(contact).equals(realName.getContact())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}
			// map.put("realNameId", realName.getId());
			// map.put("serviceDetailed", realName.getDetailed());
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
		map.put("selectedOptions", params.get("selectedOptions").toString().trim());

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
		int specifi = Integer.valueOf(specifiString);
		if (specifi < 0 || specifi > 4) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danweicuowo.getMessage());
		}
		map.put("specifi", specifi);

		boolean b = false;

		if (commodityPacking != 1) {
			String cations = params.get("cations").toString().trim();
			if (cations == null || cations.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigekong.getMessage());
			}
			if (LegalCheck.isNumericFolse(cations)) {
				map.put("cations", cations);
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigecuowo.getMessage());
			}

		}

		// 判断 选择的包装方式与单位是否统一
		if (commodityPacking == 1) {
			if (specifi != 2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}

		} else if (commodityPacking == 2) {
			if (specifi != 2 && specifi != 1) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		} else if (commodityPacking == 3) {
			if (specifi != 3 && specifi != 4) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		}

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
				b = LegalCheck.isNumericFolse(deliveryCollectString);
				if (b) {
					int deliveryCollect = (int) (float1.valueOf(deliveryCollectString) * 100);
					map.put("deliveryType", deliveryType); // 送货方式
					map.put("deliveryCollect", deliveryCollect);// 运费

				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.yunfeicuowo.getMessage());
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

	// 获取自己发布过的商品名
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

	// 公开列表中获取商品名
	@Override
	public ServerResponse<Object> wholesaleCommodity_serviceType(Map<String, Object> params) {
		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		// 类型
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String selectedOptions = null;

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shichangsuozaichengqukong.getMessage());

		}

		Integer provincesId = selectedOptions_list.get(0);
		Integer cityId = selectedOptions_list.get(1);
		Integer districtCountyId = selectedOptions_list.get(2);
		// 判断省市区id是否正确
		selectedOptions = "%" + cityMapper.checkeCity(provincesId, cityId, districtCountyId) + "%";
		// 商品名
		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType != null && !serviceType.equals("")) {
			serviceType = "%" + serviceType + "%";
		}

		String companyName = params.get("companyName").toString().trim();
		String createTime = DateTimeUtil.dateToAll();
		List<String> listString = wholesaleCommodityMapper.wholesaleCommodity_serviceType(releaseType, selectedOptions,
				serviceType, companyName, createTime);
		if (listString.size() == 0) {
			selectedOptions = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
			listString = wholesaleCommodityMapper.wholesaleCommodity_serviceType(releaseType, selectedOptions,
					serviceType, companyName, createTime);
		}

		return ServerResponse.createBySuccess(listString);
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

						if (getWelfareStatus == 1) {
							aaCommodity.setIsValidity("显示中-价格有效期内");
							isButten.setHide(true);
						} else {
							aaCommodity.setIsValidity("隐藏中-价格有效期内");
							isButten.setRelease(true);

						}
						// 查询有无未送货的订单
//						 private boolean isDisplayEdit = false; //编辑键
//						 private boolean isDisplayHide = false; //隐藏键
//						 private boolean isDisplayRelease = false; //发布键
//						 private boolean isDisplayRefresh = false; //刷新键
//						 private boolean isDisplayDelete = false; //删除键
						// 判断有无未到终态的订单
						serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 0);
						if (serverResponse.getStatus() == 0) {
							orderStatus_count = (int) serverResponse.getData();
							if (orderStatus_count != 0) {
								// 如果有,显示刷新键
								isButten.setRefresh(true);
								isButten.setOrder(true);
								aaCommodity.setIsButten(isButten);
							} else {
								isButten.setEdit(true);
								isButten.setRefresh(true);
								isButten.setDelete(true);
								// 查看有无结束订单
								serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 9);
								if (serverResponse.getStatus() == 0) {
									orderStatus_count = (int) serverResponse.getData();
									if (orderStatus_count > 0) {
										isButten.setOrder(true);
									}
								}
								aaCommodity.setIsButten(isButten);
							}
							equipmentList.set(a, aaCommodity);

						} else {
							return serverResponse;
						}

					} else if (commodityType == 2) {
						aaCommodity.setIsValidity("未显示-价格有效期已结束");

						// 判断有无订单全部
						serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 9);
						if (serverResponse.getStatus() == 0) {
							orderStatus_count = (int) serverResponse.getData();
							if (orderStatus_count > 0) {
								isButten.setOrder(true);
							} else {
								// 只显示编辑键
								isButten.setDelete(true);
								isButten.setEdit(true);
							}
						}

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
										isButten.setOrder(true);
										aaCommodity.setIsButten(isButten);
									} else {
										isButten.setEdit(true);
										if (aaCommodity.getWelfareStatus() == 1) {
											// WelfareStatus=2隐藏中 1发布中
											isButten.setHide(true);
										} else {
											// 显示发布键
											isButten.setDelete(true);
											isButten.setRelease(true);
										}
										isButten.setRefresh(true);
										isButten.setDelete(true);
										serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 9);
										if (serverResponse.getStatus() == 0) {
											orderStatus_count = (int) serverResponse.getData();
											if (orderStatus_count > 0) {
												isButten.setOrder(true);
											}
										}
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

							serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 9);
							if (serverResponse.getStatus() == 0) {
								orderStatus_count = (int) serverResponse.getData();
								if (orderStatus_count > 0) {
									isButten.setOrder(true);
								} else {
									// 只显示编辑键
									isButten.setDelete(true);
									isButten.setEdit(true);
								}
							}
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

				serverResponse = orderService.get_conduct_order(aaCommodity.getId(), 9);
				if (serverResponse.getStatus() == 0) {
					orderStatus_count = (int) serverResponse.getData();
					if (orderStatus_count > 0) {
						isButten.setOrder(true);
					} else {
						// 只显示编辑键
						isButten.setDelete(true);
						isButten.setEdit(true);
					}
				}

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
		String detailed = null;
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

		if ((contact.equals("") || contact == null) && (detailed == null || detailed.equals(""))
				&& (companyName.equals("") || companyName == null)) {
			zongtiaoshu = wholesaleCommodityMapper.adminWholesaleCommodity_no(0, releaseType);
			list_equipmentall = wholesaleCommodityMapper.adminWholesaleCommodity((currentPage - 1) * pageSize, pageSize,
					0, releaseType);
		} else {
			zongtiaoshu = wholesaleCommodityMapper.adminWholesaleCommodity_no_realName(contact, companyName, detailed,
					releaseType);
			list_equipmentall = wholesaleCommodityMapper.adminWholesaleCommodity_realName((currentPage - 1) * pageSize,
					pageSize, contact, companyName, detailed, releaseType);
		}

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页
		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}

	public int getreleaseType(long id) {
		return wholesaleCommodityMapper.getreleaseType( id);
	}
	@Override
	public ServerResponse<String> operation_userWholesaleCommodity(long userId, Map<String, Object> params) {
		String type = params.get("type").toString().trim();

		String idString = params.get("id").toString().trim();
		long id = 0;
		if (type != null && !type.equals("") && idString != null && !idString.equals("")) {
			int type_int = Integer.valueOf(type);
			if (type_int < 1 || type_int > 6) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}

			id = Long.valueOf(idString);

			// 有发布中或者未开始的广告不能操作
			if (bunnerService.getguanggaocount(id, getreleaseType(id)) > 0) {
				ServerResponse.createByErrorMessage(ResponseMessage.yougonggongxuanchuan.getMessage());
			}

			int result = wholesaleCommodityMapper.checkout_count(id, userId);
			if (result == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shangpinchaxunkong.getMessage());
			}

			String updateTime = DateTimeUtil.dateToAll();
			if (type_int == 1 || type_int == 3 || type_int == 4) {

				result = wholesaleCommodityMapper.operation_userWholesaleCommodity(id, type_int, updateTime);
			} else if (type_int == 5) {

				synchronized (this) {
					ServerResponse<Object> serverResponse = orderService.get_conduct_order(id, 0);
					if (serverResponse.getStatus() == 0) {
						result = (int) serverResponse.getData();
						if (result != 0) {
							return ServerResponse
									.createByErrorMessage(ResponseMessage.shangpinyoudingdanbunnegchan.getMessage());
						} else {
							result = wholesaleCommodityMapper.operation_userWholesaleCommodity(id, type_int,
									updateTime);
						}
					} else {
						return ServerResponse.createByErrorMessage(serverResponse.getMsg());
					}

				}

			}

			else if (type_int == 6) {

				WholesaleCommodity wholesaleCommodity = wholesaleCommodityMapper.get_userWholesaleCommodity_id(userId,
						id);

				if (wholesaleCommodity == null) {
					return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
				}

				// 检查图片
				ServerResponse<String> serverResponseString = setPictureUrl(
						(ArrayList<Picture>) params.get("pictureUrl"), wholesaleCommodity.getPictureUrl());
				if (serverResponseString.getStatus() != 0) {
					return serverResponseString;
				}
				// 检查其他输入
				ServerResponse<Object> serverResponse = check_evaluate(userId, params, 2);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}

				Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
				map.put("createTime", wholesaleCommodity.getCreateTime());
				map.put("id", wholesaleCommodity.getId());
				map.put("pictureUrl", serverResponseString.getMsg());
				map.put("authentiCationStatus", 1);
				map.put("welfareStatus", 4);

				WholesaleCommodity wholesaleCommodity_create = (WholesaleCommodity) BeanMapConvertUtil
						.convertMap(WholesaleCommodity.class, map);

				// {result=true, message=验证通过} 返回结果
				Map<String, Object> checknullMap = AnnotationDealUtil.validate(wholesaleCommodity_create);
				if ((boolean) checknullMap.get("result") == true
						&& ((String) checknullMap.get("message")).equals("验证通过")) {

					result = wholesaleCommodityMapper.update_wholesaleCommodity(wholesaleCommodity_create);

					if (result > 0) {
						try {
							List<Picture> listObj4 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"),
									List.class, Picture.class);
							for (int a = 0; a < listObj4.size(); a++) {
								Picture picture = listObj4.get(a);
								if (picture.getUseStatus() == 2) {
									FileControl.deleteFile(Const.PATH_E_IMG + picture.getUserName());
									pictureMapper.updatePicture(picture.getId());

								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				} else if ((boolean) checknullMap.get("result") == false) {
					return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
				} else {
					return ServerResponse.createByErrorMessage("系统异常稍后重试");
				}
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
			}

			if (result == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.caozuoshibai.getMessage());
			}

			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
	}

	// 编辑时检查图片并重新赋值
	public ServerResponse<String> setPictureUrl(Object object, String PictureUrl) {
		// 前端传入
		List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) object, List.class, Picture.class);
		// 数据库查询
		List<Picture> listObj4 = JsonUtil.string2Obj(PictureUrl, List.class, Picture.class);
		if (listObj3.size() == 0 || listObj4.size() == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.tupianbunnegkong.getMessage());
		}
		List<Picture> listObjCun = new ArrayList<Picture>();

		// 处理编辑后
		for (int a3 = 0; a3 < listObj3.size(); a3++) {
			Picture picture = listObj3.get(a3);
			long id3 = picture.getId();
			int useStatus = picture.getUseStatus();
			if (useStatus == 1) {
				// 1上传更新为3使用
				pictureMapper.updatePictureUse(id3);
				picture.setUseStatus(3);
				listObjCun.add(picture);
			} else if (useStatus == 3) {

				boolean fanduanshifouweiyijingchunli = false;

				for (int a4 = 0; a4 < listObj4.size(); a4++) {
					if (id3 == listObj4.get(a4).getId()) {
						listObjCun.add(picture);
						fanduanshifouweiyijingchunli = true;
						break;
					}
				}
				if (fanduanshifouweiyijingchunli == false) {
					return ServerResponse.createByErrorMessage(ResponseMessage.benditupianbucunzai.getMessage());
				}
			}
		}
		if (listObjCun.size() > 0) {
			return ServerResponse.createBySuccessMessage(JsonUtil.obj2StringPretty(listObjCun));
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.tupianbunnegkong.getMessage());

	}

	@Override
	public ServerResponse<Object> get_userWholesaleCommodity_id(long userId, long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		WholesaleCommodity wholesaleCommodity = wholesaleCommodityMapper.get_userWholesaleCommodity_id(userId, id);

		if (wholesaleCommodity == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}

		wholesaleCommodity.setRealNameId(wholesaleCommodity.getCations());

		ServerResponse<Object> serverResponse = orderService.get_conduct_order(id, 0);
		if (serverResponse.getStatus() == 0) {
			int orderStatus_count = (int) serverResponse.getData();
			if (orderStatus_count != 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shangpinyoudingdanbunenggai.getMessage());
			}

			List<Integer> selectedOptions_list = JsonUtil.string2Obj(wholesaleCommodity.getSelectedOptions(),
					List.class);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("data", wholesaleCommodity);
			map.put("selectedOptions", selectedOptions_list);
			return ServerResponse.createBySuccess(map);
		} else {
			return serverResponse;
		}
	}

	// 公开列表
	@Override
	public ServerResponse<Object> getWholesaleCommodityPublicList(Map<String, Object> params) {
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
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		// 类型
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String selectedOptions = null;

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确

			selectedOptions = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		}

		if (selectedOptions == null || selectedOptions.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shichangsuozaichengqukong.getMessage());
		}
		// 商品名
		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType != null && !serviceType.equals("")) {
			serviceType = "%" + serviceType + "%";
		}

		String companyName = params.get("companyName").toString().trim();
		String createTime = DateTimeUtil.dateToAll();

		Page<WholesaleCommodity> equipment_pagePage = new Page<WholesaleCommodity>();

		long zongtiaoshu = wholesaleCommodityMapper.getWholesaleCommodityPublicListNo(releaseType, selectedOptions,
				serviceType, companyName, createTime);

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		List<WholesaleCommodity> list_equipmentall = wholesaleCommodityMapper.getWholesaleCommodityPublicList(
				(currentPage - 1) * pageSize, pageSize, releaseType, selectedOptions, serviceType, companyName,
				createTime);

		for (int i = 0; i < list_equipmentall.size(); i++) {
			WholesaleCommodity equipment = list_equipmentall.get(i);
			List<Picture> listObj3 = JsonUtil.string2Obj(equipment.getPictureUrl(), List.class, Picture.class);
			Picture picture = listObj3.get(0);
			equipment.setPictureUrl(picture.getPictureUrl());
			list_equipmentall.set(i, equipment);
		}
		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}

	@Override
	public ServerResponse<Object> getWholesaleCommodityPublicId(long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		WholesaleCommodity wholesaleCommodity = wholesaleCommodityMapper.getWholesaleCommodityPublicId(id);
		if (wholesaleCommodity != null) {

			wholesaleCommodity.setPictureUrl(PictureUtil.listToString(wholesaleCommodity.getPictureUrl()));
			RealName realName = realNameMapper.getRealName(wholesaleCommodity.getUserId());
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			Evaluate evaluate = new Evaluate();
			if (wholesaleCommodity.getEvaluateid() != 0) {
				evaluate = evaluateMapper.selectEvvaluateById(wholesaleCommodity.getEvaluateid());

			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("wholesaleCommodity", wholesaleCommodity);
			map.put("realName", realName);
			map.put("evaluate", evaluate);
			return ServerResponse.createBySuccess(map);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}

	}

	@Override
	public ServerResponse<Object> getWholesaleCommodityBoolean(WholesaleCommodity wholesaleCommodity) {
		int a = wholesaleCommodityMapper.getWholesaleCommodityBoolean(wholesaleCommodity);
		if (a == 0) {
			return ServerResponse.createByError();
		}
		return ServerResponse.createBySuccess();
	}

	@Override
	public ServerResponse<Object> getWholesalecommodity(String selectedOptions, int releaseType) {
		List<WholesaleCommodity> allCommonMenu = wholesaleCommodityMapper.getWholesalecommodity(selectedOptions,
				releaseType);
		return ServerResponse.createBySuccess(allCommonMenu);
	}

	@Override
	public List<Integer> getCommodityJiage(WholesaleCommodity wholesaleCommodity) {

		List<Integer> list = wholesaleCommodityMapper.getCommodityJiage(wholesaleCommodity);
		if (list.size() == 0) {
			String serviceDetailed = wholesaleCommodity.getServiceDetailed();

			Matcher slashMatcher = Pattern.compile("/").matcher(serviceDetailed);
			int mIdx = 0;
			while (slashMatcher.find()) {
				mIdx++;
				if (mIdx == 2) {
					break;
				}
			}
			mIdx = slashMatcher.start();
			serviceDetailed = serviceDetailed.substring(0, mIdx) + "%";
			wholesaleCommodity.setServiceDetailed(serviceDetailed);
			list = wholesaleCommodityMapper.getCommodityJiage(wholesaleCommodity);
			if (list.size() == 0) {
				wholesaleCommodity.setServiceDetailed(null);
				list = wholesaleCommodityMapper.getCommodityJiage(wholesaleCommodity);
			}
		}
		return list;
	}

	@Override
	public List<WholesaleCommodity> adminGetWcall(long userId) {
		return wholesaleCommodityMapper.adminGetWcall( userId);
	}

}
