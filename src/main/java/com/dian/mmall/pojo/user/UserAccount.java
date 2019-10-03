package com.dian.mmall.pojo.user;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
	// 实名信息 ,
	private long id;

	@IsEmptyAnnotation(message = "实名ID不能为空")
	private long realnameId; // 实名id

	@IsEmptyAnnotation(message = "用户ID不能为空")
	private long userId;

	private String contact; // 收/送货联系方式
	@IsEmptyAnnotation(message = "转账联系人姓名不能为空")
	private String consigneeName; //转账人姓名
	
	private String bankCard;//银行卡
	private String alipay;//支付宝
	
	private long balance;//总余额包含冻结+可用
	private long outAmount;//提现总额
	private long outInAmount;//提现中金额
	private long frozenAmount;//接单时冻结金额
	private long availableAmount;//可用余额
	private String createTime;
	private String updateTime;
	
}
