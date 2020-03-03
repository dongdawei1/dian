package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.gongfu.DepartmentStore;

@Mapper
public interface DepartmentStoreMapper {

	int create_departmentStore(DepartmentStore departmentStore);

	int countNum(int releaseType, long userId);

	long adminDepartmentStore_no(String contact, Integer releaseType);

	List<DepartmentStore> adminDepartmentStore(int pageLength, int pageSize, String contact, Integer releaseType);

	int examineDepartmentStore(DepartmentStore departmentStore);

	long get_myDepartmentStore_list_no(Integer releaseType, Integer welfareStatus, long userId);

	List<DepartmentStore> get_myDepartmentStore_list(int pageLength, int pageSize, Integer releaseType, Integer welfareStatus,
			long userId);

	int operation_userDepartmentStore(long userId, long id, int type, String timeString, String termOfValidity);

	int update_departmentStore(DepartmentStore departmentStore);

	DepartmentStore get_userDepartmentStore_id(long userId, long id);

	List<String> getDepartmentStoreTitleList(Integer releaseType, String detailed, String serviceType,
			String releaseTitle, Integer type);

	long getDepartmentStorePublicListNo(Integer releaseType, String detailed, String releaseTitle, String serviceType);

	List<DepartmentStore> getDepartmentStorePublicList(int pageLength, Integer releaseType, int pageSize, String releaseTitle,
			String detailed, String serviceType);

	DepartmentStore getDepartmentStoreDetails(long id);

	List<DepartmentStore> adminGetDsall(long userId);

}
