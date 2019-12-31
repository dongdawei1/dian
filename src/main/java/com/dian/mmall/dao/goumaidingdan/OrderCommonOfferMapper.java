package com.dian.mmall.dao.goumaidingdan;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.goumaidingdan.OrderCommonOffer;

@Mapper
public interface OrderCommonOfferMapper {

	List<OrderCommonOffer> getInitial(long orderFormId);

	OrderCommonOffer getSuccess(long orderFormId, long saleUserId);

}
