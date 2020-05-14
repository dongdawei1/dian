package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.fabu.QuXian;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.FabuMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.fabu.Fabu;
import com.dian.mmall.pojo.fabu.FabuList;
import com.dian.mmall.pojo.fabu.FanHui;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.FabuService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FabuUtil;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;

@Service
public class FabuServiceImpl implements FabuService {
	@Autowired
	private FabuMapper fabuMapper;

	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private EvaluateMapper evaluateMapper;
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	@Autowired
	private BunnerService bunnerService;
	@Autowired
	private CityMapper cityMapper;
	@Override
	public ServerResponse<String> createfabu(User user, Map<String, Object> params) {

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		// 检查权限
		if (!LegalCheck.checkefaburole(releaseType, user.getRole())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		// 输入合法检查，必填，有非法字符等
		ServerResponse<String> response = LegalCheck.legalCheckFrom(params);
		if (response.getStatus() != 0) {
			return response;
		}
		// 校验发布总数
		if (!LegalCheck.checkefabuzongshu(releaseType, fabuMapper.getFabuCount(releaseType, user.getId()))) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabudadaoshangxian.getMessage());
		}

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(user.getId());
		if (realName == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}
		ServerResponse<Object> chResponse = FabuUtil.check_evaluate(user, params, 1, realName, releaseType);
		if (chResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(chResponse.getMsg());
		}

		Map<String, Object> map = (Map<String, Object>) chResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		// 图片
		List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
				Picture.class);
		int list_size = listObj3.size();
		// 把getUse_status()==1 放到这个集合中
		List<Picture> listObj4 = new ArrayList<Picture>();

		if (list_size > 0) {
			int count = 0;
			count = 0;
			for (int a = 0; a < list_size; a++) {
				Picture picture = listObj3.get(a);
				if (picture.getUseStatus() == 1) {
					picture.setUserId(user.getId());
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
			if (count > 5) {
				// 判断没有删除的图片是否大于规定
				return ServerResponse.createByErrorMessage("图片数量不能超过 " + 5 + "个");
			}
			if (count == 0) {
				return ServerResponse.createByErrorMessage("图片不能为空");
			}
		} else {
			return ServerResponse.createByErrorMessage("图片不能为空");
		}
		map.put("pictureUrl", JsonUtil.obj2StringPretty(listObj4));
		Fabu fabu = (Fabu) BeanMapConvertUtil.convertMap(Fabu.class, map);
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(fabu);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = fabuMapper.createfabu(fabu);
			if (count == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
			long id = fabuMapper.getid(fabu);
			// 创建 接单用户评价
			Evaluate evaluate = new Evaluate();
			evaluate.setPermissionid(id);
			evaluate.setUserId(user.getId());
			evaluate.setReleaseid(releaseType);
			evaluateMapper.adminAddOrderCreateEvaluate(evaluate);

			long evid = evaluateMapper.getid(evaluate);
			fabuMapper.upevaluate(id, evid);
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}
	}

	@Override
	public ServerResponse<Object> getfabuad(Map<String, Object> params) {

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

		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = Integer.valueOf(releaseTypeString);

		Page<Fabu> equipment_pagePage = new Page<Fabu>();

		long zongtiaoshu = fabuMapper.adminfabuno(contact, releaseType);

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		List<Fabu> list_equipmentall = fabuMapper.adminfabu((currentPage - 1) * pageSize, pageSize, contact,
				releaseType);

		List<ServiceType> serviceTypeList = serviceTypeMapper.get_serviceTypeAll(releaseType);

		for (int a1 = 0; a1 < list_equipmentall.size(); a1++) {
			Fabu re = list_equipmentall.get(a1);
			RealName realName = realNameMapper.getRealName(re.getUserId());
			re.setContact(EncrypDES.decryptPhone(realName.getContact()));
			re.setRealNameId(realName.getCompanyName());
			re.setDetailed(realName.getDetailed());
			re.setUpdateTime(realName.getAddressDetailed());

			String serviceTypeString = re.getServiceType();
			long userId = re.getUserId();
			if (re.getReleaseType() != 13 && re.getReleaseType() != 14 && re.getReleaseType() != 15
					&& re.getReleaseType() != 17 && releaseType != 19) {
				boolean bo = false;
				for (int a = 0; a < serviceTypeList.size(); a++) {
					ServiceType serviceType = serviceTypeList.get(a);
					String serviceTypeName = serviceType.getServiceTypeName();
					if (serviceTypeString.equals(serviceTypeName) && userId == serviceType.getCreateUserId()
							&& serviceType.getAuthentiCationStatus() == 1) {
						re.setServiceType(Const.SERVICETYPEDAI + serviceTypeString);
						re.setEvaluateid(serviceType.getId());
						bo = true;
					} else if (serviceTypeString.equals(serviceTypeName)
							&& serviceType.getAuthentiCationStatus() == 2) {
						re.setEvaluateid(serviceType.getId());
						bo = true;
					}

				}
				if (bo == false) {
					re.setEvaluateid(-1);
					re.setServiceType(Const.SERVICETYPENO + serviceTypeString);
				}
			}

			list_equipmentall.set(a1, re);
		}

		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);

	}
//14是租房信息in(14,15)

	@Override
	public ServerResponse<Object> getmyfabu(User user, Map<String, Object> params) {
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

		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);
		}

		if (releaseType != 14) {
			// 检查权限
			if (!LegalCheck.checkefaburole(releaseType, user.getRole())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
			}
		}

		String getwelfareStatusString = params.get("welfareStatus").toString().trim();
		Integer welfareStatus = null;
		if (getwelfareStatusString != null && !getwelfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(getwelfareStatusString);
		}

		Page<Fabu> equipment_pagePage = new Page<Fabu>();

		long zongtiaoshu = fabuMapper.getmyfabuno(releaseType, welfareStatus, user.getId());

		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		List<Fabu> list_equipmentall = fabuMapper.getmyfabu((currentPage - 1) * pageSize, pageSize, releaseType,
				welfareStatus, user.getId());
		RealName realName = realNameMapper.getRealName(user.getId());

		for (int a = 0; a < list_equipmentall.size(); a++) {
			Fabu fa = list_equipmentall.get(a);
			fa.setContact(EncrypDES.decryptPhone(realName.getContact()));
			fa.setDetailed(realName.getDetailed());
			fa.setRealNameId(realName.getAddressDetailed());
			fa.setUserType(realName.getCompanyName());
			fa.setExamineName(FabuUtil.releaseTypeString(fa.getReleaseType()));
			fa.setExamineTime(FabuUtil.welfareStatusString(fa.getWelfareStatus()));
			list_equipmentall.set(a, fa);
		}

		equipment_pagePage.setDatas(list_equipmentall);
		return ServerResponse.createBySuccess(equipment_pagePage);
	}

	// 编辑，操作
	@Override
	public ServerResponse<String> upfabu(User user, Map<String, Object> params) {
		String typeStirng = params.get("type").toString().trim();
		String userIdStirng = params.get("userId").toString().trim();
		String idStirng = params.get("id").toString().trim();
		String releaseTypeStirng = params.get("releaseType").toString().trim();
		if (typeStirng != null && !typeStirng.equals("") && userIdStirng != null && !userIdStirng.equals("")
				&& idStirng != null && !idStirng.equals("") && releaseTypeStirng != null
				&& !releaseTypeStirng.equals("")) {
			int type = Integer.valueOf(typeStirng);
			if (type < 1 || type > 9) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
			long userId = Long.valueOf(userIdStirng);
			long id = Long.valueOf(idStirng);
			int releaseType = Integer.valueOf(releaseTypeStirng);
			if (userId != user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}

			// 有发布中或者未开始的广告不能操作
			if (bunnerService.getguanggaocount(id, releaseType) > 0) {
				ServerResponse.createByErrorMessage(ResponseMessage.yougonggongxuanchuan.getMessage());
			}
			// 检查权限
			if (!LegalCheck.checkefaburole(releaseType, user.getRole())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
			}

			String timeString = DateTimeUtil.dateToAll();
			String termOfValidity = FabuUtil.termOfValidityString(releaseType);
			int result = 0;
			if (type == 1 || type == 2 || type == 3 || type == 4 || type == 5) {
				result = fabuMapper.upfabu(userId, id, type, timeString, termOfValidity);
				if (result == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.caozuoshibai.getMessage());
				}
				if(type == 5) {
					evaluateMapper.delEv(userId,id);
				}
				
			} else if (type == 6) {
				ServerResponse<String> response = editfabu(user, params);
				if (response.getStatus() != 0) {
					return response;
				}
			}
			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
	}

	private ServerResponse<String> editfabu(User user, Map<String, Object> params) {
		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		// 检查权限
		if (!LegalCheck.checkefaburole(releaseType, user.getRole())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		String idStirng = params.get("id").toString().trim();

		if (idStirng == null || idStirng.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		int id = Integer.valueOf(idStirng);
		ServerResponse<Object> re = getmyfabubyid(user.getId(), id);
		if (re.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(re.getMsg());
		}
		Fabu seleFabu = (Fabu) re.getData();
		if (releaseType != seleFabu.getReleaseType()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabulleixbunneg.getMessage());
		}

		// 判断实名信息是否正确
		RealName realName =realNameMapper.getRealName(user.getId());
		// 输入合法检查，必填，有非法字符等
		ServerResponse<String> response = LegalCheck.legalCheckFrom(params);
		if (response.getStatus() != 0) {
			return response;
		}

		params.remove("type");
		ServerResponse<Object> chResponse = FabuUtil.check_evaluate(user, params, 2, realName, releaseType);
		if (chResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(chResponse.getMsg());
		}

		Map<String, Object> map = (Map<String, Object>) chResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		// 检查图片
		ServerResponse<String> serverResponseString = setPictureUrl((ArrayList<Picture>) params.get("pictureUrl"),
				seleFabu.getPictureUrl());
		if (serverResponseString.getStatus() != 0) {
			return serverResponseString;
		}
		map.put("pictureUrl", serverResponseString.getMsg());
		map.put("id", seleFabu.getId());
		Fabu fabu = (Fabu) BeanMapConvertUtil.convertMap(Fabu.class, map);
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(fabu);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int result = fabuMapper.editfabu(fabu);
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
				return ServerResponse.createBySuccess();
			}

		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		}

		return ServerResponse.createByErrorMessage("系统异常稍后重试");

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
	public ServerResponse<Object> getmyfabubyid(long userId, long id) {
		Fabu fa = fabuMapper.getmyfabubyid(userId, id);
		if (fa == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		RealName realName = realNameMapper.getRealName(fa.getUserId());
		fa.setContact(EncrypDES.decryptPhone(realName.getContact()));
		fa.setDetailed(realName.getDetailed());
		fa.setRealNameId(realName.getAddressDetailed());
		fa.setUserType(realName.getCompanyName());
		return ServerResponse.createBySuccess(fa);
	}
    //公开列表
	@Override
	public ServerResponse<Object> getfabulist(Map<String, Object> params) {

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

		String releaseTypeString = params.get("releaseType").toString().trim();
//	      releaseType: '',
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);		
		}else {
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
//	      releaseTitle: '', //标题
			String releaseTitle = params.get("releaseTitle").toString().trim();

			if (releaseTitle != null && !releaseTitle.equals("")) {
				releaseTitle = "%" + releaseTitle + "%";
			}
//	      serviceType: '',//维修项目，设备名称
			String serviceType = params.get("serviceType").toString().trim();
			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}
			
			//	      fouseSizeGreater: '',
//	      fouseSizeLess: '',//面积小于
			String fouseSizeGreaterString = params.get("fouseSizeGreater").toString().trim();
			String fouseSizeLessString = params.get("fouseSizeLess").toString().trim();
			Integer fouseSizeGreater = null;
			Integer fouseSizeLess = null;
			if(releaseType==14 || releaseType==15) {
			if (fouseSizeGreaterString != null && !fouseSizeGreaterString.equals("")) {
				fouseSizeGreater = Integer.valueOf(fouseSizeGreaterString);
			}
			if (fouseSizeLessString != null && !fouseSizeLessString.equals("")) {
				fouseSizeLess = Integer.valueOf(fouseSizeLessString);
			}
			if (fouseSizeLess != null && fouseSizeGreater != null) {
				if (fouseSizeGreater > fouseSizeLess) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjicuowu.getMessage());
				}
			}
			}
			
			
			Page<FabuList> equipment_pagePage = new Page<FabuList>();

			long zongtiaoshu = fabuMapper.getfabulistno(releaseType, detailed, releaseTitle,
					serviceType,fouseSizeGreater,fouseSizeLess);

			if (zongtiaoshu == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				zongtiaoshu = fabuMapper.getfabulistno(releaseType, detailed, releaseTitle,
						serviceType,fouseSizeGreater,fouseSizeLess);
			}
		//	getfabulist
			equipment_pagePage.setTotalno(zongtiaoshu);
			equipment_pagePage.setPageSize(pageSize);
			equipment_pagePage.setCurrentPage(currentPage); // 当前页
			// 查询list
			List<FabuList> fabulist = fabuMapper.getfabulist(
					(currentPage - 1) * pageSize,  pageSize, releaseType, detailed, releaseTitle,
					serviceType,fouseSizeGreater,fouseSizeLess);

			for (int i = 0; i < fabulist.size(); i++) {
				FabuList fa = fabulist.get(i);
				List<Picture> listObj3 = JsonUtil.string2Obj(fa.getPictureUrl(), List.class, Picture.class);
				Picture picture = listObj3.get(0);
				fa.setPictureUrl(picture.getPictureUrl());
				if(fa.getServiceDetailed().length()>13) {
					fa.setServiceDetailed(fa.getServiceDetailed().substring(0, 12)+"..");
				}
				fabulist.set(i, fa);
			}

			equipment_pagePage.setDatas(fabulist);
			return ServerResponse.createBySuccess(equipment_pagePage);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getfabutiao(Map<String, Object> params) {
	
		String releaseTypeString = params.get("releaseType").toString().trim();
//	      releaseType: '',
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);		
		}else {
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

			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}

			String fouseSizeGreaterString = params.get("fouseSizeGreater").toString().trim();
			String fouseSizeLessString = params.get("fouseSizeLess").toString().trim();
			Integer fouseSizeGreater = null;
			Integer fouseSizeLess = null;
			if(releaseType==14 || releaseType==15) {
			if (fouseSizeGreaterString != null && !fouseSizeGreaterString.equals("")) {
				fouseSizeGreater = Integer.valueOf(fouseSizeGreaterString);
			}
			if (fouseSizeLessString != null && !fouseSizeLessString.equals("")) {
				fouseSizeLess = Integer.valueOf(fouseSizeLessString);
			}
			if (fouseSizeLess != null && fouseSizeGreater != null) {
				if (fouseSizeGreater > fouseSizeLess) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjicuowu.getMessage());
				}
			}
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

			List<String> fabutiaoList = fabuMapper.getfabutiao(releaseType, detailed, releaseTitle,
					serviceType,fouseSizeGreater,fouseSizeLess, type);
			if (fabutiaoList.size() == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				fabutiaoList = fabuMapper.getfabutiao(releaseType, detailed, releaseTitle,
						serviceType,fouseSizeGreater,fouseSizeLess, type);
			}

			return ServerResponse.createBySuccess(fabutiaoList);

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getfabubyid(long id) {
		FanHui fanHui=fabuMapper.getfabubyid( id);
		if(fanHui==null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		
		RealName realName = realNameMapper.getRealName(fanHui.getUserId());
		fanHui.setContact(EncrypDES.decryptPhone(realName.getContact()));
		fanHui.setDetailed(realName.getDetailed());
		fanHui.setRealNameId(realName.getAddressDetailed());
		fanHui.setUpdateTime(realName.getCompanyName());
		fanHui.setUserId(0);
		fanHui.setUserType(FabuUtil.releaseTypeString(fanHui.getReleaseType()));
		
		Evaluate evaluate =evaluateMapper.selectEvvaluateById(fanHui.getEvaluateid());
		if(evaluate!=null) {
			evaluate.setUserId(0);
		}
				
		Map<String, Object> map=new  HashMap<String, Object>();
		map.put("fabu", fanHui);
		map.put("evaluate", evaluate);
		return ServerResponse.createBySuccess(map);
	}

	@Override
	public List<FanHui> adminGetWcall(long userId) {
		
		return fabuMapper.adminGetWcall(userId);
	}

	@Override
	public ServerResponse<Object> getquxian(long userId) {
		int cityId=realNameMapper.getcityId(userId);
		QuXian[] quXians=QuXian.values();
		List<String> respList=null;
		for (int i = 0; i < quXians.length; i++) {
			if (quXians[i].getCityDistrictCountyId() == cityId) {
				respList=quXians[i].getDistrictCountyNames();
				break;
			}
		}
		return ServerResponse.createBySuccess(respList);
	}

	//app查询除简历出租的全部发布
	@Override
	public ServerResponse<Object> getfabulista(Map<String, Object> params) {		
		//		currentPage: 0
//		pageSize: 12
//		serviceType: ""
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

		
		
		String releaseTypeString = params.get("releaseType").toString().trim();
		Integer releaseType = null;
		if (releaseTypeString != null && !releaseTypeString.equals("")) {
			releaseType = Integer.valueOf(releaseTypeString);		
		}else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
		}
		
		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		
		Integer provincesId=0;
		Integer cityId =0;
		Integer districtCountyId = 0;
		if(selectedOptions_list.size() ==3) {
			provincesId = selectedOptions_list.get(0);
		    cityId = selectedOptions_list.get(1);
			districtCountyId = selectedOptions_list.get(2);
		}else {
			RealName realName=realNameMapper.getUserRealName((long) params.get("userId"));
			provincesId = realName.getProvincesId();
		    cityId = realName.getCityId();
			districtCountyId = realName.getDistrictCountyId();
		}
		
		
			
			// 判断省市区id是否正确
			String detailed = "%" + cityMapper.checkeCity(provincesId, cityId, districtCountyId) + "%";
			
			String serviceType = params.get("serviceType").toString().trim();
			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}
			Page<FabuList> equipment_pagePage = new Page<FabuList>();
			long zongtiaoshu = fabuMapper.getfabulistano(releaseType, detailed, 
					serviceType);

			if (zongtiaoshu == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				zongtiaoshu = fabuMapper.getfabulistano(releaseType, detailed, 
						serviceType);

			}
		//	getfabulist
			equipment_pagePage.setTotalno(zongtiaoshu);
			equipment_pagePage.setPageSize(pageSize);
			equipment_pagePage.setCurrentPage(currentPage); // 当前页
			if (zongtiaoshu == 0) {
				equipment_pagePage.setDatas(null);
				return ServerResponse.createBySuccess(equipment_pagePage);

			}
			// 查询list
			List<FabuList> fabulist = fabuMapper.getfabulista(
					(currentPage - 1) * pageSize,  pageSize, releaseType, detailed, serviceType);
		
			for (int i = 0; i < fabulist.size(); i++) {
				FabuList fa = fabulist.get(i);
				List<Picture> listObj3 = JsonUtil.string2Obj(fa.getPictureUrl(), List.class, Picture.class);
				Picture picture = listObj3.get(0);
				fa.setPictureUrl(picture.getPictureUrl());
				if(fa.getServiceDetailed().length()>19) {
					fa.setServiceDetailed(fa.getServiceDetailed().substring(0, 18)+"..");
				}
				fabulist.set(i, fa);
			}
			equipment_pagePage.setDatas(fabulist);
			return ServerResponse.createBySuccess(equipment_pagePage);
	}

}
