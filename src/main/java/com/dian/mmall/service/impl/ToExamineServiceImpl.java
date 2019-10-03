package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.OrderUserMapper;
import com.dian.mmall.dao.PeixunMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.DepartmentStoreMapper;
import com.dian.mmall.dao.releaseDao.EquipmentMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.dao.releaseDao.WineAndTablewareMapper;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.gongfu.DepartmentStore;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.qianyue.Peixun;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.OrderUser;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.ToExamineService;
import com.dian.mmall.service.UserAccountService;
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
@Service("toExamineService")
public class ToExamineServiceImpl implements ToExamineService {
	@Autowired
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;
	@Autowired
	private ReleaseWelfareMapper releaseWelfareMapper;
	@Autowired
	private ResumeMapper resumeMapper;
	@Autowired
	private RentMapper rentMapper;
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	@Autowired
	private EquipmentMapper equipmentMapper;
	@Autowired
	private WineAndTablewareMapper wineAndTablewareMapper;
	@Autowired
	private DepartmentStoreMapper departmentStoreMapper;
	@Autowired
	private PeixunMapper peixunMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private OrderUserMapper orderUserMapper;
	@Autowired
	private UserAccountService userAccountService;

	// 全部审核
	public ServerResponse<String> examineAll(User user, Map<String, Object> params) {
		String userId = params.get("userId").toString().trim();
		String id = params.get("id").toString().trim();
		if (userId == null || userId.contentEquals("") || id == null || id.contentEquals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidhuoshenpixiangbucunzi.getMessage());
		}
		int authentiCationStatus = Integer.valueOf(params.get("authentiCationStatus").toString().trim());
		if (authentiCationStatus != 2 && authentiCationStatus != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}

		long user_id = Long.valueOf(userId);
		long releaseWelfareId = Long.valueOf(id);
		params.put("userId", user_id);
		params.put("id", releaseWelfareId);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		params.put("examineTime", formatter.format(new Date()));
		params.put("examineName", user.getUsername());

		int resultCount = 0;

		String tabuleTypeString = params.get("tabuleType").toString().trim();
		if (tabuleTypeString == null || tabuleTypeString.contentEquals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		int tabuleType = Integer.valueOf(tabuleTypeString);
		params.remove("tabuleType");
		// 先判断是否需要审批审批类型
		if (tabuleType == 18 || tabuleType == 7 || tabuleType == 12) {
			String isServiceTypeString = params.get("isServiceType").toString().trim();
			if (isServiceTypeString == null || isServiceTypeString.contentEquals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixinbixuan.getMessage());
			}
			int isServiceType = Integer.valueOf(isServiceTypeString);

			String serviceTypeIdString = params.get("serviceTypeId").toString().trim();
			if (serviceTypeIdString == null || serviceTypeIdString.contentEquals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shangpinfuwuleixingidnull.getMessage());
			}
			long serviceTypeId = Integer.valueOf(serviceTypeIdString);
			if (authentiCationStatus == 2) {
				if (isServiceType == 3) {
					return ServerResponse.createByErrorMessage(ResponseMessage.shenhefuwuleixin.getMessage());
				}
			}

			int result = 0;
			// 不管发布状态是否通过，只要服务类型通过就去更新服务类型库
			if (serviceTypeId != -1) {
				if ((isServiceType == 2 || isServiceType == 3)) {
					ServiceType serviceType = new ServiceType();
					serviceType.setId(serviceTypeId);
					serviceType.setAuthentiCationStatus(isServiceType);
					serviceType.setExamineName(user.getUsername());
					serviceType.setExamineTime(formatter.format(new Date()));
					result = serviceTypeMapper.updatebyId(serviceType);
					if (result == 0) {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.shangpinfuwuleixluokushibai.getMessage());
					} else {
						serviceTypeMapper.deletebyId(serviceType);
					}

				} else {
					result = serviceTypeMapper.selectbyId(serviceTypeId);
					if (result == 0) {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.shangpinleixinchaxunshibai.getMessage());
					}
				}
			} else {
				if (isServiceType == 1 || isServiceType == 2 || authentiCationStatus == 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.fuwuleixinIdcuowu.getMessage());
				}
			}
		}

		if (authentiCationStatus == 2) {
			params.put("welfareStatus", 1);
			params.put("authentiCationStatus", 2);

		} else if (authentiCationStatus == 3) {
			String authentiCationFailure = params.get("authentiCationFailure").toString().trim();
			if (authentiCationFailure == null || authentiCationFailure.contentEquals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
			}
			params.put("authentiCationStatus", 3);
			params.put("welfareStatus", 4);
			params.put("authentiCationFailure", authentiCationFailure);
		}

		params.remove("serviceTypeId");
		params.remove("isServiceType");

		if (tabuleType == 13) {
			MenuAndRenovationAndPestControl releaseWelfare = (MenuAndRenovationAndPestControl) BeanMapConvertUtil
					.convertMap(MenuAndRenovationAndPestControl.class, params);
			resultCount = menuAndRenovationAndPestControlMapper.examineMrp(releaseWelfare);
		} else if (tabuleType == 14) {
			Rent rent = (Rent) BeanMapConvertUtil.convertMap(Rent.class, params);
			resultCount = rentMapper.examineResume(rent);
		} else if (tabuleType == 30) {
			ReleaseWelfare releaseWelfare = (ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class,
					params);
			resultCount = releaseWelfareMapper.examineReleaseWelfare(releaseWelfare);
		} else if (tabuleType == 31) {
			Resume resume = (Resume) BeanMapConvertUtil.convertMap(Resume.class, params);
			resultCount = resumeMapper.examineResume(resume);
		} else if (tabuleType == 18) {
			// 正常落库即可 和菜 共用一个
			Equipment equipment = (Equipment) BeanMapConvertUtil.convertMap(Equipment.class, params);
			resultCount = equipmentMapper.examineEquipment(equipment);
		} else if (tabuleType == 7) {
			// 正常落库即可 和菜 共用一个
			WineAndTableware wineAndTableware = (WineAndTableware) BeanMapConvertUtil.convertMap(WineAndTableware.class,
					params);
			resultCount = wineAndTablewareMapper.examineEquipment(wineAndTableware);
		} else if (tabuleType == 12) {
			DepartmentStore departmentStore = (DepartmentStore) BeanMapConvertUtil.convertMap(DepartmentStore.class,
					params);
			resultCount = departmentStoreMapper.examineDepartmentStore(departmentStore);
		}

		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		}
		return ServerResponse.createBySuccessMessage(ResponseMessage.shenpishenggong.getMessage());
	}

	// 查询培训地址
	@Override
	public ServerResponse<Object> getAddressDetailed(Map<String, Object> params) {
		String detailed = params.get("detailed").toString().trim();
		if (detailed == null || detailed.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		String addressDetailed = params.get("addressDetailed").toString().trim();
		if (addressDetailed != null && !addressDetailed.equals("")) {
			addressDetailed = "%" + addressDetailed + "%";
		}

		List<String> addressDetailedList = peixunMapper.getAddressDetailed(detailed, addressDetailed);
		return ServerResponse.createBySuccess(addressDetailedList);
	}

	@Override
	public ServerResponse<Object> getAccurateressDetailed(Map<String, Object> params) {

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
		if (detailed == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.bixuxianxuanzedizhi.getMessage());
		}

		params.put("detailed", detailed);
		return getAddressDetailed(params);

	}

	// 创建培训地址
	@Override
	public ServerResponse<String> createAddressDetailed(String username, Map<String, Object> params) {
		Peixun peixun = new Peixun();
		peixun.setAddressDetailed(params.get("addressDetailed").toString().trim());
		peixun.setConsigneeName(params.get("consigneeName").toString().trim());
		peixun.setContact(params.get("contact").toString().trim());
		peixun.setDetailed(params.get("detailed").toString().trim());
		peixun.setExamineName(username);
		peixun.setExamineTime(DateTimeUtil.dateToAll());
		int result = peixunMapper.createAddressDetailed(peixun);
		if (result > 0) {
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		}

	}

	@Override
	public ServerResponse<String> admin_create_orderUser(String examineName, Map<String, Object> params) {

		String isReceipString = params.get("isReceipt").toString().trim();
		if (isReceipString == null || isReceipString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
		}
		Integer isReceipt = Integer.valueOf(isReceipString);

		if (isReceipt == 2) { // 通过

			ServerResponse<Object> serverResponse = check_evaluate(examineName, params, 1);
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
			Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
			map.put("authentiCationStatus", 1);
			map.put("welfareStatus", 4);
			OrderUser orderUser = (OrderUser) BeanMapConvertUtil.convertMap(OrderUser.class, map);
			// {result=true, message=验证通过} 返回结果
			Map<String, Object> checknullMap = AnnotationDealUtil.validate(orderUser);
			if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
				// 校验 账户信息
				Map<String, Object> userAccount = new HashMap<String, Object>();

				String accountName = params.get("accountName").toString().trim();
				String bankCard = params.get("bankCard").toString().trim();
				String alipay = params.get("alipay").toString().trim();

				String availableAmount = params.get("availableAmount").toString().trim();

				if ((bankCard == null || bankCard.equals("")) && (alipay == null || alipay.equals(""))) {
					return ServerResponse.createByErrorMessage(ResponseMessage.tuizhibaojinkaxinx.getMessage());
				}

				if (accountName == null || accountName.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.zhanghuxingmingnull.getMessage());
				}

				if (availableAmount == null || availableAmount.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.zhibaojinbunnegnull.getMessage());
				}
				long availableAmountLong = Long.valueOf(availableAmount);

				if (availableAmountLong < 300) {
					return ServerResponse.createByErrorMessage(ResponseMessage.zhibaojinxioyue.getMessage());
				}
				userAccount.put("availableAmount", availableAmountLong);
				userAccount.put("consigneeName", map.get("consigneeName"));
				userAccount.put("bankCard", bankCard);
				userAccount.put("alipay", alipay);
				userAccount.put("createTime", map.get("updateTime"));
				userAccount.put("realnameId", map.get("realnameId"));
				userAccount.put("userId", map.get("userId"));

				// 账户信息校验添加完成————————上

				Map<String, Object> realNameMap = new HashMap<String, Object>();
				realNameMap.put("addressDetailed", map.get("addressDetailed").toString());
				realNameMap.put("companyName", map.get("companyName").toString());

				realNameMap.put("consigneeName", map.get("consigneeName").toString());
				realNameMap.put("contact", map.get("contact").toString());
				realNameMap.put("examineAddReceiptName", examineName);
				realNameMap.put("id", map.get("realnameId"));
				realNameMap.put("isReceip", isReceipt);

				RealName realName = (RealName) BeanMapConvertUtil.convertMap(RealName.class, realNameMap);

				// 创建接单商户
				int count = orderUserMapper.admin_create_orderUser(orderUser);
				if (count == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
				// 创建用户账户 新
				ServerResponse<String> userAccountServiceResponse = userAccountService
						.admin_create_userAccount(userAccount);

				// 更新实名库

				count = realNameMapper.admin_set_addOrder(realName);
				// TODO创建流水

				return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
			} else if ((boolean) checknullMap.get("result") == false) {
				return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
			} else {
				return ServerResponse.createByErrorMessage("系统异常稍后重试");
			}
		} else if (isReceipt == 6) { // 不通过
			String authentiCationFailure = params.get("authentiCationFailure").toString().trim();
			if (authentiCationFailure == null || authentiCationFailure.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
			}
			long realnameId = Long.valueOf(params.get("id").toString().trim());
			RealName realName = new RealName();
			realName.setId(realnameId);
			realName.setAuthentiCationFailure(authentiCationFailure);
			realName.setIsReceipt(isReceipt);
			realName.setExamineAddReceiptName(examineName);
			int count = realNameMapper.admin_set_addOrder(realName);
			if (count != 0) {
				return ServerResponse.createBySuccess();
			}
			return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
		}
	}
	// 各种校验

	public ServerResponse<Object> check_evaluate(String examineName, Map<String, Object> params, int type) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致
		long userId = Long.valueOf(params.get("userId").toString().trim());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);

		String dateString = DateTimeUtil.dateToAll();
		long realnameId = Long.valueOf(params.get("id").toString().trim());
		RealName realName = realNameMapper.admin_select_signingOrderById(realnameId);
		if (realName == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouchaxundaoshimingxinxi.getMessage());
		}
		map.put("realnameId", realnameId);
		if (type == 1) {
			map.put("createTime", dateString);
			map.put("authentiCationStatus", 1);
			map.put("branch", 100); // 新创建默认100分 以后减分

		}

		// 判断电话
		String contact = params.get("contact").toString().trim();
		// 判断手机号是否合法
		ServerResponse<String> serverContact = LegalCheck.legalCheckMobilePhone(contact);
		if (serverContact.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverContact.getMsg());
		}

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		String detailed = null;
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		}
		map.put("contact", EncrypDES.encryptPhone(contact));
		map.put("consigneeName", params.get("consigneeName").toString().trim());
		map.put("companyName", params.get("companyName").toString().trim());
		map.put("addressDetailed", params.get("addressDetailed").toString().trim());
		map.put("detailed", detailed);
		map.put("delivery", params.get("delivery").toString().trim());

		// 判断电话
		String urgentContact = params.get("urgentContact").toString().trim();
		// 判断手机号是否合法
		if (urgentContact != null && !urgentContact.equals("")) {
			serverContact = LegalCheck.legalCheckMobilePhone(urgentContact);
			if (serverContact.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverContact.getMsg());
			}
			map.put("urgentContact", urgentContact);
		}
		map.put("urgentName", params.get("urgentName").toString().trim());
		map.put("licenseUrl", params.get("licenseUrl").toString().trim());

		map.put("licenseEndTime", params.get("licenseEndTime").toString().trim());
		map.put("healthyEndTime", params.get("healthyEndTime").toString().trim());
		map.put("updateTime", dateString);
		map.put("userType", Integer.valueOf(params.get("userType").toString().trim()));
		map.put("contractNo", params.get("contractNo").toString().trim());
		map.put("examineTime", dateString);
		map.put("examineName", examineName);
		return ServerResponse.createBySuccess(map);

	}

}
