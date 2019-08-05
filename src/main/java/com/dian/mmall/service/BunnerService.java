package com.dian.mmall.service;

import com.dian.mmall.common.ServerResponse;

public interface BunnerService {

	ServerResponse<Object> getBunnerList(Integer permissionid, Integer bunnerType);

}
