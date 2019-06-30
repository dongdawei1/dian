package com.dian.mmall.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.CookieUtil;
import com.dian.mmall.util.JsonUtil;
import com.dian.mmall.util.RedisShardedPoolUtil;

public class CheckLand {
	//检查管理员
    public static ServerResponse<Object> checke_role(HttpServletRequest httpServletRequest){
    	
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
     	if(StringUtils.isEmpty(loginToken)){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}
     	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
     	User user = JsonUtil.string2Obj(userJsonStr,User.class);
     	if(user == null){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}	
     	if(user.getRole()!=1 || !user.getUsername().equals("z222222221") ) {
     		return ServerResponse.createByErrorMessage("没有权限");
     	}
     	return ServerResponse.createBySuccess(user);
    }
    
    //检查登陆
    public static ServerResponse<Object> checke_land(HttpServletRequest httpServletRequest,String...menu ){
    	//TODO  写死的代码 如果不是这个用户名将查不到
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
     	if(StringUtils.isEmpty(loginToken)){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}
     	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
     	User user = JsonUtil.string2Obj(userJsonStr,User.class);
     	if(user == null){
     		return ServerResponse.createByErrorMessage("用户登陆已过期");
     	}	
     	
     	
     	
//        //商家2
//        isCreatePosition:false,//发布职位权限 2,5
//        isWindowRental:false,//窗口出租 2,3,6
//        isPurchase:false,//采购信息 2
//        // 厨具/电器/设备维修3
//        isKitchenUtensils:false,//发布厨具 3
//        isRepair:false,//发布维修信息 3
//        //蔬菜/调料/水产禽蛋4
//        isCommodity:false,//发布商品 4,5
//         isBooth:false,//摊位出租4，5,6
//
//       //酒水/消毒餐具/清洁用品5
//        isTableware:false,//消毒餐具5
//        isWine:false,//酒水5
//
//        //商铺/摊位出租6
//        //装修/菜谱/广告牌/杀虫灭蟑
//        isMenu:false,//菜谱 7
//        isRenovation:false,//装修7
//        isPestControl:false,//灭虫7
//        //求职11
//        isJobWanted:false,//求职11
//        //工服百货
//        isDepartmentStore:false,//百货12
     	
     	if(menu.length>0 ) {
          int role=user.getRole();
     		if(menu.equals("/home/recruitWorkers") ) {
     			if(role!=2&&role!=5) {
         		return ServerResponse.createByErrorMessage("没有权限");}
         	}else if(menu.equals("/home/lease") ) {
     			if(role!=2&&role!=3&&role!=6) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/release") ) {
     			if(role!=2) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/kitchenUtensils") || menu.equals("/home/repair")) {
     			if(role!=3) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/vegetables") || menu.equals("/home/grainAndOil") || menu.equals("/home/seasoning")
            	||	menu.equals("/home/clean") || menu.equals("/home/aquaticProduct")) {
     			if(role!=4&& role!=5) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/rentalBooth") ) {
     			if(role!=4 &&role!=5 &&role!=6) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/wine") || menu.equals("/home/tableware")) {
     			if(role!=5) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/renovation") || menu.equals("/home/pestControl") ||menu.equals("/home/menu") ) {
     			if(role!=7) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/jobWanted") ) {
     			if(role!=11) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }else if(menu.equals("/home/departmentStore") ) {
     			if(role!=12) {
             		return ServerResponse.createByErrorMessage("没有权限");}
            }
     		
     		
     	}
     	return ServerResponse.createBySuccess(user);
    }
 
    public static void main(String[] args) {
    
	}
}
