package com.dian.mmall.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.common.fabu.QuXian;
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
		map.put("termOfValidity", termOfValidityString(releaseType));
		String createTime = DateTimeUtil.dateToAll();
		// 判断电话
		String contact = params.get("contact").toString().trim();

		// 判断手机号是否合法
		ServerResponse<String> serverContact = LegalCheck.legalCheckMobilePhone(contact);
		if (type == 1) { // TODO新建时才检查总数

			// 获取用户类型
			ServerResponse<String> serverResponse = SetBean.setRole(currentUser.getRole());
			// 检查用户类型
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}
			map.put("userType", serverResponse.getMsg());
			map.put("releaseType", releaseType);
			
		} 
		if (!EncrypDES.encryptPhone(contact).equals(realName.getContact())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shimingxinxibuyizhi.getMessage());
		}
		map.put("createTime", createTime);
		map.put("updateTime", createTime);

		map.put("consigneeName", params.get("consigneeName").toString().trim());

		String serviceDetailed = params.get("serviceDetailed").toString().trim();
		if (releaseType != 15 && releaseType != 14) {
			QuXian[] quXians = QuXian.values();

			ArrayList<String> respList = null;
			for (int i = 0; i < quXians.length; i++) {
				if (quXians[i].getCityDistrictCountyId() == Integer.parseInt(realName.getCityId()+"")  ) {
					respList = quXians[i].getDistrictCountyNames();
					break;
				}
			}
			
			if(respList==null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());
			}
			String s=serviceDetailed.substring(1, serviceDetailed.length()-1);
	    	String[] serviceDetailedlList= s.split(",");
			StringBuffer stringBuffer = new StringBuffer();
			if (serviceDetailedlList.length > 0) {
				for (int i = 0; i < serviceDetailedlList.length; i++) {
					for (int a = 0; a < respList.size(); a++) {
						if (respList.get(a).equals(serviceDetailedlList[i].trim())) {
							stringBuffer.append(serviceDetailedlList[i] + "/");
							break;
						}
					}
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.fuwuchengshicuowu.getMessage());
			}

			map.put("serviceDetailed", stringBuffer.toString());
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
				if (releaseType < 1 || releaseType > 1000000) {
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

	public static String releaseTypeString(int releaseType) {
		String releaseTypeString = null;
		if (releaseType == 4) {
			releaseTypeString = "蔬菜";
		} else if (releaseType == 5) {
			releaseTypeString = "粮油";
		} else if (releaseType == 6) {
			releaseTypeString = "副食/调料";
		} else if (releaseType == 29) {
			releaseTypeString = "水产/禽蛋";
		} else if (releaseType == 9) {
			releaseTypeString = "清洁用品";
		} else if (releaseType == 11) {
			releaseTypeString = "桌椅餐具";
		} else if (releaseType == 101) {
			releaseTypeString = "工服";
		} else if (releaseType == 102) {
			releaseTypeString = "百货";
		} else if (releaseType == 103) {
			releaseTypeString = "绿植";
		} else if (releaseType == 104) {
			releaseTypeString = "装饰用品";
		} else if (releaseType == 7) {
			releaseTypeString = "酒水饮料";
		} else if (releaseType == 8) {
			releaseTypeString = "消毒餐具";
		} else if (releaseType == 33) {
			releaseTypeString = "电器/设备出售";
		} else if (releaseType == 34) {
			releaseTypeString = "二手电器/设备出售";
		} else if (releaseType == 18) {
			releaseTypeString = "维修电器/设备";
		} else if (releaseType == 14) {
			releaseTypeString = "店面/窗口出租";
		} else if (releaseType == 15) {
			releaseTypeString = "摊位出租转让";
		} else if (releaseType == 13) {
			releaseTypeString = "菜谱/广告";
		} else if (releaseType == 17) {
			releaseTypeString = "装修";
		} else if (releaseType == 19) {
			releaseTypeString = "灭虫";
		}
		return releaseTypeString;
	}

	// 1发布中，2隐藏中，3删除,4审核中,5不在有效期
	public static String welfareStatusString(Integer welfareStatus) {
		String welfareStatusString = null;
		if (welfareStatus == 1) {
			welfareStatusString = "发布中";
		} else if (welfareStatus == 2) {
			welfareStatusString = "隐藏中";
		} else if (welfareStatus == 4) {
			welfareStatusString = "审核中";
		} else if (welfareStatus == 5) {
			welfareStatusString = "不在有效期";
		}
		return welfareStatusString;
	}

	public static String termOfValidityString(int releaseType) {
		String welfareStatusString = null;
		if (releaseType == 33 || releaseType == 34 || releaseType == 18 || releaseType == 13 || releaseType == 17
				|| releaseType == 19 || releaseType == 7 || releaseType == 8) {

			welfareStatusString = DateTimeUtil.a_few_days_later0(365);
		} else if (releaseType == 14 || releaseType == 15) {

			welfareStatusString = DateTimeUtil.a_few_days_later0(90);
		} else if (releaseType == 101 || releaseType == 102 || releaseType == 103 || releaseType == 104
				|| releaseType == 4 || releaseType == 5 || releaseType == 6 || releaseType == 9 || releaseType == 29
				|| releaseType == 11) {
			welfareStatusString = DateTimeUtil.a_few_days_later0(180);
		}
		return welfareStatusString;
	}
}
