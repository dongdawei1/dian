package com.dian.mmall.pojo;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wholesale {
	private long id;
	private String examineName;
	private String examineTime;
	
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer authentiCationStatus;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过,4删除
	@IsEmptyAnnotation(message="创建用户ID不能为空")
	private long  createUserId;
	@MaxSize(max = 40, message = "城市不能大于40字")
	@IsEmptyAnnotation(message="城市不能为空")
	private String detailed;// 省市区
	@MaxSize(max = 100, message = "地址不能大于100字")
	@IsEmptyAnnotation(message="地址不能为空")
	private String addressDetailed; // 详细地址，
	
	@MaxSize(max = 30, message = "市场名不能大于30字")
	@IsEmptyAnnotation(message="市场名不能为空")
	private String wholesaleName;
	
	private String serviceContact; // 紧急联系方式
	private String serviceName; // 紧急联系人
}
