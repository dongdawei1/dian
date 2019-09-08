package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.shichang.FoodAndGrain;
import com.dian.mmall.pojo.weixiuAnddianqi.Equipment;

@Mapper
public interface EquipmentMapper {
	   //计算发布过的总数 
     int countNum(int releaseType, long userId);

	int create_equipment(Equipment equipment);

	long adminEquipment_no(String contact);

	List<Equipment> adminEquipment(int pageLength, int pageSize, String contact);

	int examineEquipment(Equipment equipment);

	long get_userEquipment_list_no(Integer releaseType, Integer welfareStatus, long userId);

	List<Equipment> get_myEquipment_list(int pageLength, int pageSize, Integer releaseType, Integer welfareStatus, long userId);

	int operation_userequipment(long userId, long id, int type, String timeString, String termOfValidity);

	Equipment get_userequipment_id(long userId, long id);

	int update_equipment(Equipment equipment);

	List<String> getEquipmentReleaseTitleList(Integer releaseType, String detailed, String serviceType, String releaseTitle,
			Integer type);

	long getEquipmentListNo(Integer releaseType, String detailed, String releaseTitle, String serviceType);

	List<Equipment> getEquipmentPublicList(int pageLength, Integer releaseType, int pageSize, String releaseTitle,
			String detailed, String serviceType);

	Equipment getEquipmentDetails(long id);
   
	
	
	//下边是菜的，公用一个库
	int create_foodAndGrain(FoodAndGrain foodAndGrain);

	long get_myFoodAndGrain_list_no(Integer releaseType, Integer welfareStatus, long userId);

	List<FoodAndGrain> get_myFoodAndGrain_list(int pageLength, int pageSize, Integer releaseType, Integer welfareStatus,
			long userId);

	FoodAndGrain get_userFoodAndGrain_id(long userId, long id);

	int operation_userFoodAndGrain(long userId, long id, int type, String timeString,
			String termOfValidity);

	int update_foodAndGrain(FoodAndGrain equipment_create);

	List<String> getFoodAndGrainTitleList(Integer releaseType, String detailed, String serviceType, String releaseTitle,
			Integer type);

	long getFoodAndGrainPublicListNo(Integer releaseType, String detailed, String releaseTitle, String serviceType);

	List<FoodAndGrain> getFoodAndGrainPublicList(int pageLength, Integer releaseType, int pageSize, String releaseTitle,
			String detailed, String serviceType);

	FoodAndGrain getFoodAndGrainDetails(long id);

	long adminFoodAndGrain_no(String contact, Integer releaseType);

	List<FoodAndGrain> adminFoodAndGrain(int pageLength, int pageSize, String contact, Integer releaseType);


}
