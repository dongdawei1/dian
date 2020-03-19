package com.dian.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.PictureNum;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.FabuMapper;
import com.dian.mmall.dao.PictureMapper;
import com.dian.mmall.dao.RealNameMapper;
import com.dian.mmall.pojo.fabu.Fabu;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.RealName;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.FabuService;
import com.dian.mmall.util.AnnotationDealUtil;
import com.dian.mmall.util.BeanMapConvertUtil;
import com.dian.mmall.util.FabuUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;

@Service
public class FabuServiceImpl implements FabuService {
	@Autowired
	private FabuMapper fabuMapper;

	@Autowired
	private RealNameMapper realNameMapper;
	@Autowired
	private PictureMapper pictureMapper;

	@Override
	public ServerResponse<String> createfabu(User user, Map<String, Object> params) {

		String releaseTypeString = params.get("releaseType").toString().trim();
		if (releaseTypeString == null || releaseTypeString.equals("")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabuleixingkong.getMessage());
		}
		int releaseType = Integer.valueOf(releaseTypeString);
		// 检查权限
		if (!LegalCheck.checkefaburole(releaseType, user.getRole())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouquanxian.getMessage());
		}
		// 输入合法检查，必填，有非法字符等
		ServerResponse<String> response = LegalCheck.legalCheckFrom(params);
		if (response.getStatus() != 0) {
			return response;
		}
		// 校验发布总数
		if (!LegalCheck.checkefabuzongshu(releaseType, fabuMapper.getFabuCount(releaseType, user.getId()))) {
			return ServerResponse.createByErrorMessage(ResponseMessage.fabudadaoshangxian.getMessage());
		}

		// 判断实名信息是否正确
		RealName realName = realNameMapper.getRealName(user.getId());
		if (realName == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoqushimingxinxishibai.getMessage());
		}
		ServerResponse<Object> chResponse = FabuUtil.check_evaluate(user, params, 1, realName, releaseType);
		if (chResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(chResponse.getMsg());
		}

		Map<String, Object> map = (Map<String, Object>) chResponse.getData();
		map.put("authentiCationStatus", 1);
		map.put("welfareStatus", 4);

		// 图片
		List<Picture> listObj3 = JsonUtil.list2Obj((ArrayList<Picture>) params.get("pictureUrl"), List.class,
				Picture.class);
		int list_size = listObj3.size();
		// 把getUse_status()==1 放到这个集合中
		List<Picture> listObj4 = new ArrayList<Picture>();
		
		if (list_size > 0) {
			int count = 0;
			count = 0;
			for (int a = 0; a < list_size; a++) {
				Picture picture = listObj3.get(a);
				if (picture.getUseStatus() == 1) {
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
			if (count > 5) {
				// 判断没有删除的图片是否大于规定
				return ServerResponse.createByErrorMessage("图片数量不能超过 " + 5 + "个");
			}
			if (count == 0) {
				return ServerResponse.createByErrorMessage("图片不能为空");
			}
		} else {
			return ServerResponse.createByErrorMessage("图片不能为空");
		}
		map.put("pictureUrl", JsonUtil.obj2StringPretty(listObj4));
		Fabu fabu = (Fabu) BeanMapConvertUtil.convertMap(Fabu.class, map);
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(fabu);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int count = fabuMapper.createfabu(fabu);
			if (count == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.LuoKuShiBai.getMessage());
			}
			return ServerResponse.createBySuccessMessage(ResponseMessage.ChengGong.getMessage());
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}

	// private long evaluateid;// 评价ID
	// 字段长度注解校验

}}
