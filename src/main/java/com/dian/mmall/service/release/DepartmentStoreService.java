package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface DepartmentStoreService {

	ServerResponse<String> create_departmentStore(User user, Map<String, Object> params);

	ServerResponse<Object> get_myDepartmentStore_list(User user, Map<String, Object> params);

	ServerResponse<String> operation_userDepartmentStore(User user, Map<String, Object> params);

	ServerResponse<Object> get_userDepartmentStore_id(long userId, long id);

	ServerResponse<Object> getDepartmentStoreTitleList(Map<String, Object> params);

	ServerResponse<Object> getDepartmentStorePublicList(Map<String, Object> params);

	ServerResponse<Object> getDepartmentStoreDetails(long id);

	ServerResponse<Object> adminDepartmentStore(Map<String, Object> params);

}
