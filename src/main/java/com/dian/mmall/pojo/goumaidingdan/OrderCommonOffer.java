package com.dian.mmall.pojo.goumaidingdan;

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
public class OrderCommonOffer {
	//报价表
	private long id;
	private long saleUserId;  //销售商id
	private long orderFormId;  //订单id
	private long commodityZongJiage;//总价
	
	private Integer commodStatus;//0创建1 抢单成功2失败
	private Integer oldOrNew;  //是否是老客 0是老客(显示商户名)，1是新客
	private Integer recommend;//是否是推荐商户
	
	private String createTime;
	private String updateTime;
}
