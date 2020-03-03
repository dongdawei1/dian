package com.dian.mmall.pojo.zhiwei;

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
public class Resume {
	private long id;
	
	  private long userId;  //ol
	  @IsEmptyAnnotation(message="求职类型不能为空")
	  private String position; //职位类型  ol
	  @IsEmptyAnnotation(message="薪资不能为空")
	  private String salary;//薪水必填 ol
	  @IsEmptyAnnotation(message="学历不能为空")
	  private String  education;//学历 ol
	  @IsEmptyAnnotation(message="工作年限不能为空")
	  private String experience; //工作年限 ol
	
	
	  
	  @IsEmptyAnnotation(message="年龄不能为空")
	  private Integer age;  //年龄 ol
	  @IsEmptyAnnotation(message="性别不能为空")
	  private String  gender;//性别   ol
	  @IsEmptyAnnotation(message="求职地域不能为空")
	  private String detailed;//求职地域 ol
	  @IsEmptyAnnotation(message="所在地域不能为空")
	  private String addressDetailed;//现居住地也用插件吧 不要从实名中获取 ol
	  @IsEmptyAnnotation(message="自我描述不能为空")
	 	@MaxSize(max=100, message="自我描述 100字以下")
	  private String describeOne;//职位描述 100字以下   ol
	
	  private String email;  //非必填 ol
	  
	  @IsEmptyAnnotation(message="审核状态不能为空")
	  private Integer welfareStatus; // 1发布中，2隐藏中，3删除,4审核中,5不在有效期不显示 ol
	  @IsEmptyAnnotation(message="是否公开手机号不能为空")
	  private Integer isPublishContact; // 是否公开手机 1公开，2隐藏  ol
	  @IsEmptyAnnotation(message="创建时间不能为空")
	  private String createTime; //ol
	  private String updateTime;  //非必填
	 
	  //拉取实名信息 

	  private Integer authentiCationStatus;  //状态审批状态 1 审批中 ，2通过，3审核不通过  ol
	  private String authentiCationFailure; //审核失败原因   非必填
		//实名人员
	  private String examineName;//审核人员   非必填
	  private String examineTime;//审核时间   非必填
	 //实名信息
	 
	  private String contact;  //实名联系联系方式  ol
	  private String consigneeName; //姓名 ol
	  private Integer releaseType=31;
	 
	  private String userType;//用户类型 ol
	
	  private String  termOfValidity;//职位有效期 创建时间加 90天

}
