package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface MenuAndRenovationAndPestControlService {
    //创建
	ServerResponse<String> create_menuAndRenovationAndPestControl(User user, Map<String, Object> params);
    //用户自己获取自己的发布
	ServerResponse<Object> get_usermrp_list(User user, Map<String, Object> params);
	//审核列表
	ServerResponse<Object> getmrpAll(Map<String, Object> params);
	//操作
	ServerResponse<String> operation_usermrp(User user, Map<String, Object> params);
	//用户根据id获取发布
	ServerResponse<Object> get_usermrp_id(User user, long id);
	//公开信息列表页
	ServerResponse<Object> getmrpList(Map<String, Object> params);
   //根据类型获取标题
	ServerResponse<Object> getReleaseTitleList(Map<String, Object> params);
	//公开查看详情
	ServerResponse<Object> getMrpDetails(long id);
	

}
