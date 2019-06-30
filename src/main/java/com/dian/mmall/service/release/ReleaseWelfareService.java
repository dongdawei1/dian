package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ReleaseWelfareService {

	ServerResponse<String> create_position(User currentUser, Map<String, Object> params);
    //管理员审批获取全部待审批的招聘
	ServerResponse<Object> getReleaseWelfareAll(Map<String, Object> params);
	//审核招聘
	ServerResponse<String> examineReleaseWelfare(User user, Map<String, Object> params);
	//用户全部发布职位
	ServerResponse<Object> get_position_list(User user, Map<String, Object> params);

}
