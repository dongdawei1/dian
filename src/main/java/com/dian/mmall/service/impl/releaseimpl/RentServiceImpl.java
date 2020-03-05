package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("rentService")
public class RentServiceImpl implements RentService {
	@Autowired
	private RentMapper rentMapper;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private GetPublishingsService getPublishingsService;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private BunnerService bunnerService;

	@Override
	public ServerResponse<String> create_rent(User user, Map<String, Object> params) {

		ServerResponse<Object> serverResponse = check_evaluate(user, params, 1);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);
		Rent rent = (Rent) BeanMapConvertUtil.convertMap(Rent.class, map);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(rent);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = rentMapper.create_rent(rent);
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
		map.put("termOfValidity", DateTimeUtil.a_few_days_later0(90));
		
		String createTime = DateTimeUtil.dateToAll();

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != Const.DIANMIANP && releaseType != Const.TANWEIP) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String fouseSizeString = params.get("fouseSize").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.mianjibunnegweikong.getMessage());
		}
		int fouseSize = 0;
		try {
			fouseSize = Integer.valueOf(fouseSizeString);
			if (releaseType < 1 || releaseType > 10000) {
				return ServerResponse.createByErrorMessage(ResponseMessage.mianjibuhefa.getMessage());
			}
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.mianjibuhefa.getMessage());
		}

		map.put("fouseSize", fouseSize);

		int count = 0;
		if (type == 1) { // TODO新建时才检查总数
			// 判断是否超过可以发布的总数
			count = rentMapper.countNum(releaseType, userId);
			if (count > 5) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			}
			map.put("createTime", createTime);
			map.put("releaseType", releaseType);
			// 获取用户类型
			ServerResponse<String> serverResponse = SetBean.setRole(currentUser.getRole());
			// 检查用户类型
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
			map.put("userType", serverResponse.getMsg());
		}
		map.put("updateTime", createTime);
		

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
			int getNum = PictureNum.zufangang.getNum();
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
			// 城市
//				List<Integer> selectedOptions_list=JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(), List.class);
//				if(selectedOptions_list.size()==3) {
//				Integer	provincesId=selectedOptions_list.get(0);
//				Integer	cityId=selectedOptions_list.get(1);
//				Integer   districtCountyId=selectedOptions_list.get(2);
//				   //判断省市区id是否正确
//				    if(provincesId>10000 &&  cityId>10000 && districtCountyId>10000) {
//				    	String city=cityMapper.checkeCity(provincesId,cityId,districtCountyId);
//				    	 if( city==null) {
//			 				return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
//			 			}
//				    	 map.put("detailed", city);
//			 		}else {
//			 			return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
//			 		}
//				}else {
//					return	ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
//				}

		}

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
			serverResponse = LegalCheck.legalCheckMobilePhone(contact);
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
			// map.put("contact", EncrypDES.encryptPhone(contact));
			if (!EncrypDES.encryptPhone(contact).equals(realName.getContact())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}
			map.put("consigneeName", params.get("consigneeName").toString().trim());

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}

		map.put("serviceDetailed", params.get("serviceDetailed").toString().trim());
		map.put("remarks", params.get("remarks").toString().trim());
		map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
		map.put("releaseTitle", params.get("releaseTitle").toString().trim());
		return ServerResponse.createBySuccess(map);

	}

	@Override
	public ServerResponse<Object> get_myRent_list(User user, Map<String, Object> params) {
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

		String getwelfareStatusString = params.get("welfareStatus").toString().trim();
		Integer welfareStatus = null;
		if (getwelfareStatusString != null && !getwelfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(getwelfareStatusString);
		}

		Page<Rent> rent = new Page<Rent>();

		long zongtiaoshu = rentMapper.get_myRent_list_no(welfareStatus, user.getId());

		rent.setTotalno(zongtiaoshu);
		rent.setPageSize(pageSize);
		rent.setCurrentPage(currentPage); // 当前页

		List<Rent> list_rentall = rentMapper.get_myRent_list((currentPage - 1) * pageSize, pageSize, welfareStatus,
				user.getId());

		RealName realName = realNameMapper.getRealName(user.getId());
		for (int a = 0; a < list_rentall.size(); a++) {
			Rent rent_no = list_rentall.get(a);
			rent_no.setContact(EncrypDES.decryptPhone(realName.getContact()));
			rent_no.setDetailed(realName.getDetailed());
			rent_no.setRealNameId(realName.getAddressDetailed());
			rent_no.setCompanyName(realName.getCompanyName());
		}

		rent.setDatas(list_rentall);
		return ServerResponse.createBySuccess(rent);
	}

	@Override
	public ServerResponse<Object> adminMent(Map<String, Object> params) {
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

		Page<Rent> rent_pagePage = new Page<Rent>();
		long zongtiaoshu = rentMapper.adminMent_no(contact);
		rent_pagePage.setTotalno(zongtiaoshu);
		rent_pagePage.setPageSize(pageSize);
		rent_pagePage.setCurrentPage(currentPage); // 当前页
		List<Rent> list_rentall = rentMapper.adminMent((currentPage - 1) * pageSize, pageSize, contact);
		for (int a = 0; a < list_rentall.size(); a++) {
			Rent re = list_rentall.get(a);
			RealName realName = realNameMapper.getRealName(re.getUserId());
			re.setContact(EncrypDES.decryptPhone(realName.getContact()));
			re.setDetailed(realName.getDetailed());
			re.setRealNameId(realName.getAddressDetailed());
			re.setCompanyName(realName.getCompanyName());
			list_rentall.set(a, re);
			realName = null;
		}
		rent_pagePage.setDatas(list_rentall);
		return ServerResponse.createBySuccess(rent_pagePage);
	}

	public int getreleaseType(long id) {
		return rentMapper.getreleaseType(id);
	}
	
	@Override
	public ServerResponse<String> operation_userment(User user, Map<String, Object> params) {
		String type = params.get("type").toString().trim();
		String userId = params.get("userId").toString().trim();
		String id = params.get("id").toString().trim();
		long idLong = 0;
		if (type != null && !type.equals("") && userId != null && !userId.equals("") && id != null && !id.equals("")) {
			int type_int = Integer.valueOf(type);
			if (type_int < 1 || type_int > 9) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
			long userIdLong = Long.valueOf(userId);
			idLong = Long.valueOf(id);
			if (userIdLong != user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}
			// 有发布中或者未开始的广告不能操作
			if (bunnerService.getguanggaocount(idLong, getreleaseType(idLong)) > 0) {
				ServerResponse.createByErrorMessage(ResponseMessage.yougonggongxuanchuan.getMessage());
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = null;
			String termOfValidity = DateTimeUtil.a_few_days_later0(90);
			int result = 0;
			if (type_int == 1 || type_int == 2) {
				timeString = formatter.format(new Date());
				result = rentMapper.operation_userment(userIdLong, idLong, type_int, timeString, termOfValidity);
			} else if (type_int == 3 || type_int == 4 || type_int == 5 ) {
				timeString = formatter.format(new Date());
				result = rentMapper.operation_userment(userIdLong, idLong, type_int, timeString, termOfValidity);
			} else if (type_int == 6) {

				ServerResponse<Object> serverResponse = get_userrent_id(userIdLong, idLong);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				Rent rent = (Rent) serverResponse.getData();

				// 检查图片
				ServerResponse<String> serverResponseString = setPictureUrl(
						(ArrayList<Picture>) params.get("pictureUrl"), rent.getPictureUrl());
				if (serverResponseString.getStatus() != 0) {
					return serverResponseString;
				}
				// 检查其他输入
				serverResponse = check_evaluate(user, params, 2);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}

				Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
				map.put("createTime", rent.getCreateTime());
				map.put("id", rent.getId());
				map.put("pictureUrl", serverResponseString.getMsg());
				map.put("authentiCationStatus", 1);
				map.put("welfareStatus", 4);
				map.put("userType", rent.getUserType());
				map.put("releaseType", rent.getReleaseType());

				Rent rent_create = (Rent) BeanMapConvertUtil.convertMap(Rent.class, map);
				// {result=true, message=验证通过} 返回结果
				Map<String, Object> checknullMap = AnnotationDealUtil.validate(rent_create);
				if ((boolean) checknullMap.get("result") == true
						&& ((String) checknullMap.get("message")).equals("验证通过")) {
					result = rentMapper.update_rent(rent_create);

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

	@Override
	public ServerResponse<Object> get_userrent_id(long userId, long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		Rent rent = rentMapper.get_userrent_id(userId, id);
		if (rent == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		RealName realName = realNameMapper.getRealName(userId);
		rent.setContact(EncrypDES.decryptPhone(realName.getContact()));
		rent.setDetailed(realName.getDetailed());
		rent.setRealNameId(realName.getAddressDetailed());
		rent.setCompanyName(realName.getCompanyName());
		return ServerResponse.createBySuccess(rent);
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
	public ServerResponse<Object> getrentList(Map<String, Object> params) {
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
			if (releaseType != 14 && releaseType != 15) {
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

			String serviceDetailed = params.get("serviceDetailed").toString().trim();

			String fouseSizeGreaterString = params.get("fouseSizeGreater").toString().trim();
			Integer fouseSizeGreater = null;
			if (fouseSizeGreaterString != null && !fouseSizeGreaterString.equals("")) {
				fouseSizeGreater = Integer.valueOf(fouseSizeGreaterString);
			}

			String fouseSizeLessString = params.get("fouseSizeLess").toString().trim();
			Integer fouseSizeLess = null;
			if (fouseSizeLessString != null && !fouseSizeLessString.equals("")) {
				fouseSizeLess = Integer.valueOf(fouseSizeLessString);
			}

			if (fouseSizeLess != null && fouseSizeGreater != null) {
				if (fouseSizeGreater > fouseSizeLess) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjicuowu.getMessage());
				}
			}

			Page<Rent> rent_pagePage = new Page<Rent>();

			long zongtiaoshu = rentMapper.getrentListNo(releaseType, detailed, fouseSizeGreater, fouseSizeLess,
					serviceDetailed);

			if (zongtiaoshu == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				zongtiaoshu = rentMapper.getrentListNo(releaseType, detailed, fouseSizeGreater, fouseSizeLess,
						serviceDetailed);
			}

			rent_pagePage.setTotalno(zongtiaoshu);
			rent_pagePage.setPageSize(pageSize);
			rent_pagePage.setCurrentPage(currentPage); // 当前页
			// 查询list
			List<Rent> mrpList = rentMapper.getrentList((currentPage - 1) * pageSize, releaseType, pageSize,
					fouseSizeGreater, fouseSizeLess, detailed, serviceDetailed);
			for (int i = 0; i < mrpList.size(); i++) {

				Rent rent = mrpList.get(i);
				RealName realName = realNameMapper.getRealName(rent.getUserId());
				rent.setDetailed(realName.getDetailed());
				List<Picture> listObj3 = JsonUtil.string2Obj(rent.getPictureUrl(), List.class, Picture.class);
				Picture picture = listObj3.get(0);
				rent.setPictureUrl(picture.getPictureUrl());
				mrpList.set(i, rent);
				realName = null;
			}

			rent_pagePage.setDatas(mrpList);
			return ServerResponse.createBySuccess(rent_pagePage);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getServiceDetailedList(Map<String, Object> params) {

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			String detailed = "%" + cityMapper.checkeCity(provincesId, cityId, districtCountyId) + "%";

			String fouseSizeGreaterString = params.get("fouseSizeGreater").toString().trim();
			Integer fouseSizeGreater = null;
			if (fouseSizeGreaterString != null && !fouseSizeGreaterString.equals("")) {
				fouseSizeGreater = Integer.valueOf(fouseSizeGreaterString);
			}

			String fouseSizeLessString = params.get("fouseSizeLess").toString().trim();
			Integer fouseSizeLess = null;
			if (fouseSizeLessString != null && !fouseSizeLessString.equals("")) {
				fouseSizeLess = Integer.valueOf(fouseSizeLessString);
			}

			if (fouseSizeLess != null && fouseSizeGreater != null) {
				if (fouseSizeGreater > fouseSizeLess) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjicuowu.getMessage());
				}
			}

			String releaseTypeString = params.get("releaseType").toString().trim();
			Integer releaseType = null;
			if (releaseTypeString != null && !releaseTypeString.equals("")) {
				releaseType = Integer.valueOf(releaseTypeString);
				if (releaseType != 14 && releaseType != 15) {
					return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
				}
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunleixingbunnegweikong.getMessage());
			}

			List<String> serviceDetailedList = rentMapper.getServiceDetailedList(releaseType, detailed,
					fouseSizeGreater, fouseSizeLess);
			if (serviceDetailedList.size() == 0) {
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				serviceDetailedList = rentMapper.getServiceDetailedList(releaseType, detailed, fouseSizeGreater,
						fouseSizeLess);
			}

			return ServerResponse.createBySuccess(serviceDetailedList);

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> get_rent_id(long id) {
		if (id <= 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		Rent rent = rentMapper.get_rent_id(id);

		if (rent == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		RealName realName = realNameMapper.getRealName(rent.getUserId());
		rent.setContact(EncrypDES.decryptPhone(realName.getContact()));
		rent.setDetailed(realName.getDetailed());
		rent.setRealNameId(realName.getAddressDetailed());
		rent.setCompanyName(realName.getCompanyName());
		return ServerResponse.createBySuccess(rent);
	}

	@Override
	public List<Rent> adminGetRentall(long userId) {

		return rentMapper.adminGetRentall(userId);
	}

}