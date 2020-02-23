package com.dian.comController;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ResponseCode;
import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.pojo.yanzhengma.CheckPicCode;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LogUtil;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(Const.APIV2 + "log/")
@Slf4j
public class LoginController {
	@Autowired
	private IUserService iUserService;

	// 用户登录
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> login(@RequestBody Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse httpServletResponse) {

		String captcha = params.get("captcha").toString().trim();
		String getPicCode = RedisShardedPoolUtil.get(params.get("uuid").toString().trim());
		if (!captcha.equalsIgnoreCase(getPicCode)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YanZhengMaCuoWu.getMessage());
		}

		ServerResponse<String> response = iUserService.login(params);
		if (response.getStatus() == ResponseCode.SUCCESS.getCode()) {
			return LogUtil.setTocken(request.getHeader("appid"), httpServletResponse, response.getMsg());		
		} else {
			return response;
		}
	}

	// 用户注册
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> create(@RequestBody Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse httpServletResponse) {
		String uuid = params.get("uuid").toString().trim();
		String captcha = params.get("captcha").toString().trim();
		String getPicCode = RedisShardedPoolUtil.get(uuid);
		if (!captcha.equalsIgnoreCase(getPicCode)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YanZhengMaCuoWu.getMessage());
		}
		ServerResponse<String> serverResponse = iUserService.createUser(params);
		if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
			return LogUtil.setTocken(request.getHeader("appid"), httpServletResponse, serverResponse.getMsg());		
		}
		return serverResponse;
	}
	
	

	// 获取验证码 @RequestBody Map<String,Object> params
	@ResponseBody
	@RequestMapping(value = "captcha", method = RequestMethod.GET)
	public ServerResponse<String> captcha(HttpServletRequest request, @RequestParam String uuid) {
		String base64PicCodeImage;
		String getPicCode;

		try {
			base64PicCodeImage = CheckPicCode.encodeBase64ImgCode();
			getPicCode = CheckPicCode.getPicCode();

			if (base64PicCodeImage != null && getPicCode != null) {

				if (RedisShardedPoolUtil.exists(uuid)) {
					// 根据ip把验证码放到数据库
					RedisShardedPoolUtil.del(uuid);
					RedisShardedPoolUtil.setEx(uuid, getPicCode, 8 * 40);
				} else {
					RedisShardedPoolUtil.setEx(uuid, getPicCode, 8 * 40);
				}

				return ServerResponse.createBySuccessMessage(base64PicCodeImage);
			}
		} catch (IOException e) {
			return ServerResponse.createByErrorMessage(ResponseMessage.YangZhengMaShengChengShiBai.getMessage());
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.YangZhengMaShengChengShiBai.getMessage());

	}
	// 获取用户信息

	@RequestMapping(value = "get_user_info", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> getUserInfo(HttpServletRequest httpServletRequest) {

		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if (StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
		}
		String userJsonStr = RedisShardedPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		if (user != null) {
			return ServerResponse.createBySuccess(JsonUtil.obj2String(user));
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.HuoQuDengLuXinXiShiBai.getMessage());
	}
}
