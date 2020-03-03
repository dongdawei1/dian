package com.dian.mmall.pojo.chuzufang;

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
public class Rent {
	private long id;
	private long userId;  //必填后端获取
	@IsEmptyAnnotation(message="发布类型不能为空") 
	private Integer  releaseType;  //发布类型 14,15  前端必传
	
	@MinSize(min=6,message="标题长度不能小于6位")
	@MaxSize(max=14, message="标题长度不能大于14位")
	@IsEmptyAnnotation(message="标题不能为空")
    private String releaseTitle; //发布标题

	@MaxSize(max=500, message="介绍不能大于500字")
	@IsEmptyAnnotation(message="介绍不能为空")
	private String serviceIntroduction;
	
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message="详细地址不能为空")
	private String serviceDetailed;
	@IsEmptyAnnotation(message="图片不能为空") 
	private String pictureUrl; //图片地址
	
	@IsEmptyAnnotation(message="面积不能为空") 
	private Integer fouseSize;
	
	@IsEmptyAnnotation(message="发布状态不能为空") //1发布中，2隐藏中，3删除,4审核中,5不在有效期
	private Integer welfareStatus;
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer authentiCationStatus;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过
	private String authentiCationFailure; //审核失败原因
	private String examineName;
	private String examineTime;
	@IsEmptyAnnotation(message="创建时间不能为空")
    private String createTime;   //必填
	@IsEmptyAnnotation(message="有效期不能为空")
    private String  termOfValidity;
    private String updateTime;
	private String userType;
	@IsEmptyAnnotation(message="联系人不能为空")
	private String consigneeName;
	
	private String detailed;
	private String contact;
	private String realNameId;
	private String companyName;

   
}
