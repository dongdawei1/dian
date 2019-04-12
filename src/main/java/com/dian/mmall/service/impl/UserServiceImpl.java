package com.dian.mmall.service.impl;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by geely
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
//
    @Autowired
    private UserMapper userMapper;

 //登陆
    @Override
    public ServerResponse<User> login(String username, String password) {
        User user1 =userMapper.checkUsername(username);
       System.out.println(user1);
        if(user1 ==null){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
   
       // String md5Password = MD5Util.MD5EncodeUtf8(password);
        String passwordString=user1.getPassword();
        System.out.println(passwordString+"   "+password+" ........");
        if(!passwordString.equals(password)){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }

      //把密码置空在页面上不明文出现
        //user1.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
       //置为null以后不会系列化，不会传给前端
        user1.setPassword(null);
      return ServerResponse.createBySuccess("登录成功",user1);
    }

  //注册完以后登录
    @Override
    public ServerResponse<User> login(String username) {
        User user1 =userMapper.selectUsername(username);
      //把密码置空在页面上不明文出现
      //  user1.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
     
      return ServerResponse.createBySuccess("注册成功跳转Home",user1);
    }

    
    
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount =0;// userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount =0;// userMapper.checkUsername(str);
                if(resultCount > 0 ){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = 0;//userMapper.checkEmail(str);
                if(resultCount > 0 ){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question =null; //userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount =0;// userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }


    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = 0;//userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = 0;//userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount =resultCount;// userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount =0;// userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
  
        int updateCount =0;// userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }



    public ServerResponse<User> getInformation(long userId){
        User user =null;// userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }




    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



	@Override
	public List<Map<String, Object>> getall() {
		// TODO Auto-generated method stub
		return userMapper.getall();
	}


//判断用户名是否可用
	@Override
	public ServerResponse<User> checkUsername(String username) {
		 User user1 =userMapper.checkUsername(username);
	       System.out.println(user1);
	        if(user1 ==null){
	            return ServerResponse.createByErrorMessage("可以注册");
	        }
	   
	        user1=null;
	      return ServerResponse.createBySuccess("用户名被注册，请换一个",user1);
		
	}

  //注册后返回id

	@Override
	public long createUser(User user) {
		
		int count= userMapper.createUser(user);
		System.out.println(count);
		
		return selectUserId(user);
	}


//根据用户名查询id
	@Override
	public long selectUserId(User user) {
		
		return userMapper.checkUsername(user.getUsername()).getId();
	}

	



}
