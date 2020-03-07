package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.BunnerMapper;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.OrderExampleTimerMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.releaseDao.MenuAndRenovationAndPestControlMapper;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.banner.DibuBunner;
import com.dian.mmall.pojo.chuzufang.Rent;
import com.dian.mmall.pojo.gongfu.DepartmentStore;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;
import com.dian.mmall.service.BunnerService;
import com.dian.mmall.service.impl.releaseimpl.MenuAndRenovationAndPestControlServiceImpl;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.JsonUtil;

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
	private MenuAndRenovationAndPestControlMapper menuAndRenovationAndPestControlMapper;

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
			if(fanwei==3) {
				fanwei=1;
			}else if(fanwei==4) {
				fanwei=2;
			}
			
			if (fanwei == 0) {
				String detailed = null;
				map.put("detailed", "全国");
				List<DibuBunner> lsTanchuang = bunnerMapper.getisguanggao(detailed, fanwei, bunnerType, moren);
				map.put("listdi", lsTanchuang);
			} else if (fanwei == 1 ) {
				String detailed = "%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%";
				if (detailed.equals("%%")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
				}
				map.put("detailed", detailed);
				List<DibuBunner> lsTanchuang = bunnerMapper.getisguanggao(detailed, fanwei, bunnerType, moren);
				map.put("listdi", lsTanchuang);
			} else if (fanwei == 2 ) {
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
			//范围范围 0全国优先级最高，1全市，2全区 ,3手动全市省，4手动县区
			if(fanwei==3) {
				fanwei=1;
			}else if(fanwei==4) {
				fanwei=2;
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
			int permissionid = Integer.parseInt(params.get("permissionid").toString().trim());

			// private String createTime; //活动创建时间
//			private String updateTime;
			String createTime = DateTimeUtil.dateToAll();
			DibuBunner dibuBunner = new DibuBunner();
			if (tablenameid == 1) {
				dibuBunner.setReleaseType("职位信息");
			} else if (tablenameid == 2) {
				dibuBunner.setReleaseType("出租信息");
			} else if (tablenameid == 3) {
				dibuBunner.setReleaseType("简历信息");
			} else if (tablenameid == 4) {
				dibuBunner.setReleaseType("零售信息");
			} else if (tablenameid == 5) {
				dibuBunner.setReleaseType("百货信息");
			} else if (tablenameid == 6) {
				dibuBunner.setReleaseType("酒水饮料消毒餐具");
			} else if (tablenameid == 7) {
				dibuBunner.setReleaseType("新旧电器维修");
			} else if (tablenameid == 8) {
				dibuBunner.setReleaseType("装修灭虫广告牌");
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
						co = bunnerMapper.quanguoshouyetanchuan(startTime, endTime);
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					} else if (fanwei == 1) {
						co = bunnerMapper.quanguoshouyetanchuan(startTime, endTime);
						if (co > 0) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						co = bunnerMapper.quanshishouyetanchuan(startTime, endTime,
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
						co = bunnerMapper.guoshouyelunbo(startTime, endTime, bunnerType);
						if (co > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
						int shi = bunnerMapper.shishouyelunbo(startTime, endTime, bunnerType,
								"%" + cityMapper.checkeCityTuo(provincesId, cityId) + "%");
						if (co + shi > 2) {
							return ServerResponse.createByErrorMessage(ResponseMessage.ciquyushijianduan.getMessage());
						}
					} else if (fanwei == 2) {
						co = bunnerMapper.guoshouyelunbo(startTime, endTime, bunnerType);
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

}
