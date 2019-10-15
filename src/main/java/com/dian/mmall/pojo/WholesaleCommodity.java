package com.dian.mmall.pojo;

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
public class WholesaleCommodity {
	private long id;
	@IsEmptyAnnotation(message = "创建用户ID不能为空")
	private long userId; 
	
	private long evaluateid;//评价ID
	
	private Integer servicFrequenc;//交易次数
	
	@IsEmptyAnnotation(message = "实名id不能为空")
	private long realNameId;//

	

	
	
	
	@IsEmptyAnnotation(message = "发布类型不能为空")
	private Integer releaseType; 
	
//    4蔬菜出售5粮油出售6调料/副食出售29水产/禽蛋出售9清洁用品</el-radio>
	
	
	@MaxSize(max=40, message="商品名称不能大于40字")
	@IsEmptyAnnotation(message = "商品名称不能为空")
	private String  serviceType; 
	
	@MinSize(min=6,message="标题长度不能小于6位")
	@MaxSize(max=14, message="标题长度不能大于14位")
	@IsEmptyAnnotation(message="标题不能为空")
    private String releaseTitle; //发布标题
	
	
	@MaxSize(max=40, message="商品产地不能大于40字")
	@IsEmptyAnnotation(message = "商品产地不能为空")
	private String  detailed;
	
	private String  addressDetailed; //地址详情
	
	@IsEmptyAnnotation(message="包装方式不能为空") //ok
	private Integer commodityPacking;  //3瓶, 2袋 ，1散装
	
	@MaxSize(max=40, message="商品规格不能大于40字")
	@IsEmptyAnnotation(message = "商品规格不能为空")
	private String  commoditySpecifications; //散装, kg ,g,  L ,ML, 
	 
	@MaxSize(max=12, message="商品单价不能大于12位")
	@IsEmptyAnnotation(message = "商品单价不能为空")
	private long  commodityJiage; // 
	
	@MaxSize(max=12, message="商品数量不能大于12字")
	@IsEmptyAnnotation(message = "商品数量不能为空")
	private long  commodityCountNo;  
	
	@MaxSize(max=12, message="商品预定数量不能大于12字")
	@IsEmptyAnnotation(message = "商品预定数量不能为空")
	private long   commodityReserveNo;
	
	@MaxSize(max=12, message="商品剩余数量不能大于12字")
	@IsEmptyAnnotation(message = "商品剩余数量不能为空")
	private long   commoditySurplusNo;//
	
	
	@MaxSize(max=50, message="介绍不能大于50字")
	@IsEmptyAnnotation(message="介绍不能为空")
	private String serviceIntroduction; 
	
	private String	remarks;  //备注 30字以内
	
	@IsEmptyAnnotation(message="图片不能为空") 
	private String pictureUrl; //图片地址
	
	@IsEmptyAnnotation(message = "是否接受预定不能为空")
	private Integer reserve;  //是否接受预定 1接受 2 不接受
	
	@IsEmptyAnnotation(message="送货方式不能为空") 
	private Integer deliveryType;//1自取  ,2送货,  3自取+送货  4满免
	
	@IsEmptyAnnotation(message="运费收取方式不能为空") 
	private long deliveryCollect; //运费
	
	@IsEmptyAnnotation(message = "报价开始时间")
	private String startTime;// 已到货   当前时间，明天到货
	
	
	
	@IsEmptyAnnotation(message = "报价结束时间")
	private String endTime;// 结束时间 距离开始时间 不能大于2天

	@IsEmptyAnnotation(message = "创建时间不能为空")
	private String createTime;
	
	private String updateTime; // 非必填
	
	
	
	@IsEmptyAnnotation(message="发布状态不能为空") //1发布中，2隐藏中，3删除,4审核中,5不在有效期
	private Integer welfareStatus;
	@IsEmptyAnnotation(message="审核状态不能为空")
	private Integer authentiCationStatus;  //必填  后端加//审批状态 1 审批中 ，2通过，3审核不通过
	private String authentiCationFailure; //审核失败原因
	private String examineName;
	private String examineTime;
}
