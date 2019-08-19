package com.dian.mmall.pojo.zhiwei;

import com.dian.mmall.common.zhiwei.Welfare;
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
public class ReleaseWelfare {
	private long id;
	
  private long userId;

  @IsEmptyAnnotation(message="职位类型不能为空")
  private String position; //职位类型
  @MinSize(min=1,message="招聘人数在1-99之间")
	@MaxSize(max=2, message="招聘人数在1-99之间")
  private Integer number;//招聘人数  100
  @IsEmptyAnnotation(message="薪资不能为空")
  private String salary;//薪水必填
  @IsEmptyAnnotation(message="福利不能为空")
  private String welfare;//福利
  @IsEmptyAnnotation(message="学历不能为空")
  private String education; //学历
  @IsEmptyAnnotation(message="工作年限不能为空")
  private String experience; //工作年限
  @IsEmptyAnnotation(message="年龄要求不能为空")
  private String age;
  @IsEmptyAnnotation(message="性别要求不能为空")
  private String gender; 
  @IsEmptyAnnotation(message="职位描述不能为空")
 	@MaxSize(max=100, message="职位描述 100字以下")
  private String describeOne;//职位描述 100字以下  
  
  @IsEmptyAnnotation(message="介绍人奖励不能为空")
  private String introductoryAward;//介绍人奖励  必填
  private String email;  //非必填
  @IsEmptyAnnotation(message="审核状态不能为空")
  private Integer welfareStatus; // 1发布中，2隐藏中，3删除,4审核中,5不在有效期不显示
  @IsEmptyAnnotation(message="是否公开手机号不能为空")
  private Integer isPublishContact; // 是否公开手机 1公开，2隐藏
  @IsEmptyAnnotation(message="创建时间不能为空")
  private String createTime;
  private String updateTime;  //非必填
  @IsEmptyAnnotation(message="实际工作地址不能为空")
  @MaxSize(max=100, message="工作地址 100字以下")
  private String workingAddress; //工作地址，默认填充实名中的地址，可以修改
  
  private Integer addressConsistency;//工作地址是否与实名地址一致 1一致，2不一致
  //拉取实名信息 

  private Integer authentiCationStatus;  //状态审批状态 1 审批中 ，2通过，3审核不通过
  private String authentiCationFailure; //审核失败原因   非必填
	//实名人员
  private String examineName;//审核人员   非必填
  private String examineTime;//审核时间   非必填

  @IsEmptyAnnotation(message="联系方式不能为空")
  private String contact;  //系联系方式 可修改
  @IsEmptyAnnotation(message="联系人不能为空")
  private String consigneeName; //联系人姓名 回显置灰 不可修改
  private String userType;//用户类型
  @IsEmptyAnnotation(message="有效期不能为空")
  private String  termOfValidity;//职位有效期 创建时间加 30天
  @IsEmptyAnnotation(message="实名id不能为空")
  private Long realNameId;
  //实名信息
  private String detailed;//省市区  回显置灰
  @IsEmptyAnnotation(message="企业名称不能为空")
  private String  companyName;//公司名称
	 
}
