package com.dian.mmall.dao.goumaidingdan;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.goumaidingdan.OrderCommonOffer;

@Mapper
public interface OrderCommonOfferMapper {

	List<OrderCommonOffer> getInitial(long orderFormId);

	OrderCommonOffer getSuccess(long orderFormId, long saleUserId);
	int operation_purchase_evaluate_all(long orderFormId, String updateTime);

	void operation_purchase_evaluate_id(long orderFormId, String updateTime, long saleUserId);

	void operation_purchase_evaluate_noSelected(long orderFormId, String updateTime);

	void operation_purchase_evaluate_selected(long orderFormId, String updateTime, long saleUserId,long id);

	int getInitialCount(long orderFormId);

	void uptateGuanDan(long orderFormId, long saleUserId, String updateTime);


}
