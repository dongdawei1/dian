package com.dian.mmall.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealName {
	
	//实名信息  ,
	private long id;
	private long userId;
	private String userName;
	private String userType;
    private Integer provincesId;      //省
    private Integer cityId;    //市
	 private Integer districtCountyId; //区，
	 private String detailed;//省市区
	 private String  addressDetailed; //详细地址，
	 private String licenseUrl;   //营业执照图片url
	 private String contact;  //收/送货联系方式
	 private String consigneeName; //收/送货人姓名 
	 private Integer authentiCationStatus;  //状态审批状态 1 审批中 ，2通过，3审核不通过
	 private String authentiCationFailure; //审核失败原因
	 private String email;  
	 private Integer eag;   //求职年龄
	 private String gender; //性别
	 private String createTime;
	 private String updateTime;
	 private String examineName;//审核人员
	 private String examineTime;//审核时间
	 private String companyName;//非必填，企业名称
	 
	 private Integer isReceipt;//是否接单  1默认值不接 2接单用户
	 private String  addReceiptTime;
	 
	 private String  examineAddReceiptName;//添加管理员
	 private String  commitAddReceiptName;//通知用户人员
	 private String  qianyueTime;//预约线下签约时间
	 private String  qianyueDetailed;//签约地址
	@Override
	public String toString() {
		return "RealName [id=" + id + ", userId=" + userId + ", provincesId=" + provincesId + ", cityId=" + cityId
				+ ", districtCountyId=" + districtCountyId + ", detailed=" + detailed + ", addressDetailed="
				+ addressDetailed + ", licenseUrl=" + licenseUrl + ", contact=" + contact + ", consigneeName="
				+ consigneeName + ", authentiCationStatus=" + authentiCationStatus + ", authentiCationFailure="
				+ authentiCationFailure + ", email=" + email + ", createTime=" + createTime + ", updateTime="
				+ updateTime + "]";
	}

}
