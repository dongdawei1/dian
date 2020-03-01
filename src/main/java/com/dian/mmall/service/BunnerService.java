package com.dian.mmall.service;

import com.dian.mmall.common.ServerResponse;

public interface BunnerService {


	ServerResponse<Object> getBunnerList(Integer role, Integer permissionid, Integer bunnerType, long userId);


	int getguanggaocount(long biaozhongID, int biaoID);

}
