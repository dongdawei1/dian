package com.dian.mmall.pojo.user;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;
import com.dian.mmall.util.checknullandmax.MinSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUser {
	// 实名信息 ,
	private long id;

	@IsEmptyAnnotation(message = "实名ID不能为空")
	private long realnameId;  //实名id

	@IsEmptyAnnotation(message = "用户ID不能为空")
	private long userId;

	@MaxSize(max = 15, message = "送货人姓名不能大于15位")
	@IsEmptyAnnotation(message = "送货人姓名不能为空")
	private String consigneeName; // 收/送货人姓名

	@IsEmptyAnnotation(message = "企业名称不能为空")
	private String companyName;// 企业名称

	@IsEmptyAnnotation(message = "详细地址不能为空")
	private String addressDetailed; // 详细地址，

	@IsEmptyAnnotation(message = "城区不能为空")
	private String detailed;// 省市区

	@IsEmptyAnnotation(message = "送货范围不能为空")
	private String delivery;// 送货范围 1 本区2 全市
	private Integer servicFrequenc;// 交易次数
	private long evaluateid;// 评价id

	@IsEmptyAnnotation(message = "送货人手机不能为空")
	private String contact; // 收/送货联系方式
	
	private String urgentContact; // 紧急联系方式
	private String urgentName; // 紧急联系人
	
	private String licenseUrl; // 图片存贮地址

	@IsEmptyAnnotation(message = "营业执照截止时间不能为空")
	private String licenseEndTime; // 营业执照结束时间

	@IsEmptyAnnotation(message = "健康证失效时间不能为空")
	private String healthyEndTime; // 健康证结束时间
	
	private String createTime;
	private String updateTime;
	
	private String examineName;// 审核人员
	private String examineTime;// 审核时间
	private Integer authentiCationStatus; //  1 接单 ，2已退出
 
	@IsEmptyAnnotation(message = "经验方式不能为空")
	private Integer userType;// 1自营2合作
	@IsEmptyAnnotation(message = "经验方式不能为空")
	private String contractNo;// 1合同编号
	private Integer branch;// 服务分
}
