package com.dian.mmall.pojo.fabu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FabuList {
	private long id;
	private Integer mianjia; 
	private String pictureUrl; //图片地址
	private String serviceDetailed;  //全市和来电确认 ,租房手工填写
	private String releaseTitle; //发布标题
	private String serviceType; //类型 数据库读取加用户自定义添加
	private Integer  releaseType;  //发布类型 
}
