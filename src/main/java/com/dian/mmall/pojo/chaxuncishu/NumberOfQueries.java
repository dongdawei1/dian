package com.dian.mmall.pojo.chaxuncishu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumberOfQueries {
	private long id;
	private long userId;
	private Integer countQueries;
	private String dateString;
	private Integer queriesType;
}
