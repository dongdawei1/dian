package com.dian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.dingshi.OrderExampleTimerService;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.DateTimeUtil;

import java.util.List;
import java.util.Map;

@Controller
public class HelloController {
	// 注入jdbc
	@Autowired
	IUserService i;
	@Autowired
	private  OrderExampleTimerService orderExampleTimerService;
//	@ResponseBody
//	@GetMapping("/query")
//	public Object map() {
//      
//		// List<Map<String, Object>> list = i.getall(1);
//	//	return i.getall(1);
//	}
}
