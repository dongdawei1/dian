package com.dian.mmall.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.CityMapper;
import com.dian.mmall.dao.WholesaleMapper;
import com.dian.mmall.pojo.Wholesale;
import com.dian.mmall.service.WholesaleService;
import com.dian.mmall.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("wholesaleService")
public class WholesaleServiceImpl implements WholesaleService {

	@Autowired
	private WholesaleMapper wholesaleMapper;
	@Autowired
	private CityMapper cityMapper;

	@Override
	public ServerResponse<Object> getwholesale(Map<String, Object> params) {
		List<Integer> selectedOptions_list = JsonUtil.string2Obj(params.get("selectedOptions").toString().trim(),
				List.class);
		if (selectedOptions_list.size() == 3) {
			Integer provincesId = selectedOptions_list.get(0);
			Integer cityId = selectedOptions_list.get(1);
			Integer districtCountyId = selectedOptions_list.get(2);
			// 判断省市区id是否正确
			String detailed = cityMapper.checkeCity(provincesId, cityId, districtCountyId);			
			String	companyName=params.get("companyName").toString().trim();
			
			if(companyName!=null && !companyName.equals("")) {
				companyName="%"+companyName+"%";
			}
			return ServerResponse.createBySuccess(wholesaleMapper.getwholesale(detailed,companyName));
			
		}else {
			return ServerResponse.createByErrorMessage(ResponseMessage.ChengShiBuHeFa.getMessage());
		}
	}
}
