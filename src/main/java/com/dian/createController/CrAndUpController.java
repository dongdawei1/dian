package com.dian.createController;

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
import com.dian.mmall.service.OrderService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.LogUtil;
import com.dian.mmall.util.MD5Util;
import com.dian.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(Const.APIV5 + "cr/")
@Slf4j
public class CrAndUpController {

	/** 商户发布采购订单 */
	@Autowired
	private OrderService orderService;

}
