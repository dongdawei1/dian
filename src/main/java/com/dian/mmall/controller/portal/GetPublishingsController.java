package com.dian.mmall.controller.portal;

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

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.release.GetPublishingsService;
import com.dian.mmall.service.release.ReleaseCommodityService;
import com.dian.mmall.service.release.ReleaseWelfareService;
import com.dian.mmall.service.release.ResumeService;
import com.dian.mmall.util.CheckLand;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

@Controller
@RequestMapping("/api/getPublishings/")
public class GetPublishingsController {
   String recruitWorkers="/home/recruitWorkers";
	
	
	@Autowired
	private  GetPublishingsService getPublishingsService;
	@Autowired
	private ReleaseWelfareService releaseWelfareService;
    @Autowired
    private ResumeService resumeService;
	
//查询商品接口
    
    @RequestMapping(value = "getGoods",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getGoods(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	params.put("StringPath", recruitWorkers);
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
 	
    	return getPublishingsService.getMenuList(user, params);
    	
    }
    
    //职位获取电话或者邮箱   
    @RequestMapping(value = "getContact",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getContact(HttpServletRequest httpServletRequest,@RequestBody Map<String,Object> params){
   
    	//检查登陆
    	ServerResponse<Object> serverResponse=CheckLand.checke_land(httpServletRequest);
    	if(serverResponse.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage(serverResponse.getMsg());
    	}
     	User user = (User) serverResponse.getData();
    	//检查权限
     	ServerResponse<String>	serverResponse1=CheckLand.checke_see(user,params);
    	if(serverResponse1.getStatus()!=0) {
    		return ServerResponse.createByErrorMessage( serverResponse1.getMsg());
    	}
    	
//        selectType:1   //1是查询职位 ，2是查询 简历
    	String selectTypeString=params.get("selectType").toString().trim();
        if(selectTypeString==null || selectTypeString.equals("")) {
        	return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
        }		
        int selectType=Integer.valueOf(selectTypeString);
        //1电话2邮箱
        if(selectType==1 ) {
        	return releaseWelfareService.getContact(user, params);
        }else if(selectType==2){
        	return resumeService.getContact(user, params);
        }
        
            return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
       
    }
}
