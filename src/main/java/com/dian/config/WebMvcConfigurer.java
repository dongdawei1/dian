package com.dian.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.dian.mmall.controller.common.interfaceo.AuthorityInterceptor;
/**
 * 和springmvc的webmvc拦截配置一样
 * @author BIANP
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
	     @Override
	     public void addInterceptors(InterceptorRegistry registry) {
	        // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
	        registry.addInterceptor(AuthorityInterceptor()).addPathPatterns("/**");
	        super.addInterceptors(registry);
	     }

	 

	     @Bean
	     public AuthorityInterceptor AuthorityInterceptor() {
	         return new AuthorityInterceptor();
	     }
	}
