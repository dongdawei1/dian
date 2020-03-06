package com.dian.mmall.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.City;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IPictureService;
import com.dian.mmall.service.RealNameService;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.SetBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("realNameService")
public class RealNameServiceImpl implements RealNameService {
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;

	private String path = Const.PATH_E_IMG;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private UserMapper userMapper;

	// 创建实名认证
	@Override
	public ServerResponse<Object> newRealName(User user, String loginToken, Map<String, Object> params) {
		String isbusiness_string = params.get("isbusiness").toString().trim();
//		if(!isbusiness_string.equals("true") && !isbusiness_string.equals("false") ) {
//		return	ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
//		}
		int isbusiness = Integer.valueOf(isbusiness_string);

		// 校验是否有特殊字符
		Map<String, Object> params_map = (Map<String, Object>) params.get("ruleForm");
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params_map);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return  ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		long user_id = user.getId();

		// 所有商户实名
		String address_detailed = null;
		int provinces_id = 0;
		int city_id = 0;
		int district_county_id = 0;
		String city = null;

		String provinces_id1 = params_map.get("provinces_id").toString().trim();
		String city_id1 = params_map.get("city_id").toString().trim();
		String district_county_id1 = params_map.get("district_county_id").toString().trim();

		if (provinces_id1 == null || provinces_id1.equals("") || city_id1 == null || city_id1.equals("")
				|| district_county_id1 == null || district_county_id1.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		provinces_id = Integer.valueOf(provinces_id1);
		city_id = Integer.valueOf(city_id1);
		district_county_id = Integer.valueOf(district_county_id1);
		// 判断省市区id是否正确
		city = cityMapper.checkeCity(provinces_id, city_id, district_county_id);
		if (city == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		String contact = params_map.get("contact").toString().trim();
		// 判断手机号是否合法
		serverResponse = LegalCheck.legalCheckMobilePhone(contact);
		if (serverResponse.getStatus() != 0) {
			return  ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		String consignee_name = params_map.get("consignee_name").toString().trim();
		if (consignee_name == null || consignee_name.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.lianxiren.getMessage());
		}

		if (isbusiness == 2 || isbusiness == 6) {
			String licenseUrl = null; // 营业执照图片url
			String email = null;

			try {
				// 图片
				List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params_map.get("licenseUrl"),
						List.class, Picture.class);
				int list_size = listObj3.size();
				// 把getUse_status()==1 放到这个集合中
				List<Picture> listObj4 = new ArrayList<Picture>();
				int getNum = PictureNum.ShiMingRenZheng.getNum();

				if (list_size > 0) {
					int count = 0;
					for (int a = 0; a < list_size; a++) {
						Picture picture = listObj3.get(a);
						if (listObj3.get(a).getUseStatus() == 1) {
							picture.setUserId(user_id);
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
				licenseUrl = JsonUtil.obj2StringPretty(listObj4);

				address_detailed = params_map.get("address_detailed").toString().trim();
				if (address_detailed == null || address_detailed.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.dizhixiangq.getMessage());
				}

				email = params_map.get("email").toString().trim();
				String companyName = params_map.get("companyName").toString().trim();
				if (companyName == null || companyName.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.gongsimingchengkong.getMessage());
				}

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setCreateTime(formatter.format(new Date()));
				realName.setUserId(user_id);

				if (email != null) {
					realName.setEmail(email);
				}
				if (isbusiness == 2) {
					realName.setAddressDianming(params_map.get("address_dianming").toString().trim());
				}

				realName.setAddressDetailed(address_detailed);
				realName.setLicenseUrl(licenseUrl);
				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consignee_name);
				realName.setCityId(city_id);
				realName.setDistrictCountyId(district_county_id);
				realName.setProvincesId(provinces_id);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setUserName(user.getUsername());
				realName.setCompanyName(companyName);
				// 添加
				serverResponse = SetBean.setRole(user.getRole());
				// 检查是否有非法输入
				if (serverResponse.getStatus() != 0) {
					return  ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				realName.setUserType(serverResponse.getMsg());
				// 检查id是否已经存在
				if (realNameMapper.isNewRealName(user_id) > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
				}
				int resultCount = realNameMapper.newRealName(realName);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}
				if (listObj4.size() > 0) {
					for (int a = 0; a < listObj4.size(); a++) {
						Picture picture = listObj4.get(a);
						pictureMapper.updatePictureUse(picture.getId());
					}
				}
				User currentUser = userMapper.selectUserById(user_id);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);
				return ServerResponse.createBySuccess(currentUser);
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		} else if (isbusiness == 11) {
			// 求职者实名
			Integer eag = 0; // 求职年龄
			String gender = null; // 性别
			String email = null;
			try {
				email = params_map.get("email").toString().trim();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setCreateTime(formatter.format(new Date()));
				realName.setUserId(user_id);

				if (email != null) {
					realName.setEmail(email);
				}
				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consignee_name);
				realName.setCityId(city_id);
				realName.setDistrictCountyId(district_county_id);
				realName.setProvincesId(provinces_id);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setUserName(user.getUsername());
				// 添加
				serverResponse = SetBean.setRole(user.getRole());
				// 检查是否有非法输入
				if (serverResponse.getStatus() != 0) {
					return  ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				realName.setUserType(serverResponse.getMsg());

				// 检查id是否已经存在
				if (realNameMapper.isNewRealName(user_id) > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.YongHuIdYiJingCunZai.getMessage());
				}
				int resultCount = realNameMapper.newRealName(realName);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}

				User currentUser = userMapper.selectUserById(user_id);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);
				return ServerResponse.createBySuccess(currentUser);
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
		} else if (isbusiness == 13) {
			// 批发商
			String email = null;
			try {
				String companyName = params_map.get("companyName").toString().trim();
				if (companyName == null || companyName.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.shichangming.getMessage());
				}
				address_detailed = params_map.get("address_detailed").toString().trim();
				if (address_detailed == null || address_detailed.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.dizhixiangq.getMessage());
				}
				// 判断必传字段
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setCreateTime(formatter.format(new Date()));
				realName.setUserId(user_id);
				realName.setCompanyName(companyName);
				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consignee_name);
				realName.setCityId(city_id);
				realName.setDistrictCountyId(district_county_id);
				realName.setProvincesId(provinces_id);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setAddressDetailed(address_detailed);
				realName.setUserName(user.getUsername());
				// 添加
				serverResponse = SetBean.setRole(user.getRole());
				// 检查是否有非法输入
				if (serverResponse.getStatus() != 0) {
					return  ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				realName.setUserType(serverResponse.getMsg());

				// 检查id是否已经存在
				if (realNameMapper.isNewRealName(user_id) > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.YongHuIdYiJingCunZai.getMessage());
				}
				int resultCount = realNameMapper.newRealName(realName);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}
				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}

				User currentUser = userMapper.selectUserById(user_id);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);

				return ServerResponse.createBySuccess(currentUser);
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
	}

	// 查询实名信息
	@Override
	public ServerResponse<Object> getRealName(User user) {
		RealName realName = realNameMapper.getRealName(user.getId());
		if (realName != null) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			realName.setExamineName(null);
			realName.setExamineTime(null);
			return ServerResponse.createBySuccess(realName);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouchaxundaoshimingxinxi.getMessage());
	}

	@Override
	public ServerResponse<Object> getUserRealName(User user) {
		RealName realName = realNameMapper.getUserRealName(user.getId());
		if (realName != null) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			realName.setExamineName(null);
			realName.setExamineTime(null);
			return ServerResponse.createBySuccess(realName);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.meiyouchaxundaoshimingxinxi.getMessage());
	}

	// 重新实名updateRealName

	@Override
	public ServerResponse<Object> updateRealName(User user, String loginToken, Map<String, Object> params) {

		String isbusinessString = params.get("isbusiness").toString().trim();

		int isbusiness = Integer.valueOf(isbusinessString);
		long userId = user.getId();
		// 校验是否有特殊字符
		Map<String, Object> params_map = (Map<String, Object>) params.get("ruleForm");
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params_map);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		User currentUser1 = userMapper.selectUserById(userId);
		if (currentUser1 != null) {
			if (currentUser1.getIsAuthentication() != 3) {
				log.info("updateRealName   ", currentUser1.getIsAuthentication());
				return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoquxinxishibai.getMessage());
		}
		String addressDetailed = null;
		int provincesId = 0;
		int cityId = 0;
		int districtCountyId = 0;
		String email = null;
		String city = null;
		Integer eag = 0; // 求职年龄
		String gender = null; // 性别

		String provinces_id1 = params_map.get("provincesId").toString().trim();
		String city_id1 = params_map.get("cityId").toString().trim();
		String district_county_id1 = params_map.get("districtCountyId").toString().trim();

		if (provinces_id1 == null || provinces_id1.equals("") || city_id1 == null || city_id1.equals("")
				|| district_county_id1 == null || district_county_id1.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		provincesId = Integer.valueOf(provinces_id1);
		cityId = Integer.valueOf(city_id1);
		districtCountyId = Integer.valueOf(district_county_id1);
		// 判断省市区id是否正确
		city = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
		if (city == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}

		String contact = params_map.get("contact").toString().trim();
		// 判断手机号是否合法
		serverResponse = LegalCheck.legalCheckMobilePhone(contact);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		String consigneeName = params_map.get("consigneeName").toString().trim();
		if (consigneeName == null || consigneeName.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.lianxiren.getMessage());
		}

		addressDetailed = params_map.get("addressDetailed").toString().trim();
		if (addressDetailed == null || addressDetailed.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dizhixiangq.getMessage());
		}

		if (isbusiness == 2 || isbusiness == 6) {
			// 商家的 实名认证
			try {
				email = params_map.get("email").toString().trim();
				String companyName = params_map.get("companyName").toString().trim();
				if (companyName == null || companyName.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.gongsimingchengkong.getMessage());
				}

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setUpdateTime(formatter.format(new Date()));
				realName.setUserId(userId);

				if (email != null) {
					realName.setEmail(email);
				}
				realName.setCompanyName(companyName);
				realName.setAddressDetailed(addressDetailed);
				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consigneeName);
				realName.setCityId(cityId);
				realName.setDistrictCountyId(districtCountyId);
				realName.setProvincesId(provincesId);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setUserName(user.getUsername());
				// 检查id是否已经存在
				RealName realName1 = realNameMapper.getUserRealName(userId);
				int resultCount = 0;

				if (realName1 != null) {
					// 检查图片
					ServerResponse<String> serverResponseString = setPictureUrl(
							(ArrayList<Picture>) params_map.get("licenseUrl"), realName1.getLicenseUrl());
					if (serverResponseString.getStatus() != 0) {
						return ServerResponse.createByErrorMessage(serverResponseString.getMsg());
						
					}
					realName.setLicenseUrl(serverResponseString.getMsg());
					realName.setId(realName1.getId());
					resultCount = realNameMapper.updateRealName(realName);

					if (resultCount == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}

					// 删除图片
					try {
						List<Picture> listObj4 = JsonUtil.list2Obj((ArrayList<Picture>) params_map.get("licenseUrl"),
								List.class, Picture.class);
						for (int a = 0; a < listObj4.size(); a++) {
							Picture picture = listObj4.get(a);
							if (picture.getUseStatus() == 2) {
								FileControl.deleteFile(path + picture.getUserName());
								pictureMapper.updatePicture(picture.getId());

							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
				}
				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}

				User currentUser = userMapper.selectUserById(userId);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);
				return ServerResponse.createBySuccess(currentUser);
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		} else if (isbusiness == 13) {
			// 批发商户
			try {

				String companyName = params_map.get("companyName").toString().trim();
				if (companyName == null || companyName.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.shichangming.getMessage());
				}

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setUpdateTime(formatter.format(new Date()));
				realName.setUserId(userId);
				realName.setCompanyName(companyName);
				realName.setAddressDetailed(addressDetailed);
				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consigneeName);
				realName.setCityId(cityId);
				realName.setDistrictCountyId(districtCountyId);
				realName.setProvincesId(provincesId);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setUserName(user.getUsername());
				// 检查id是否已经存在
				RealName realName1 = realNameMapper.getRealName(userId);
				int resultCount = 0;
				if (realName1 != null) {
					// 不等于null 更新
					realName.setId(realName1.getId());
					resultCount = realNameMapper.updateRealName(realName);
					if (resultCount == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}
				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
				}

				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}

				User currentUser = userMapper.selectUserById(userId);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);
				return ServerResponse.createBySuccess(currentUser);

			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
		} else if (isbusiness == 11) {
			// 找工作
			try {
				eag = Integer.valueOf(params_map.get("eag").toString().trim());
				// 判断年龄
				if (eag < 18 || eag > 60) {
					return ServerResponse.createByErrorMessage(ResponseMessage.NianLiFanWei.getMessage());
				}
				gender = params_map.get("gender").toString().trim();
				// 判断性别
				if (!gender.equals("男") && !gender.equals("女")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.XinBieYouWu.getMessage());
				}

				email = params_map.get("email").toString().trim();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				RealName realName = new RealName();
				realName.setUpdateTime(formatter.format(new Date()));
				realName.setUserId(userId);

				if (email != null) {
					realName.setEmail(email);
				}
				realName.setAddressDetailed(addressDetailed);

				realName.setContact(EncrypDES.encryptPhone(contact));
				realName.setConsigneeName(consigneeName);
				realName.setCityId(cityId);
				realName.setDistrictCountyId(districtCountyId);
				realName.setProvincesId(provincesId);
				realName.setAuthentiCationStatus(1);
				realName.setDetailed(city);
				realName.setEag(eag);
				realName.setGender(gender);
				realName.setUserName(user.getUsername());
				// 检查id是否已经存在
				RealName realName1 = realNameMapper.getRealName(userId);
				int resultCount = 0;
				if (realName1 != null) {
					// 不等于null 更新
					realName.setId(realName1.getId());
					resultCount = realNameMapper.updateRealName(realName);
					if (resultCount == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}
				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
				}

				user.setIsAuthentication(1);
				resultCount = userMapper.update_newRealName(user);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
				}

				User currentUser = userMapper.selectUserById(userId);
				currentUser.setMobilePhone(EncrypDES.decryptPhone(currentUser.getMobilePhone()));
				currentUser.setPassword(null);
				return ServerResponse.createBySuccess(currentUser);

			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.YongHuLeiXingCuoWu.getMessage());
	}

	// 管理员审核获取全部待审批的
	@Override
	public ServerResponse<Object> getRealNameAll(Map<String, Object> params) {
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

		String userName = params.get("userName").toString().trim();
		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}
		Page<RealName> realName_pagePage = new Page<RealName>();

		long zongtiaoshu = realNameMapper.getRealNamePageno(userName, contact);
//		System.out.println(grainAndOilMapper.getGrainAndOilPageno()+"   "+pageSize+"    "+
//				 Math.ceil(grainAndOilMapper.getGrainAndOilPageno()/pageSize)	);

		realName_pagePage.setTotalno(zongtiaoshu);
		realName_pagePage.setPageSize(pageSize);
		realName_pagePage.setCurrentPage(currentPage); // 当前页

		List<RealName> list_realname = new ArrayList();
		List<RealName> list_srelnameall = realNameMapper.getRealNameAll((currentPage - 1) * pageSize, pageSize,
				userName, contact);
		for (RealName realName : list_srelnameall) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			list_realname.add(realName);
		}

		realName_pagePage.setDatas(list_realname);

		return ServerResponse.createBySuccess(realName_pagePage);
	}

	// 实名审核和添加实名
	@Override
	public ServerResponse<Object> examineRealName(User user, Map<String, Object> params, String loginToken) {
		String isArtificialString = params.get("isArtificial").toString().trim();
		if (isArtificialString == null || isArtificialString.contentEquals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
		}
		int isArtificial = Integer.valueOf(isArtificialString);
		if (isArtificial == 1) { // 审批
			String user_beishenhe = params.get("userId").toString().trim();
			if (user_beishenhe == null || user_beishenhe.contentEquals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
			}

			long user_id = Long.valueOf(user_beishenhe);
			User user_shenhe = userMapper.selectUserById(user_id);
			if (user_shenhe == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
			}

			RealName realName = new RealName();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			realName.setExamineTime(formatter.format(new Date()));
			realName.setExamineName(user.getUsername());
			realName.setUserId(user_id);
			int authentiCationStatus = Integer.valueOf(params.get("authentiCationStatus").toString().trim());
			if (authentiCationStatus != 2 && authentiCationStatus != 3) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}
			realName.setAuthentiCationStatus(authentiCationStatus);
			int resultCount = 0;
			if (authentiCationStatus == 2) {

				resultCount = realNameMapper.examineRealName(realName);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}

			} else if (authentiCationStatus == 3) {
				String authentiCationFailure = params.get("authentiCationFailure").toString().trim();
				if (authentiCationFailure == null || authentiCationFailure.contentEquals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ShiBaiYuanYinWeiKong.getMessage());
				}
				realName.setAuthentiCationFailure(authentiCationFailure);
				resultCount = realNameMapper.examineRealName(realName);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
			}
			user_shenhe.setIsAuthentication(authentiCationStatus);
			resultCount = userMapper.update_newRealName(user_shenhe);
			if (resultCount == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}

			// user_shenhe = userMapper.selectUserById(user_shenhe.getId());
			user_shenhe.setMobilePhone(EncrypDES.decryptPhone(user_shenhe.getMobilePhone()));
			user_shenhe.setPassword(null);
			return ServerResponse.createBySuccess(user_shenhe);
		} else if (isArtificial == 2) { // 添加
			String username = params.get("userName").toString().trim();
			if (username == null || username.contentEquals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());
			}
			User user2 = userMapper.checkUsername(username);
			if (user2 == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuIdBuJingCunZai.getMessage());
			}
			String mobilePhone = params.get("mobilePhone").toString().trim();
			if (!mobilePhone.equals(EncrypDES.decryptPhone(user2.getMobilePhone()))) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ZhuoCeShouJiCuoWu.getMessage());
			}

			// 校验是否有特殊字符
			ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
			// 检查是否有非法输入
			if (serverResponse.getStatus() != 0) {
				return ServerResponse.createByErrorMessage(serverResponse.getMsg());
			}

			// 所有商户实名
			String address_detailed = null;
			int provinces_id = 0;
			int city_id = 0;
			int district_county_id = 0;
			String licenseUrl = null; // 营业执照图片url
			String contact = null; // 收/送货联系方式
			String email = null;
			String city = null;
			Integer eag = 0; // 求职年龄
			String gender = null; // 性别
			try {
				provinces_id = Integer.valueOf(params.get("provinces_id").toString().trim());
				city_id = Integer.valueOf(params.get("city_id").toString().trim());
				district_county_id = Integer.valueOf(params.get("district_county_id").toString().trim());
				// 判断省市区id是否正确
				if (provinces_id > 10000 && city_id > 10000 && district_county_id > 10000) {
					city = cityMapper.checkeCity(provinces_id, city_id, district_county_id);
					if (city == null) {
						return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
					}
				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
				}
				eag = Integer.valueOf(params.get("eag").toString().trim());
				// 判断年龄
				if (eag < 18 || eag > 60) {
					return ServerResponse.createByErrorMessage(ResponseMessage.NianLiFanWei.getMessage());
				}

				gender = params.get("gender").toString().trim();
				// 判断性别
				if (!gender.equals("男") && !gender.equals("女")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.XinBieYouWu.getMessage());
				}
				// 图片
				List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("licenseUrl"), List.class,
						Picture.class);
				int list_size = listObj3.size();
				// 把getUse_status()==1 放到这个集合中
				List<Picture> listObj4 = new ArrayList<Picture>();
				int getNum = PictureNum.ShiMingRenZheng.getNum();

				if (list_size > 0) {
					int count = 0;
					for (int a = 0; a < list_size; a++) {
						Picture picture = listObj3.get(a);
						if (listObj3.get(a).getUseStatus() == 1) {
							picture.setUserId(user2.getId());
							picture.setUseStatus(3);

							Picture picture1 = pictureMapper.selectPictureBYid(picture.getId());
							if (!picture.getPictureUrl().equals(picture1.getPictureUrl())) {
								return ServerResponse.createByErrorMessage("图片地址不一致");
							}
							pictureMapper.updatePictureAdmin(picture);
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
				licenseUrl = JsonUtil.obj2StringPretty(listObj4);

				address_detailed = params.get("address_detailed").toString().trim();
				contact = params.get("contact").toString().trim();
				email = params.get("email").toString().trim();

				// 判断手机号是否合法
				serverResponse = LegalCheck.legalCheckMobilePhone(contact);
				if (serverResponse.getStatus() != 0) {
					return ServerResponse.createByErrorMessage(serverResponse.getMsg());
				}
				// 判断必传字段
				if (address_detailed.length() < 101 && address_detailed.length() > 0) {
					RealName realName = new RealName();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					realName.setExamineTime(formatter.format(new Date()));
					realName.setExamineName(user.getUsername());
					realName.setUserId(user2.getId());
					realName.setUserName(user2.getUsername());
					realName.setAuthentiCationStatus(2);
					realName.setCreateTime(formatter.format(new Date()));

					if (email != null) {
						realName.setEmail(email);
					}
					realName.setAddressDetailed(address_detailed);
					realName.setLicenseUrl(licenseUrl);
					realName.setContact(EncrypDES.encryptPhone(contact));
					realName.setConsigneeName(username);
					realName.setCityId(city_id);
					realName.setDistrictCountyId(district_county_id);
					realName.setProvincesId(provinces_id);
					realName.setDetailed(city);
					realName.setEag(eag);
					realName.setGender(gender);
					// 添加
					serverResponse = SetBean.setRole(user2.getRole());
					// 检查用户类型
					if (serverResponse.getStatus() != 0) {
						return ServerResponse.createByErrorMessage(serverResponse.getMsg());
					}
					realName.setUserType(serverResponse.getMsg());
					// 检查id是否已经存在
					if (realNameMapper.isNewRealName(user2.getId()) > 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
					}
					int resultCount = realNameMapper.newRealName(realName);
					if (resultCount == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
					}
					user2.setIsAuthentication(2);
					resultCount = userMapper.update_newRealName(user2);
					if (resultCount == 0) {
						return ServerResponse
								.createByErrorMessage(ResponseMessage.GengXinYongHuXinXiShiBai.getMessage());
					}
					user2.setPassword(null);
					user2.setMobilePhone(mobilePhone);
					return ServerResponse.createBySuccess(user2);
				}
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			} catch (Exception e) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
			}

		}
		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
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
	public ServerResponse<Object> getRealNameById(long id) {
		if (id > 0) {
			RealName realName = realNameMapper.getRealNameById(id);
			if (realName != null) {
				return ServerResponse.createBySuccess(realName);
			} else {
				return ServerResponse.createByError();
			}
		} else {
			return ServerResponse.createByError();
		}
	}

	@Override
	public ServerResponse<Object> getRealNameByIdContact(long id) {
		if (id > 0) {
			RealName realName = realNameMapper.getRealNameByIdContact(id);
			if (realName != null) {
				realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
				return ServerResponse.createBySuccess(realName);
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}
	}

	@Override
	public ServerResponse<String> addOrder(User user, Map<String, Object> params) {
		ServerResponse<Object> serverResponse = getRealName(user);
		if (serverResponse.getStatus() == 0) {
			RealName realName = (RealName) serverResponse.getData();
			if (realName.getIsReceipt() == 2 || realName.getIsReceipt() == 3) {
				return ServerResponse.createByErrorMessage(ResponseMessage.yishijiedian.getMessage());
			}
			String addReceiptTime = DateTimeUtil.dateToAll();
			int result = realNameMapper.addOrder(user.getId(), addReceiptTime);
			if (result != 0) {
				return ServerResponse.createBySuccess();
			}
			return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshimingshixishibai.getMessage());
		}
	}

	// 管理员 获取待添加接单人员
	@Override
	public ServerResponse<Object> admin_select_addOrder(Map<String, Object> params) {
//		 companyName:'',
//         contact: '',
//         currentPage: 1,
//         pageSize: 20,//每页显示的数量
//         value2: '',		
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

		String userName = params.get("userName").toString().trim();
		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		String value2 = params.get("value2").toString().trim();
		String statTimeString = null;
		String endTimeString = null;
		if (value2.length() == 24) {
			statTimeString = value2.substring(1, 11).trim();
			endTimeString = value2.substring(13, 23).trim() + " 23:59:59";
		}

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

		String isReceiptString = params.get("isReceipt").toString().trim();
		Integer isReceipt = -1;
		if (isReceiptString != null && !isReceiptString.equals("")) {
			isReceipt = Integer.valueOf(isReceiptString);
		}

		Page<RealName> realName_pagePage = new Page<RealName>();

		long zongtiaoshu = realNameMapper.admin_select_addOrderNo(userName, contact, statTimeString, endTimeString,
				detailed, isReceipt);

		realName_pagePage.setTotalno(zongtiaoshu);
		realName_pagePage.setPageSize(pageSize);
		realName_pagePage.setCurrentPage(currentPage); // 当前页

		List<RealName> list_realname = new ArrayList();
		List<RealName> list_srelnameall = realNameMapper.admin_select_addOrder((currentPage - 1) * pageSize, pageSize,
				userName, contact, statTimeString, endTimeString, detailed, isReceipt);
		for (RealName realName : list_srelnameall) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			list_realname.add(realName);
		}

		realName_pagePage.setDatas(list_realname);

		return ServerResponse.createBySuccess(realName_pagePage);
	}

	@Override
	public ServerResponse<Object> admin_select_signingOrder(Map<String, Object> params) {

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

		String userName = params.get("userName").toString().trim();
		String contact = params.get("contact").toString().trim();
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}

		String value2 = params.get("value2").toString().trim();
		String statTimeString = null;
		String endTimeString = null;
		if (value2.length() == 24) {
			statTimeString = value2.substring(1, 11).trim();
			endTimeString = value2.substring(13, 23).trim() + " 23:59:59";
		}

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

		String addressDetailed = params.get("addressDetailed").toString().trim();

		String isReceiptString = params.get("isReceipt").toString().trim();
		Integer isReceipt = -1;
		if (isReceiptString != null && !isReceiptString.equals("")) {
			isReceipt = Integer.valueOf(isReceiptString);
		}

		Page<RealName> realName_pagePage = new Page<RealName>();

		long zongtiaoshu = realNameMapper.admin_select_signingOrderNo(userName, contact, statTimeString, endTimeString,
				detailed, addressDetailed, isReceipt);

		realName_pagePage.setTotalno(zongtiaoshu);
		realName_pagePage.setPageSize(pageSize);
		realName_pagePage.setCurrentPage(currentPage); // 当前页

		List<RealName> list_realname = new ArrayList();
		List<RealName> list_srelnameall = realNameMapper.admin_select_signingOrder((currentPage - 1) * pageSize,
				pageSize, userName, contact, statTimeString, endTimeString, detailed, addressDetailed, isReceipt);
		for (RealName realName : list_srelnameall) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			list_realname.add(realName);
		}

		realName_pagePage.setDatas(list_realname);

		return ServerResponse.createBySuccess(realName_pagePage);
	}

	// 更新通知申请接单人员状态
	@Override
	public ServerResponse<String> admin_update_addOrder(String commitAddReceiptName, Map<String, Object> params) {
		String isReceiptString = params.get("isReceipt").toString().trim();
		if (isReceiptString == null || isReceiptString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shenhezhuangt.getMessage());
		}
		int result = 0;
		Integer isReceipt = Integer.valueOf(isReceiptString);

		String addReceiptTime = DateTimeUtil.dateToAll();
		String idString = params.get("id").toString().trim();
		if (idString == null || idString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shimingidbunengweikong.getMessage());
		}
		long id = Long.valueOf(idString);
		if (isReceipt == 4) {
			// 更新并创建
			String qianyueTime = params.get("value1").toString().trim();
			String qianyueDetailed = params.get("addressDetailed").toString().trim();
			if (qianyueTime == null || qianyueDetailed == null || qianyueDetailed.equals("")
					|| qianyueTime.equals("")) {
				return ServerResponse
						.createByErrorMessage(ResponseMessage.qianyueshijianhuodidianbunengkong.getMessage());
			}
			result = realNameMapper.admin_update_addOrder(id, addReceiptTime, qianyueDetailed, qianyueTime, isReceipt,
					commitAddReceiptName);
		} else if (isReceipt == 5) {
			// 更新
			result = realNameMapper.admin_update_addOrder(id, addReceiptTime, null, null, isReceipt,
					commitAddReceiptName);
		} else {
			// 直接返回
			return ServerResponse.createByErrorMessage(ResponseMessage.zhuantaicuowu.getMessage());
		}

		if (result > 0) {
			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
	}

	@Override
	public ServerResponse<Object> admin_select_signingOrderById(long id) {
		RealName realName = realNameMapper.admin_select_signingOrderById(id);
		if (realName == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
		return ServerResponse.createBySuccess(realName);
	}

	@Override
	public ServerResponse<Object> admin_select_realNameByContact(String contact) {
		if (contact.length() != 11 && contact != null && !contact.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
		}
		if (contact.length() == 11) {
			contact = EncrypDES.encryptPhone(contact);
		}
		RealName realName = realNameMapper.admin_select_realNameByContact(contact);
		if (realName != null) {
			return ServerResponse.createBySuccess(realName);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> getRealNameByuserId(long id) {
		if (id > 0) {
			RealName realName = realNameMapper.getRealNameByuserId(id);
			if (realName != null) {
				return ServerResponse.createBySuccess(realName);
			} else {
				return ServerResponse.createByError();
			}
		} else {
			return ServerResponse.createByError();
		}
	}

	@Override
	public ServerResponse<Object> admin_guangggao_realName(String userName) {

		if (userName == null || userName.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.gongsimingchengkong.getMessage());
		}

		RealName realName = realNameMapper.admin_select_realNameByUsername(userName);
		
		if (realName != null) {
			realName.setContact(EncrypDES.decryptPhone(realName.getContact()));
			return ServerResponse.createBySuccess(realName);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.weichadaojieguo.getMessage());
	}

}
