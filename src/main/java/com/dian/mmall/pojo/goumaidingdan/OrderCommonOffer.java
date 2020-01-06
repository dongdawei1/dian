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
	private String saleCompanyName;  //销售商商户名
	private String saleUserAddressDetailed;  //销售商店面地址
	private long orderFormId;  //订单id
	private long commodityZongJiage;//总价
	
	private Integer commodStatus;//0创建1 抢单成功2失败
	private Integer oldOrNew;  //是否是老客 0是老客(显示商户名)，1是新客
	private Integer recommend;//是否是推荐商户0是，1否
	
	private String createTime;
	private String updateTime;
	
	private String contact;
	private String consigneeName;

//  varchar(20) NULL收/送货联系方式
//  varchar(15) NULL收/送货人姓名
}
