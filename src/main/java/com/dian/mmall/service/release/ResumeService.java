package com.dian.mmall.service.release;

import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public interface ResumeService {
   //创建简历
	ServerResponse<String> create_resume(User user, Map<String, Object> params);
    //根据id查询
	ServerResponse<Object> select_resume_by_id(long id);
	//操作
	ServerResponse<String> operation_resume(User user, Map<String, Object> params);
	//待审核列表
	ServerResponse<Object> getTrialResumeAll(Map<String, Object> params);
	//审核
	ServerResponse<String> examineResume(User user, Map<String, Object> params);
	ServerResponse<Object> get_resume_all(User user, Map<String, Object> params);
	//获取电话或者邮箱
	ServerResponse getContact(User user, Map<String, Object> params);

}
