package com.dian.mmall.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncrypDES {
	
	/**
     * main
     * @param args
     */
	public static void main(String[] args) {
		String val = "13810875784";//~a76hqkggq8hqhh
		String result=encryptPhone(val);
		System.out.println(result);
		String rawdata=decryptPhone("~aqb24a0qgwkwqh");//~aqbaqgwkwqh//~aqb24a0qgwkwqh
		System.out.println(rawdata);
	}
//	~ahqkg2g0qhq6hh
//	13358484844
	/**
	 * 数字 0 1 2 3 4 5 6 7 8 9
	 * 字母 b a c g h k o w q p
	 * @param val
	 * @return 数字转字母
	 */
	public static String getPhoneStr(String val) {
		String returnStr = "";
		if (val != null && val.length() > 0) {
			byte[] obj = val.getBytes();
			for (int i = 0; i < obj.length; i++) {
				returnStr += getLetter(String.valueOf(val.charAt(i)));
			}
		}
		return returnStr;
	}
    /**
     * 字母转数字
     * 字母 b a c g h k o w q p
     * 数字 0 1 2 3 4 5 6 7 8 9
     * @param val
     * @return
     */
	public static String getPhoneNum(String val){
		String returnStr = "";
		if (val != null && val.length() > 0) {
			byte[] obj = val.getBytes();
			for (int i = 0; i < obj.length; i++) {
				returnStr += getMapData().get(String.valueOf(val.charAt(i)));
			}
		}
		return returnStr;
	}
	
	
	
	
	/***
	 * 换位号码中两组数字：第二位和第六位交换，第三位和第五位交换，位数号按从左往右数
	 * 13510642584
	 * 16015342584
	 */
	public static String changeNum(String val){
		
		if(!isEmpty(val)){
			String str="";
			char [] phone=val.toCharArray();
			for(int i=0;i<phone.length;i++){
				if(i==1||i==2||i==4||i==5){
					if(i==1)
					    str+=phone[5];
					if(i==2)
						str+=phone[4];
					if(i==4)
						str+=phone[2];
					if(i==5)
						str+=phone[1];
				}else{
					str+=phone[i];
				}
			}
		    return str;
		}
		return val;
	}
	
	/**
	 * 通过Map值获取key
	 * @return
	 */
	public static String getLetter(String val) {
		for(Entry<String,String> entry:getMapData().entrySet()){
		    if(val.equals(entry.getValue()))
		       return String.valueOf(entry.getKey());
		}
		return val;
	}
	
	/**
	 * 字母 b a c g h k o w q p
     * 数字 0 1 2 3 4 5 6 7 8 9
	 * @return Map
	 */
	public static Map<String,String> getMapData(){
		Map<String,String> map = new HashMap<String,String>();
			map.put("b","0");
			map.put("a","1");
			map.put("c","2");
			map.put("g","3");
			map.put("h","4");
			map.put("k","5");
			map.put("o","6");
			map.put("w","7");
			map.put("q","8");
			map.put("p","9");
		return map;
	}
	
	
	
	/**
	 * 字符串前增加“~”
	 */
	public static String prefixStr(String val){
		return "~"+val;
	}
	
	/**
	 *  过滤字符串前的“~”
	 */
	public static String filterStr(String val){
		if(val!=null&&val.indexOf("~")>-1){
			return val.substring(1,val.length());
		}
		if(val!=null&&val.indexOf("%")>-1){
			val=val.replace("%7E","~").replace("%7e","~");
			return val.substring(1,val.length());
		}
		return val;
	}
	
	/**
	 * 去除所有数字
	 */
	public static String removeNum(String val){
		return val.replaceAll("\\d+","");
	}
	
	
	/**
	 * 随机插入3个数字
	 */
	public static String insertNum(String val){
		String num=radomNum(3);
		//往指定长度字符串加入随机数字
		for(int i=0;i<num.length();i++){
			Integer inNum= Integer.parseInt(radomNum(1));
			String result=val.substring(0,inNum>val.length()?val.length():inNum)+num.charAt(i)+val.substring(inNum>val.length()?val.length():inNum,val.length());
		    val=result;
		}
		return val;
	}
	
	
	/**
	 * 随机生成i位数字
	 * i<10
	 */
	public static String radomNum(int i){
		Random random = new Random();
		String val=String.valueOf(random.nextInt());
		return val.substring(0,i).indexOf("-")>-1?radomNum(i):val.substring(0,i);
	}
	
	
	/**
	 * ：加密步骤：
	 * 1、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
	 * 2、将全部号码转换为对应的字符
	 * 3、任意位置插入三个随机数字
	 * 4、在步骤C之后的字符串前加上”~”
	 * @return
	 */
	public static String encryptPhone(String phoneStr){
		//1.换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
		phoneStr=changeNum(phoneStr);
		//2.将全部号码转换为对应的字符
		phoneStr=getPhoneStr(phoneStr);
		//3.任意位置插入三个随机数字
		//phoneStr=insertNum(phoneStr);
		//4.在步骤C之后的字符串前加上”~”
		phoneStr=prefixStr(phoneStr);
		return phoneStr;
	}
	
	
	/**
	 * ：解密步骤：
	 * 1、去除字符串前”~”
	 * 2、去除所有数字
	 * 3、将剩余字母全部转换为对应的数字
	 * 4、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
	 * @return
	 */
	public static String decryptPhone(String phoneStr){
		if(!isNumber(phoneStr)){
			//1.去除字符串前”~”
			phoneStr=filterStr(phoneStr);
			//2.去除所有数字
			phoneStr=removeNum(phoneStr);
			//3.将剩余字母全部转换为对应的数字
			phoneStr=getPhoneNum(phoneStr);
			//4.换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
			phoneStr=changeNum(phoneStr);
		}
		return phoneStr;
	}
 
       
 
    /**
     * 正则表达式数字验证
     * 
     * @author crab
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (str != null && !str.equals("")) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match = pattern.matcher(str);
            return match.matches();
        } else {
            return false;
        }
    }
 
    /**
     * 字符串非空非null判断 crab
     */
    public static boolean isEmpty(String val) {
        if (val == null || val.equals("") || val.equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }



}
