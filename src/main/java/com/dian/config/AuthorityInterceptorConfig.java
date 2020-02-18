package com.dian.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.dian.guolvAndlanjie.ImgAuthorityInterceptor;
import com.dian.guolvAndlanjie.LogAuthorityInterceptor;
import com.dian.mmall.common.Const;

import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
/**
 * 和springmvc的webmvc拦截配置一样
 * @author BIANP
 */
@Configuration
public class AuthorityInterceptorConfig extends WebMvcConfigurationSupport {
	  /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${cbs.imagesPath}")
    private String mImagesPath;    
	
	   @Override
	     public void addInterceptors(InterceptorRegistry registry) {
	        // 拦截所有请求，通过判断是否有   AuthorityInterceptor() 实现类
	        registry.addInterceptor(imgAuthorityInterceptor())
	                .addPathPatterns("/**")
	                .excludePathPatterns("/img/**");
	        // 拦截所有请求，通过判断是否有   AuthorityInterceptor() 实现类
	        registry.addInterceptor(logAuthorityInterceptor())
	                .addPathPatterns(Const.APIV1+"**");
	        
	     }

	   @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	       //把请求img 映射至真实地址
		   registry.addResourceHandler("/img/**").addResourceLocations(mImagesPath);
	    }
        
	     @Bean   //图片拦截
	     public ImgAuthorityInterceptor imgAuthorityInterceptor() {
	         return new ImgAuthorityInterceptor();
	     }
	     
	    @Bean    //登陆拦截
	    public LogAuthorityInterceptor logAuthorityInterceptor() {
	         return new LogAuthorityInterceptor();
	     }
	   
	}
