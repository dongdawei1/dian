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
}
