package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReleaseWelfareMapper {
	
  int countReleaseWelfare(long userId);
}
