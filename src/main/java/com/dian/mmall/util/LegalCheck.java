package com.dian.mmall.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LegalCheck {
   //判断字段输入是否合法
	public static ServerResponse<String> legalCheckFrom(Map<String, Object> params){

		params.remove("StringPath");
		Set<String> key=params.keySet();
		  for(String s11  : key){
			  
			  Object s12=params.get(s11);	
			  
			  if((s12  instanceof String) && s12!= null){
				if(((String) s12).toLowerCase().indexOf("delete")>=0 || ((String) s12).toLowerCase().indexOf("update")>=0 || ((String) s12).indexOf("=")>=0
						|| ((String) s12).toLowerCase().indexOf("or")>=0) {
					log.info("非法字段map------>"+s12);		
				return	ServerResponse.createByErrorMessage(s12+ResponseMessage.ShuRuBuHeFa.getMessage());
				}    
		      }		  
		  }
		  return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());
		
	}
	//判断字段输入是否合法
		public static ServerResponse<String> legalCheckNonum(Map<String, Object> params){

			params.remove("StringPath");
			Set<String> key=params.keySet();
			  for(String s11  : key){
				  
				  Object s12=params.get(s11);	
				  
				  if((s12  instanceof String) && s12!= null){
					  String s13=s12.toString();
					    char[] ch = s13.toCharArray();
				        for (char c : ch) {
				            if (c < 0x4E00 || c > 0x9FBF) {
				            	return	ServerResponse.createByErrorMessage(s12+ResponseMessage.ShuRuBuHeFa.getMessage());
				            }
				        }
			      }else {
			    	  return	ServerResponse.createByErrorMessage("名称只能是汉字切不能有空格");
			      }		  
			  }
			  return ServerResponse.createBySuccess();
			
		}
		//判断字段输入是否合法
				public static ServerResponse<String> legalCheckServiceTypeName(String params){

					params=params.trim();
					  if(params==null || params.equals("")) {
						  return	ServerResponse.createByErrorMessage("名称不能为空");
					}
				   
				    char[] ch = params.toCharArray();
					   for (char c : ch) {
						    if (c < 0x4E00 || c > 0x9FBF) {
								  return	ServerResponse.createByErrorMessage("名称只能是汉字切不能有空格");
						    }
						}
					  return ServerResponse.createBySuccessMessage(params);
					
				}
	//判断是否是合法角色
		public static ServerResponse<String> legalCheckRole(String role){
			
			if(role.indexOf("2")!=0 && role.indexOf("3")!=0 && role.indexOf("4")!=0  && role.indexOf("5")!=0  &&  role.indexOf("6")!=0   && role.indexOf("7")!=0 &&
	    			 role.indexOf("8")!=0 && role.indexOf("10")!=0 && role.indexOf("11")!=0 && role.indexOf("12")!=0 ) {
				log.info("非法字段role------>"+role);
				return	ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());		
	    	}
			return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());
			
		}
	
		//判断手机号是否合法
		public static ServerResponse<String> legalCheckMobilePhone(String mobilePhone){
			Pattern pattern = Pattern.compile("[0-9]*");	
			if(mobilePhone.length()==11 && pattern.matcher(mobilePhone).matches()) {
                    return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());	
					}
					return	ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());	
				}
}
