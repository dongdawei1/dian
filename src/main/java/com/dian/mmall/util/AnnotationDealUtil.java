package com.dian.mmall.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;
import com.dian.mmall.util.checknullandmax.MinSize;



/**
 * 注解验证处理类
 * 
 * @author zhy
 * @date 2017/2/22
 */
public class AnnotationDealUtil {
	private static Logger log = Logger.getAnonymousLogger();

	/**
	 * 注解验证电泳方法
	 * 
	 * @author zhy
	 * @param bean 验证的实体
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> validate(Object bean) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("message", "验证通过");
		result.put("result", true);
		Class<?> cls = bean.getClass();
		
		// 检测field是否存在
		try {
			// 获取实体字段集合
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				// 通过反射获取该属性对应的值
				f.setAccessible(true);
				// 获取字段值
				Object value = f.get(bean);
				// 获取字段上的注解集合
				Annotation[] arrayAno = f.getAnnotations();
				for (Annotation annotation : arrayAno) {
					// 获取注解类型（注解类的Class）
					Class<?> clazz = annotation.annotationType();
					// 获取注解类中的方法集合
					Method[] methodArray = clazz.getDeclaredMethods();
					for (Method method : methodArray) {
						// 获取方法名
						String methodName = method.getName();
						// 过滤错误提示方法的调用
						if(methodName.equals("message")) {
							continue;
						}
						// 初始化注解验证的方法处理类 （我的处理方法卸载本类中）
						Object obj = AnnotationDealUtil.class.newInstance();
						// 获取方法
						try {
							// 根据方法名获取该方法
							Method m = obj.getClass().getDeclaredMethod(methodName, Object.class, Field.class);
							// 调用该方法
							result = (Map<String, Object>)m.invoke(obj, value, f);
							/* 验证结果 有一处失败则退出 */
							if(result.get("result").equals(false)) {
								return result;
							}
						} catch (Exception e) {
							e.printStackTrace();
							log.info("找不到该方法:"+methodName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("验证出错");
		}
		return result;
	}

	/**
	 * 验证是否空值
	 * 
	 * @author zhy
	 * @param value 参数值
	 * @param field 字段
	 * @return
	 */
	public Map<String, Object> isEmpty(Object value, Field field) {
		Map<String, Object> validateResult = new HashMap<String, Object>();
		IsEmptyAnnotation annotation = field.getAnnotation(IsEmptyAnnotation.class);
		if(value == null || value.equals("")) {
			//validateResult.put("message", field.getName() + annotation.message());
			validateResult.put("message",  annotation.message());
			validateResult.put("result", false);
		} else {
			validateResult.put("message", "验证通过");
			validateResult.put("result", true);
		}
		return validateResult;
	}
	
	/**
	 * 验证最小值
	 * 
	 * @author zhy
	 * @param value 参数值
	 * @param field 字段
	 * @return
	 */
	public Map<String, Object> min(Object value, Field field) {
		Map<String, Object> validateResult = new HashMap<String, Object>();
		MinSize annotation = field.getAnnotation(MinSize.class);
		if(value != null && value.toString().length() < annotation.min()) {
			validateResult.put("message", annotation.message());
			validateResult.put("result", false);
		} else {
			validateResult.put("message", "验证通过");
			validateResult.put("result", true);
		}
		return validateResult;
	}
	
	/**
	 * 验证最大值
	 * 
	 * @author zhy
	 * @param value 参数值
	 * @param field 字段
	 * @return
	 */
	public Map<String, Object> max(Object value, Field field) {
		Map<String, Object> validateResult = new HashMap<String, Object>();
		MaxSize annotation = field.getAnnotation(MaxSize.class);
		if(value != null && value.toString().length() > annotation.max()) {
			validateResult.put("message", annotation.message());
			validateResult.put("result", false);
		} else {
			validateResult.put("message", "验证通过");
			validateResult.put("result", true);
		}
		return validateResult;
	}
}
