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

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.tupian.Picture;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.IPictureService;
import com.dian.mmall.service.IUserService;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.FileControl;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/uploadDown/")
public class UploadDownController {
  
	//图片访问地址  http://localhost:8080/img/201907251001091.jpg

    @Autowired
    private IPictureService ipics;
    
    private  String path="E:/img/";
	
    /**
     * 文件上传
     * @param picture
     * @param request
     * @return
     */
    @RequestMapping("upload")   //不写请求方式是 get和post都支持
    @ResponseBody
    public ServerResponse<Object> upload(@RequestParam MultipartFile picture, HttpServletRequest httpServletRequest) {
       
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		 return ServerResponse.createByErrorMessage(ResponseMessage.DengLuGuoQi.getMessage());
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    
        //获取文件在服务器的储存位置
       // String path = httpServletRequest.getSession().getServletContext().getRealPath("/upload");
    	File filePath = new File(path);
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
        String fileName = date +(int)(Math.random()*10000) + "." + type;
       
        //在指定路径下创建一个文件
        File targetFile = new File(path, fileName);
        Picture picture1=new Picture();
        //将文件保存到服务器指定位置
        try {
        //	http://localhost:8080/img/201907251001091.jpg
            picture.transferTo(targetFile);       
            //将文件在服务器的存储路径返回
            picture1.setCreateTime(date);           
            picture1.setTocken(loginToken);
            picture1.setUserId(user.getId());
            picture1.setUserName(fileName);
            picture1.setUseStatus(1);        
            picture1.setPictureName(originalFileName);
            
            //TODO 访问地址写死的
            //picture1.setPicture_url(path + fileName);
            String linshiString="http://localhost:8080/img/";
            picture1.setPictureUrl(linshiString + fileName);
          
           ipics.createPicture(picture1);
           
           Picture  picture2=  ipics.selectPicture(picture1);
           if(picture2!=null) {
        	   System.out.println(picture2.toString());
        	   return ServerResponse.createBySuccess(picture2);
           }
           return ServerResponse.createByErrorMessage(ResponseMessage.shangchuanshibai.getMessage());
           
           // return new Result(true,path + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage(ResponseMessage.shangchuanshibai.getMessage());
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
    public ServerResponse<String> update(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	if(StringUtils.isEmpty(loginToken)){
    		return ServerResponse.createByErrorMessage(ResponseMessage.DengLuGuoQi.getMessage());
    	}
    	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
    	User user = JsonUtil.string2Obj(userJsonStr,User.class);
    	
    	
    	 String idString=params.get("id").toString().trim();
    	 if(idString==null || idString.equals("") ) {
    		 return ServerResponse.createByErrorMessage(ResponseMessage.idbucunzai.getMessage());
    	 }
    	 long id=Long.valueOf(idString);
     	Picture  picture2=  ipics.selectPictureBYid(id);
     	
     	String pictureNameString=params.get("pictureName").toString().trim();
    	 String userNameString=params.get("userName").toString().trim();
    	
    	if(!userNameString.equals(picture2.getUserName()) || !pictureNameString.equals(picture2.getPictureName())) {
    		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
    	}
    	if(user.getId()!=picture2.getUserId()) {
    		 return ServerResponse.createByErrorMessage(ResponseMessage.yonghuidbucunzai.getMessage());
    	}
        
       Boolean fal =FileControl.deleteFile(path+userNameString);
       if(fal==true) {
    	   ipics.updatePicture(id);
    	   return ServerResponse.createBySuccess(); 
       }
       return ServerResponse.createByErrorMessage(ResponseMessage.shangchushibai.getMessage());
    
    }
}
