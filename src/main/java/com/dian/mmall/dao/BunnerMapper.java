package com.dian.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.banner.DibuBunner;

@Mapper
public interface BunnerMapper {


	List<DibuBunner> getBunnerList(Integer role, Integer permissionid, Integer bunnerType, String detailed, String date);

	int getguanggaocount(long tableId, int permissionid);

	List<DibuBunner> getisguanggao(String detailed, int fanwei, int bunnerType, int moren);


	void creatdu(DibuBunner dibuBunner);

	int quanguoshouyetanchuan(String startTime, String endTime);

	int quanshishouyetanchuan(String startTime, String endTime, String detailed);

	int quanqushouyetanchuan(String startTime, String endTime, String detailed);

	int guoshouyelunbo(String startTime, String endTime, int bunnerType);

	int shishouyelunbo(String startTime, String endTime, int bunnerType, String detailed);

	int qushouyelunbo(String startTime, String endTime, int bunnerType, String detailed);

	int quanshihexianshouyetanchuan(String startTime, String endTime, String detailed);

	int shihexianshouyelunbo(String startTime, String endTime, int bunnerType, String detailed);

}
