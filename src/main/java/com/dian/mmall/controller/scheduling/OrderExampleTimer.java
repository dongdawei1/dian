package com.dian.mmall.controller.scheduling;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dian.mmall.service.OrderService;

@Component
public class OrderExampleTimer {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		@Autowired
		private OrderService orderService;
	   
       //处理订单的定时任务 3分钟执行一次
       @Scheduled(fixedRate = 1000*60*3)
       public void timerOrderStatus() {
    	   orderService.timerOrderStatus();
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
