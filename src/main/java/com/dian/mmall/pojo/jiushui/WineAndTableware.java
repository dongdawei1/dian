package com.dian.mmall.pojo.jiushui;

import java.util.List;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;
import com.dian.mmall.util.checknullandmax.MinSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WineAndTableware {
	private long id;
	private long userId; // 必填后端获取
	private long evaluateid;// 评价ID

	private Integer servicFrequenc;// 交易次数

	@IsEmptyAnnotation(message = "发布类型不能为空")
	private Integer releaseType; // 发布类型 18设备维修,33新,33旧 前端必传

	@MinSize(min = 6, message = "标题长度不能小于6位")
	@MaxSize(max = 14, message = "标题长度不能大于14位")
	@IsEmptyAnnotation(message = "标题不能为空")
	private String releaseTitle; // 发布标题

	@IsEmptyAnnotation(message = "具体类型不能为空")
	private String serviceType; // 类型 数据库读取加用户自定义添加

	@MaxSize(max = 400, message = "商品及价格不能大于400字")
	@IsEmptyAnnotation(message = "商品及价格不能为空")
	private String serviceAndprice; // 项目及价格

	@MaxSize(max = 500, message = "介绍不能大于500字")
	@IsEmptyAnnotation(message = "介绍不能为空")
	private String serviceIntroduction;

	private String remarks; // 备注 30字以内

	@IsEmptyAnnotation(message = "图片不能为空")
	private String pictureUrl; // 图片地址

	@IsEmptyAnnotation(message = "销售区域不能为空")
	private String serviceDetailed; // 全市和来电确认

//	@IsEmptyAnnotation(message="菜单id不能为空") //13和17和19 的综合评价
//	private Integer permissionid=1379;

	@IsEmptyAnnotation(message = "发布状态不能为空") // 1发布中，2隐藏中，3删除,4审核中,5不在有效期
	private Integer welfareStatus;
	@IsEmptyAnnotation(message = "审核状态不能为空")
	private Integer authentiCationStatus; // 必填 后端加//审批状态 1 审批中 ，2通过，3审核不通过
	private String authentiCationFailure; // 审核失败原因


	
	
	@IsEmptyAnnotation(message = "联系人不能为空")
	private String consigneeName;

	 
	
	private String examineName;
	private String examineTime;
	@IsEmptyAnnotation(message = "创建时间不能为空")
	private String createTime; // 必填
	private String updateTime;
	private String userType;
	
	private String contact; //以下是备用
	private String realNameId;
	private String detailed; //
	@IsEmptyAnnotation(message = "有效期不能为空")
	private String termOfValidity;// 职位有效期 创建时间加
	
}
