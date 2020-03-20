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
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.chaxuncishu.NumberOfQueriesMapper;
import com.dian.mmall.dao.releaseDao.ReleaseWelfareMapper;
import com.dian.mmall.dao.releaseDao.ResumeMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.chaxuncishu.NumberOfQueries;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("resumeService")
public class ResumeServiceImp implements ResumeService {
	@Autowired
	private ResumeMapper resumeMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;

	@Autowired
	private GetPublishingsService getPublishingsService;

	@Autowired
	private NumberOfQueriesMapper numberOfQueriesMapper;
	@Autowired
	private BunnerService bunnerService;

	// 创建和编辑简历type=1 创建 && type=2 编辑
	public ServerResponse<String> create_resume(User user, Map<String, Object> params) {

		String typeString = params.get("type").toString().trim();
		if (typeString == null || typeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}
		int type = Integer.valueOf(typeString);
		if (type != 1 && type != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
		}

		ServerResponse<Object> serverResponse = check_position(user, params, type);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		Resume resume = (Resume) BeanMapConvertUtil.convertMap(Resume.class, map);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(resume);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = 0;
			if (type == 1) {
				count = resumeMapper.create_resume(resume);
			} else {
				count = resumeMapper.update_resume(resume);
			}

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

//各种校验
	public ServerResponse<Object> check_position(User currentUser, Map<String, Object> params, int type) {

		// 判断用户id与 tocken是否一致
		long userId = Long.valueOf(params.get("userId").toString().trim());
		if (userId != currentUser.getId()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("termOfValidity", DateTimeUtil.a_few_days_later0(90));
		String createTime = DateTimeUtil.dateToAll();
		// 判断是否超过可以发布的总数
		Resume resume = resumeMapper.selectResumeById(userId);
		if (type == 1) {
			if (resume != null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yifabuguojianli.getMessage());
			}
			map.put("updateTime", createTime);
			map.put("createTime", createTime);

		} else {
			if (resume == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.weifabuguojianli.getMessage());
			}
			map.put("updateTime", createTime);
			map.put("createTime", resume.getCreateTime());
			map.put("id", resume.getId());
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
		if ( genString == null || genString.equals("") ) {
			return ServerResponse.createByErrorMessage(ResponseMessage.xingbiebuhefa.getMessage());
		}
		int gender = Integer.valueOf(genString);
		if (gender == 1) {
			map.put("gender", "男");
		} else if (gender == 2) {
			map.put("gender", "女");
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

		// 校验职位类型
		StringBuffer stringBuffer = new StringBuffer();
		Position[] position = Position.values();
		for (int a1 = 0; a1 < position.length; a1++) {
			stringBuffer.append(position[a1].getPositionType());
		}
		String string = stringBuffer.toString();
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
		;
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
		;
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
		;
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
		String age_par = params.get("age").toString().trim();
		if (age_par.equals("") || age_par == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlikong.getMessage());
		}
		int age = Integer.valueOf(age_par);
		if (age < 18 || age > 60) {
			return ServerResponse.createByErrorMessage(ResponseMessage.nianlingbuhefa.getMessage());
		}
		map.put("age", age);
		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(currentUser.getId());
		if (realName != null) {

			// 判断电话
			String contact = params.get("contact").toString().trim();
			if (!EncrypDES.decryptPhone(realName.getContact()).equals(contact)) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}
			// 手机不存 map.put("contact", realName.getContact());
			// 判断实名姓名
			String consigneeName = realName.getConsigneeName();
			if (!consigneeName.equals(params.get("consigneeName").toString().trim())) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
			}
			// 实名不存 map.put("consigneeName", consigneeName);
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

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确

			map.put("detailed", cityMapper.checkeCity(provincesId, cityId, districtCountyId));
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions1").toString().trim(), List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确

			map.put("addressDetailed", cityMapper.checkeCity(provincesId, cityId, districtCountyId));
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		map.put("describeOne", params.get("describeOne").toString().trim());
		map.put("email", params.get("email").toString().trim());
		map.put("releaseType", Const.JIANLIP);
		return ServerResponse.createBySuccess(map);

	}

	@Override
	public ServerResponse<Object> select_resume_by_id(long userId) {
		// 判断是否超过可以发布的总数
		Resume resume = resumeMapper.selectResumeById(userId);
		if (resume == null) {
			return ServerResponse.createBySuccess();
		}
		RealName realName = realNameMapper.getRealName(userId);
		resume.setContact(EncrypDES.decryptPhone(realName.getContact()));
		resume.setConsigneeName(realName.getConsigneeName());
		return ServerResponse.createBySuccess(resume);
	}

	public int getreleaseType(long id) {
		return resumeMapper.getreleaseType(id);
	}

//操作
	@Override
	public ServerResponse<String> operation_resume(User user, Map<String, Object> params) {
		String type = params.get("type").toString().trim();
		String userId = params.get("userId").toString().trim();
		String id = params.get("id").toString().trim();
		if (type != null && !type.equals("") && userId != null && !userId.equals("") && id != null && !id.equals("")) {
			int type_int = Integer.valueOf(type);
			if (type_int < 1 || type_int > 9) {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
			long userIdLong = Long.valueOf(userId);
			Resume resume = resumeMapper.selectResumeById(userIdLong);
			if (resume == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
			}
			
			
			if (userIdLong != user.getId()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
			}
			long idLong = Long.valueOf(id);
			// 有发布中或者未开始的广告不能操作
			if (bunnerService.getguanggaocount(idLong, getreleaseType(idLong)) > 0) {
				ServerResponse.createByErrorMessage(ResponseMessage.yougonggongxuanchuan.getMessage());
			}

			String timeString = DateTimeUtil.dateToAll();
			String a_few_days_later = DateTimeUtil.a_few_days_later0(90);
			int result = 0;
			resume.setUpdateTime(timeString);
			if (type_int == 1) {
				resume.setTermOfValidity(a_few_days_later);
				result = resumeMapper.operation_resume(type_int, resume);
			} else if (type_int == 2) {
				resume.setWelfareStatus(2);
				result = resumeMapper.operation_resume(type_int, resume);
			} else if (type_int == 3) {
				resume.setWelfareStatus(3);
				result = resumeMapper.operation_resume(type_int, resume);
			} else if (type_int == 4) {
				resume.setTermOfValidity(a_few_days_later);
				resume.setWelfareStatus(1);
				result = resumeMapper.operation_resume(type_int, resume);
			} else if (type_int == 5) {
				resume.setTermOfValidity(a_few_days_later);
				resume.setWelfareStatus(1);
				result = resumeMapper.operation_resume(type_int, resume);
			}  else {
				return ServerResponse.createByErrorMessage(ResponseMessage.canshuyouwu.getMessage());
			}
	
			if (result > 0) {
				return ServerResponse.createBySuccess();
			}

		}
		return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
	}

//审核分页
	@Override
	public ServerResponse<Object> getTrialResumeAll(Map<String, Object> params) {
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

		String userName = params.get("userName").toString().trim();
		String contact = params.get("contact").toString().trim();

		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		Page<Resume> releaseWelfare_pagePage = new Page<Resume>();

		long zongtiaoshu = resumeMapper.getRresumePageno(userName, contact);

		releaseWelfare_pagePage.setTotalno(zongtiaoshu);
		releaseWelfare_pagePage.setPageSize(pageSize);
		releaseWelfare_pagePage.setCurrentPage(currentPage); // 当前页

		List<Resume> list_resumeall = resumeMapper.getRresumeAll((currentPage - 1) * pageSize, pageSize, userName,
				contact);
		if (list_resumeall.size() > 0) {
			for (int a = 0; a < list_resumeall.size(); a++) {
				Resume resume = list_resumeall.get(a);
				RealName realName = realNameMapper.getRealName(resume.getUserId());
				resume.setContact(EncrypDES.decryptPhone(realName.getContact()));
				resume.setConsigneeName(realName.getConsigneeName());
				resume.setContact(EncrypDES.decryptPhone(resume.getContact()));
				list_resumeall.set(a, resume);
				realName = null;
			}
		}

		releaseWelfare_pagePage.setDatas(list_resumeall);
		return ServerResponse.createBySuccess(releaseWelfare_pagePage);
	}

	@Override
	public ServerResponse<Object> get_resume_all(User user, Map<String, Object> params) {
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
		String provinces_id = null;
		String city_id = null;
		String district_county_id = null;

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			provinces_id = selectedOptions_list.get(0) + "";
			city_id = selectedOptions_list.get(1) + "";
			district_county_id = selectedOptions_list.get(2) + "";
			// 判断省市区id是否正确
		}

		String detailed = "%" + getPublishingsService.ctiy(provinces_id, city_id, district_county_id) + "%";

		String position = params.get("position").toString().trim();
		Page<Resume> resume_pagePage = new Page<Resume>();
		long count = resumeMapper.getUserRresumePageno(detailed, position);
		if (count == 0) {
			detailed = "%" + getPublishingsService.ctiy(provinces_id, city_id, null) + "%";
			count = resumeMapper.getUserRresumePageno(detailed, position);
		}
		resume_pagePage.setTotalno(count);
		resume_pagePage.setPageSize(pageSize);
		resume_pagePage.setCurrentPage(currentPage); // 当前页
		resume_pagePage
				.setDatas(resumeMapper.getUserRresumeList((currentPage - 1) * pageSize, pageSize, detailed, position));
		return ServerResponse.createBySuccess(resume_pagePage);
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
		} // TODO 1是查询类型 招聘联系方式 2是查询 简历联系方式

		Resume resume = null;

		Map<String, String> returnMap = new HashMap<String, String>();

		int eslectType = 2;

		NumberOfQueries numberOfQueries = getNumberOfQueries(user.getId(), eslectType);
		String dateString = DateTimeUtil.dateToDay();
		if (numberOfQueries == null) {

			resume = resumeMapper.getResumeContactById(id, queriesType);
			if (resume == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
			}

			numberOfQueries = new NumberOfQueries();
			numberOfQueries.setCountQueries(1);
			numberOfQueries.setQueriesType(eslectType);
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

			resume = resumeMapper.getResumeContactById(id, queriesType);
			if (resume == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
			}
			numberOfQueriesMapper.updateNumberOfQueries(numberOfQueries);
		}
		RealName realName = realNameMapper.getRealName(resume.getUserId());
		// 1电话2邮箱
		if (queriesType == 1) {
			returnMap.put("contact", EncrypDES.decryptPhone(realName.getContact()));
			returnMap.put("email", null);
		} else {
			returnMap.put("contact", null);
			returnMap.put("email", resume.getEmail());
		}
		returnMap.put("consigneeName", realName.getConsigneeName());
		return ServerResponse.createBySuccess(returnMap);
	}

//计算查询次数
	public NumberOfQueries getNumberOfQueries(long userId, int queriesType) {
		return numberOfQueriesMapper.getNumberOfQueries(userId, queriesType);
	}

	@Override
	public List<Resume> adminGetResumeall(long userId) {
		return resumeMapper.adminGetResumeall(userId);
	}
}
