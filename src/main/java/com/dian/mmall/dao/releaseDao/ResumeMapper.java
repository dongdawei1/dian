package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.zhiwei.ReleaseWelfare;
import com.dian.mmall.pojo.zhiwei.Resume;

@Mapper
public interface ResumeMapper {
    
	Resume selectResumeById(long userId);

	int create_resume(Resume resume);

	int operation_resume(int type, Resume resume);
    //更新
	int update_resume(Resume resume);
     //审核总条数
	long getRresumePageno(String userName, String contact);
    //待审批分页
	List<Resume> getRresumeAll(int pageLength, int pageSize, String userName, String contact);
   //审核
	int examineResume(Resume resume);
    //分页查询条数
	long getUserRresumePageno(String detailed, String position);
   //分页查询
	List<Resume>  getUserRresumeList(int pageLength, int pageSize, String detailed, String position);

	Resume getResumeContactById(int id, int queriesType);

}
