package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.banner.DibuBunner;

@Mapper
public interface BunnerMapper {


	List<DibuBunner> getBunnerList(Integer role, Integer permissionid, Integer bunnerType, String detailed, String date);

	int getguanggaocount(long tableId, int permissionid);

	List<DibuBunner> getisguanggao(String detailed, int fanwei, int bunnerType, int moren);

	int getisguanggaocount(String detailed, int fanwei, int bunnerType, int moren, String startTime, String endTime);

	void creatdu(DibuBunner dibuBunner);

}
