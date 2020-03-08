package com.dian.mmall.service.impl;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.dao.UserMapper;
import com.dian.mmall.pojo.TUserRole;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.service.TUserRoleService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.DateTimeUtil;
import com.dian.mmall.util.EncrypDES;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LegalCheck;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by geely
 */
@Slf4j
@Service("iUserService")
public class UserServiceImpl implements IUserService {
//
	@Autowired
	private UserMapper userMapper;

	public ServerResponse<String> register(User user) {
//        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
//        if(!validResponse.isSuccess()){
//            return validResponse;
//        }
//        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
//        if(!validResponse.isSuccess()){
//            return validResponse;
//        }
//        user.setRole(Const.Role.ROLE_CUSTOMER);
//        //MD5加密
//        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
//        int resultCount =0;// userMapper.insert(user);
//        if(resultCount == 0){
//            return ServerResponse.createByErrorMessage("注册失败");
//        }
		return ServerResponse.createBySuccessMessage("注册成功");
	}

	public ServerResponse<String> checkValid(String str, String type) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
			// 开始校验
			if (Const.USERNAME.equals(type)) {
				int resultCount = 0;// userMapper.checkUsername(str);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				int resultCount = 0;// userMapper.checkEmail(str);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("email已存在");
				}
			}
		} else {
			return ServerResponse.createByErrorMessage("参数错误");
		}
		return ServerResponse.createBySuccessMessage("校验成功");
	}

	public ServerResponse selectQuestion(String username) {

		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if (validResponse.isSuccess()) {
			// 用户不存在
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String question = null; // userMapper.selectQuestionByUsername(username);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("找回密码的问题是空的");
	}

	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int resultCount = 0;// userMapper.checkAnswer(username,question,answer);
		if (resultCount > 0) {
			// 说明问题及问题答案是这个用户的,并且是正确的
			String forgetToken = UUID.randomUUID().toString();
			RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX + username, forgetToken, 60 * 60 * 12);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("问题的答案错误");
	}

	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("参数错误,token需要传递");
		}
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if (validResponse.isSuccess()) {
			// 用户不存在
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX + username);
		if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
			return ServerResponse.createByErrorMessage("token无效或者过期");
		}

		if (org.apache.commons.lang3.StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			int rowCount = 0;// userMapper.updatePasswordByUsername(username,md5Password);

			if (rowCount > 0) {
				return ServerResponse.createBySuccessMessage("修改密码成功");
			}
		} else {
			return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
		}
		return ServerResponse.createByErrorMessage("修改密码失败");
	}

	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		// 防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
		int resultCount = 0;// userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage("旧密码错误");
		}

		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = resultCount;// userMapper.updateByPrimaryKeySelective(user);
		if (updateCount > 0) {
			return ServerResponse.createBySuccessMessage("密码更新成功");
		}
		return ServerResponse.createByErrorMessage("密码更新失败");
	}

	public ServerResponse<User> updateInformation(User user) {
		// username是不能被更新的
		// email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
		int resultCount = 0;// userMapper.checkEmailByUserId(user.getEmail(),user.getId());
		if (resultCount > 0) {
			return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());

		int updateCount = 0;// userMapper.updateByPrimaryKeySelective(updateUser);
		if (updateCount > 0) {
			return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
		}
		return ServerResponse.createByErrorMessage("更新个人信息失败");
	}

	public ServerResponse<User> getInformation(long userId) {
		User user = null;// userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return ServerResponse.createByErrorMessage("找不到当前用户");
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);

	}

	// backend

	/**
	 * 校验是否是管理员
	 * 
	 * @param user
	 * @return
	 */
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

//测试获取全部用户
	@Override
	public List<Map<String, Object>> getall(int type) {
		// TODO Auto-generated method stub
		return userMapper.getall(type);
	}

	// 登陆
	@Override
	public ServerResponse<Object> login(Map<String, Object> params) {

		String usernamrString = params.get("username").toString().trim();
		String passwordString = params.get("password").toString().trim();

		if (usernamrString.length() > 7 && passwordString.length() > 7) {
			User user1 = userMapper.checkUsername(usernamrString);
			if (user1 == null) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingMiMaCouWu.getMessage());
			}
			String md5Password = MD5Util.MD5EncodeUtf8(passwordString);
			String getpassword = user1.getPassword();
			if (!getpassword.equals(md5Password)) {
				return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingMiMaCouWu.getMessage());
			}

			// 把密码置空在页面上不明文出现
			// user1.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
			// 置为null以后不会系列化，不会传给前端
			user1.setPassword(null);
			user1.setMobilePhone(EncrypDES.decryptPhone(user1.getMobilePhone()));
			return ServerResponse.createBySuccess(user1);
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingMiMaGeShiCouWu.getMessage());
		}
	}

//判断用户名是否可用
	@Override
	public ServerResponse<String> checkUsername(String username) {
		User user1 = userMapper.checkUsername(username);
		if (user1 == null) {
			return ServerResponse.createBySuccessMessage(ResponseMessage.YongHuMingKeYong.getMessage());
		}
		user1 = null;
		return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeYong.getMessage());

	}

	// 用户注册

	@Override
	public ServerResponse<Object> createUser(Map<String, Object> params) {
		String password = params.get("pass").toString().trim();
		String checkPass = params.get("checkPass").toString().trim();
		if (!password.equals(checkPass)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.MiMaBuYiZhi.getMessage());
		}
		// 判断长度
		if (!(password.length() > 7 && password.length() < 18)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.MiMaBuHeFa.getMessage());
		}
		// 校验是否有特殊字符
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		// 判断用户角色
		String role = params.get("role").toString().trim();
		serverResponse = LegalCheck.legalCheckRole(role);
		// 检查角色是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		String username = params.get("name").toString().trim();
		String mobilePhone = params.get("mobilePhone").toString().trim();
		// 判断手机号是否合法
		serverResponse = LegalCheck.legalCheckMobilePhone(mobilePhone);
		if (serverResponse.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}

		// 检查用户名是否重复
		ServerResponse<String> check_name = checkUsername(username);
		// 如果返回是空可以注册
		if (check_name.getStatus() != 0) {
			return ServerResponse.createByErrorMessage(check_name.getMsg());
		}

		User user = new User();
		user.setCreateTime(DateTimeUtil.dateToAll());
		// MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(password));
		user.setUsername(username);
		user.setMobilePhone(EncrypDES.encryptPhone(mobilePhone));
		user.setRole(Integer.parseInt(role));
		user.setIsAuthentication(4);// 状态审批状态 1 审批中 ，2通过，3审核不通过，4未申请

		try {
			// 创建用户
			int resultCount = userMapper.createUser(user);
			if (resultCount == 0) {
				user = null;
				return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
			}

			user.setPassword(null);
			user.setId(selectUserId(user));
			user.setMobilePhone(mobilePhone);
			return ServerResponse.createBySuccess(user);
		} catch (Exception e) {
			log.info("createUserError   ", e);
			return ServerResponse.createByErrorMessage(ResponseMessage.ChuangJianYongHuShiBai.getMessage());
		}
	}

//根据用户名查询id
	@Override
	public long selectUserId(User user) {

		return userMapper.checkUsername(user.getUsername()).getId();
	}

	// 编辑用户基本信息
	@Override
	public ServerResponse<String> update_information(long id, Map<String, Object> params) {
		boolean ismobilePhone = false;

		// 校验是否有特殊字符
		ServerResponse<String> serverResponse = LegalCheck.legalCheckFrom(params);
		// 检查是否有非法输入
		if (serverResponse.getStatus() != 0) {
			return serverResponse;
		}

		User user = userMapper.selectUserById(id);

		String newusernamr = params.get("username").toString().trim();
		if (!newusernamr.equals(user.getUsername())) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YongHuMingBuKeXiuGai.getMessage());
		}

		String md5_rowPassword = MD5Util.MD5EncodeUtf8(params.get("rowPassword").toString().trim());

		// 判断原始密码是否正确
		if (user.getPassword().equals(md5_rowPassword)) {

			String newmobilePhone = params.get("mobilePhone").toString().trim();
			String md5_newmobilePhone = EncrypDES.encryptPhone(newmobilePhone);

			// 判断手机号是否合法
			serverResponse = LegalCheck.legalCheckMobilePhone(newmobilePhone);
			if (serverResponse.getStatus() != 0) {
				return serverResponse;
			}
			User new_User = new User();
			new_User.setId(user.getId());
			new_User.setUsername(newusernamr);
			new_User.setMobilePhone(md5_newmobilePhone);

			String newPassword = params.get("newPassword").toString().trim();
			String checkenewPassword = params.get("checkenewPassword").toString().trim();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new_User.setUpdateTime(formatter.format(new Date()));
			// 没有修改密码就进不去
			if ((checkenewPassword != null && !checkenewPassword.equals(""))
					|| (newPassword != null && !newPassword.equals(""))) {

				// 判断密码两个密码是否一样
				if (!newPassword.equals(checkenewPassword)) {
					return ServerResponse.createByErrorMessage(ResponseMessage.MiMaBuYiZhi.getMessage());
				}

				// 判断长度
				if (checkenewPassword.length() > 7 && checkenewPassword.length() < 18) {
					// 落库
					new_User.setPassword(MD5Util.MD5EncodeUtf8(checkenewPassword));
					// 创建用户
					int resultCount = userMapper.update_information(new_User);
					if (resultCount == 0) {
						return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
					}
					// 落库成功重新登陆
					return ServerResponse
							.createBySuccessMessage(ResponseMessage.BianJiChengGongChongXinDengLu.getMessage());

				} else {
					return ServerResponse.createByErrorMessage(ResponseMessage.MiMaBuHeFa.getMessage());
				}
			} else {
				// 没有修改密码
				new_User.setPassword(user.getPassword());
				int resultCount = userMapper.update_information(new_User);
				if (resultCount == 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.XiTongYiChang.getMessage());
				}
				// 落库成功
				new_User.setPassword(null);
				new_User.setRole(user.getRole());
				new_User.setCreateTime(user.getCreateTime());
				new_User.setMobilePhone(newmobilePhone);
				new_User.setIsAuthentication(user.getIsAuthentication());
				return ServerResponse.createBySuccessMessage(JsonUtil.obj2String(new_User));
			}
		} else {
			return ServerResponse.createByErrorMessage(ResponseMessage.YuanShiMiMaCuoWu.getMessage());
		}
	}

	@Override
	public ServerResponse<Object> selectUserById(long userId) {
		User user = userMapper.selectUserById(userId);
		if(user==null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.huoquxinxishibai.getMessage());
		}
		user.setPassword(null);
		return ServerResponse.createBySuccess(user);
	}

	@Override
	public ServerResponse<Object> getuserbyname(String userName) {
		User user= userMapper.checkUsername(userName);
		if (user == null) {
			return ServerResponse.createByError();
		}
		return ServerResponse.createBySuccess(user);
	}

}
