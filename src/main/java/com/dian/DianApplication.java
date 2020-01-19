package com.dian;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.dian.mmall.controller.common.SessionExpireFilter;

@SpringBootApplication
@EnableScheduling

public class DianApplication {

	public static void main(String[] args) {
		SpringApplication.run(DianApplication.class, args);
	}
 
	// 自己配置的 filter 生效SessionExpireFilter  
//	@Bean
//    public FilterRegistrationBean  filterRegistrationBean() {
//		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//		SessionExpireFilter httpBasicFilter = new SessionExpireFilter();
//		registrationBean.setFilter(httpBasicFilter);
//		List<String> urlPatterns = new ArrayList<String>();
//		//拦截所有请求
//	    urlPatterns.add("/*");
//	    //urlPatterns.add("/user/*");
//	    registrationBean.setUrlPatterns(urlPatterns);
//	    return registrationBean;
//    }


	
}

