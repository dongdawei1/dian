package com.dian.mmall.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.TUserRole;

import lombok.extern.slf4j.Slf4j;
 
/**
 * 实体类和map之间转化
 * 
 *
 */

@Slf4j
public class BeanMapConvertUtil {
	/** 
     * 将一个 JavaBean 对象转化为一个  Map 
     * @param bean 要转化的JavaBean 对象 
     * @return 转化出来的  Map 对象 
     * @throws IntrospectionException 如果分析类属性失败 
     * @throws IllegalAccessException 如果实例化 JavaBean 失败 
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败 
     */  
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    public static Map convertBean(Object bean)  
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {  
        Class type = bean.getClass();  
        Map returnMap = new HashMap();  
        BeanInfo beanInfo = Introspector.getBeanInfo(type);  
  
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();  
        for (int i = 0; i< propertyDescriptors.length; i++) {  
            PropertyDescriptor descriptor = propertyDescriptors[i];  
            String propertyName = descriptor.getName();  
            if (!propertyName.equals("class")) {  
                Method readMethod = descriptor.getReadMethod();  
                Object result = readMethod.invoke(bean, new Object[0]);  
                if (result != null) {  
                    returnMap.put(propertyName, result);  
                } else {  
                    returnMap.put(propertyName, "");  
                }  
            }  
        }  
        return returnMap;  
    }
 
 
 
    /** 
     * 将一个 Map 对象转化为一个 JavaBean 
     * @param type 要转化的类型 
     * @param map 包含属性值的 map 
     * @return 转化出来的 JavaBean 对象 
     * @throws IntrospectionException 如果分析类属性失败 
     * @throws IllegalAccessException 如果实例化 JavaBean 失败 
     * @throws InstantiationException 如果实例化 JavaBean 失败 
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败 
     */  
   
    public static Object convertMap(Class type, Map map) {  
       
    	BeanInfo beanInfo = null; // 获取类属性  
       
    	try {
    		beanInfo = Introspector.getBeanInfo(type);
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage("获取类属性异常");
		}
    	
    	
    	Object obj =null; // 创建 JavaBean 对象  
  
    	try {
    		obj = type.newInstance(); // 创建 JavaBean 对象
		} catch (Exception e) {
			return ServerResponse.createByErrorMessage("创建 JavaBean异常");
		}
    	
    	
        // 给 JavaBean 对象的属性赋值  
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();  
        for (int i = 0; i< propertyDescriptors.length; i++) {  
            PropertyDescriptor descriptor = propertyDescriptors[i];  
            String propertyName = descriptor.getName();  
  
            if (map.containsKey(propertyName)) {  
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。  
                Object value = map.get(propertyName);  
  
                Object[] args = new Object[1];  
                args[0] = value;  
//                log.info("args-------------" +args);
//                log.info("value-----------" +value);
                try {
					descriptor.getWriteMethod().invoke(obj, args);
					
				} catch (IllegalAccessException e) {
					 log.warn("类型转换异常",propertyName);
					 log.warn("类型转换",e);
					
					return ServerResponse.createByErrorMessage("转换 JavaBean异常");
				} catch (IllegalArgumentException e) {
					 log.warn("类型转换异常",propertyName);
					log.warn("类型转换",e);
					return ServerResponse.createByErrorMessage("转换 JavaBean异常");
				} catch (InvocationTargetException e) {
					 log.warn("类型转换异常",propertyName);
					log.warn("类型转换",e);
					return ServerResponse.createByErrorMessage("转换 JavaBean异常");
				}   
            }  
        }  
        return obj;  
    }
 
    
//    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException, IntrospectionException {
//		
//    	TUserRole  tu=new TUserRole();
//    
//		 Map<String, String> roleid2 =new HashMap<String, String>();
//		// roleid2.put("roleid1", "roleid1");
//		 Map<String, Object>  map=new HashMap<String, Object>();
//    	
//    	map.put("id", 1);
//    	map.put("userid", 2222222222222L);
//    	map.put("roleid1", "roleid1");
//    	map.put("roleid2", roleid2);
//    	
//    	
//    	tu=	(TUserRole) BeanMapConvertUtil.convertMap(TUserRole.class,map);
//    	
//    	System.out.println(tu.getId()+"  "+tu.getRoleid2());
//    	
//    	System.out.println(AnnotationDealUtil.validate(tu).toString());
//    	
//	}
 
}