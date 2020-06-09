package com.dian.mmall.pojo;

import com.dian.mmall.pojo.gongyong.IsButten;
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
public class WholesaleCommodityFanhui {	
	private long id;
	@IsEmptyAnnotation(message = "发布类型不能为空")
	private Integer releaseType;

//    4蔬菜出售5粮油出售6调料/副食出售29水产/禽蛋出售9清洁用品</el-radio>
	private String serviceType;
	private String releaseTitle; // 发布标题

	@IsEmptyAnnotation(message = "包装方式不能为空") // ok
	private Integer commodityPacking; // 3瓶, 2袋 ，1散装

	@IsEmptyAnnotation(message = "包装单位不能为空")
	private Integer specifi; // 散装, kg ,g, L ,ML,

	@MaxSize(max = 8, message = "商品规格不能大于8字")
	private String cations; // kg ,g, L ,ML
	// specifi:2,//包装/规格 单位 散装, kg ,g, L ,ML, commoditySpecifications:'散装',//产品规格
	// cations:0,//包装/规格 输入框

	@MaxSize(max = 12, message = "商品单价不能大于12位")
	@IsEmptyAnnotation(message = "商品单价不能为空")
	private long commodityJiage; //
	private double jage;
	private long commodityCountNo;

	

	@MaxSize(max = 12, message = "商品剩余数量不能大于12字")
	@IsEmptyAnnotation(message = "商品剩余数量不能为空")
	private long commoditySurplusNo;//

	@IsEmptyAnnotation(message = "图片不能为空")
	private String pictureUrl; // 图片地址
	private String addressDetailed;
	
}
