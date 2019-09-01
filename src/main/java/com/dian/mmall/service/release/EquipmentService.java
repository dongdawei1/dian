package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface EquipmentService {

	ServerResponse<String> create_equipment(User user, Map<String, Object> params);

	ServerResponse<Object> adminEquipment(Map<String, Object> params);

	ServerResponse<Object> get_myEquipment_list(User user, Map<String, Object> params);

	ServerResponse<String> operation_userequipment(User user, Map<String, Object> params);

	ServerResponse<Object> get_userequipment_id(long userId, long id);

	ServerResponse<Object> getequipmentList(Map<String, Object> params);

	ServerResponse<Object> getEquipmentReleaseTitleList(Map<String, Object> params);

	ServerResponse<Object> getEquipmentPublicList(Map<String, Object> params);

	ServerResponse<Object> getEquipmentDetails(long id);

}
