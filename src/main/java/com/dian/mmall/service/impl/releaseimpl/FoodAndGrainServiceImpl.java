package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.dian.mmall.dao.releaseDao.EquipmentMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.service.release.FoodAndGrainService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.PictureUtil;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("foodAndGrainService")
public class FoodAndGrainServiceImpl implements FoodAndGrainService {
	// TODO和发布维修使用同一个表以后加字段 不要加必填
	@Autowired
	private EquipmentMapper equipmentMapper;
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

	@Override
	public ServerResponse<String> create_foodAndGrain(User user, Map<String, Object> params) {
		ServerResponse<Object> serverResponse = check_evaluate(user, params, 1);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		FoodAndGrain foodAndGrain = (FoodAndGrain) BeanMapConvertUtil.convertMap(FoodAndGrain.class, map);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(foodAndGrain);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = equipmentMapper.create_foodAndGrain(foodAndGrain);
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

	// 各种校验
	public ServerResponse<Object> check_evaluate(User currentUser, Map<String, Object> params, int type) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致
		long userId = Long.valueOf(params.get("userId").toString().trim());
		if (userId != currentUser.getId()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);

		String createTime = DateTimeUtil.dateToAll();

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);

		if (releaseType != Const.SHUCAIP && releaseType != Const.LIANGYOUP && releaseType != Const.TIAOLIAO  
				&& releaseType != Const.QINGJIEP && releaseType != Const.ZHUOYIP && releaseType != Const.SHUICHAN) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
		int count = 0;
		if (type == 1) { // TODO新建时才检查总数
			// 判断是否超过可以发布的总数
			count = equipmentMapper.countNum(releaseType, userId);
			if (count > 20) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			}
			map.put("createTime", createTime);
		}
		map.put("releaseType", releaseType);
		map.put("updateTime", createTime);
		map.put("termOfValidity", DateTimeUtil.a_few_days_later(90));

		// 判断是否实名
		if (currentUser.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		if (type == 1) { // 新建
			// 图片
			List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
					Picture.class);
			int list_size = listObj3.size();
			// 把getUse_status()==1 放到这个集合中
			List<Picture> listObj4 = new ArrayList<Picture>();
			int getNum = PictureNum.caishangpin.getNum();
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

		params.put("serviceAndprice", JsonUtil.obj2String(params.get("serviceAndprice")));

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(currentUser.getId());
		if (realName != null) {

			// 判断电话
			String contact = params.get("contact").toString().trim();
			// 判断手机号是否合法
			ServerResponse<String> serverContact = LegalCheck.legalCheckMobilePhone(contact);
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverContact.getMsg());
			}
			// map.put("contact", EncrypDES.encryptPhone(contact));
			map.put("consigneeName", params.get("consigneeName").toString().trim());
			// map.put("detailed", realName.getDetailed());
			// map.put("realNameId", realName.getId());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}

		// 获取用户类型
		serverResponse = SetBean.setRole(currentUser.getRole());
		// 检查用户类型
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		map.put("userType", serverResponse.getMsg());

		String serviceDetailed = params.get("serviceDetailed").toString().trim();
		if (!serviceDetailed.equals("全市") && !serviceDetailed.equals("来电确认")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());
		}
		map.put("serviceDetailed", serviceDetailed);

		map.put("serviceType", params.get("serviceType").toString().trim());
		map.put("serviceAndprice", params.get("serviceAndprice").toString().trim());
		map.put("remarks", params.get("remarks").toString().trim());
		map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
		map.put("releaseTitle", params.get("releaseTitle").toString().trim());
		return ServerResponse.createBySuccess(map);

	}

	@Override
	public ServerResponse<Object> get_myFoodAndGrain_list(User user, Map<String, Object> params) {
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
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixinbixuan.getMessage());
		}

		String getwelfareStatusString = params.get("welfareStatus").toString().trim();
		Integer welfareStatus = null;
		if (getwelfareStatusString != null && !getwelfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(getwelfareStatusString);
		}

		Page<FoodAndGrain> equipment_pagePage = new Page<FoodAndGrain>();

		long zongtiaoshu = equipmentMapper.get_myFoodAndGrain_list_no(releaseType, welfareStatus, user.getId());

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		List<FoodAndGrain> list_equipmentall = equipmentMapper.get_myFoodAndGrain_list((currentPage - 1) * pageSize,
				pageSize, releaseType, welfareStatus, user.getId());

		RealName realName = realNameMapper.getRealName(user.getId());
		for (int a = 0; a < list_equipmentall.size(); a++) {
			FoodAndGrain foodAndGrain = list_equipmentall.get(a);
			foodAndGrain.setContact(EncrypDES.decryptPhone(realName.getContact()));
			foodAndGrain.setDetailed(realName.getDetailed());
			foodAndGrain.setRealNameId(realName.getCompanyName());
			foodAndGrain.setExamineTime(realName.getAddressDetailed());
			list_equipmentall.set(a, foodAndGrain);
		}

		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}

	@Override
	public ServerResponse<String> operation_userFoodAndGrain(User user, Map<String, Object> params) {
		String type = params.get("type").toString().trim();
		String userId = params.get("userId").toString().trim();
		String id = params.get("id").toString().trim();
		long idLong = 0;
		if (type != null && !type.equals("") && userId != null && !userId.equals("") && id != null && !id.equals("")) {
			int type_int = Integer.valueOf(type);
			if (type_int < 1 || type_int > 6) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
			long userIdLong = Long.valueOf(userId);
			idLong = Long.valueOf(id);
			if (userIdLong != user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = null;
			String termOfValidity = DateTimeUtil.a_few_days_later(90);
			int result = 0;
			if (type_int == 1 || type_int == 2) {
				timeString = formatter.format(new Date());
				result = equipmentMapper.operation_userFoodAndGrain(userIdLong, idLong, type_int, timeString,
						termOfValidity);
			} else if (type_int == 3 || type_int == 4 || type_int == 5) {
				timeString = formatter.format(new Date());
				result = equipmentMapper.operation_userFoodAndGrain(userIdLong, idLong, type_int, timeString,
						termOfValidity);
			}

			else if (type_int == 6) {

				ServerResponse<Object> serverResponse = get_userFoodAndGrain_id(userIdLong, idLong);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				FoodAndGrain foodAndGrain = (FoodAndGrain) serverResponse.getData();

				// 检查图片
				ServerResponse<String> serverResponseString = setPictureUrl(
						(ArrayList<Picture>) params.get("pictureUrl"), foodAndGrain.getPictureUrl());
				if (serverResponseString.getStatus() != 0) {
					return serverResponseString;
				}
				// 检查其他输入
				serverResponse = check_evaluate(user, params, 2);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}

				Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
				map.put("createTime", foodAndGrain.getCreateTime());
				map.put("id", foodAndGrain.getId());
				map.put("pictureUrl", serverResponseString.getMsg());
				map.put("authentiCationStatus", 1);
				map.put("welfareStatus", 4);

				FoodAndGrain equipment_create = (FoodAndGrain) BeanMapConvertUtil.convertMap(FoodAndGrain.class, map);
				// {result=true, message=验证通过} 返回结果
				Map<String, Object> checknullMap = AnnotationDealUtil.validate(equipment_create);
				if ((boolean) checknullMap.get("result") == true
						&& ((String) checknullMap.get("message")).equals("验证通过")) {
					result = equipmentMapper.update_foodAndGrain(equipment_create);

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
	public ServerResponse<Object> get_userFoodAndGrain_id(long userId, long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		FoodAndGrain equipment = equipmentMapper.get_userFoodAndGrain_id(userId, id);
		if (equipment == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		RealName realName = realNameMapper.getRealName(equipment.getUserId());
		equipment.setContact(EncrypDES.decryptPhone(realName.getContact()));
		return ServerResponse.createBySuccess(equipment);
	}

	@Override
	public ServerResponse<Object> getFoodAndGrainList(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerResponse<Object> getFoodAndGrainTitleList(Map<String, Object> params) {
		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			String detailed = "%" + cityMapper.checkeCity(provincesId, cityId, districtCountyId) + "%";

			String releaseTitle = params.get("releaseTitle").toString().trim();

			if (releaseTitle != null && !releaseTitle.equals("")) {
				releaseTitle = "%" + releaseTitle + "%";
			}

			String serviceType = params.get("serviceType").toString().trim();

			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}

			String releaseTypeString = params.get("releaseType").toString().trim();
			Integer releaseType = null;
			if (releaseTypeString != null && !releaseTypeString.equals("")) {
				releaseType = Integer.valueOf(releaseTypeString);
				if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 29 && releaseType != 9
						&& releaseType != 11) {
					return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
				}
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
			}

			String typeString = params.get("type").toString().trim();
			Integer type = null;
			if (typeString != null && !typeString.equals("")) {
				type = Integer.valueOf(typeString);
				if (type != 1 && type != 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.shangpinfuwuleixingidnull.getMessage());
				}
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.shangpinfuwuleixingidnull.getMessage());
			}

			List<String> equipmentReleaseTitleList = equipmentMapper.getFoodAndGrainTitleList(releaseType, detailed,
					serviceType, releaseTitle, type);
			if (equipmentReleaseTitleList.size() == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				equipmentReleaseTitleList = equipmentMapper.getFoodAndGrainTitleList(releaseType, detailed, serviceType,
						releaseTitle, type);
			}

			return ServerResponse.createBySuccess(equipmentReleaseTitleList);

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getFoodAndGrainPublicList(Map<String, Object> params) {
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
			if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 29 && releaseType != 9
					&& releaseType != 11) {
				return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			String detailed = "%" + cityMapper.checkeCity(provincesId, cityId, districtCountyId) + "%";

			String releaseTitle = params.get("releaseTitle").toString().trim();

			if (releaseTitle != null && !releaseTitle.equals("")) {
				releaseTitle = "%" + releaseTitle + "%";
			}

			String serviceType = params.get("serviceType").toString().trim();

			Page<FoodAndGrain> equipment_pagePage = new Page<FoodAndGrain>();

			long zongtiaoshu = equipmentMapper.getFoodAndGrainPublicListNo(releaseType, detailed, releaseTitle,
					serviceType);

			if (zongtiaoshu == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				zongtiaoshu = equipmentMapper.getFoodAndGrainPublicListNo(releaseType, detailed, releaseTitle,
						serviceType);
			}

			equipment_pagePage.setTotalno(zongtiaoshu);
			equipment_pagePage.setPageSize(pageSize);
			equipment_pagePage.setCurrentPage(currentPage); // 当前页
			// 查询list
			List<FoodAndGrain> equipmentList = equipmentMapper.getFoodAndGrainPublicList((currentPage - 1) * pageSize,
					releaseType, pageSize, releaseTitle, detailed, serviceType);
			List<FoodAndGrain> setEquipmentList = new ArrayList<FoodAndGrain>();
			for (int i = 0; i < equipmentList.size(); i++) {
				FoodAndGrain equipment = equipmentList.get(i);
				List<Picture> listObj3 = JsonUtil.string2Obj(equipment.getPictureUrl(), List.class, Picture.class);
				Picture picture = listObj3.get(0);
				equipment.setPictureUrl(picture.getPictureUrl());
				setEquipmentList.add(equipment);
			}

			equipment_pagePage.setDatas(setEquipmentList);
			return ServerResponse.createBySuccess(equipment_pagePage);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getFoodAndGrainDetails(long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		FoodAndGrain equipment = equipmentMapper.getFoodAndGrainDetails(id);
		if (equipment == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		RealName realName = realNameMapper.getRealName(equipment.getUserId());
		equipment.setContact(EncrypDES.decryptPhone(realName.getContact()));
		equipment.setDetailed(realName.getDetailed());
		equipment.setRealNameId(realName.getCompanyName());
		equipment.setExamineTime(realName.getAddressDetailed());

		// 处理图片
		equipment.setPictureUrl(PictureUtil.listToString(equipment.getPictureUrl()));
		Map<String, Object> map = new HashMap<String, Object>();
		Evaluate evaluate = new Evaluate();
		if (equipment.getEvaluateid() != 0) {
			evaluate = evaluateMapper.selectEvvaluateById(equipment.getEvaluateid());

		}
		map.put("evaluate", evaluate);
		map.put("result", equipment);
		return ServerResponse.createBySuccess(map);

	}

	@Override
	public ServerResponse<Object> adminFoodAndGrain(Map<String, Object> params) {
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

		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
			if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 29 && releaseType != 9
					&& releaseType != 11) {
				return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}

		Page<FoodAndGrain> equipment_pagePage = new Page<FoodAndGrain>();

		long zongtiaoshu = equipmentMapper.adminFoodAndGrain_no(contact, releaseType);

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		List<FoodAndGrain> list_equipmentall = equipmentMapper.adminFoodAndGrain((currentPage - 1) * pageSize, pageSize,
				contact, releaseType);

		List<ServiceType> serviceTypeList = serviceTypeMapper.get_serviceTypeAll(releaseType);

		for (int a1 = 0; a1 < list_equipmentall.size(); a1++) {
			FoodAndGrain re = list_equipmentall.get(a1);
			long userId = re.getUserId();
			RealName realName = realNameMapper.getRealName(userId);
			re.setContact(EncrypDES.decryptPhone(realName.getContact()));
			re.setRealNameId(realName.getCompanyName());
			re.setDetailed(realName.getDetailed());
			re.setUpdateTime(realName.getAddressDetailed());
			String serviceTypeString = re.getServiceType();

			boolean bo = false;
			for (int a = 0; a < serviceTypeList.size(); a++) {
				ServiceType serviceType = serviceTypeList.get(a);
				String serviceTypeName = serviceType.getServiceTypeName();
				if (serviceTypeString.equals(serviceTypeName) && userId == serviceType.getCreateUserId()
						&& serviceType.getAuthentiCationStatus() == 1) {
					re.setServiceType(Const.SERVICETYPEDAI + serviceTypeString);
					re.setEvaluateid(serviceType.getId());
					bo = true;
				} else if (serviceTypeString.equals(serviceTypeName) && serviceType.getAuthentiCationStatus() == 2) {
					re.setEvaluateid(serviceType.getId());
					bo = true;
				}

			}
			if (bo == false) {
				re.setEvaluateid(-1);
				// 不能随意改
				re.setServiceType(Const.SERVICETYPENO + serviceTypeString);
			}

			list_equipmentall.set(a1, re);
		}

		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}

	@Override
	public List<FoodAndGrain> adminGetFgall(long userId) {
		return equipmentMapper.adminGetFgall( userId);
	}

}
