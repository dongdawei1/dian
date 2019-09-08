package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface FoodAndGrainService {

	ServerResponse<String> create_foodAndGrain(User user, Map<String, Object> params);


	ServerResponse<Object> get_myFoodAndGrain_list(User user, Map<String, Object> params);


	ServerResponse<String> operation_userFoodAndGrain(User user, Map<String, Object> params);


	ServerResponse<Object> get_userFoodAndGrain_id(long id, long id2);


	ServerResponse<Object> getFoodAndGrainList(Map<String, Object> params);


	ServerResponse<Object> getFoodAndGrainTitleList(Map<String, Object> params);


	ServerResponse<Object> getFoodAndGrainPublicList(Map<String, Object> params);


	ServerResponse<Object> getFoodAndGrainDetails(long id);


	ServerResponse<Object> adminFoodAndGrain(Map<String, Object> params);

}
