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
public class FanHui {
	private long id;
	private long userId;
	private long evaluateid;//评价ID	
	private Integer servicFrequenc;//交易次数	
	@IsEmptyAnnotation(message="发布类型不能为空") 
	private Integer  releaseType;  //发布类型 18设备维修,33新,33旧  前端必传
	private String userType;
	
	
    private String releaseTitle; //发布标题
	
	private String serviceType; //类型 数据库读取加用户自定义添加
	

	private String serviceAndprice; //项目及价格
	
	
	private String serviceIntroduction;
	
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message="图片不能为空") 
	private String pictureUrl; //图片地址	
	@IsEmptyAnnotation(message="销售区域不能为空")
	private String serviceDetailed;  //全市和来电确认 ,租房手工填写
	
	@IsEmptyAnnotation(message="面积不能为空") 
	private Integer mianjia;  //租房是面积 ,灭虫是起步价格 ，其他发布为空
		
	@IsEmptyAnnotation(message="联系人不能为空")
	private String consigneeName;
	@IsEmptyAnnotation(message="创建时间不能为空")
    private String createTime;   //必填
    private String updateTime;
	
    private String realNameId;//备用
	private String detailed;  //备用
	private String contact;//备用
	private String termOfValidity;


}
