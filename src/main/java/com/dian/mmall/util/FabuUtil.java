package com.dian.mmall.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;

public class FabuUtil {

	// 各种校验
	public static ServerResponse<Object> check_evaluate(User currentUser, Map<String, Object> params, int type,
			RealName realName, int releaseType) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致
		long userId = Long.valueOf(params.get("userId").toString().trim());
		if (userId != currentUser.getId()) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMinghuoidbuyizhi.getMessage());
		}
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userId", userId);
		map.put("servicFrequenc", 0);
		if (releaseType == 33 || releaseType == 34 || releaseType == 18 || releaseType == 13 || releaseType == 17
				|| releaseType == 19 || releaseType == 7 || releaseType == 8) {

			map.put("termOfValidity", DateTimeUtil.a_few_days_later0(365));
		} else if (releaseType == 14 || releaseType == 15) {

			map.put("termOfValidity", DateTimeUtil.a_few_days_later0(90));
		} else if (releaseType == 101 || releaseType == 102 || releaseType == 103 || releaseType == 104
				|| releaseType == 4 || releaseType == 5 || releaseType == 6 || releaseType == 9 || releaseType == 29
				|| releaseType == 11) {
			map.put("termOfValidity", DateTimeUtil.a_few_days_later0(180));
		}
		String createTime = DateTimeUtil.dateToAll();
		if (type == 1) { // TODO新建时才检查总数
			map.put("createTime", createTime);
		}
		map.put("releaseType", releaseType);
		map.put("updateTime", createTime);

		// 判断电话
		String contact = params.get("contact").toString().trim();
		// 判断手机号是否合法
		ServerResponse<String> serverContact = LegalCheck.legalCheckMobilePhone(contact);

		if (!EncrypDES.encryptPhone(contact).equals(realName.getContact())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
		}

		map.put("consigneeName", params.get("consigneeName").toString().trim());
		// 获取用户类型
		ServerResponse<String> serverResponse = SetBean.setRole(currentUser.getRole());
		// 检查用户类型
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		map.put("userType", serverResponse.getMsg());
		String serviceDetailed = params.get("serviceDetailed").toString().trim();

		if (releaseType != 15 && releaseType != 14) {
			if (!serviceDetailed.equals("全市") && !serviceDetailed.equals("来电确认")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());
			}
			map.put("serviceDetailed", serviceDetailed);
		} else {
			map.put("serviceDetailed", serviceDetailed);
		}
		if (releaseType == 13 || releaseType == 17 || releaseType == 19 || releaseType == 15 || releaseType == 14) {
			String mianjiaString = params.get("mianjia").toString().trim();
			if (mianjiaString == null || mianjiaString.equals("")) {
				if (releaseType == 15 || releaseType == 14) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjibunnegweikong.getMessage());
				}
				return ServerResponse.createByErrorMessage(ResponseMessage.qibujiagebuhekong.getMessage());
			}
			int mianjia = 0;
			try {
				mianjia = Integer.valueOf(mianjiaString);
				if (releaseType < 1 || releaseType > 10000) {
					return ServerResponse.createByErrorMessage(ResponseMessage.mianjibuhefa.getMessage());
				}
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.mianjibuhefa.getMessage());
			}
			map.put("mianjia", mianjia);
		} else {
			map.put("serviceAndprice", JsonUtil.obj2String(params.get("serviceAndprice")));
			map.put("serviceType", params.get("serviceType").toString().trim());
		}

		map.put("remarks", params.get("remarks").toString().trim());
		map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());
		map.put("releaseTitle", params.get("releaseTitle").toString().trim());
		return ServerResponse.createBySuccess(map);

	}
}
