package com.dian.mmall.util;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

import ch.qos.logback.core.status.Status;

public class SetBean {
	
public static ServerResponse<String> setRole(Integer role){
	if(role!=0) {
		if(role==2 ) {
			return ServerResponse.createBySuccessMessage("需求企业");
		}else if(role==3 || role==4 || role==5 || role==7 || role==12 ) {
			return ServerResponse.createBySuccessMessage("发布企业");
		}else if(role==6 || role==11  ) {
			return ServerResponse.createBySuccessMessage("求职/出租");
		}else if(role==8 ) {
			return ServerResponse.createBySuccessMessage("无店面直供");
		}else if(role==1 ) {
			return ServerResponse.createBySuccessMessage("测试账户");
		}
		
	}
	
	return ServerResponse.createByErrorMessage(ResponseMessage.JueSeBuHeFa.getMessage());
}
	
}
