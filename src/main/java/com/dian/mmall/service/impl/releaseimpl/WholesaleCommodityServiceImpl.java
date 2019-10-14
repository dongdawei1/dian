package com.dian.mmall.service.impl.releaseimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.dao.ServiceTypeMapper;
import com.dian.mmall.dao.releaseDao.EvaluateMapper;
import com.dian.mmall.dao.releaseDao.WholesaleCommodityMapper;
import com.dian.mmall.pojo.WholesaleCommodity;
import com.dian.mmall.pojo.jiushui.WineAndTableware;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.WholesaleCommodityService;
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
@Service("wholesaleCommodity")
public class WholesaleCommodityServiceImpl implements WholesaleCommodityService {
	@Autowired
	private PictureMapper pictureMapper;
	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private ServiceTypeMapper serviceTypeMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private EvaluateMapper evaluateMapper;
	@Autowired
	private WholesaleCommodityMapper wholesaleCommodityMapper;

	@Override
	public ServerResponse<String> create_wholesaleCommodity(User user, Map<String, Object> params) {
		ServerResponse<Object> serverResponse = check_evaluate(user, params, 1);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		Map<String, Object> map = (Map<String, Object>) serverResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		WholesaleCommodity wholesaleCommodity = (WholesaleCommodity) BeanMapConvertUtil
				.convertMap(WholesaleCommodity.class, map);
		// {result=true, message=验证通过} 返回结果
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(wholesaleCommodity);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = wholesaleCommodityMapper.create_wholesaleCommodity(wholesaleCommodity);
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

	public ServerResponse<Object> check_evaluate(User currentUser, Map<String, Object> params, int type) {
		// type 1 新建，2为编辑
		// 判断用户id与 tocken是否一致
		// 判断是否实名
		if (currentUser.getIsAuthentication() != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.yonghuweishiming.getMessage());
		}

		// 判断非法输入
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		long userId = currentUser.getId();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);

		String createTime = DateTimeUtil.dateToAll();

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		if (releaseType != 4 && releaseType != 5 && releaseType != 6 && releaseType != 9 && releaseType != 29) {
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
		int count = 0;
		if (type == 1) { // TODO新建时才检查总数
			// 判断是否超过可以发布的总数
			count = wholesaleCommodityMapper.countNum(releaseType, userId);
			if (count > 50) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chaoguofabuzongshu.getMessage());
			}
			map.put("createTime", createTime);
		}
		map.put("releaseType", releaseType);
		map.put("updateTime", createTime);

		if (type == 1) { // 新建
			// 图片
			List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
					Picture.class);
			int list_size = listObj3.size();
			// 把getUse_status()==1 放到这个集合中
			List<Picture> listObj4 = new ArrayList<Picture>();
			int getNum = PictureNum.pifashangpin.getNum();
			if (list_size > 0) {
				count = 0;
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

			map.put("pictureUrl", JsonUtil.obj2StringPretty(listObj4));
		}

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(currentUser.getId());
		if (realName != null) {
			map.put("realNameId", realName.getId());
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}

		String serviceType = params.get("serviceType").toString().trim();
		if (serviceType == null || serviceType.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinmingkong.getMessage());
		}
		count = serviceTypeMapper.getserviceTypeNameCount(serviceType, 2);
		if (count == 0) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shangpinchaxunshibai.getMessage());
		}
		map.put("serviceType", serviceType);
		map.put("releaseTitle", params.get("releaseTitle").toString().trim());

		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确

			map.put("detailed", cityMapper.checkeCity(provincesId, cityId, districtCountyId));
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.chengsshicuowo.getMessage());
		}
		map.put("serviceDetailed", params.get("serviceDetailed").toString().trim());

		String commodityPackingString = params.get("commodityPacking").toString().trim();
		if (commodityPackingString == null || commodityPackingString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshikong.getMessage());
		}
		int commodityPacking = Integer.valueOf(commodityPackingString);
		if (commodityPacking != 1 && commodityPacking != 2 && commodityPacking != 3) {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshicuowo.getMessage());
		}

		map.put("commodityPacking", commodityPacking);

//		 specifi:2,//包装/规格  单位 散装,1 g,  2 kg ,3 ML,4 L ,  commoditySpecifications:'散装',//产品规格
		String specifiString = params.get("specifi").toString().trim();
		if (specifiString == null || specifiString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danweikong.getMessage());
		}
		int specifi = Integer.valueOf(specifiString);
		if (specifi == 1) {
			specifiString = "g";
		} else if (specifi == 2) {
			specifiString = "kg";
		} else if (specifi == 3) {
			specifiString = "ML";
		} else if (specifi == 4) {
			specifiString = "L";
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.baozhuangfangshicuowo.getMessage());
		}

		String commoditySpecifications = null;
		boolean b = false;
		if (commodityPacking != 1) {
			String cationsString = params.get("cations").toString().trim();
			if (cationsString == null || cationsString.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigekong.getMessage());
			}
			b = LegalCheck.isNumericInt(cationsString);
			if (b == false) {
				return ServerResponse.createByErrorMessage(ResponseMessage.guigecuowo.getMessage());
			}
			int cations = Integer.valueOf(cationsString);
			commoditySpecifications = cations + specifiString;
		}

		// 判断 选择的包装方式与单位是否统一
		if (commodityPacking == 1) {
			if (specifi != 2) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
			commoditySpecifications = "散装";
		} else if (commodityPacking == 2) {
			if (specifi != 2 && specifi != 1) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		} else if (commodityPacking == 3) {
			if (specifi != 3 && specifi != 4) {
				return ServerResponse.createByErrorMessage(ResponseMessage.danweiyubaozhuangbupipei.getMessage());
			}
		}
		map.put("commoditySpecifications", commoditySpecifications);
		// 单价
		String commodityJiageString = params.get("commodityJiage").toString().trim();
		if (commodityJiageString == null || commodityJiageString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danjiakong.getMessage());
		}
		b = LegalCheck.isNumericFolse(commodityJiageString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.danjiacuowo.getMessage());
		}
		// 转成分
		Float float1 = Float.valueOf(commodityJiageString) * 100;
		long commodityJiage = float1.longValue();
		map.put("commodityJiage", commodityJiage);
		// 总数
		String commodityCountNoString = params.get("commodityCountNo").toString().trim();
		if (commodityCountNoString == null || commodityCountNoString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shuliangkong.getMessage());
		}
		b = LegalCheck.isNumericInt(commodityCountNoString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.shuliangcuowo.getMessage());
		}
		long commodityCountNo = Integer.valueOf(commodityCountNoString);
		map.put("commodityCountNo", commodityCountNo);
		map.put("commodityReserveNo", 0);
		map.put("commoditySurplusNo", commodityCountNo);
		map.put("remarks", params.get("remarks").toString().trim());
		map.put("serviceIntroduction", params.get("serviceIntroduction").toString().trim());

		// 是否支持线上预定
		String reserveString = params.get("reserve").toString().trim();
		if (reserveString == null || reserveString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingkong.getMessage());
		}
		b = LegalCheck.isNumericInt(reserveString);
		if (b == false) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingcuowo.getMessage());
		}
		int reserve = Integer.valueOf(reserveString);
		if (reserve != 1 && reserve != 2) {
			return ServerResponse.createByErrorMessage(ResponseMessage.zhichiyudingcuowo.getMessage());
		}
		map.put("reserve", reserve);

		if (reserve == 1) {

			String deliveryTypeString = params.get("deliveryType").toString().trim();
			if (deliveryTypeString == null || deliveryTypeString.equals("")) {
				return ServerResponse.createByErrorMessage(ResponseMessage.shuliangkong.getMessage());
			}
			b = LegalCheck.isNumericInt(deliveryTypeString);
			if (b == false) {
				return ServerResponse.createByErrorMessage(ResponseMessage.songhuokong.getMessage());
			}
			// 送货方式 //1自取 ,2送货, 3自取+送货 4满免
			int deliveryType = Integer.valueOf(deliveryTypeString);
			if (deliveryType == 1) {
				map.put("deliveryType", deliveryType); // 送货方式
				map.put("deliveryCollect", 0);// 运费
			} else {
				// 运费
				String deliveryCollectString = params.get("deliveryCollect").toString().trim();
				if (deliveryCollectString == null || deliveryCollectString.equals("")) {
					return ServerResponse.createByErrorMessage(ResponseMessage.yunfeikong.getMessage());
				}
				b = LegalCheck.isNumericInt(deliveryCollectString);
				if (b == false) {
					return ServerResponse.createByErrorMessage(ResponseMessage.yunfeicuowo.getMessage());
				}
			}
		} else {
			// 不支持线上预定
			map.put("deliveryType", 1); // 送货方式
			map.put("deliveryCollect", 0);// 运费
		}

		return ServerResponse.createBySuccess(map);

	}
}
