package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.fabu.Fabu;

@Mapper
public interface FabuMapper {
 
	int getFabuCount(int releaseType, long userId);

	int createfabu(Fabu fabu);

	long getid(Fabu fabu);

	void upevaluate(long id, long evaluateid);

	long adminfabuno(String contact, Integer releaseType);

	List<Fabu> adminfabu(int pageLength, int pageSize, String contact, Integer releaseType);

	int examineResume(Fabu fabu);

	long getmyfabuno(Integer releaseType, Integer welfareStatus, long userId);

	List<Fabu> getmyfabu(int pageLength, int pageSize, Integer releaseType, Integer welfareStatus, long userId);

	int upfabu(long userId, long id, int type, String timeString, String termOfValidity);

	Fabu getmyfabubyid(long userId, long id);

}
