package com.dian.mmall.service.release;

import java.util.List;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;

public interface ReleaseWelfareService {

	ServerResponse<String> create_position(User currentUser, Map<String, Object> params);
    //管理员审批获取全部待审批的招聘
	ServerResponse<Object> getReleaseWelfareAll(Map<String, Object> params);

	//用户全部发布职位
	ServerResponse<Object> get_position_list(User user, Map<String, Object> params);
	//操作
	ServerResponse<String> position_operation(User user, Map<String, Object> params);
	// 获取职位电话
	ServerResponse getContact(User user, Map<String, Object> params);
	
	ServerResponse<Object> get_position_all(User user, Map<String, Object> params);
	List<ReleaseWelfare> adminGetzZWall(long userId);

}
