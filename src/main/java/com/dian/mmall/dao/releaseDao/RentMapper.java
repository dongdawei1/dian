package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.chuzufang.Rent;

@Mapper
public interface RentMapper {

	int countNum(int releaseType, long userId);

	int create_rent(Rent rent);

	long get_myRent_list_no(Integer welfareStatus, long userId);

	List<Rent> get_myRent_list(int pageLength, int pageSize, Integer welfareStatus, long userId);
	                        
	long adminMent_no(String contact);

	List<Rent> adminMent(int pageLength, int pageSize, String contact);
    //审核
	int examineResume(Rent rent);

	int operation_userment(long userId, long id, int type, String timeString, String termOfValidity);

	Rent get_userrent_id(long userId, long id);

	int update_rent(Rent rent_create);

	List<String> getServiceDetailedList(Integer releaseType, String detailed, Integer fouseSizeGreater,
			Integer fouseSizeLess);

	long getrentListNo(Integer releaseType, String detailed, Integer fouseSizeGreater, Integer fouseSizeLess, String serviceDetailed);

	List<Rent> getrentList(int pageLength, Integer releaseType, int pageSize, Integer fouseSizeGreater, Integer fouseSizeLess,
			String detailed, String serviceDetailed);

	Rent get_rent_id(long id);

	List<Rent> adminGetRentall(long userId);


}
