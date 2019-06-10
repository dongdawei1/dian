package com.dian.mmall.pojo.commodity;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;

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
public class GrainAndOil {
	private long id;
	private long userId;  //必填后端获取
	private long numberOfChecks; //交易次数
	
	@MinSize(min=3,message="商品名长度不能小于3位")
	@MaxSize(max=10, message="商品名长度不能大于10位")
	@IsEmptyAnnotation(message="商品名不能为空")
	
	private String  commodityName;  //商品名  前端必传
	@MinSize(min=2,message="产地长度不能小于2位")
	@MaxSize(max=10, message="产地长度不能大于10位")
	@IsEmptyAnnotation(message="产地不能为空")
	private String  placeOfOrigin; //产地  前端必传
	
	@MinSize(min=2,message="品牌长度不能小于2位")
	@MaxSize(max=10, message="品牌长度不能大于10位")
	@IsEmptyAnnotation(message="品牌不能为空")
	private String	brand; //品牌  前端必传
	
	@MinSize(min=2,message="规格长度不能小于2位")
	@MaxSize(max=10, message="规格长度不能大于10位")
	@IsEmptyAnnotation(message="规格不能为空")
	private String	specifications;//规格 前端必传
	
	@MaxSize(max=10, message="价格长度不能大于10位")
	@IsEmptyAnnotation(message="价格不能为空")
	private String	price; //价格 // 前端必填
	

	@IsEmptyAnnotation(message="有效期开始日期不能为空")
	private String	priceEffectiveStart;   //必填//有效期开始日期
	@IsEmptyAnnotation(message="有效期结束日期不能为空")
	private String	priceEffectiveEnd;  //必填 //有效期结束日期
	
	
	private String	remarks;  //备注 10字以内
	private String pictureUrl; //图片地址	
	
	private Integer isReceivingPurchase;  //必填//判断是否在价格有效期内，1在2 不在 如果在就显示 一键发起采购信息键
	
	@IsEmptyAnnotation(message="菜单id不能为空")
	private Integer permissionid; //菜单id //前端必填 代码中写死
	@IsEmptyAnnotation(message="商品类型不能为空")
	private String	commoditytype;  //商品类型	 //必填 前端传
	
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer approval_status;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过
	private String approval_failure; //审核失败原因
	
	@IsEmptyAnnotation(message="创建时间不能为空")
    private String createTime;   //必填
    private String updateTime;
	
    private Integer isDelete;  //必填  后端加// 1 正常 ，2已删除
    
    @Override
	public String toString() {
		return "GrainAndOil [id=" + id + ", userId=" + userId + ", numberOfChecks=" + numberOfChecks
				+ ", commodityName=" + commodityName + ", placeOfOrigin=" + placeOfOrigin + ", brand=" + brand
				+ ", specifications=" + specifications + ", price=" + price + ", priceEffectiveStart="
				+ priceEffectiveStart + ", priceEffectiveEnd=" + priceEffectiveEnd + ", remarks=" + remarks
				+ ", pictureUrl=" + pictureUrl + ", isReceivingPurchase=" + isReceivingPurchase + ", permissionid="
				+ permissionid + ", commoditytype=" + commoditytype + ", approval_status=" + approval_status
				+ ", approval_failure=" + approval_failure + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
    
    
    
} 
