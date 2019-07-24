package com.dian.mmall.dao.releaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dian.mmall.pojo.meichongguanggao.MenuAndRenovationAndPestControl;
import com.dian.mmall.pojo.pingjia.Evaluate;

@Mapper
public interface MenuAndRenovationAndPestControlMapper {
	   //计算发布过的总数   5条以下
		int countNum(int releaseType, long userId);

		int create_menuAndRenovationAndPestControl(MenuAndRenovationAndPestControl menuAndRenovationAndPestControl);
       
		long get_usermrp_list_no(Integer releaseType, Integer welfareStatus, long userId);

		List<MenuAndRenovationAndPestControl> get_usermpr_list_all(int pageLength, int pageSize, Integer releaseType,
				Integer welfareStatus, long userId);
        //审核总数
		long get_mrp_list_no(Integer releaseType, String contact);
        //审核列表
		List<MenuAndRenovationAndPestControl> get_mpr_list_all(int pageLength, int pageSize, Integer releaseType,
				String contact);
      //审核
		int examineMrp(MenuAndRenovationAndPestControl releaseWelfare);
}
