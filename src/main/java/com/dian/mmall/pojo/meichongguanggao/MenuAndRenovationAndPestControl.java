package com.dian.mmall.pojo.meichongguanggao;

import java.util.Date;

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
public class MenuAndRenovationAndPestControl {
	private long id;
	private long userId;  //必填后端获取
	private long evaluateid;//评价ID
	
	private Integer servicFrequenc;//交易次数
	
	
	@IsEmptyAnnotation(message="发布类型不能为空") 
	private Integer  releaseType;  //发布类型 13,17,19  前端必传
	
	@MinSize(min=6,message="标题长度不能小于6位")
	@MaxSize(max=14, message="标题长度不能大于14位")
	@IsEmptyAnnotation(message="标题不能为空")
    private String releaseTitle; //发布标题

	@MaxSize(max=500, message="服务介绍不能大于500字")
	@IsEmptyAnnotation(message="服务介绍不能为空")
	private String serviceIntroduction;
	
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message="图片不能为空") 
	private String pictureUrl; //图片地址	
	
	@IsEmptyAnnotation(message="起步价不能为空") 
	private String startPrice; //起步价
	
	@IsEmptyAnnotation(message="服务域不能为空")
	private String serviceDetailed;
	
//	@IsEmptyAnnotation(message="菜单id不能为空") //13和17和19 的综合评价
//	private Integer permissionid=1379;
	
	@IsEmptyAnnotation(message="发布状态不能为空") //1发布中，2隐藏中，3删除,4审核中,5不在有效期
	private Integer welfareStatus;
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer authentiCationStatus;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过
	private String authentiCationFailure; //审核失败原因
	@IsEmptyAnnotation(message="实名区域不能为空")
	private String detailed;
	@IsEmptyAnnotation(message="实名地址不能为空")
	private String addressDetailed;
	@IsEmptyAnnotation(message="实名联系方式不能为空")
	private String contact;
	@IsEmptyAnnotation(message="实名联系人不能为空")
	private String consigneeName;
	@IsEmptyAnnotation(message="公司名称不能为空")
	private String companyName;  //公司名
	
	private String examineName;
	private String examineTime;
	@IsEmptyAnnotation(message="创建时间不能为空")
    private String createTime;   //必填
    private String updateTime;
	private String userType;
   
}
