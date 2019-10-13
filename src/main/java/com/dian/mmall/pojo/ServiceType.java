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
public class ServiceType {
	private long id;
	@MaxSize(max=20, message="服务/商品名称不能超过20字")
	@IsEmptyAnnotation(message="服务/商品名称不能为空")
	private String serviceTypeName;//服务商品 名称

	@IsEmptyAnnotation(message="菜单id不能为空")
	private Integer  releaseType;   //菜单id
	
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer authentiCationStatus;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过,4删除
	private long  createUserId; 
	
	private String examineName;
	private String examineTime;
	
	private String pictureUrl; //图片地址
}
