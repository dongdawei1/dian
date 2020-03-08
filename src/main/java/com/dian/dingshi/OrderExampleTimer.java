package com.dian.dingshi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dian.mmall.service.OrderService;
import com.dian.mmall.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderExampleTimer {
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	@Autowired
	private OrderService orderService;

	// 处理订单的定时任务 3分钟执行一次
	@Scheduled(fixedRate = 1000 * 60 * 3)
	public void timerOrderStatus() {
		orderService.timerOrderStatus();
	}

	// 处理微信支付的状态=0的 3分钟执行一次
	@Scheduled(fixedRate = 1000 * 60 * 5)
	public void timerSelsetPayOrder() {
		orderService.timerSelsetPayOrder();
	}

	@Autowired
	private OrderExampleTimerService orderExampleTimerService;

	// 每天00点40分01秒时执行,全部过期定时任务
	@Scheduled(cron = "01 00 00 * * ?")
	public void timer_guanggaoguoqi() {
		String dateString = DateTimeUtil.dateToAll();
		orderExampleTimerService.timer_guanggaoguoqi(dateString);
	}

	// 每天00点40分01秒时执行,全部过期定时任务
	@Scheduled(cron = "01 03 00 * * ?")
	public void timer_guanggaoshengxiao() {
		String dateString = DateTimeUtil.dateToAll();
		orderExampleTimerService.timer_guanggaoshengxiao(dateString);
	}

	// 每天00点40分01秒时执行,全部过期定时任务
	@Scheduled(cron = "01 40 00 * * ?")
	public void timer_guoqi() {
		String dateString = DateTimeUtil.dateToAll();
		for (int a = 1; a < 7; a++) {
			orderExampleTimerService.upall(a, dateString);
			if (a == 1) {
				log.warn("table-{}, id-{} ->过期", "wineandtableware", a);
			} else if (a == 2) {
				log.warn("table-{}, id-{} ->过期", "equipment", a);
			} else if (a == 3) {
				log.warn("table-{}, id-{} ->过期", "rent", a);
			} else if (a == 4) {
				log.warn("table-{}, id-{} ->过期", "menuandrenovationandpestcontrol", a);
			} else if (a == 5) {
				log.warn("table-{}, id-{} ->过期", "resume", a);
			} else if (a == 6) {
				log.warn("table-{}, id-{} ->过期", "departmentstore", a);
			}
		}
	}

	// 每天01点00分01秒时执行 全部删除定时任务
	@Scheduled(cron = "01 00 01 * * ?")
	public void timer_shanchu() {
		String dateString = DateTimeUtil.dateToAll();
		String termOfValidity = DateTimeUtil.a_few_days_later(-30);
		for (int a = 1; a < 7; a++) {
			orderExampleTimerService.delall(a, dateString, termOfValidity);
			if (a == 1) {
				log.warn("table-{}, id-{} ->删除", "wineandtableware", a);
			} else if (a == 2) {
				log.warn("table-{}, id-{} ->删除", "equipment", a);
			} else if (a == 3) {
				log.warn("table-{}, id-{} ->删除", "rent", a);
			} else if (a == 4) {
				log.warn("table-{}, id-{} ->删除", "menuandrenovationandpestcontrol", a);
			} else if (a == 5) {
				log.warn("table-{}, id-{} ->删除", "resume", a);
			} else if (a == 6) {
				log.warn("table-{}, id-{} ->删除", "departmentstore", a);
			}
		}
	}

	// 每天00点30分01秒时执行
	@Scheduled(cron = "01 30 00 * * ?")
	public void timerShanchu() {
		System.out.println("current time : " + dateFormat.format(new Date()));
	}
//       //第一次延迟1秒执行，当执行完后2秒再执行
//       @Scheduled(initialDelay = 1000, fixedDelay = 2000)
//       public void timerInit() {
//           System.out.println("init : "+dateFormat.format(new Date()));
//       }
//
//       //每天20点16分50秒时执行
//       @Scheduled(cron = "50 16 20 * * ?")
//       public void timerCron() {
//           System.out.println("current time : "+ dateFormat.format(new Date()));
//       }

}
