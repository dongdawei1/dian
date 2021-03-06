package com.dian.mmall.service.impl.releaseimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.visitor.functions.If;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ReleaseCount;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.zhiwei.Age;
import com.dian.mmall.common.zhiwei.Education;
import com.dian.mmall.common.zhiwei.Experience;
import com.dian.mmall.common.zhiwei.IntroductoryAward;
import com.dian.mmall.common.zhiwei.Position;
import com.dian.mmall.common.zhiwei.Salary;
import com.dian.mmall.common.zhiwei.Welfare;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.chaxuncishu.NumberOfQueriesMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.chaxuncishu.NumberOfQueries;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.impl.UserServiceImpl;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("releaseWelfareService")
public class ReleaseWelfareServiceImpl implements ReleaseWelfareService {

	@Autowired
	private ReleaseWelfareMapper releaseWelfareMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private NumberOfQueriesMapper numberOfQueriesMapper;

	@Autowired
	private GetPublishingsService getPublishingsService;

	@Autowired
	private BunnerService bunnerService;

	public ServerResponse<String> create_position(User currentUser, Map<String, Object> params) {

		ServerResponse<Object> serverResponse = check_position(currentUser, params);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		params.clear();
		params = (Map<String, Object>) serverResponse.getData();
		params.put("authentiCationStatus", 1);
		params.put("welfareStatus", 4);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = formatter.format(new Date());
		params.put("createTime", timeString);
		params.put("updateTime", timeString);
		
		ReleaseWelfare releaseWelfare = (ReleaseWelfare) BeanMapConvertUtil.convertMap(ReleaseWelfare.class, params);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(releaseWelfare);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = releaseWelfareMapper.create_position(releaseWelfare);
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}

		return ServerResponse.createBySuccessMessage(ResponseMessage.BianJiChengGong.getMessage());

	}

	// 管理员获取全部待审批的发布职位
	@Override
	public ServerResponse<Object> getReleaseWelfareAll(Map<String, Object> params) {
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

		String userName = params.get("companyName").toString().trim();
		if (userName != null && !userName.equals("")) {
			userName = "%" + userName + "%";
		}

		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		Page<ReleaseWelfare> releaseWelfare_pagePage = new Page<ReleaseWelfare>();

		long zongtiaoshu = releaseWelfareMapper.getReleaseWelfarePageno(userName, contact);

		releaseWelfare_pagePage.setTotalno(zongtiaoshu);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); // 当前页

		List<ReleaseWelfare> list_releaseWelfareall = releaseWelfareMapper
				.getReleaseWelfareAll((currentPage - 1) * pageSize, pageSize, userName, contact);

		for (int a = 0; a < list_releaseWelfareall.size(); a++) {
			ReleaseWelfare releaseWelfare = list_releaseWelfareall.get(a);
			RealName realName = realNameMapper.getRealName(releaseWelfare.getUserId());

			releaseWelfare.setContact(EncrypDES.decryptPhone(realName.getContact()));
			releaseWelfare.setDetailed(realName.getDetailed());
			releaseWelfare.setRealNameId(realName.getAddressDetailed());
			releaseWelfare.setCompanyName(realName.getCompanyName());
			list_releaseWelfareall.set(a, releaseWelfare);
			realName = null;
		}

		releaseWelfare_pagePage.setDatas(list_releaseWelfareall);
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
	}

	// 各种校验
	public ServerResponse<Object> check_position(User currentUser, Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断用户id与 tocken是否一致
		long userId = currentUser.getId();
		map.put("userId", userId);
		map.put("termOfValidity", DateTimeUtil.a_few_days_later0(30));
		// 判断是否超过可以发布的总数
		if (releaseWelfareMapper.countReleaseWelfare(userId) >= ReleaseCount.create_position.getCount()) {
			return ServerResponse.createByErrorMessage(
					ResponseMessage.fabuzongshudayu.getMessage() + ReleaseCount.create_position.getCount());
		}
		// 判断是否实名
		if (currentUser.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		// 转男女code为字符串 
		String genString = params.get("gender").toString().trim();
		if (genString == null || genString.equals("")  ) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
		}
		int gender = Integer.valueOf(genString);
		if (gender == 1) {
			map.put("gender", "男");
		} else if (gender == 2) {
			map.put("gender", "女");
		} else if (gender == 3) {
			map.put("gender", "不限");
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
		}

		// 判断是否公开电话
		String isPublishContactString = params.get("isPublishContact").toString().trim();
		if (isPublishContactString == null || isPublishContactString.equals("")  ) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
		}
		int isPublishContact = Integer.valueOf(isPublishContactString);
		if (isPublishContact != 1 && isPublishContact != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
		}
		map.put("isPublishContact", isPublishContact);
		// 判断邮箱和联系方式必须有一个公开email
		String email = params.get("email").toString().trim();
		if (isPublishContact == 2 && (email == null || email.equals("")  )) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongkaidianhuahuozheshuruyouxiang.getMessage());
		}
		map.put("email", email);

		// 检查福利输入是否合法
		List<String> listObj3 = JsonUtil.list2Obj((ArrayList<String>) params.get("welfare"), List.class, String.class);
		if (listObj3.isEmpty()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fulibunengweikong.getMessage());
		}

		StringBuffer stringBuffer = new StringBuffer();
		Welfare[] strWelfare = Welfare.values();
		for (int a1 = 0; a1 < strWelfare.length; a1++) {
			stringBuffer.append(strWelfare[a1].getWelfare());
		}

		String string = stringBuffer.toString();
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		for (int a = 0; a < listObj3.size(); a++) {
			String welfare = ".*" + listObj3.get(a) + ".*";
			boolean isMatch = Pattern.matches(welfare, string);
			if (isMatch == false) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fulibuhefa.getMessage());
			}
			stringBuffer.append(listObj3.get(a) + "/");
		}
		map.put("welfare", stringBuffer.toString());

		// 校验职位类型
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		Position[] position = Position.values();
		for (int a1 = 0; a1 < position.length; a1++) {
			stringBuffer.append(position[a1].getPositionType());
		}
		string = stringBuffer.toString();
		String position_par = params.get("position").toString().trim();
		if (position_par == null || position_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhiweikong.getMessage());
		}
		String position_check = ".*" + position_par + ".*";
		boolean isMatch = Pattern.matches(position_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhiweibuhefa.getMessage());
		}
		map.put("position", position_par);

		// 校验工资
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		Salary[] salary = Salary.values();
		for (int a1 = 0; a1 < salary.length; a1++) {
			stringBuffer.append(salary[a1].getSalary());
		}
		string = stringBuffer.toString();

		String salary_par = params.get("salary").toString().trim();
		if (salary_par == null || salary_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongzikong.getMessage());
		}
		String salary_check = ".*" + salary_par + ".*";
		isMatch = Pattern.matches(salary_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongzibuhefa.getMessage());
		}
		map.put("salary", salary_par);
		// 学历
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		Education[] education = Education.values();
		for (int a1 = 0; a1 < education.length; a1++) {
			stringBuffer.append(education[a1].getEducation());
		}
		string = stringBuffer.toString();

		String education_par = params.get("education").toString().trim();
		if (education_par == null || education_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xuelibuhefa.getMessage());
		}
		String education_check = ".*" + education_par + ".*";
		isMatch = Pattern.matches(education_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xuelibuhefa.getMessage());
		}
		map.put("education", education_par);

		// 工作年限
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		Experience[] experience = Experience.values();
		for (int a1 = 0; a1 < experience.length; a1++) {
			stringBuffer.append(experience[a1].getExperience());
		}
		string = stringBuffer.toString();

		String experience_par = params.get("experience").toString().trim();
		if (experience_par == null || experience_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianxiankong.getMessage());
		}
		String experience_check = ".*" + experience_par + ".*";
		isMatch = Pattern.matches(experience_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianxianbuhefa.getMessage());
		}
		map.put("experience", experience_par);

		// 年龄范围
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		Age[] age = Age.values();
		for (int a1 = 0; a1 < age.length; a1++) {
			stringBuffer.append(age[a1].getAge());
		}
		string = stringBuffer.toString();

		String age_par = params.get("age").toString().trim();
		if (age_par == null || age_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlikong.getMessage());
		}
		String age_check = ".*" + age_par + ".*";
		isMatch = Pattern.matches(age_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlingbuhefa.getMessage());
		}
		map.put("age", age_par);

		// 介绍奖励
		stringBuffer = stringBuffer.delete(0, stringBuffer.length());
		IntroductoryAward[] introductoryAward = IntroductoryAward.values();
		for (int a1 = 0; a1 < introductoryAward.length; a1++) {
			stringBuffer.append(introductoryAward[a1].getIntroductoryAward());
		}
		string = stringBuffer.toString();

		String introductoryAward_par = params.get("introductoryAward").toString().trim();
		if (introductoryAward_par == null || introductoryAward_par.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jianglikong.getMessage());
		}
		String introductoryAward_check = ".*" + introductoryAward_par + ".*";
		isMatch = Pattern.matches(introductoryAward_check, string);
		if (isMatch == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.jianglibuhefa.getMessage());
		}
		map.put("introductoryAward", introductoryAward_par);

		// 招聘人数 number
		String numberString = params.get("number").toString().trim();
		if (numberString == null || numberString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhaopinrenshu.getMessage());
		}
		int number = Integer.parseInt(numberString);
		if (number < 0 || number > 100) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhaopinrenshu.getMessage());
		}
		map.put("number", number);
		// 备注describeOne
		String describeOne = params.get("describeOne").toString().trim();
		if (describeOne != null && describeOne.length() > 100) {
			return ServerResponse.createByErrorMessage(ResponseMessage.beizhuchaoguo.getMessage());
		}
		map.put("describeOne", describeOne);
		// 联系人
		String consigneeName = params.get("consigneeName").toString().trim();
		if (consigneeName == null || consigneeName.length() > 15) {
			return ServerResponse.createByErrorMessage(ResponseMessage.lianrxrengeshicuowo.getMessage());
		}
		map.put("consigneeName", consigneeName);
		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(currentUser.getId());
		if (realName != null) {
			String realNameIdString = params.get("realNameId").toString().trim();
			long realNameId = 0;
			if (realNameIdString != null && !realNameIdString.equals("")) {
				realNameId = Long.valueOf(realNameIdString);
				// 判断实名id
				if (realNameId != realName.getId()) {
					return ServerResponse.createByErrorMessage(ResponseMessage.shimingIDkong.getMessage());
				}
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingIDkong.getMessage());
			}
			// 判断实际工作地址和实名地址是否一致
			String addressDetailed = realName.getAddressDetailed();
			String workingAddress = params.get("workingAddress").toString().trim();
			map.put("workingAddress", workingAddress);
			if (!addressDetailed.equals(workingAddress)) {
				map.put("addressConsistency", 2); // 2不一致1一致
			} else {
				map.put("addressConsistency", 1);
			}

			// 判断省市区
			String detailed = params.get("detailed").toString().trim();
			if (!detailed.equals(realName.getDetailed())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}
			// 校验电话
			String contact = params.get("contact").toString().trim();
			// 判断手机号是否合法
			ServerResponse<String> serverContact = LegalCheck.legalCheckMobilePhone(contact);
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverContact.getMsg());
			}
			if (!EncrypDES.encryptPhone(contact).equals(realName.getContact())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}

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
		map.put("releaseType", Const.ZHIWEIP);
		return ServerResponse.createBySuccess(map);

	}

	// 用户获取发布的职位
	@Override
	public ServerResponse<Object> get_position_list(User user, Map<String, Object> params) {
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

		if (pageSize_string != null && !pageSize_string.equals("")) {
			pageSize = Integer.parseInt(pageSize_string);
			if (pageSize <= 0) {
				return ServerResponse.createByErrorMessage("每页展示条数不能小于0");
			}
		} else {
			return ServerResponse.createByErrorMessage("请正确输入每页展示条数");
		}

		String position = params.get("position").toString().trim();
		String getwelfareStatusString = params.get("welfareStatus").toString().trim();
		Integer welfareStatus = null;
		if (getwelfareStatusString != null && !getwelfareStatusString.equals("")) {
			welfareStatus = Integer.valueOf(getwelfareStatusString);
		}

		Page<ReleaseWelfare> releaseWelfare_pagePage = new Page<ReleaseWelfare>();

		long zongtiaoshu = releaseWelfareMapper.get_position_list_no(position, welfareStatus, user.getId());
		releaseWelfare_pagePage.setTotalno(zongtiaoshu);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); // 当前页

		List<ReleaseWelfare> list_releaseWelfareall = releaseWelfareMapper
				.get_position_list_all((currentPage - 1) * pageSize, pageSize, position, welfareStatus, user.getId());

		RealName realName = realNameMapper.getRealName(user.getId());

		for (int a = 0; a < list_releaseWelfareall.size(); a++) {
			ReleaseWelfare releaseWelfare = list_releaseWelfareall.get(a);
			releaseWelfare.setContact(EncrypDES.decryptPhone(realName.getContact()));
			releaseWelfare.setDetailed(realName.getDetailed());
			releaseWelfare.setCompanyName(realName.getCompanyName());
			list_releaseWelfareall.set(a, releaseWelfare);
		}

		releaseWelfare_pagePage.setDatas(list_releaseWelfareall);
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
	}

	// 操作列
	@Override
	public ServerResponse<String> position_operation(User user, Map<String, Object> params) {

		String type = params.get("type").toString().trim();
		String userId = params.get("userId").toString().trim();
		String id = params.get("id").toString().trim();
		if (type != null && !type.equals("") && userId != null && !userId.equals("") && id != null && !id.equals("")) {
			int type_int = Integer.valueOf(type);
			if (type_int < 1 || type_int > 9) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
			long userIdLong = Long.valueOf(userId);
			long idLong = Long.valueOf(id);

			// 有发布中或者未开始的广告不能操作
			if (bunnerService.getguanggaocount(idLong, Const.ZHIWEIP) > 0) {
				ServerResponse.createByErrorMessage(ResponseMessage.yougonggongxuanchuan.getMessage());
			}

			if (userIdLong != user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = null;
			int result = 0;
			 if (type_int == 1 || type_int == 2 || type_int == 3 || type_int == 4 || type_int == 5 || type_int == 7 || type_int == 8 ) {
				timeString = formatter.format(new Date());
				result = releaseWelfareMapper.position_operation(userIdLong, idLong, type_int, timeString,DateTimeUtil.a_few_days_later0(30));
			} else if (type_int == 6) {
				// 判断非法输入
				ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
				// 检查是否有非法输入
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				ReleaseWelfare releaseWelfare = new ReleaseWelfare();
				releaseWelfare.setTermOfValidity(DateTimeUtil.a_few_days_later0(30));
				// 判断是否公开电话
				String isPublishContactString = params.get("isPublishContact").toString().trim();
				if (isPublishContactString == null || isPublishContactString.equals("") ) {
					return ServerResponse
							.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
				}
				int isPublishContact = Integer.valueOf(isPublishContactString);
				if (isPublishContact != 1 && isPublishContact != 2) {
					return ServerResponse
							.createByErrorMessage(ResponseMessage.shifougongkaidianhualeixingcuowu.getMessage());
				}
				releaseWelfare.setIsPublishContact(isPublishContact);
				// 判断邮箱和联系方式必须有一个公开
				String email = params.get("email").toString().trim();
				if (isPublishContact == 2 && (email == null || email.equals("")  )) {
					return ServerResponse
							.createByErrorMessage(ResponseMessage.gongkaidianhuahuozheshuruyouxiang.getMessage());
				}
				releaseWelfare.setEmail(email);

				// 判断实名信息是否正确
				RealName realName = realNameMapper.getRealName(userIdLong);
				if (realName != null) {
					// 判断实际工作地址和实名地址是否一致
					String addressDetailed = realName.getAddressDetailed();
					if (!addressDetailed.equals(params.get("workingAddress").toString().trim())) {
						// 2不一致1一致
						releaseWelfare.setAddressConsistency(2);
					} else {
						releaseWelfare.setAddressConsistency(1);
					}
					releaseWelfare.setWorkingAddress(params.get("workingAddress").toString().trim());
					releaseWelfare.setDescribeOne(params.get("describeOne").toString().trim());
					releaseWelfare.setId(idLong);
					releaseWelfare.setUserId(userIdLong);
					timeString = formatter.format(new Date());
					releaseWelfare.setUpdateTime(timeString);
					result = releaseWelfareMapper.position_operation_edit(releaseWelfare);
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

	@Override
	public ServerResponse getContact(User user, Map<String, Object> params) {
		String queriesTypeString = params.get("queriesType").toString().trim();
		if (queriesTypeString == null || queriesTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int queriesType = Integer.valueOf(queriesTypeString);
		// 1电话2邮箱
		if (queriesType != 1 && queriesType != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}

		String idString = params.get("id").toString().trim();
		if (idString == null || idString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
		int id = Integer.valueOf(idString);
		if (queriesType < 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}

		ReleaseWelfare releaseWelfare = null;

		Map<String, String> returnMap = new HashMap<String, String>();

		// TODO 1是查询类型 招聘电话
		NumberOfQueries numberOfQueries = getNumberOfQueries(user.getId(), 1);
		String dateString = DateTimeUtil.dateToDay();
		if (numberOfQueries == null) {

			releaseWelfare = releaseWelfareMapper.getReleaseWelfareById(id, queriesType);

			if (releaseWelfare == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
			}
			numberOfQueries = new NumberOfQueries();
			numberOfQueries.setCountQueries(1);
			numberOfQueries.setQueriesType(1);
			numberOfQueries.setUserId(user.getId());
			numberOfQueries.setDateString(dateString);

			numberOfQueriesMapper.setNumberOfQueries(numberOfQueries);
		} else {
			int countQueries = numberOfQueries.getCountQueries();
			if (countQueries >= 20 && numberOfQueries.getDateString().equals(dateString)) {
				return ServerResponse.createByErrorMessage(ResponseMessage.weibaozhengxinxianquan.getMessage());
			}
			if (numberOfQueries.getDateString().equals(dateString)) {
				numberOfQueries.setCountQueries(countQueries + 1);
			} else {
				numberOfQueries.setCountQueries(1);
			}
			numberOfQueries.setDateString(dateString);

			releaseWelfare = releaseWelfareMapper.getReleaseWelfareById(id, queriesType);
			if (releaseWelfare == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
			}
			int count = numberOfQueriesMapper.updateNumberOfQueries(numberOfQueries);
		}
		// 1电话2邮箱
		if (queriesType == 1) {
			returnMap.put("contact",
					EncrypDES.decryptPhone(realNameMapper.getRealName(releaseWelfare.getUserId()).getContact()));
			returnMap.put("email", null);
		} else {
			returnMap.put("contact", null);
			returnMap.put("email", releaseWelfare.getEmail());
		}
		returnMap.put("consigneeName", releaseWelfare.getConsigneeName());
		return ServerResponse.createBySuccess(returnMap);
	}

	// 计算查询次数
	public NumberOfQueries getNumberOfQueries(long userId, int queriesType) {
		return numberOfQueriesMapper.getNumberOfQueries(userId, queriesType);
	}

//分页展示职位
	@Override
	public ServerResponse<Object> get_position_all(User user, Map<String, Object> params) {
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

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		String provinces_id = null;
		String city_id = null;
		String district_county_id = null;
		if (selectedOptions_list.size() == 3) {
			provinces_id = selectedOptions_list.get(0) + "";
			city_id = selectedOptions_list.get(1) + "";
			district_county_id = selectedOptions_list.get(2) + "";
			// 判断省市区id是否正确
		}else {
			RealName realName=realNameMapper.getUserRealName(user.getId());
			provinces_id = realName.getProvincesId()+ "";
			city_id = realName.getCityId()+ "";
			district_county_id = realName.getDistrictCountyId()+ "";
		}

		String detailed = "%" + getPublishingsService.ctiy(provinces_id, city_id, district_county_id) + "%";
		String position = params.get("position").toString().trim();
		Page<ReleaseWelfare> releaseWelfare_pagePage = new Page<ReleaseWelfare>();
		long count = releaseWelfareMapper.getUserReleaseWelfarePageno(detailed, position);
		if (count == 0) {
			detailed = "%" + getPublishingsService.ctiy(provinces_id, city_id, null) + "%";
			count = releaseWelfareMapper.getUserReleaseWelfarePageno(detailed, position);
		}
		releaseWelfare_pagePage.setTotalno(count);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); // 当前页
       if(count == 0) {
    	   releaseWelfare_pagePage.setDatas(new ArrayList<ReleaseWelfare>() );
   		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
       }
       
       
		List<ReleaseWelfare> list_releaseWelfareall = releaseWelfareMapper
				.getUserReleaseWelfareList((currentPage - 1) * pageSize, pageSize, detailed, position);
		if (list_releaseWelfareall.size() > 0) {
			for (int a = 0; a < list_releaseWelfareall.size(); a++) {

				ReleaseWelfare releaseWelfare = list_releaseWelfareall.get(a);
				RealName realName = realNameMapper.getRealName(releaseWelfare.getUserId());
				releaseWelfare.setDetailed(realName.getDetailed());
				releaseWelfare.setCompanyName(realName.getCompanyName());
				list_releaseWelfareall.set(a, releaseWelfare);
			}
		}
		releaseWelfare_pagePage.setDatas(list_releaseWelfareall);
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);

	}

	@Override
	public List<ReleaseWelfare> adminGetzZWall(long userId) {

		return releaseWelfareMapper.adminGetzZWall(userId);
	}

}
