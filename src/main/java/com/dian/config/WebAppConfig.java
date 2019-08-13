package com.dian.config;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.dian.mmall.common.Const;

import javax.servlet.MultipartConfigElement;

//上传配置类
//图片放到/F:/fileUpload/后，从磁盘读取的图片数据scr将会变成images/picturename.jpg的格式
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {
//public class WebAppConfig extends WebMvcConfigurationSupport {
    /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${cbs.imagesPath}")
    private String mImagesPath;

//	private String mImagesPath=Const.PATH_E_IMG;
	
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大KB,MB
        factory.setMaxFileSize("1024MB");
        //设置总上传数据总大小
        factory.setMaxRequestSize("1024MB");
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	 if(mImagesPath.equals("") || mImagesPath.equals("${cbs.imagesPath}")){
             String imagesPath = WebAppConfig.class.getClassLoader().getResource("").getPath();
             if(imagesPath.indexOf(".jar")>0){
                 imagesPath = imagesPath.substring(0, imagesPath.indexOf(".jar"));
             }else if(imagesPath.indexOf("classes")>0){
                 imagesPath = "file:"+imagesPath.substring(0, imagesPath.indexOf("classes"));
             }
             imagesPath = imagesPath.substring(0, imagesPath.lastIndexOf("/"))+"/img/";
             mImagesPath = imagesPath;
         }
         registry.addResourceHandler("/img/**").addResourceLocations(mImagesPath);
         super.addResourceHandlers(registry);


    }

}
