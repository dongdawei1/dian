package com.dian.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.dian.mmall.controller.common.SessionExpireFilterApi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {
 //读取配置文件application.yml中spring.datasource 节点的设置
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
       return  new DruidDataSource();
    }

    //配置Druid的监控
    //1、配置一个管理后台的Servlet
//   @Bean
//    public ServletRegistrationBean statViewServlet(){
//        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//        Map<String,String> initParams = new HashMap<>();
//
//        initParams.put("loginUsername","admin");
//        initParams.put("loginPassword","123456");
//        initParams.put("allow","");//默认就是允许所有访问
//        initParams.put("deny","192.168.15.21");//决绝谁访问
//       
//        initParams.put("/img/**", "anon");
//        bean.setInitParameters(initParams);
//        return bean;
//    }
//
//
//    //2、配置一个web监控的filter
//   @Bean
//    public FilterRegistrationBean webStatFilter(){
//        FilterRegistrationBean bean = new FilterRegistrationBean();
//        bean.setFilter(new WebStatFilter());
//
//        Map<String,String> initParams = new HashMap<>();
//        initParams.put("exclusions","*.js,*.css,/druid/*");
//
//        bean.setInitParameters(initParams);
//
//        bean.setUrlPatterns(Arrays.asList("/*"));
//
//        return  bean;
//    }
   
   @Bean
   public FilterRegistrationBean<SessionExpireFilterApi> RegistTest1(){
       //通过FilterRegistrationBean实例设置优先级可以生效
       //通过@WebFilter无效
       FilterRegistrationBean<SessionExpireFilterApi> bean = new FilterRegistrationBean<SessionExpireFilterApi>();
       bean.setFilter(new SessionExpireFilterApi());//注册自定义过滤器
       bean.setName("sessionExpireFilterApi");//过滤器名称
       bean.addUrlPatterns("/api/*");//过滤所有路径
       bean.setOrder(2);//优先级，最顶级
       return bean;
   }
 
}
