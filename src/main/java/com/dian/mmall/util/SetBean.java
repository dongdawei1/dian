package com.dian.mmall.util;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

import ch.qos.logback.core.status.Status;

public class SetBean {
	
	/**
	 * 订单表中用这个了 sql中   ‘需求企业’ 不能改
	 * */
	
public static ServerResponse<String> setRole(Integer role){
	if(role!=0) {
		if(role==2 ) {
			return ServerResponse.createBySuccessMessage("需求企业");
		}else if(role==3  ) {
			return ServerResponse.createBySuccessMessage("厨/电/二手设备");
		}else if(role==4  ) {
			return ServerResponse.createBySuccessMessage("粮油菜水产清洁桌椅零售");
		}else if( role==5   ) {
			return ServerResponse.createBySuccessMessage("酒水/消毒餐具");
		}else if(role==12 ) {
			return ServerResponse.createBySuccessMessage("工服/百货/绿植/装饰用品");
		}else if(role==6) {
			return ServerResponse.createBySuccessMessage("商铺/摊位出租");
		}else if( role==11  ) {
			return ServerResponse.createBySuccessMessage("求职");
		}else if(role==8 ) {
			return ServerResponse.createBySuccessMessage("无店面直供");
		}else if(role==1 ) {
			return ServerResponse.createBySuccessMessage("测试账户");
		}else if(role==7 ) {
			return ServerResponse.createBySuccessMessage("装修广告灭虫");
		}else if(role==13 ) {
			return ServerResponse.createBySuccessMessage("粮油菜水产清洁桌椅批发");
		}else if(role==10 ) {
			return ServerResponse.createBySuccessMessage("淘特色农产品");
		}
		
	}
	
	return ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
}
	
}
