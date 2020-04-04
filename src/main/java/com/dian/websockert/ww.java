package com.dian.websockert;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.comController.LoginController;
import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("log1/")
@Slf4j
public class ww {
	@Autowired
	WebsockertService websockertService;
	// 获取验证码 @RequestBody Map<String,Object> params
	@ResponseBody
	@RequestMapping(value = "c", method = RequestMethod.GET)
	public void captcha() {
		websockertService.remote("eee", null);
	}
	// 获取验证码 @RequestBody Map<String,Object> params
		@ResponseBody
		@RequestMapping(value = "cc", method = RequestMethod.GET)
		public void captcha1() {
			System.out.println(111);
			websockertService.remote1("eee", null);
		}
}
