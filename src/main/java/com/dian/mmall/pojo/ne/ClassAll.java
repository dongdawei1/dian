package com.dian.mmall.pojo.ne;

import com.dian.mmall.pojo.gongyong.IsButten;
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
public class ClassAll {
	private long id;

	private long userId;
	
	@IsEmptyAnnotation(message = "有效期不能为空")
	private String termOfValidity;// 职位有效期 创建时间加
	@IsEmptyAnnotation(message = "联系人不能为空")
	private String consigneeName; // 联系人姓名 回显置灰 不可修改
	@IsEmptyAnnotation(message = "审核状态不能为空")
	private Integer welfareStatus; // 1发布中，2隐藏中，3删除,4审核中,5不在有效期不显示
	@IsEmptyAnnotation(message = "创建时间不能为空")
	private String createTime;
	/*招聘开始*/
	private String gender;
	private Integer isPublishContact; // 是否公开手机 1公开，2隐藏
	private String email; // 非必填
	private String welfare;// 福利
	private String position; // 职位类型
	private String salary;// 薪水必填
	private String education; // 学历
	private String experience; // 工作年限
	private String age;
	private String introductoryAward;// 介绍人奖励 必填
	private Integer number;// 招聘人数 100
	private String describeOne;// 职位描述 100字以下
	private String workingAddress; // 工作地址，默认填充实名中的地址，可以修改
	private Integer addressConsistency;// 工作地址是否与实名地址一致 1一致，2不一致
	private String updateTime; // 非必填
	private Integer releaseType=30;
	// 拉取实名信息
	private Integer authentiCationStatus; // 状态审批状态 1 审批中 ，2通过，3审核不通过
	private String authentiCationFailure; // 审核失败原因 非必填
	// 实名人员
	private String examineName;// 审核人员 非必填
	private String examineTime;// 审核时间 非必填

	private String userType;// 用户类型


	// 冗余字段
	private String detailed;// 省市区 回显置灰
	private String companyName;// 公司名称
	private String contact; // 系联系方式 可修改
	private String realNameId;
}
