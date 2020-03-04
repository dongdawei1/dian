package com.dian.mmall.service.impl;

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
import com.dian.mmall.common.liushui.Payment;
import com.dian.mmall.common.liushui.ReceiptsAndPayments;
import com.dian.mmall.common.liushui.TransactionType;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.OrderUserMapper;
import com.dian.mmall.dao.PeixunMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.DepartmentStoreMapper;
import com.dian.mmall.dao.releaseDao.EquipmentMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.RentMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.dao.releaseDao.WholesaleCommodityMapper;
import com.dian.mmall.dao.releaseDao.WineAndTablewareMapper;
import com.dian.mmall.pojo.CreateGanggaoVo;
import com.dian.mmall.pojo.Liushui;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.gongfu.DepartmentStore;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.pingjia.Evaluate;
import com.dian.mmall.pojo.qianyue.Peixun;
import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.OrderUser;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.LiushuiService;
import com.dian.mmall.service.ToExamineService;
import com.dian.mmall.service.UserAccountService;
import com.dian.mmall.service.release.DepartmentStoreService;
import com.dian.mmall.service.release.EquipmentService;
import com.dian.mmall.service.release.FoodAndGrainService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.service.release.RentService;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.service.release.WholesaleCommodityService;
import com.dian.mmall.service.release.WineAndTablewareService;
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
	@Autowired
	private LiushuiService liushuiService;
	@Autowired
	private EvaluateMapper evaluateMapper;

	@Autowired
	private WholesaleCommodityMapper wholesaleCommodityMapper;

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
		} else if (tabuleType == 35) {
			WholesaleCommodity wholesaleCommodity = (WholesaleCommodity) BeanMapConvertUtil
					.convertMap(WholesaleCommodity.class, params);
			resultCount = wholesaleCommodityMapper.examineWholesaleCommodity(wholesaleCommodity);
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
				long availableAmountLong = Long.valueOf(availableAmount) * 100;

				if (availableAmountLong < 30000) {
					return ServerResponse.createByErrorMessage(ResponseMessage.zhibaojinxioyue.getMessage());
				}
				userAccount.put("availableAmount", availableAmountLong);
				userAccount.put("balance", availableAmountLong);
				userAccount.put("consigneeName", accountName);
				userAccount.put("bankCard", bankCard);
				userAccount.put("alipay", alipay);
				userAccount.put("createTime", map.get("updateTime"));
				userAccount.put("realnameId", map.get("realnameId"));
				long userId = (long) map.get("userId");
				userAccount.put("userId", userId);

				// 账户信息校验添加完成————————上

				List<Integer> selectedOptions_list = JsonUtil
						.string2Obj(params.get("selectedOptions").toString().trim(), List.class);

				Integer provincesId = selectedOptions_list.get(0);
				Integer cityId = selectedOptions_list.get(1);
				Integer districtCountyId = selectedOptions_list.get(2);

				Map<String, Object> realNameMap = new HashMap<String, Object>();
				realNameMap.put("addressDetailed", map.get("addressDetailed").toString());
				realNameMap.put("companyName", map.get("companyName").toString());

				realNameMap.put("consigneeName", map.get("consigneeName").toString());
				realNameMap.put("contact", map.get("contact").toString());
				realNameMap.put("examineAddReceiptName", examineName);
				realNameMap.put("id", map.get("realnameId"));
				realNameMap.put("isReceipt", isReceipt);
				realNameMap.put("detailed", map.get("detailed"));
				realNameMap.put("provincesId", provincesId);
				realNameMap.put("cityId", cityId);
				realNameMap.put("districtCountyId", districtCountyId);

				RealName realName = (RealName) BeanMapConvertUtil.convertMap(RealName.class, realNameMap);

				OrderUser OrderUser1 = orderUserMapper.getOrderUserById(userId);
				if (OrderUser1 == null) {
					int count = orderUserMapper.admin_create_orderUser(orderUser);
					if (count == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}
				} else {
					int authentiCationStatus = OrderUser1.getAuthentiCationStatus();
					if (authentiCationStatus == 1) {
						return ServerResponse.createByErrorMessage(ResponseMessage.yijingshi.getMessage());
					}
					int count = orderUserMapper.updateOrderUser(orderUser);
					if (count == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}
				}
				// 查看有无账户
				int userAccountCount = userAccountService.admin_select_userAccount_byId(userId);

				ServerResponse<String> userAccountServiceResponse = null;
				if (userAccountCount == 0) {
					// 创建用户账户 新
					userAccountServiceResponse = userAccountService.admin_create_userAccount(userAccount);

				} else {
					userAccountServiceResponse = userAccountService.admin_update_userAccount(userAccount);
				}

				if (userAccountServiceResponse != null) {
					if (userAccountServiceResponse.getStatus() != 0) {
						return ServerResponse.createByErrorMessage(userAccountServiceResponse.getMsg());
					}
				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.yonghuzhanghu.getMessage());
				}
				// 创建流水
				Liushui liushui = new Liushui();
				liushui.setUserId(userId);
				liushui.setReceivablesUserId(userId);
				liushui.setAmount(availableAmountLong);
				liushui.setPayment(Payment.chongzhi.getPayment());
				liushui.setTransactionType(TransactionType.zhibaojin.getTransactionType());
				liushui.setReceiptsAndPayments(ReceiptsAndPayments.xianxia.getReceiptsAndPayments());
				liushui.setAccountNo(Const.XITONGSHOUKUAN); // 系统收款账户
				liushui.setCreateTime(map.get("updateTime").toString());
				// TODO 后期优化
				liushui.setLiushuiStatus(1); // 1 初始，2 失败，3成功
				userAccountServiceResponse = null;
				userAccountServiceResponse = liushuiService.create_liushui(liushui);

				if (userAccountServiceResponse != null) {
					if (userAccountServiceResponse.getStatus() != 0) {
						return ServerResponse.createByErrorMessage(userAccountServiceResponse.getMsg());
					}
				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.chuangjianliushuishibai.getMessage());
				}

				// 更新实名库
				realNameMapper.admin_set_addOrder(realName);

				// 创建 接单用户评价
				Evaluate evaluate = new Evaluate();
				evaluate.setPermissionid(-1);
				evaluate.setUserId(userId);
				evaluate.setReleaseid(-1);
				evaluateMapper.adminAddOrderCreateEvaluate(evaluate);

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
			map.put("urgentContact", EncrypDES.encryptPhone(urgentContact));
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

	@Autowired
	private ReleaseWelfareService releaseWelfareService;

	@Autowired
	private RentService rentService;

	@Autowired
	private BunnerService bunnerService;

	@Autowired
	private ResumeService resumeService;
	@Autowired
	private FoodAndGrainService foodAndGrainService;
	@Autowired
	private DepartmentStoreService departmentStoreService;
	@Autowired
	private WineAndTablewareService wineAndTablewareService;
	@Autowired
	private EquipmentService equipmentService;
	private int zhiweib = 1;
	private int chuzub = 2;
	private int jianlib = 3;
	private int cailingshoub = 4;
	private int baihuob = 5;
	private int jiub = 6;
	private int dianqib = 7;

	@Override
	public ServerResponse<Object> getUserCreate(User user) {
		long userId = user.getId();
		int role = user.getRole();
		CreateGanggaoVo responseVo = new CreateGanggaoVo();

		List<CreateGanggaoVo> listVos = new ArrayList<CreateGanggaoVo>();
		/*
		 * dibubunner 表 permissionid
		 * 
		 * //招聘permissionid==30 店面/窗口出租permissionid==14
		 * 
		 * private boolean quxiaoguanggao=false; private boolean bianjiguanggao=false;
		 * private boolean tianjiaguanggao=false; private boolean deletefabu=true;
		 */

		int index = 0;
		if (role == 2) {
			// 餐饮/酒店等企业可以发布 招聘，窗口出租
			// 招聘permissionid==30
			List<ReleaseWelfare> rwList = releaseWelfareService.adminGetzZWall(userId);
			if (rwList.size() > 0) {
				for (int a = 0; a < rwList.size(); a++) {
					CreateGanggaoVo createGanggaoRw = new CreateGanggaoVo();
					createGanggaoRw.setTablenameid(zhiweib);
					createGanggaoRw.setPermissionid(Const.ZHIWEIP);
					createGanggaoRw.setPermissionName("招聘信息");
					if (bunnerService.getguanggaocount(userId, createGanggaoRw.getPermissionid()) > 0) {
						createGanggaoRw.setQuxiaoguanggao(true);
						createGanggaoRw.setBianjiguanggao(true);
						createGanggaoRw.setDeletefabu(false);
					} else {
						createGanggaoRw.setTianjiaguanggao(true);
					}
					createGanggaoRw.setDataObject(rwList.get(a));
					listVos.set(index, createGanggaoRw);
					index++;
				}
			}
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		} else if (role == 3) {
			// 厨具电器二手设备等
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

			// 电器设备
			List<Equipment> eqList = equipmentService.adminGetEqall(userId);
			if (eqList.size() > 0) {
				for (int a = 0; a < eqList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(dianqib);
					Equipment rent = eqList.get(a);
					if (rent.getReleaseType() == Const.SHEBEIXIUP) {
						createGanggaoRe.setPermissionid(Const.SHEBEIXIUP);
						createGanggaoRe.setPermissionName("电器设备维修");
					} else if (rent.getReleaseType() == Const.SHEBEIMAI) {
						createGanggaoRe.setPermissionid(Const.SHEBEIMAI);
						createGanggaoRe.setPermissionName("电器设备销售新");
					} else if (rent.getReleaseType() == Const.SHEBEIJIU) {
						createGanggaoRe.setPermissionid(Const.SHEBEIJIU);
						createGanggaoRe.setPermissionName("电器设备二手");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		} else if (role == 4) {
			// 菜米蛋禽等零售
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 简历
			List<Resume> esumeList = resumeService.adminGetResumeall(userId);
			if (esumeList.size() > 0) {
				for (int a = 0; a < esumeList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(jianlib);
					createGanggaoRe.setPermissionid(Const.JIANLIP);
					createGanggaoRe.setPermissionName("个人简历（需手动加图）");

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(esumeList.get(a));
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

			// 零售
			List<FoodAndGrain> fgList = foodAndGrainService.adminGetFgall(userId);
			if (fgList.size() > 0) {
				for (int a = 0; a < fgList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(cailingshoub);
					FoodAndGrain foodAndGrain = fgList.get(a);
					if (foodAndGrain.getReleaseType() == Const.SHUCAIP) {
						createGanggaoRe.setPermissionid(Const.SHUCAIP);
						createGanggaoRe.setPermissionName("零售蔬菜");
					} else if (foodAndGrain.getReleaseType() == Const.LIANGYOUP) {
						createGanggaoRe.setPermissionid(Const.LIANGYOUP);
						createGanggaoRe.setPermissionName("零售粮油");
					} else if (foodAndGrain.getReleaseType() == Const.TIAOLIAO) {
						createGanggaoRe.setPermissionid(Const.TIAOLIAO);
						createGanggaoRe.setPermissionName("零售调料/副食");
					} else if (foodAndGrain.getReleaseType() == Const.QINGJIEP) {
						createGanggaoRe.setPermissionid(Const.QINGJIEP);
						createGanggaoRe.setPermissionName("零售清洁用品");

					} else if (foodAndGrain.getReleaseType() == Const.ZHUOYIP) {
						createGanggaoRe.setPermissionid(Const.ZHUOYIP);
						createGanggaoRe.setPermissionName("零售桌椅");
					} else if (foodAndGrain.getReleaseType() == Const.SHUICHAN) {
						createGanggaoRe.setPermissionid(Const.SHUICHAN);
						createGanggaoRe.setPermissionName("零售水产蛋禽");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(foodAndGrain);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		} else if (role == 5) {
			// 酒水/消毒餐具 招聘 店面出租
			// 招聘permissionid==30
			List<ReleaseWelfare> rwList = releaseWelfareService.adminGetzZWall(userId);
			if (rwList.size() > 0) {
				for (int a = 0; a < rwList.size(); a++) {
					CreateGanggaoVo createGanggaoRw = new CreateGanggaoVo();
					createGanggaoRw.setTablenameid(zhiweib);
					createGanggaoRw.setPermissionid(Const.ZHIWEIP);
					if (bunnerService.getguanggaocount(userId, createGanggaoRw.getPermissionid()) > 0) {
						createGanggaoRw.setQuxiaoguanggao(true);
						createGanggaoRw.setBianjiguanggao(true);
						createGanggaoRw.setDeletefabu(false);
					} else {
						createGanggaoRw.setTianjiaguanggao(true);
					}
					createGanggaoRw.setDataObject(rwList.get(a));
					listVos.set(index, createGanggaoRw);
					index++;
				}
			}

			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 消毒餐具酒水
			List<WineAndTableware> wtList = wineAndTablewareService.adminGetWtall(userId);
			if (wtList.size() > 0) {
				for (int a = 0; a < wtList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(jiub);
					WineAndTableware wt = wtList.get(a);

					if (wt.getReleaseType() == Const.JIUSHUIP) {
						createGanggaoRe.setPermissionid(Const.JIUSHUIP);
						createGanggaoRe.setPermissionName("酒水饮料");
					} else if (wt.getReleaseType() == Const.CANJUP) {
						createGanggaoRe.setPermissionid(Const.CANJUP);
						createGanggaoRe.setPermissionName("消毒餐具");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(wt);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		} else if (role == 6) {
			// 专出租门脸和窗口
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		} else if (role == 11) {
			// 找工作 简历
			List<Resume> esumeList = resumeService.adminGetResumeall(userId);
			if (esumeList.size() > 0) {
				for (int a = 0; a < esumeList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(jianlib);
					createGanggaoRe.setPermissionid(Const.JIANLIP);
					createGanggaoRe.setPermissionName("个人简历（需手动加图）");

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(esumeList.get(a));
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
		}
		// 工服101/百货102/绿植103/装饰用品104
//		public static final int GONGFUP=101;
//		public static final int BAIHUOP=102;
//		public static final int LVZHIP=103;
//		public static final int ZHUANGSHIP=104;
		else if (role == 12) {
			// 工服百货
			List<DepartmentStore> esumeList = departmentStoreService.adminGetDsall(userId);
			if (esumeList.size() > 0) {
				for (int a = 0; a < esumeList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(baihuob);
					DepartmentStore departmentStore = esumeList.get(a);
					if (departmentStore.getReleaseType() == Const.GONGFUP) {
						createGanggaoRe.setPermissionid(Const.GONGFUP);
						createGanggaoRe.setPermissionName("市场工服");
					} else if (departmentStore.getReleaseType() == Const.BAIHUOP) {
						createGanggaoRe.setPermissionid(Const.BAIHUOP);
						createGanggaoRe.setPermissionName("市场百货");
					} else if (departmentStore.getReleaseType() == Const.LVZHIP) {
						createGanggaoRe.setPermissionid(Const.LVZHIP);
						createGanggaoRe.setPermissionName("市场绿植");
					} else if (departmentStore.getReleaseType() == Const.ZHUANGSHIP) {
						createGanggaoRe.setPermissionid(Const.ZHUANGSHIP);
						createGanggaoRe.setPermissionName("市场装饰用品");
					}
					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(departmentStore);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
		} else if (role == 13) {
			// 菜米禽蛋批发
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		}

		else if (role == 1) {
			// 酒水/消毒餐具 招聘
			// 招聘permissionid==30
			List<ReleaseWelfare> rwList = releaseWelfareService.adminGetzZWall(userId);
			if (rwList.size() > 0) {
				for (int a = 0; a < rwList.size(); a++) {
					CreateGanggaoVo createGanggaoRw = new CreateGanggaoVo();
					createGanggaoRw.setTablenameid(zhiweib);
					createGanggaoRw.setPermissionid(Const.ZHIWEIP);
					if (bunnerService.getguanggaocount(userId, createGanggaoRw.getPermissionid()) > 0) {
						createGanggaoRw.setQuxiaoguanggao(true);
						createGanggaoRw.setBianjiguanggao(true);
						createGanggaoRw.setDeletefabu(false);
					} else {
						createGanggaoRw.setTianjiaguanggao(true);
					}
					createGanggaoRw.setDataObject(rwList.get(a));
					listVos.set(index, createGanggaoRw);
					index++;
				}
			}
			// 简历
			List<Resume> esumeList = resumeService.adminGetResumeall(userId);
			if (esumeList.size() > 0) {
				for (int a = 0; a < esumeList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(jianlib);
					createGanggaoRe.setPermissionid(Const.JIANLIP);
					createGanggaoRe.setPermissionName("个人简历（需手动加图）");

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(esumeList.get(a));
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 窗口出租
			List<Rent> rentList = rentService.adminGetRentall(userId);
			if (rentList.size() > 0) {
				for (int a = 0; a < rentList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(chuzub);
					Rent rent = rentList.get(a);
					if (rent.getReleaseType() == Const.DIANMIANP) {
						createGanggaoRe.setPermissionid(Const.DIANMIANP);
						createGanggaoRe.setPermissionName("出租店面");
					} else if (rent.getReleaseType() == Const.TANWEIP) {
						createGanggaoRe.setPermissionid(Const.TANWEIP);
						createGanggaoRe.setPermissionName("出租窗口");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 零售
			List<FoodAndGrain> fgList = foodAndGrainService.adminGetFgall(userId);
			if (fgList.size() > 0) {
				for (int a = 0; a < fgList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(cailingshoub);
					FoodAndGrain foodAndGrain = fgList.get(a);
					if (foodAndGrain.getReleaseType() == Const.SHUCAIP) {
						createGanggaoRe.setPermissionid(Const.SHUCAIP);
						createGanggaoRe.setPermissionName("零售蔬菜");
					} else if (foodAndGrain.getReleaseType() == Const.LIANGYOUP) {
						createGanggaoRe.setPermissionid(Const.LIANGYOUP);
						createGanggaoRe.setPermissionName("零售粮油");
					} else if (foodAndGrain.getReleaseType() == Const.TIAOLIAO) {
						createGanggaoRe.setPermissionid(Const.TIAOLIAO);
						createGanggaoRe.setPermissionName("零售调料/副食");
					} else if (foodAndGrain.getReleaseType() == Const.QINGJIEP) {
						createGanggaoRe.setPermissionid(Const.QINGJIEP);
						createGanggaoRe.setPermissionName("零售清洁用品");

					} else if (foodAndGrain.getReleaseType() == Const.ZHUOYIP) {
						createGanggaoRe.setPermissionid(Const.ZHUOYIP);
						createGanggaoRe.setPermissionName("零售桌椅");
					} else if (foodAndGrain.getReleaseType() == Const.SHUICHAN) {
						createGanggaoRe.setPermissionid(Const.SHUICHAN);
						createGanggaoRe.setPermissionName("零售水产蛋禽");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(foodAndGrain);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 找工作 简历
			List<DepartmentStore> dsumeList = departmentStoreService.adminGetDsall(userId);
			if (dsumeList.size() > 0) {
				for (int a = 0; a < dsumeList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(baihuob);
					DepartmentStore departmentStore = dsumeList.get(a);
					if (departmentStore.getReleaseType() == Const.GONGFUP) {
						createGanggaoRe.setPermissionid(Const.GONGFUP);
						createGanggaoRe.setPermissionName("市场工服");
					} else if (departmentStore.getReleaseType() == Const.BAIHUOP) {
						createGanggaoRe.setPermissionid(Const.BAIHUOP);
						createGanggaoRe.setPermissionName("市场百货");
					} else if (departmentStore.getReleaseType() == Const.LVZHIP) {
						createGanggaoRe.setPermissionid(Const.LVZHIP);
						createGanggaoRe.setPermissionName("市场绿植");
					} else if (departmentStore.getReleaseType() == Const.ZHUANGSHIP) {
						createGanggaoRe.setPermissionid(Const.ZHUANGSHIP);
						createGanggaoRe.setPermissionName("市场装饰用品");
					}
					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(departmentStore);
					listVos.set(index, createGanggaoRe);
					index++;
				}

			}

			// 工服百货
			List<DepartmentStore> deList = departmentStoreService.adminGetDsall(userId);
			if (deList.size() > 0) {
				for (int a = 0; a < deList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(baihuob);
					DepartmentStore departmentStore = deList.get(a);
					if (departmentStore.getReleaseType() == Const.GONGFUP) {
						createGanggaoRe.setPermissionid(Const.GONGFUP);
						createGanggaoRe.setPermissionName("市场工服");
					} else if (departmentStore.getReleaseType() == Const.BAIHUOP) {
						createGanggaoRe.setPermissionid(Const.BAIHUOP);
						createGanggaoRe.setPermissionName("市场百货");
					} else if (departmentStore.getReleaseType() == Const.LVZHIP) {
						createGanggaoRe.setPermissionid(Const.LVZHIP);
						createGanggaoRe.setPermissionName("市场绿植");
					} else if (departmentStore.getReleaseType() == Const.ZHUANGSHIP) {
						createGanggaoRe.setPermissionid(Const.ZHUANGSHIP);
						createGanggaoRe.setPermissionName("市场装饰用品");
					}
					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(departmentStore);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}
			// 电器设备
			List<Equipment> eqList = equipmentService.adminGetEqall(userId);
			if (eqList.size() > 0) {
				for (int a = 0; a < eqList.size(); a++) {
					CreateGanggaoVo createGanggaoRe = new CreateGanggaoVo();
					createGanggaoRe.setTablenameid(dianqib);
					Equipment rent = eqList.get(a);
					if (rent.getReleaseType() == Const.SHEBEIXIUP) {
						createGanggaoRe.setPermissionid(Const.SHEBEIXIUP);
						createGanggaoRe.setPermissionName("电器设备维修");
					} else if (rent.getReleaseType() == Const.SHEBEIMAI) {
						createGanggaoRe.setPermissionid(Const.SHEBEIMAI);
						createGanggaoRe.setPermissionName("电器设备销售新");
					} else if (rent.getReleaseType() == Const.SHEBEIJIU) {
						createGanggaoRe.setPermissionid(Const.SHEBEIJIU);
						createGanggaoRe.setPermissionName("电器设备二手");
					}

					if (bunnerService.getguanggaocount(userId, createGanggaoRe.getPermissionid()) > 0) {
						createGanggaoRe.setQuxiaoguanggao(true);
						createGanggaoRe.setBianjiguanggao(true);
						createGanggaoRe.setDeletefabu(false);
					} else {
						createGanggaoRe.setTianjiaguanggao(true);
					}
					createGanggaoRe.setDataObject(rent);
					listVos.set(index, createGanggaoRe);
					index++;
				}
			}

		}
		return null;
	}
}
