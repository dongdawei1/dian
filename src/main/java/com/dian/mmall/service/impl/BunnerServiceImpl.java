package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.BunnerMapper;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.OrderExampleTimerMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.Page;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.banner.DibuBunner;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.FabuUtil;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.Strin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("bunnerService")
public class BunnerServiceImpl implements BunnerService {
	@Autowired
	private BunnerMapper bunnerMapper;
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private OrderExampleTimerMapper omap;

	@Autowired
	private IUserService iUserService;

	@Override
	public ServerResponse<Object> getBunnerList(Integer role, Integer permissionid, Integer bunnerType, long userId) {

		String detailed = "%" + realNameMapper.getDetailed(userId) + "%";
		String date = DateTimeUtil.dateToAll();
		List<DibuBunner> listBunner = bunnerMapper.getBunnerList(role, permissionid, bunnerType, detailed, date);
		int size = listBunner.size();

		if (size == 0) {
			listBunner = bunnerMapper.getBunnerList(role, null, bunnerType, detailed, date);
			size = listBunner.size();
			if (size == 0 && detailed != null) {

				// 获得第一个点的位置
				int index = detailed.indexOf("/");
				// 根据第一个点的位置 获得第二个点的位置
				index = detailed.indexOf("/", index + 1);
				// 根据第二个点的位置，截取 字符串。得到结果 result
				String result = detailed.substring(0, index) + "%";

				listBunner = bunnerMapper.getBunnerList(role, null, bunnerType, result, date);
			}
			return ServerResponse.createBySuccess(listBunner);
		} else {
			return ServerResponse.createBySuccess(listBunner);
		}

	}

	@Override
	public int getguanggaocount(long tableId, int tableName) {
		return bunnerMapper.getguanggaocount(tableId, tableName);
	}

	@Override
	public ServerResponse<Object> isguanggao(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			int bunnerType = Integer.parseInt(params.get("bunnerType").toString().trim());

			if (bunnerType == 0) {
				map.put("bunnerType", "首页弹窗");
			} else if (bunnerType == 1) {
				map.put("bunnerType", "首页轮播");
			} else if (bunnerType == 2) {
				map.put("bunnerType", "详情页轮播");
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
			}

			// 是否是默认 0是1不是，先查1
			int moren = Integer.parseInt(params.get("moren").toString().trim());
			if (moren == 0) {
				map.put("moren", "默认显示");
			} else if (moren == 1) {
				map.put("moren", "非默认显示");
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
			}

			int fanwei = Integer.parseInt(params.get("fanwei").toString().trim());
			if (fanwei == 3) {
				fanwei = 1;
			} else if (fanwei == 4) {
				fanwei = 2;
			}

			if (fanwei == 0) {
				String detailed = null;
				map.put("detailed", "全国");
				List<DibuBunner> lsTanchuang = bunnerMapper.getisguanggao(detailed, fanwei, bunnerType, moren);
				map.put("listdi", lsTanchuang);
			} else if (fanwei == 1) {
				String detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				if (detailed.equals("%%")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
				}
				map.put("detailed", detailed);
				List<DibuBunner> lsTanchuang = bunnerMapper.getisguanggao(detailed, fanwei, bunnerType, moren);
				map.put("listdi", lsTanchuang);
			} else if (fanwei == 2) {
				String detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
				if (detailed == null) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
				}
				map.put("detailed", detailed);
				List<DibuBunner> lsTanchuang = bunnerMapper.getisguanggao(detailed, fanwei, bunnerType, moren);
				map.put("listdi", lsTanchuang);
			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
			}

//			 bunnerType: '1',//0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
//	          fanwei:'2',//范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区
			return ServerResponse.createBySuccess(map);
		}

		return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());

	}

	@Override
	public ServerResponse<String> crguanggao(User user, Map<String, Object> params) {

//         permissionid: '',//哪个菜单下  //o

//

//         imgUrl: [],//图片
		// url: '',//跳转链接 //o
//      dibuBunnerbiaoti: '',//标题    //o

		// long userId=Long.parseLong();
		int tablenameid = Integer.parseInt(params.get("tablenameid").toString().trim());
		long tableId = Long.parseLong(params.get("tableId").toString().trim());
		long userId = Long.parseLong(params.get("userId").toString().trim());

		String termOfValidity = omap.getcoutn(tablenameid, tableId, userId) + " 00:00:00";
		// {userId=1, permissionid=4, tableId=1, url=/details/foodAndGrainDetails/1/4,
		// dibuBunnerbiaoti=1222222, bunnerType=1, moren=1, fanwei=2,
		// selectedOptions=[310000, 310100, 310101],
		// imgUrl=[{pictureName=123.jpeg,
		// pictureUrl=http://localhost:8080/img/20200307210743204.jpeg, useStatus=1,
		// id=683, userName=20200307210743204.jpeg, userId=1}],
		// value1=[2020-03-13 00:00:00, 2020-03-14 00:00:00], tablenameid=4}
		List<String> value1_list = JsonUtil.list2Obj((List<String>) params.get("value1"), List.class, String.class);
		if (value1_list.size() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabushijiancuowu.getMessage());
		}
		String startTime = value1_list.get(0);
		String endTime = value1_list.get(1);

		// 判断时间是否小于当前时间
		ServerResponse<Object> serverResponseObject = DateTimeUtil.isPastDate(startTime, 1);
		if (serverResponseObject.getStatus() == 0) {
			if (!(boolean) serverResponseObject.getData()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuowo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}
		serverResponseObject = DateTimeUtil.isPastDate(endTime, 1);
		if (serverResponseObject.getStatus() == 0) {
			if (!(boolean) serverResponseObject.getData()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuijieshushicuowo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}
		// 判断时间 是否小于 发布结束时间
		serverResponseObject = DateTimeUtil.isdaxiao(startTime, termOfValidity);
		if (serverResponseObject.getStatus() == 0) {
			if ((boolean) serverResponseObject.getData()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}
		serverResponseObject = DateTimeUtil.isdaxiao(endTime, termOfValidity);
		if (serverResponseObject.getStatus() == 0) {
			if ((boolean) serverResponseObject.getData()) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuo.getMessage());
			}
		} else {
			return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
		}

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);

		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);

			String detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
			if (detailed == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.ChengShichaxunshibai.getMessage());
			}

//	         bunnerType: 1,//0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
//	         moren: 1,//是否是默认 0是1不是，先查1

			int bunnerType = Integer.parseInt(params.get("bunnerType").toString().trim());
			if (bunnerType != 0 && bunnerType != 1 && bunnerType != 2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuweizhi.getMessage());
			}
			int moren = Integer.parseInt(params.get("moren").toString().trim());
			if (moren != 0 && moren != 1) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixing.getMessage());
			}
			int fanwei = Integer.parseInt(params.get("fanwei").toString().trim());
			if (fanwei != 0 && fanwei != 1 && fanwei != 2 && fanwei != 3 && fanwei != 4) {
				return ServerResponse.createByErrorMessage(ResponseMessage.fabuchengshi.getMessage());
			}
			// 范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区
			if (fanwei == 3) {
				fanwei = 1;
			} else if (fanwei == 4) {
				fanwei = 2;
			}

			String dibuBunnerbiaoti = params.get("dibuBunnerbiaoti").toString().trim();
			String url = params.get("url").toString().trim();
			if (dibuBunnerbiaoti == null || url == null || dibuBunnerbiaoti.equals("") || url.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shurucuowo.getMessage());
			}

			// 图片
			List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("imgUrl"), List.class,
					Picture.class);
			int list_size = listObj3.size();
			// 把getUse_status()==1 放到这个集合中
			List<Picture> listObj4 = new ArrayList<Picture>();
			int getNum = PictureNum.guanggao.getNum();
			if (list_size > 0) {
				int c = 0;
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
						c += 1;
					}
				}
				if (c > getNum) {
					// 判断没有删除的图片是否大于规定
					return ServerResponse.createByErrorMessage("图片数量不能超过 " + getNum + "张");
				}
				if (c == 0) {
					return ServerResponse.createByErrorMessage("图片不能为空");
				}
			} else {
				return ServerResponse.createByErrorMessage("图片不能为空");
			}
			//类型== 其他表的releaseType 
			int permissionid = Integer.parseInt(params.get("permissionid").toString().trim());

			// private String createTime; //活动创建时间
//			private String updateTime;
			String createTime = DateTimeUtil.dateToAll();
			DibuBunner dibuBunner = new DibuBunner();
			if (tablenameid == 1) {
				dibuBunner.setReleaseType("职位信息");
			} else if (tablenameid == 2) {
				dibuBunner.setReleaseType(FabuUtil.releaseTypeString(permissionid));
			} else if (tablenameid == 3) {
				dibuBunner.setReleaseType("简历信息");
			} else if (tablenameid == 9) {
				dibuBunner.setReleaseType("批发信息");
			}

			dibuBunner.setTableId(tableId);
			dibuBunner.setCreateId(user.getId());
			dibuBunner.setUrl(url);
			dibuBunner.setImgUrl(JsonUtil.obj2StringPretty(listObj4));
			dibuBunner.setExamineName(user.getUsername());
			dibuBunner.setStartTime(startTime);
			dibuBunner.setEndTime(endTime);
			dibuBunner.setBunnerStatus(0);
			dibuBunner.setDetailed(detailed);
			dibuBunner.setUserId(userId);
			dibuBunner.setMoren(moren);
			dibuBunner.setFanwei(fanwei);
			dibuBunner.setDibuBunnerbiaoti(dibuBunnerbiaoti);
			dibuBunner.setPermissionid(permissionid);
			dibuBunner.setBunnerType(bunnerType);
			dibuBunner.setTablenameid(tablenameid);
			dibuBunner.setCreateTime(createTime);
			dibuBunner.setUpdateTime(createTime);
			// 如果是moren==1要查询没有重复
			if (moren == 1) {
				int co = 0;
				//// bunnerType 0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
//		         fanwei: 2,//范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区

				if (bunnerType == 0) {
					if (fanwei == 0) {
						// 全国
						co = bunnerMapper.quanguoshouyetanchuan(startTime, endTime);
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					} else if (fanwei == 1) {
						// 全国
						co = bunnerMapper.quanguoshouyetanchuan(startTime, endTime);
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						// 全省
						co = bunnerMapper.quanshihexianshouyetanchuan(startTime, endTime,
								"%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%");
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}

					} else if (fanwei == 2) {
						co = bunnerMapper.quanguoshouyetanchuan(startTime, endTime);
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						co = bunnerMapper.quanshishouyetanchuan(startTime, endTime,
								"%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%");
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						co = bunnerMapper.quanqushouyetanchuan(startTime, endTime, detailed);

						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					}
				} else if (bunnerType == 1 || bunnerType == 2) {
					if (fanwei == 0) {
						co = bunnerMapper.guoshouyelunbo(startTime, endTime, bunnerType);
						if (co > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					} else if (fanwei == 1) {
						co = bunnerMapper.guoshou(startTime, endTime, bunnerType);
						if (co > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						int shi = bunnerMapper.shihexianshouyelunbo(startTime, endTime, bunnerType,
								"%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%");
						if (co + shi > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					} else if (fanwei == 2) {
						co = bunnerMapper.guoshou(startTime, endTime, bunnerType);
						if (co > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						int shi = bunnerMapper.shishouyelunbo(startTime, endTime, bunnerType,
								"%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%");
						if (co + shi > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						int qu = bunnerMapper.qushouyelunbo(startTime, endTime, bunnerType, detailed);
						if (co + shi + qu > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					}
				}
				bunnerMapper.creatdu(dibuBunner);
				return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());

			}
			// 如果是moren==0直接插入，默认可以重复使用时查询最新
			bunnerMapper.creatdu(dibuBunner);
			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
	}

	@Override
	public ServerResponse<Object> agetguangaoAll(Map<String, Object> params) {
//		  userName: '',
//          selectedOptions: [],//城市detailed
//          fanwei:'',
//          //分页开始
//          currentPage: 1,
//          pageSize: 20,//每页显示的数量

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

		int fanwei = -1;
		String string = params.get("fanwei").toString().trim();
		if (string != null && !string.equals("")) {
			fanwei = Integer.parseInt(string);
		}

		String detailed = null;

		if (fanwei != 0) {
			List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
					List.class);
			if (selectedOptions_list.size() == 3) {
				Integer provincesId = selectedOptions_list.get(0);
				Integer cityId = selectedOptions_list.get(1);
				Integer districtCountyId = selectedOptions_list.get(2);
				if (fanwei == 2) {
					detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
				}
				detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				if (detailed.length() < 2) {
					ServerResponse.createByErrorMessage(ResponseMessage.cheshichaxunshibai.getMessage());
				}
			}
		}

		String userName = params.get("userName").toString().trim();
		long userId = 0;
		if (userName != null && !userName.equals("")) {
			ServerResponse<Object> serverResponse = iUserService.getuserbyname(userName);
			if (serverResponse.getStatus() == 0) {
				userId = ((User) serverResponse.getData()).getId();
			} else {
				ServerResponse.createByErrorMessage(ResponseMessage.huoquxinxishibai.getMessage());
			}

		}

		Page<DibuBunner> equipment_pagePage = new Page<DibuBunner>();

		long zongtiaoshu = bunnerMapper.agetguangaoAll(userId, detailed, fanwei);
		if (zongtiaoshu == 0) {
			ServerResponse.createByErrorMessage(ResponseMessage.jieguokong.getMessage());
		}
		equipment_pagePage.setTotalno(zongtiaoshu);
		equipment_pagePage.setPageSize(pageSize);
		equipment_pagePage.setCurrentPage(currentPage); // 当前页

		// 查询list
		List<DibuBunner> dibuBunner = bunnerMapper.agetguangAll((currentPage - 1) * pageSize, pageSize, userId,
				detailed, fanwei);

		if (userId != 0) {
			for (int a = 0; a < dibuBunner.size(); a++) {
				DibuBunner di = dibuBunner.get(a);
				di.setIntroduceList(userName);
				dibuBunner.set(a, di);
			}
		} else {
			for (int a = 0; a < dibuBunner.size(); a++) {
				DibuBunner di = dibuBunner.get(a);
				ServerResponse<Object> serverResponse = iUserService.selectUserById(di.getUserId());
				if (serverResponse.getStatus() == 0) {
					di.setIntroduceList(((User) serverResponse.getData()).getUsername());
				}
				dibuBunner.set(a, di);
			}
		}

		equipment_pagePage.setDatas(dibuBunner);
		return ServerResponse.createBySuccess(equipment_pagePage);

	}

	@Override
	public ServerResponse<String> aupguangao(User user, Map<String, Object> params) {
		String typeString = params.get("type").toString().trim();
		String idString = params.get("id").toString().trim();
		if (typeString != null && !typeString.equals("")) {
			int type = Integer.valueOf(typeString);
			// 1编辑，2马上发布，3关闭发布
			int retult = 0;
			String updateTime = DateTimeUtil.dateToAll();
			int id = Integer.valueOf(idString);
			if (type == 3) {
				retult = bunnerMapper.guanguanggao(updateTime, user.getId(), user.getUsername(), id, 3);
			} else if (type == 1 || type == 2) {
				if (type == 2) {
					ServerResponse<String> response = checke(id, null, type);
					if (response.getStatus() != 0) {
						return response;
					}
					retult = bunnerMapper.guanguanggao(updateTime, user.getId(), user.getUsername(), id, 1);
				} else if (type == 1) {
					int tablenameid = Integer.parseInt(params.get("tablenameid").toString().trim());
					long tableId = Long.parseLong(params.get("tableId").toString().trim());
					long userId = Long.parseLong(params.get("userId").toString().trim());

					String termOfValidity = omap.getcoutn(tablenameid, tableId, userId) + " 00:00:00";
					List<String> value1_list = JsonUtil.list2Obj((List<String>) params.get("value1"), List.class,
							String.class);
					if (value1_list.size() != 2) {
						return ServerResponse.createByErrorMessage(ResponseMessage.fabushijiancuowu.getMessage());
					}
					String startTime = value1_list.get(0);
					String endTime = value1_list.get(1);

					// 判断时间是否小于当前时间
					ServerResponse<Object> serverResponseObject = DateTimeUtil.isPastDate(startTime, 1);
					if (serverResponseObject.getStatus() == 0) {
						if (!(boolean) serverResponseObject.getData()) {
							return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuowo.getMessage());
						}
					} else {
						return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
					}
					serverResponseObject = DateTimeUtil.isPastDate(endTime, 1);
					if (serverResponseObject.getStatus() == 0) {
						if (!(boolean) serverResponseObject.getData()) {
							return ServerResponse
									.createByErrorMessage(ResponseMessage.fabuijieshushicuowo.getMessage());
						}
					} else {
						return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
					}
					// 判断时间 是否小于 发布结束时间
					serverResponseObject = DateTimeUtil.isdaxiao(startTime, termOfValidity);
					if (serverResponseObject.getStatus() == 0) {
						if ((boolean) serverResponseObject.getData()) {
							return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuo.getMessage());
						}
					} else {
						return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
					}
					serverResponseObject = DateTimeUtil.isdaxiao(endTime, termOfValidity);
					if (serverResponseObject.getStatus() == 0) {
						if ((boolean) serverResponseObject.getData()) {
							return ServerResponse.createByErrorMessage(ResponseMessage.fabuishicuo.getMessage());
						}
					} else {
						return ServerResponse.createByErrorMessage(serverResponseObject.getMsg());
					}

					int fanwei = Integer.parseInt(params.get("fanwei").toString().trim());
					if (fanwei != 0 && fanwei != 1 && fanwei != 2 && fanwei != 3 && fanwei != 4) {
						return ServerResponse.createByErrorMessage(ResponseMessage.fabuchengshi.getMessage());
					}
					DibuBunner cheBunner = cheBunner = bunnerMapper.agetguanggao(id);
					String detailed = null;
					if (fanwei == 3 || fanwei == 4) {

						List<Integer> selectedOptions_list = JsonUtil
								.string2Obj(params.get("selectedOptions").toString().trim(), List.class);

						if (selectedOptions_list.size() == 3) {
							Integer provincesId = selectedOptions_list.get(0);
							Integer cityId = selectedOptions_list.get(1);
							Integer districtCountyId = selectedOptions_list.get(2);

							detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);
							if (detailed == null) {
								return ServerResponse
										.createByErrorMessage(ResponseMessage.ChengShichaxunshibai.getMessage());
							}
						}
					} else {
						detailed = cheBunner.getDetailed();
					}

					// 范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区
					if (fanwei == 3) {
						fanwei = 1;
					} else if (fanwei == 4) {
						fanwei = 2;
					}
//				         bunnerType: 1,//0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
//				         moren: 1,//是否是默认 0是1不是，先查1

					int bunnerType = Integer.parseInt(params.get("bunnerType").toString().trim());
					if (bunnerType != 0 && bunnerType != 1 && bunnerType != 2) {
						return ServerResponse.createByErrorMessage(ResponseMessage.fabuweizhi.getMessage());
					}
					int moren = Integer.parseInt(params.get("moren").toString().trim());
					if (moren != 0 && moren != 1) {
						return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixing.getMessage());
					}

					String dibuBunnerbiaoti = params.get("dibuBunnerbiaoti").toString().trim();
					String url = params.get("url").toString().trim();
					if (dibuBunnerbiaoti == null || url == null || dibuBunnerbiaoti.equals("") || url.equals("")) {
						return ServerResponse.createByErrorMessage(ResponseMessage.shurucuowo.getMessage());
					}

					// 检查图片
					ServerResponse<String> serverResponseString = setPictureUrl(
							(ArrayList<Picture>) params.get("imgUrl"), cheBunner.getImgUrl());
					if (serverResponseString.getStatus() != 0) {
						return serverResponseString;
					}

					DibuBunner dibuBunner = new DibuBunner();
					dibuBunner.setCreateId(user.getId());
					dibuBunner.setImgUrl(serverResponseString.getMsg());
					dibuBunner.setExamineName(user.getUsername());
					dibuBunner.setStartTime(startTime);
					dibuBunner.setEndTime(endTime);
					dibuBunner.setDetailed(detailed);
					dibuBunner.setMoren(moren);
					dibuBunner.setFanwei(fanwei);
					dibuBunner.setDibuBunnerbiaoti(dibuBunnerbiaoti);
					dibuBunner.setBunnerType(bunnerType);
					dibuBunner.setUpdateTime(updateTime);
					dibuBunner.setId(id);
					ServerResponse<String> response = checke(id, dibuBunner, type);
					if (response.getStatus() != 0) {
						return response;
					}

					retult = bunnerMapper.endbunner(dibuBunner);
				}

			} else {
				return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
			}
			if (retult > 0) {
				try {
					List<Picture> listObj4 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("imgUrl"), List.class,
							Picture.class);
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
				return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());

			}
			return ServerResponse.createByErrorMessage(ResponseMessage.caozuoshibai.getMessage());

		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
		}

	}

	private ServerResponse<String> checke(long id, DibuBunner dibuBunner, int type) {
		// 1编辑，2马上开启发布
		DibuBunner cheBunner = cheBunner = bunnerMapper.agetguanggao(id);
		if (cheBunner == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.chaxunshibai.getMessage());
		}
		if (type == 2) {
			if (cheBunner.getMoren() == 0) {
				return ServerResponse.createBySuccess();
			}
			return checkefabu(cheBunner);
		} else if (type == 1) {
			return checkefabu(dibuBunner);
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.caozuoleixincuowu.getMessage());
	}

	private ServerResponse<String> checkefabu(DibuBunner dibuBunner) {
		int co = 0;
		//// bunnerType 0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
//         fanwei: 2,//范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区

		int bunnerType = dibuBunner.getBunnerType();
		int fanwei = dibuBunner.getFanwei();
		String startTime = dibuBunner.getStartTime();
		String endTime = dibuBunner.getEndTime();
		String detailed = dibuBunner.getDetailed();
		String shengdetailed = null;
		ServerResponse<String> response = Strin.setTocken(detailed, 2);
		if (response.getStatus() == 0) {
			shengdetailed = "%" + response.getMsg() + "%";
		}
		long id = dibuBunner.getId();
		if (bunnerType == 0) {
			if (fanwei == 0) {
				// 全国
				co = bunnerMapper.quanguoshouyetanchuan1(startTime, endTime, id);
				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
			} else if (fanwei == 1) {
				// 全国
				co = bunnerMapper.quanguoshouyetanchuan1(startTime, endTime, id);
				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				// 全省
				co = bunnerMapper.quanshihexianshouyetanchuan1(startTime, endTime, shengdetailed, id);
				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}

			} else if (fanwei == 2) {
				co = bunnerMapper.quanguoshouyetanchuan1(startTime, endTime, id);
				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				co = bunnerMapper.quanshishouyetanchuan1(startTime, endTime, shengdetailed, id);
				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				co = bunnerMapper.quanqushouyetanchuan1(startTime, endTime, detailed, id);

				if (co > 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
			}
		} else if (bunnerType == 1 || bunnerType == 2) {
			if (fanwei == 0) {
				co = bunnerMapper.guoshouyelunbo1(startTime, endTime, bunnerType, id);
				if (co > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
			} else if (fanwei == 1) {
				co = bunnerMapper.guoshou1(startTime, endTime, bunnerType, id);
				if (co > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				int shi = bunnerMapper.shihexianshouyelunbo1(startTime, endTime, bunnerType, shengdetailed, id);
				if (co + shi > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
			} else if (fanwei == 2) {
				co = bunnerMapper.guoshou1(startTime, endTime, bunnerType, id);
				if (co > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				int shi = bunnerMapper.shishouyelunbo1(startTime, endTime, bunnerType, shengdetailed, id);
				if (co + shi > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
				int qu = bunnerMapper.qushouyelunbo1(startTime, endTime, bunnerType, detailed, id);
				if (co + shi + qu > 2) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
				}
			}
		}
		return ServerResponse.createBySuccess();
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
	public ServerResponse<Object> getpguang(User user, Integer permissionid, Integer bunnerType, String appid) {
		String detailed = realNameMapper.getDetailed(user.getId());
		if (detailed == null || detailed.equals("")) {
			//return ServerResponse.createByError(); TODO未实名也给默认返回
			detailed="北京市/市辖区/东城区";
			
		}

		if (bunnerType != 0 && bunnerType != 1 && bunnerType != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.qingqiuxinxiyouwu.getMessage());
		}

		// bunnerType 0首页弹窗，1首页轮播，2详情页轮播
		int fanwei = 0; // 0全国优先级最高，1全市，2全区
		int moren = 1; // 1优先，0默认
		// String date = DateTimeUtil.dateToAll();

		List<DibuBunner> listBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, null);
		int size = listBunner.size();
		if (bunnerType == 0) {
			if (size > 0) {
				return cekckappid(appid, listBunner);
			}
			String shengdetailed = "%" + Strin.setTocken(detailed, 2).getMsg() + "%";
			fanwei = 1;
			listBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, shengdetailed);
			size = listBunner.size();
			if (size > 0) {
				return cekckappid(appid, listBunner);
			}
			String qudetailed = "%" + detailed + "%";
			fanwei = 2;
			listBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, qudetailed);
			size = listBunner.size();
			if (size > 0) {
				return cekckappid(appid, listBunner);
			}
			fanwei = 0; // 0全国优先级最高，1全市，2全区
			moren = 0;
			listBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, null);
			size = listBunner.size();
			if (size > 0) {
				return cekckappid(appid, listBunner);
			}
			return ServerResponse.createBySuccess(listBunner);
		}
		if (bunnerType == 1 || bunnerType == 2) {
			if (size > 2) {
				return cekckappid(appid, listBunner);
			}
			String shengdetailed = "%" + Strin.setTocken(detailed, 2).getMsg() + "%";
			fanwei = 1;
			List<DibuBunner> shenglistBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, shengdetailed);
			int shengsize = shenglistBunner.size();
			if (size + shengsize > 2) {
				listBunner.addAll(shenglistBunner);
				return cekckappid(appid, listBunner);
			}
			String qudetailed = "%" + detailed + "%";
			fanwei = 2;
			List<DibuBunner> qulistBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, qudetailed);
			int qusize = qulistBunner.size();
			if (size + shengsize + qusize > 2) {
				listBunner.addAll(shenglistBunner);
				listBunner.addAll(qulistBunner);
				return cekckappid(appid, listBunner);
			}

			fanwei = 0; // 0全国优先级最高，1全市，2全区
			moren = 0;
			List<DibuBunner> molistBunner = bunnerMapper.getpguang(bunnerType, fanwei, moren, null);
			int mosize = molistBunner.size();
			if (mosize > 0) {
				if (size + shengsize + qusize == 0) {
					return cekckappid(appid, molistBunner);
				} else {
					for (DibuBunner d : molistBunner) {
						listBunner.add(d);
						if (listBunner.size() > 2) {
							break;
						}
					}
					return cekckappid(appid, listBunner);
				}
			}
			if (size + shengsize + qusize > 0) {
				listBunner.addAll(shenglistBunner);
				listBunner.addAll(qulistBunner);
				return cekckappid(appid, listBunner);
			}
			return ServerResponse.createBySuccess(listBunner);

		}

		return ServerResponse.createByError();
	}

	private ServerResponse<Object> cekckappid(String appid, List<DibuBunner> listBunner) {
		if (appid.equals(Const.APPAPPIDP)) {
			return ServerResponse.createBySuccess(listBunner);
		} else if (appid.equals(Const.APPAPPIDA)) {
			for (int a = 0; a < listBunner.size(); a++) {
				DibuBunner bunner = listBunner.get(a);
				String string=bunner.getUrl();
				bunner.setUrl(Strin.setTockenapp(bunner.getUrl(), 2).getMsg());
				listBunner.set(a, bunner);
			}
			return ServerResponse.createBySuccess(listBunner);
		}
		return ServerResponse.createByError();
	}

}
