package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.pojo.ServiceType;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.ServiceTypeService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;

@Service("serviceTypeServic")
public class ServiceTypeServiceImpl implements ServiceTypeService {
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	private String path = Const.PATH_E_IMG;
	@Autowired
	private PictureMapper pictureMapper;

	@Override
	public ServerResponse<String> create_serviceType(User user, Map<String, Object> params) {
		return createServiceType(user, params, 1); // 待审批
	}

	@Override
	public ServerResponse<String> admin_create_serviceType(User user, Map<String, Object> params) {

		return createServiceType(user, params, 2); // 管理员加入
	}

	@Override
	public ServerResponse<Object> get_serviceType(Integer releaseType, String serviceType, Long userId) {
		if (releaseType > 0 && releaseType < 500) {

			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}
			if (releaseType == 401) {
				releaseType = 4;
			}
			else if (releaseType == 405) {
				releaseType = 5;
			} else if (releaseType == 406) {
				releaseType = 6;
			} else if (releaseType == 429) {
				releaseType = 29;
			} else if (releaseType == 409) {
				releaseType = 9;
			}
			return ServerResponse.createBySuccess(serviceTypeMapper.get_serviceType(releaseType, serviceType, userId));
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
	}

	public ServerResponse<String> createServiceType(User user, Map<String, Object> params, int authentiCationStatus) {

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		serverResponse = LegalCheck.legalCheckServiceTypeName(params.get("serviceTypeName").toString());
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
//PurchaseCreateOrder.vue
//	      <el-radio :label="33" >电器/设备出售</el-radio>
//          <el-radio :label="34" >二手电器/设备出售</el-radio>
//          <el-radio :label="18" >维修电器/设备</el-radio>
//          <el-radio :label="7" >酒水/饮料</el-radio>
//          <el-radio :label="8" >消毒餐具</el-radio>
//          <el-radio :label="4" >蔬菜出售</el-radio>
//          <el-radio :label="5" >粮油出售</el-radio>
//          <el-radio :label="6" >调料/副食出售</el-radio>
//          <el-radio :label="29" >水产/禽蛋出售</el-radio>
//          <el-radio :label="9" >清洁用品</el-radio>
//          <el-radio :label="11" >桌椅餐具</el-radio>

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.caidanIDweikong.getMessage());
		}
		Integer releaseType = 0;
		try {
			releaseType = Integer.valueOf(releaseTypeString);
			if (releaseType == 18 || releaseType == 33 || releaseType == 34) {
				releaseType = 18;
			} else if (releaseType == 4 || releaseType == 5 || releaseType == 6 || releaseType == 29 || releaseType == 9
					|| releaseType == 11 || releaseType == 7 || releaseType == 8 || releaseType == 101
					|| releaseType == 102 || releaseType == 103 || releaseType == 104) {
				//releaseType = releaseType;
			} else if (releaseType == 401) {
				releaseType = 4;
			}
			else if (releaseType == 405) {
				releaseType = 5;
			} else if (releaseType == 406) {
				releaseType = 6;
			} else if (releaseType == 429) {
				releaseType = 29;
			} else if (releaseType == 409) {
				releaseType = 9;
			}

			// TODO 新加就在这里设置
			else {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		} catch (Exception e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}

		String serviceTypeName = serverResponse.getMsg();

		ServerResponse<Object> serverResponse2 = get_serviceType(releaseType, null, user.getId());
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunyiyouleixshibai.getMessage());
		}
		List<String> service_type_list = (List<String>) serverResponse2.getData();
		if (service_type_list.size() > 0) {
			for (int a = 0; a < service_type_list.size(); a++) {
				if (serviceTypeName.equals(service_type_list.get(a))) {
					return ServerResponse.createByErrorMessage(ResponseMessage.leixingyicunzai.getMessage());
				}
			}
		}

		ServiceType serviceType = new ServiceType();
		serviceType.setReleaseType(releaseType);
		serviceType.setServiceTypeName(serviceTypeName);
		serviceType.setAuthentiCationStatus(authentiCationStatus);
		serviceType.setCreateUserId(user.getId());

		if (authentiCationStatus == 2) {
			serviceType.setExamineName(user.getUsername());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = formatter.format(new Date());
			serviceType.setExamineTime(timeString);
			// 蔬菜类型加示例图片
			if (releaseType == 4) {

				// 图片
				List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
						Picture.class);
				int list_size = listObj3.size();
				// 把getUse_status()==1 放到这个集合中
				List<Picture> listObj4 = new ArrayList<Picture>();
				int getNum = PictureNum.shucaishili.getNum();

				if (list_size > 0) {
					int count = 0;
					for (int a = 0; a < list_size; a++) {
						Picture picture = listObj3.get(a);
						if (listObj3.get(a).getUseStatus() == 1) {
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
				serviceType.setPictureUrl(JsonUtil.obj2StringPretty(listObj4));

			}

		}
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(serviceType);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = serviceTypeMapper.create_serviceType(serviceType);
			if (count == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
			return ServerResponse.createBySuccess();
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}
	}

	// 带示例图片的商品名
	@Override
	public ServerResponse<Object> get_serviceTypeUrl(Integer releaseType, String serviceType, long userId) {
		if (releaseType > 0 && releaseType <500) {

			if (serviceType != null && !serviceType.equals("")) {
				serviceType = "%" + serviceType + "%";
			}
			if (releaseType == 401) {
				releaseType = 4;
			}
			else if (releaseType == 405) {
				releaseType = 5;
			} else if (releaseType == 406) {
				releaseType = 6;
			} else if (releaseType == 429) {
				releaseType = 29;
			} else if (releaseType == 409) {
				releaseType = 9;
			}
			List<ServiceType> list = serviceTypeMapper.get_serviceTypeUrl(releaseType, serviceType);

			for (int i = 0; i < list.size(); i++) {
				ServiceType serviceType2 = list.get(i);
				if (serviceType2.getPictureUrl() != null) {

					List<Picture> listObj3 = JsonUtil.string2Obj(serviceType2.getPictureUrl(), List.class,
							Picture.class);
					serviceType2.setPictureUrl(listObj3.get(0).getPictureUrl());
					list.set(i, serviceType2);
				}
			}
			return ServerResponse.createBySuccess(list);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
		}
	}
}
