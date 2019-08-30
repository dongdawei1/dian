package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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

}
