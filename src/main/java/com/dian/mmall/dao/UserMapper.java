package com.dian.mmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.dian.mmall.pojo.user.User;
@Mapper
public interface UserMapper {
//测试mybatis
//	@Select("select * from t_user")
	List<Map<String, Object>> getall();
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(User record);
//
//    int insertSelective(User record);
	
// 根据id 查询出用户信息
    User selectUserById(long id);
//
//    int updateByPrimaryKeySelective(User record);
//
//    int updateByPrimaryKey(User record);
   //检查有没有此用户名
   User checkUsername(String username);
   //成功返回非0
   int createUser(User user);
   //注册以后返回用户名等信息
   User selectUsername(String username);
   //修改用户基本信息
   int update_information(User new_User);
//
//    int checkEmail(String email);
//
//    User selectLogin(@Param("username") String username, @Param("password")String password);
//
//    String selectQuestionByUsername(String username);
//
//    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
//
//    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);
//
//    int checkPassword(@Param(value="password")String password,@Param("userId")Integer userId);
//
//    int checkEmailByUserId(@Param(value="email")String email,@Param(value="userId")Integer userId);



  
}