package com.dian.mmall.util.checknullandmax;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * 最大长度验证类
 * 
 * @author zhy
 * @date 2017/2/22
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxSize {
	public int max() default 20;

	public String message() default "长度太长";
}
