package com.dian.mmall.dao.releaseDao;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.zhiwei.Resume;

@Mapper
public interface ResumeMapper {
    
	Resume selectResumeById(long userId);

	int create_resume(Resume resume);

	int operation_resume(int type, Resume resume);
    //更新
	int update_resume(Resume resume);

}
