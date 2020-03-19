package com.dian.mmall.dao.goumaidingdan;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.goumaidingdan.OrderCommonOffer;

@Mapper
public interface OrderCommonOfferMapper {

	List<OrderCommonOffer> getInitial(long orderFormId, int type);

	OrderCommonOffer getSuccess(long orderFormId, long saleUserId);
	int operation_purchase_evaluate_all(long orderFormId, String updateTime);

	void operation_purchase_evaluate_id(long orderFormId, String updateTime, long saleUserId);

	void operation_purchase_evaluate_noSelected(long orderFormId, String updateTime);

	void operation_purchase_evaluate_selected(long orderFormId, String updateTime, long saleUserId,long id);

	int getInitialCount(long orderFormId);

	void uptateGuanDan(long orderFormId, long saleUserId, String updateTime);

	int getmybaojia(long orderFormId, long saleUserId, int commodStatus);

	int creoffer(OrderCommonOffer offer);

	void uptateGuanDanById(long id, String updateTime);

	long getId(OrderCommonOffer offer);

	OrderCommonOffer getbysucc(long id, int commodStatus);


}
