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
  @IsEmptyAnnotation(message="用户名不能为空")
  private String userName;
  @IsEmptyAnnotation(message="职位类型不能为空")
  private String position; //职位类型
  @MinSize(min=1,message="招聘人数在1-99之间")
	@MaxSize(max=2, message="招聘人数在1-99之间")
  private Integer number;//招聘人数  100
  private Integer salaryMin;//薪水下限   非必填
  private Integer salaryMax;//薪水上限  非必填
  private String unlimited; //薪水面议  非必填
  @IsEmptyAnnotation(message="福利不能为空")
  private String welfare;//福利
  @IsEmptyAnnotation(message="学历不能为空")
  private String education; //学历
  @IsEmptyAnnotation(message="工作年限不能为空")
  private String experience; //工作年限
  @IsEmptyAnnotation(message="职位描述不能为空")
 	@MaxSize(max=100, message="职位描述 100字以下")
  private String describe;//职位描述 100字以下  
  private Integer IntroductoryAward;//介绍人奖励  非必填
  private String email;  //非必填
 
  private Integer welfareStatus; // 1发布中，2隐藏中，3删除
  @IsEmptyAnnotation(message="是否公开手机号不能为空")
  private Integer isPublishContact; // 是否公开手机 1公开，2隐藏
  @IsEmptyAnnotation(message="创建时间不能为空")
  private String createTime;
  private String updateTime;  //非必填
  @IsEmptyAnnotation(message="实际工作地址不能为空")
  private String workingAddress; //工作地址，默认填充实名中的地址，可以修改
  private Integer addressConsistency;//工作地址是否与实名地址一致 1一致，2不一致
  //拉取实名信息 

  private Integer authentiCationStatus;  //状态审批状态 1 审批中 ，2通过，3审核不通过
  private String authentiCationFailure; //审核失败原因   非必填
	//实名人员
  private String examineName;//审核人员   非必填
  private String examineTime;//审核时间   非必填
	 
}
