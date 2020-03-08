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

	long agetguangaoAll(long userId, String detailed, int fanwei);

	List<DibuBunner> agetguangAll(int pageLength, int pageSize, long userId, String detailed, int fanwei);

	int guanguanggao(String updateTime, long userId, String examineName, int id, int bunnerStatus);

	DibuBunner agetguanggao(long id);

	int guoshou(String startTime, String endTime, int bunnerType);

	int quanguoshouyetanchuan1(String startTime, String endTime, long id);

	int quanshihexianshouyetanchuan1(String startTime, String endTime, String detailed, long id);

	int quanshishouyetanchuan1(String startTime, String endTime, String detailed, long id);

	int quanqushouyetanchuan1(String startTime, String endTime, String detailed, long id);

	int guoshouyelunbo1(String startTime, String endTime, int bunnerType, long id);

	int guoshou1(String startTime, String endTime, int bunnerType, long id);

	int shihexianshouyelunbo1(String startTime, String endTime, int bunnerType, String detailed, long id);

	int shishouyelunbo1(String startTime, String endTime, int bunnerType, String detailed, long id);

	int qushouyelunbo1(String startTime, String endTime, int bunnerType, String detailed, long id);

	int endbunner(DibuBunner dibuBunner);

}
