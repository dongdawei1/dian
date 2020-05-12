package com.dian.mmall.pojo.fabu;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;
import com.dian.mmall.util.checknullandmax.MinSize;

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
public class FanhuiApp {
	private long id;
	private long userId;  //必填后端获取
	private long evaluateid;//评价ID
	private Integer servicFrequenc;//交易次数
	
	@MaxSize(max=14, message="具体类型不能大于14位")
	//@IsEmptyAnnotation(message="具体类型不能为空") 
	private String serviceType; //类型 数据库读取加用户自定义添加
	

	@MaxSize(max=500, message="商品及价格不能大于500字")
	//@IsEmptyAnnotation(message="商品及价格不能为空") 
	private String serviceAndprice; //项目及价格
	
	

	//@IsEmptyAnnotation(message="发布类型不能为空") 
	private Integer  releaseType;  //发布类型 18设备维修,33新,33旧  前端必传
	
	@MinSize(min=6,message="标题长度不能小于6位")
	@MaxSize(max=14, message="标题长度不能大于14位")
	@IsEmptyAnnotation(message="标题不能为空")
    private String releaseTitle; //发布标题
	
	
	

	@MaxSize(max=500, message="介绍不能大于500字")
	@IsEmptyAnnotation(message="描述不能为空")
	private String serviceIntroduction;
	
	
	@MaxSize(max=30, message="备注不能大于30字")
	private String	remarks;  //备注 30字以内
	
	@MaxSize(max=6000, message="图片字符不能超过6000字")
	@IsEmptyAnnotation(message="图片不能为空") 
	private String pictureUrl; //图片地址	
	//@MaxSize(max=20, message="图片字符不能超过6000")
	//@IsEmptyAnnotation(message="面积不能为空") 
	private Integer mianjia;  //租房是面积 ,灭虫是起步价格 ，其他发布为空

	
	
	@IsEmptyAnnotation(message="失效时间不能为空") 
	private String termOfValidity;
	@MaxSize(max=100, message="销售区域/详情地址不能超过100字")
	@IsEmptyAnnotation(message="销售区域/详情地址不能为空")
	private String serviceDetailed;  //全市和来电确认 ,租房手工填写
	@MaxSize(max=15, message="联系人不能超过15字")
	@IsEmptyAnnotation(message="联系人不能为空")
	private String consigneeName;
	

	@IsEmptyAnnotation(message="创建时间不能为空")
    private String createTime;   //必填
  
	
}
