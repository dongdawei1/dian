package com.dian.mmall.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;

public class CheckLand {
	
	static String meiyouquanxString=ResponseMessage.meiyouciquanxian.getMessage();
	//检查管理员
    public static ServerResponse<Object> checke_role(HttpServletRequest httpServletRequest){
    	
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
     	if(StringUtils.isEmpty(loginToken)){
     		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
     	}
     	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
     	User user = JsonUtil.string2Obj(userJsonStr,User.class);
     	if(user == null){
     		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
     	}	
     	//TODO  写死的代码 如果不是这个用户名将查不到
     	if(user.getRole()!=1 || !user.getUsername().equals("z222222221") ) {
     		return ServerResponse.createByErrorMessage(meiyouquanxString);
     	}
     	return ServerResponse.createBySuccess(user);
    }
    
    
    //检查登陆
    public static ServerResponse<Object> checke_land(HttpServletRequest httpServletRequest ){
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
     	if(StringUtils.isEmpty(loginToken)){
     		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
     	}
     	String userJsonStr = RedisShardedPoolUtil.get(loginToken);
     	User user = JsonUtil.string2Obj(userJsonStr,User.class);
     	if(user == null){
     		return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
     	}
     	return ServerResponse.createBySuccess(user);
    }
    
    //检查创建权限
    public static ServerResponse<String> getCreateRole(User user,Map<String,Object> params){
    	String menu =params.get("StringPath").toString().trim();
    	if(menu==null || menu.equals("")) {
    		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
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
     	
     	
            int role=user.getRole();
            
     		if(menu.equals("/home/recruitWorkers") || menu.equals("/home/releaseWelfare")) { //创建职位和和创建职位按键
     			if(role!=2&&role!=5&& role!=1) {
         		return ServerResponse.createByErrorMessage(meiyouquanxString);}
         	} //创建职位
     		else if(menu.equals("/home/jobWanted") ) {
     			if(role!=11&& role!=1&& role!=4) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }
     		//装修，灭虫广告菜谱创建权限  ，这几项放在一个页面中	
     		else if(menu.equals("menuAndRenovationAndPestControl")) {
     			if(role!=1&&role!=7) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			
     		}//创建米面
     		else if(menu.equals("/home/GrainAndOilPage")) {
     			if(role!=1&&role!=4) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     		}
     		else if(menu.equals("/home/lease") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if( role!=1&& role!=2&&role!=3&&role==6) {
     				return ServerResponse.createByErrorMessage(meiyouquanxString);
     			}
            }//摊位出租创建
     		else if(menu.equals("/home/rentalBooth") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if( role!=1&& role!=4&&role!=5&&role==6) {
     				return ServerResponse.createByErrorMessage(meiyouquanxString);
     		}
     			
            }
     		else if(menu.equals("/home/equipment") ) {
     			//电器
     			if( role==1&& role==3) {
     				return ServerResponse.createByErrorMessage(meiyouquanxString);
     			}
            }
     		else if(menu.equals("/home/foodAndGrain") ) {
     			//  蔬菜/调料/水产禽蛋   拥有  4,5,6,29  蔬菜，粮油，调料，水产,
     			if(role!=1 &&role!=4 ) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			
            }
     		
     		
     		
     		
     		else if(menu.equals("/home/release") ) {
     			if(role!=2 && role!=1) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/kitchenUtensils") ) {
     			if(role!=3&& role!=1) { //TODO 一下没有验证
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/vegetables") || menu.equals("/home/grainAndOil") || menu.equals("/home/seasoning")
            	||	menu.equals("/home/clean") || menu.equals("/home/aquaticProduct")) {
     			if(role!=4&& role!=5&& role!=1) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/wine") || menu.equals("/home/tableware")) {
     			if(role!=5&& role!=1) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/renovation") || menu.equals("/home/pestControl") ||menu.equals("/home/menu") ) {
     			if(role!=7&& role!=1) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/departmentStore") ) {
     			if(role!=12&& role!=1) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else {
            	return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
            }
     		return ServerResponse.createBySuccess();
    }
    //菜单查看权限
    public static ServerResponse<String> checke_see(User user,Map<String,Object> params ){
    	String menu =params.get("StringPath").toString().trim();
    	if(menu==null || menu.equals("")) {
    		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
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
     	
     
          int role=user.getRole();
          
          
          
          
     		if(menu.equals("/home/recruitWorkers") ) {
     			if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=6&&role!=7&&role!=8&&role!=11&&role!=12) {
         		return ServerResponse.createByErrorMessage(meiyouquanxString);}
         	}else if(menu.equals("/home/jobWanted") ) {//求职信息
     			if(role!=11&&role!=1&&role!=2&&role!=4&&role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }
   		//查看职位创建米面
     		else if(menu.equals("/home/GrainAndOilPage")) {
     			if(role!=1&&role!=2&&role!=4) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     		}//装修，灭虫广告菜谱查看权限	
     		else if(menu.equals("/home/pestControl")|| menu.equals("/home/menu")||menu.equals("/home/renovation")||menu.equals("menuAndRenovationAndPestControl")) {
     			if(role!=12&&role!=1&&role!=2&&role!=6&&role!=7&&role!=3) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     		}
     		else if(menu.equals("/home/lease") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if(role!=1 &&role!=2 &&role!=3&&role!=6&&role!=11) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			
            }//摊位出租创建
     		else if(menu.equals("/home/rentalBooth") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if(role!=1 &&role!=4 &&role!=5&&role!=6&&role!=11) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			
            }
     		
     		else if(menu.equals("/home/equipment") ) {
     			//看电器1,2,3,4,5,6,7,12 （查看权限）   ， 1,3查看
     			if(role!=1 &&role!=2 &&role!=3 &&role!=4 &&role!=5&&role!=6&&role!=7 &&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);} 			
            }
     		
     		else if(menu.equals("/home/foodAndGrain") ) {
     			//  蔬菜/调料/水产禽蛋   拥有  4,5,6,29  蔬菜，粮油，调料，水产,
     			if(role!=1 &&role!=2  &&role!=4 ) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     		
     			
            }
     		
     		
     		
     		
     	else if(menu.equals("/home/release") ) {
            	if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=6&&role!=7&&role!=8&&role!=11&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/kitchenUtensils")) {
     			if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }
            
     		//TODO 一下没有验证
            
            else if(menu.equals("/home/vegetables") || menu.equals("/home/grainAndOil") || menu.equals("/home/seasoning")
            	||	menu.equals("/home/clean") || menu.equals("/home/aquaticProduct")) {
     			if(role!=4&& role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/wine") || menu.equals("/home/tableware")) {
     			if(role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/renovation") || menu.equals("/home/pestControl") ||menu.equals("/home/menu") ) {
     			if(role!=7) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/departmentStore") ) {
     			if(role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else {
            	return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
            }
     		return ServerResponse.createBySuccess();
     		
    }
    

  //菜单按钮
    public static ServerResponse<Object> checke_isButten(User user,String menu ){
    	if(menu==null || menu.equals("")) {
    		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
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
     	
     
          int role=user.getRole();
          
          Map<String ,Object> isButten=new HashMap<String, Object>();
           //
     		if(menu.equals("/home/toExamine") || menu.equals("/home/businessEnquiry") || menu.equals("addRealName") ) {
     			if(role!=1) {
         		return ServerResponse.createByErrorMessage(meiyouquanxString);}
         	}//职位查看和创建职位
     		else if(menu.equals("/home/recruitWorkers") || menu.equals("/home/releaseWelfare")) { //创建职位和和创建职位按键
     			if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=6&&role!=7&&role!=8&&role!=11&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if(role==2|| role==5 || role==1) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     			
            }//首页发布键
     		else if(menu.equals("/home/release")) {
     			if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=6&&role!=7&&role!=8&&role!=11&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if(role==2||  role==1) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     		} //查看职位创建职位
     		else if(menu.equals("myJobWanted") ||menu.equals("/home/jobWanted")) {
     			if(role!=11&&role!=1&&role!=2&&role!=4&&role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if(role==4||  role==1||  role==11) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     		}
     		 //查看职位创建灭虫  menuAndRenovationAndPestControl 我的发布灭虫装修广告在一个页面
     		///home/menu 广告
     		else if(menu.equals("/home/pestControl")|| menu.equals("/home/menu")||menu.equals("/home/renovation")||menu.equals("menuAndRenovationAndPestControl")) {
     			if(role!=12&&role!=1&&role!=2&&role!=6&&role!=7&&role!=3) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1||  role==7) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     		} //查看职位创建米面
     		else if(menu.equals("/home/GrainAndOilPage")) {
     			if(role!=1&&role!=2&&role!=4) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1||  role==4) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     		}//店面出租创建
     		else if(menu.equals("/home/lease") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if(role!=1 &&role!=2 &&role!=3&&role!=6&&role!=11) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1|| role==2||role==3||role==6) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     			
            }//摊位出租创建
     		else if(menu.equals("/home/rentalBooth") ) {
     			//店面1236 11（查看权限）   ，摊位 1456 11查看
     			if(role!=1 &&role!=4 &&role!=5&&role!=6&&role!=11) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1|| role==4||role==5||role==6) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     			
            }
     		else if(menu.equals("/home/equipment") ) {
     			//看电器1,2,3,4,5,6,7,12 （查看权限）   ， 1,3查看
     			if(role!=1 &&role!=2 &&role!=3 &&role!=4 &&role!=5&&role!=6&&role!=7 &&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1|| role==3) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     			
            }
     		else if(menu.equals("/home/foodAndGrain") ) {
     			//  蔬菜/调料/水产禽蛋   拥有  4,5,6,29  蔬菜，粮油，调料，水产,
     			if(role!=1 &&role!=2  &&role!=4 ) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
     			if( role==1|| role==4) {
     				isButten.put("isCreate", true);
     			}else {
     				isButten.put("isCreate", false);
     			}
     			isButten.put("isAuthentication", user.getIsAuthentication());
     			isButten.put("isSee", true);
     			
            }
     		else if(menu.equals("/home/lease") ) {
     			if(role!=2&&role!=3&&role!=6&&role!=1&&role!=11) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/release") ) {
            	if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=6&&role!=7&&role!=8&&role!=11&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/kitchenUtensils")) {
     			if(role!=1&&role!=2&&role!=3&&role!=4&&role!=5&&role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }
            
     		
     		
     		//TODO 一下没有验证
            
            else if(menu.equals("/home/vegetables") || menu.equals("/home/grainAndOil") || menu.equals("/home/seasoning")
            	||	menu.equals("/home/clean") || menu.equals("/home/aquaticProduct")) {
     			if(role!=4&& role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/wine") || menu.equals("/home/tableware")) {
     			if(role!=5) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/renovation") || menu.equals("/home/pestControl") ||menu.equals("/home/menu") ) {
     			if(role!=7) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else if(menu.equals("/home/departmentStore") ) {
     			if(role!=12) {
             		return ServerResponse.createByErrorMessage(meiyouquanxString);}
            }else {
            	return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
            }
     		
     		isButten.put("data", user);
     		return ServerResponse.createBySuccess(isButten);
     		
    }
    
    
    
    //菜单按钮
    public static ServerResponse<Object> checke_isButten(User user,Map<String,Object> params ){
    	String menu =params.get("StringPath").toString().trim();
    	if(menu==null || menu.equals("")) {
    		return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage());
    	}	
     	 return   checke_isButten(user,menu);  	    	   		
    }
    
    
    public static void main(String[] args) {
    
	}
}
