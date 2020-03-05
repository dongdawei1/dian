package com.dian.dingshi;

import java.util.List;

public interface OrderExampleTimerService {

	List<Long> getall(int a, String dateString);

	void upall(int a, String dateString);

	void delall(int a, String dateString, String termOfValidity);

}
