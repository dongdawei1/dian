package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.jiushui.WineAndTableware;

@Mapper
public interface WineAndTablewareMapper {

	int create_wineAndTableware(WineAndTableware wineAndTableware);

	int countNum(int releaseType, long userId);

	long get_myWineAndTableware_list_no(Integer releaseType, Integer welfareStatus, long userId);

	List<WineAndTableware> get_myWineAndTableware_list(int pageLength, int pageSize, Integer releaseType, Integer welfareStatus, long userId);

	int operation_userWineAndTableware(long userId, long id, int type, String timeString);

	int update_wineAndTableware(WineAndTableware equipment_create);

	long adminWineAndTableware_no(String contact, Integer releaseType);

	List<WineAndTableware> adminWineAndTableware(int pageLength, int pageSize, String contact, Integer releaseType);

	int examineEquipment(WineAndTableware wineAndTableware);

	WineAndTableware get_userWineAndTableware_id(long userId, long id);

	List<String> getWineAndTablewareTitleList(Integer releaseType, String detailed, String releaseTitle);

	long getWineAndTablewarePublicListNo(Integer releaseType, String detailed, String releaseTitle);

	List<WineAndTableware> getWineAndTablewarePublicList(int pageLength, Integer releaseType, int pageSize, String releaseTitle,
			String detailed);

	WineAndTableware getWineAndTablewareDetails(long id);
	
}
