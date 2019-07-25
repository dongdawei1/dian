package com.dian.mmall.controller.portal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dian.mmall.common.Result;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IPictureService;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/uploadDown/")
public class UploadDownController {
  
	//图片访问地址  http://localhost:8080/img/201907251001091.jpg

    @Autowired
    private IPictureService ipics;
	
    /**
     * 文件上传
     * @param picture
     * @param request
     * @return
     */
    @RequestMapping("upload")   //不写请求方式是 get和post都支持
    @ResponseBody
    public Result upload(@RequestParam MultipartFile picture, HttpServletRequest httpServletRequest) {
       
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return new Result(false, "请登录后重试");
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	
    	
    	  
    	
        //获取文件在服务器的储存位置
       // String path = httpServletRequest.getSession().getServletContext().getRealPath("/upload");
    	String path="E:/img/";
    	File filePath = new File(path);
        System.out.println(httpServletRequest.getSession().getServletContext().getRealPath("/upload"));
        System.out.println(filePath.toPath().toString());
        System.out.println(filePath.toString());
        if (!filePath.exists() && !filePath.isDirectory()) {
            
            filePath.mkdir();
        }
 
        //获取原始文件名称(包含格式)
        String originalFileName = picture.getOriginalFilename();
        
 
        //获取文件类型，以最后一个`.`为标识
        String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        
        //获取文件名称（不包含格式）
        String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
 
        //设置文件新名称: 当前时间+文件名称（不包含格式）
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(d);
        String fileName = date + name + "." + type;
        
 
        //在指定路径下创建一个文件
        File targetFile = new File(path, fileName);
        Picture picture1=new Picture();
        //将文件保存到服务器指定位置
        try {
        //	http://localhost:8080/img/201907251001091.jpg
        	
            picture.transferTo(targetFile);       
            //将文件在服务器的存储路径返回
            picture1.setCreate_time(date);           
            picture1.setTocken(loginToken);
            picture1.setUser_id(user.getId());
            picture1.setUser_name(user.getUsername());
            picture1.setUse_status(1);        
            picture1.setPicture_name(originalFileName);
            
            //TODO 访问地址写死的
            //picture1.setPicture_url(path + fileName);
            String linshiString="http://localhost:8080/img/";
            picture1.setPicture_url(linshiString + fileName);
          
           ipics.createPicture(picture1);
           
           return new Result(true,linshiString + fileName);
           // return new Result(true,path + fileName);
        } catch (IOException e) {
           
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
    /**
     * 文件删除  修改数据库字段
     * @param picture
     * @param request
     * @return
     */
    @RequestMapping(value = "update" ,method = RequestMethod.POST)   //不写请求方式是 get和post都支持
    @ResponseBody
    public Result update(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
       
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return new Result(false, "请登录后重试");
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	Picture picture1=new Picture();          
       
    	picture1.setTocken(loginToken);
        picture1.setUser_id(user.getId());
        picture1.setUser_name(user.getUsername());
        picture1.setUse_status(1);  
        String picture_name=params.get("picture_name").toString().trim();
        String picture_url=params.get("picture_url").toString().trim();
       
        
        System.out.println(picture_name+" fffff  "+picture_url);
        if(picture_name!=null && picture_url!=null ) {
        picture1.setPicture_name(picture_name);
        picture1.setPicture_url(picture_url);
        }else {
        return new Result(true,"删除成功");
        }
        
      Picture  picture2=  ipics.selectPicture(picture1);
      
        if(picture2 != null) {
    	ipics.updatePicture(picture1);
    	picture1=null;
    	picture2=null;
    	return new Result(true,"删除成功");
    	
        }
        picture1=null;
    	picture2=null;
    	return new Result(true,"删除成功");
 
     
 
     
    
    }
}
