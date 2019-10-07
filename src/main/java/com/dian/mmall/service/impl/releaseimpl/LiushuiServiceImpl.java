package com.dian.mmall.service.impl.releaseimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.LiushuiMapper;
import com.dian.mmall.pojo.Liushui;
import com.dian.mmall.service.LiushuiService;
import com.dian.mmall.util.AnnotationDealUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("liushuiService")
public class LiushuiServiceImpl implements LiushuiService {
	@Autowired
	private LiushuiMapper liushuiMapper;

	@Override
	public ServerResponse<String> create_liushui(Liushui liushui) {
		Map<String, Object> checknullMap = AnnotationDealUtil.validate(liushui);
		if ((boolean) checknullMap.get("result") == true && ((String) checknullMap.get("message")).equals("验证通过")) {
			int result = liushuiMapper.create_liushui(liushui);
			if (result == 0) {
				return ServerResponse.createByErrorMessage(ResponseMessage.chuangjianliushuishibai.getMessage());
			}
			return ServerResponse.createBySuccessMessage(ResponseMessage.caozuochenggong.getMessage());
		} else if ((boolean) checknullMap.get("result") == false) {
			return ServerResponse.createByErrorMessage((String) checknullMap.get("message"));
		} else {
			return ServerResponse.createByErrorMessage("系统异常稍后重试");
		}
	}

}
