package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;

@Mapper
public interface ReleaseWelfareMapper {
	
  int countReleaseWelfare(long userId);
   //新建招聘
  int create_position(ReleaseWelfare releaseWelfare);
  //总条数
  long getReleaseWelfarePageno(String userName, String contact);
  //分页查询
  List<ReleaseWelfare> getReleaseWelfareAll(int pageLength, int pageSize, String userName, String contact);
  //审核
  int examineReleaseWelfare(ReleaseWelfare releaseWelfare);
  //用户查询发布的职位
 long get_position_list_no(String position, Integer welfareStatus, long userId);
  //分页查询自己发布的职位
  List<ReleaseWelfare> get_position_list_all(int pageLength, int pageSize, String position, Integer welfareStatus, long userId);
  //除编辑以外的所有操作
int position_operation(long userId, long id, int type, String timeString, String termOfValidity);
//审核失败后重新发布
int position_operation_edit(ReleaseWelfare releaseWelfare);
//根据查询条件查询出全部
long getUserReleaseWelfarePageno(String detailed, String position);
//用户查询
List<ReleaseWelfare> getUserReleaseWelfareList(int pageLength, int pageSize, String detailed, String position);
//查询商户联系方式
ReleaseWelfare getReleaseWelfareById(int id, int queriesType);
List<ReleaseWelfare> adminGetzZWall(long userId);
}
