package com.dian.mmall.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.dian.mmall.common.ResponseMessage;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.util.mingan.SensitivewordFilter;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LegalCheck {
	// 检查管理员
	public static boolean checke_role(HttpServletRequest httpServletRequest) {

		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if (StringUtils.isEmpty(loginToken)) {
			return false;
		}
		String userJsonStr = RedisShardedPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		if (user == null) {
			return false;
		}
		// TODO 写死的代码 如果不是这个用户名将查不到
		if (user.getRole() != 1 || !user.getUsername().equals("z222222221")) {
			return false;
		} else {
			return true;
		}

	}

	// 检查管理员
	public static ServerResponse<Object> checke_guanli_user(HttpServletRequest httpServletRequest) {

		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if (StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
		}
		String userJsonStr = RedisShardedPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		if (user == null) {
			return ServerResponse.createByErrorMessage(ResponseMessage.dengluguoqi.getMessage());
		}
		// TODO 写死的代码 如果不是这个用户名将查不到
		if (user.getRole() != 1 || !user.getUsername().equals("z222222221")) {
			return ServerResponse.createByErrorMessage(ResponseMessage.meiyouciquanxian.getMessage());
		}
		return ServerResponse.createBySuccess(user);
	}

	// 发布权限
	public static boolean checkefaburole(int releaseType, int role) {
		if (releaseType == 4 || releaseType == 5 || releaseType == 6 || releaseType == 9 || releaseType == 29
				|| releaseType == 11) {
			// 4蔬菜出售 5粮油出售 6调料/副食出售 29水产/禽蛋出售 9清洁用品 11桌椅餐具
			if (role == 4 || role == 1) {
				return true;
			}
		}
		// 101工服102百货103绿植104装饰用品
		else if (releaseType == 101 || releaseType == 102 || releaseType == 103 || releaseType == 104) {
			if (role == 12 || role == 1) {
				return true;
			}
		}
		// 13菜谱/广告17装修19灭虫
		else if (releaseType == 13 || releaseType == 17 || releaseType == 19) {
			if (role == 7 || role == 1) {
				return true;
			}
		}
		// 7酒水/饮料8消毒餐具
		else if (releaseType == 7 || releaseType == 8) {
			if (role == 5 || role == 1) {
				return true;
			}
		}
		// 33电器/设备出售34二手电器/设备出售18维修电器/设备
		else if (releaseType == 33 || releaseType == 34 || releaseType == 18) {
			if (role == 3 || role == 1) {
				return true;
			}
		}
		// 15摊位出租转让
		else if (releaseType == 15) {
			if (role == 4 || role == 1 || role == 5 || role == 6 || role == 12 || role == 13) {
				return true;
			}
		}
		// 14店面/窗口出租
		else if (releaseType == 14) {
			if (role == 3 || role == 1 || role == 2 || role == 6) {
				return true;
			}
		}
		return false;
	}

	// 发布总数
	public static boolean checkefabuzongshu(int releaseType, int counr) {
		if (releaseType == 4 || releaseType == 5 || releaseType == 6 || releaseType == 9 || releaseType == 29
				|| releaseType == 11) {
			// 4蔬菜出售 5粮油出售 6调料/副食出售 29水产/禽蛋出售 9清洁用品 11桌椅餐具
			if (counr <21) {
				return true;
			}
		}
		// 101工服102百货103绿植104装饰用品
		else if (releaseType == 101 || releaseType == 102 || releaseType == 103 || releaseType == 104) {
			if (counr <21) {
				return true;
			}
		}
		// 13菜谱/广告17装修19灭虫
		else if (releaseType == 13 || releaseType == 17 || releaseType == 19) {
			if (counr <21) {
				return true;
			}
		}
		// 7酒水/饮料8消毒餐具
		else if (releaseType == 7 || releaseType == 8) {
			if (counr <21) {
				return true;
			}
		}
		// 33电器/设备出售34二手电器/设备出售18维修电器/设备
		else if (releaseType == 33 || releaseType == 34 || releaseType == 18) {
			if (counr <21) {
				return true;
			}
		}
		// 15摊位出租转让
		else if (releaseType == 15) {
			if (counr <11) {
				return true;
			}
		}
		// 14店面/窗口出租
		else if (releaseType == 14) {
			if (counr <11) {
				return true;
			}
		}
		return false;
	}
	
	
	// 判断字段输入是否合法
	public static ServerResponse<String> legalCheckFrom(Map<String, Object> params) {
	    params.remove("StringPath");
        String content = params.toString();
         SensitivewordFilter filter = new SensitivewordFilter();
		 Set<String> set = filter.getSensitiveWord(content, 1);
		if(set.size()>0) {
			return ServerResponse.createByErrorMessage( ResponseMessage.ShuRuBuHeFa.getMessage()+set);
		}
		String illegal = "`~!#%^&*+\\|;'\"<>?○●★☆☉♀♂※¤╬の〆";
		char isLegalChar = 't';
		L1: for (int i = 0; i < content.length(); i++) {
			for (int j = 0; j < illegal.length(); j++) {
				if (content.charAt(i) == illegal.charAt(j)) {
					isLegalChar = content.charAt(i);
					break L1;
				}
			}
		}
		if(isLegalChar!='t') {
			return ServerResponse.createByErrorMessage( ResponseMessage.ShuRuBuHeFa.getMessage()+isLegalChar);
		}
		
		Set<String> key = params.keySet();
		for (String s11 : key) {
			Object s12 = params.get(s11);
			if ((s12 instanceof String) && s12 != null) {
				if (((String) s12).toLowerCase().indexOf("delete") >= 0
						|| ((String) s12).toLowerCase().indexOf("update") >= 0 || ((String) s12).indexOf("=") >= 0
						|| ((String) s12).toLowerCase().indexOf("or") >= 0) {
					return ServerResponse.createByErrorMessage(ResponseMessage.ShuRuBuHeFa.getMessage()+s12);
				}
			}
		}
		return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());

	}

	
	// 判断字段输入是否合法
	public static ServerResponse<String> legalCheckNonum(Map<String, Object> params) {

		params.remove("StringPath");
		Set<String> key = params.keySet();
		for (String s11 : key) {

			Object s12 = params.get(s11);

			if ((s12 instanceof String) && s12 != null) {
				String s13 = s12.toString();
				char[] ch = s13.toCharArray();
				for (char c : ch) {
					if (c < 0x4E00 || c > 0x9FBF) {
						return ServerResponse.createByErrorMessage(s12 + ResponseMessage.ShuRuBuHeFa.getMessage());
					}
				}
			} else {
				return ServerResponse.createByErrorMessage("名称只能是汉字切不能有空格");
			}
		}
		return ServerResponse.createBySuccess();

	}

	// 判断字段输入是否合法
	public static ServerResponse<String> legalCheckServiceTypeName(String params) {

		params = params.trim();
		if (params == null || params.equals("")) {
			return ServerResponse.createByErrorMessage("名称不能为空");
		}

//		char[] ch = params.toCharArray();
//		for (char c : ch) {
//			if (c < 0x4E00 || c > 0x9FBF) {
//				return ServerResponse.createByErrorMessage("名称只能是汉字切不能有空格");
//			}
//		}
		return ServerResponse.createBySuccessMessage(params);

	}

	// 判断是否是合法角色
	public static ServerResponse<String> legalCheckRole(String role) {

		if (role.indexOf("2") != 0 && role.indexOf("3") != 0 && role.indexOf("4") != 0 && role.indexOf("5") != 0
				&& role.indexOf("6") != 0 && role.indexOf("7") != 0 && role.indexOf("8") != 0 && role.indexOf("10") != 0
				&& role.indexOf("11") != 0 && role.indexOf("12") != 0 && role.indexOf("13") != 0) {
			log.info("非法字段role------>" + role);
			return ServerResponse.createByErrorMessage(ResponseMessage.CaiDanBuCunZai.getMessage());
		}
		return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());

	}

	// 判断手机号是否合法
	public static ServerResponse<String> legalCheckMobilePhone(String mobilePhone) {
		Pattern pattern = Pattern.compile("[0-9]*");
		if (mobilePhone.length() == 11 && pattern.matcher(mobilePhone).matches()) {
			return ServerResponse.createBySuccessMessage(ResponseMessage.ShuRuHeFa.getMessage());
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.ShouJiHaoBuHeFa.getMessage());
	}

	// 判断是不是整数
	public static boolean isNumericFolse(String str) {

		// 正数，最多两位小数
		Pattern pattern = Pattern.compile("^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$");
		return pattern.matcher(str).matches();

	}

	public static boolean isNumericInt(String string) {
		// 正整数 不含0
		Pattern pattern = Pattern.compile("^([1-9]\\d*|0)$");
		if (!string.equals("0")) {
			// Regex reg=new Regex("^([1-9]\\d*|0)$");
			return pattern.matcher(string).matches();
		}
		return false;
	}

	// 整数含0
	public static ServerResponse<Object> isNumericInthan0(String str) {
		if ((str.matches("[0-9]+")) && (Integer.parseInt(str) > 0)) {
			// 如果以上条件成立，则str是正整数，可以将str转为int类型，得到一个正整数n
			return ServerResponse.createBySuccess(Integer.parseInt(str));
		}
		return ServerResponse.createByErrorMessage(ResponseMessage.yunfeicuowo.getMessage());
	}

	public static void main(String[] args) {

		HashMap<String, Object> sHashMap = new HashMap<String, Object>();
		sHashMap.put("www", "www");
		sHashMap.put("www11", 222);
		sHashMap.put("www1", "DW");
		sHashMap.put("www12", "delete");
		// Float float1 = Float.valueOf("0.22") * 100;
		String float1 = "12";
		// long commodityJiage = float1.longValue();
		
		System.out.println(legalCheckFrom(sHashMap).getMsg());
		
		char isLegalChar = 't';
		if(isLegalChar!='t') {
			System.out.println("LegalCheck.main()");
		}
		
		if(isLegalChar=='t') {
			System.out.println("LegalCheck.main(2222222)");
		}
		if(isLegalChar=='2') {
			System.out.println("LegalCheck.main(222222222221)");
		}
	}
}
