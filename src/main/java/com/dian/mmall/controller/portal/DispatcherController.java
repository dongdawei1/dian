package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.common.Const;
import com.dian.mmall.common.ServerResponse;
import com.dian.mmall.pojo.Permission;
import com.dian.mmall.pojo.user.User;
import com.dian.mmall.service.PermissionService;

@Controller
@RequestMapping(Const.PCAPI + "permission")
public class DispatcherController {

	@Autowired
	private PermissionService permissionService;
	/**
	 * 根据角色过去菜单,已经写死，不在从后端获取
	 * */ 

	@ResponseBody
	//@RequestMapping(value = "/loadData")
	public ServerResponse<Object> loadData(HttpServletRequest httpServletRequest) {
       
//	     <div v-for="(item, index) in menuList" :key="index">
//	     <!--一级菜单导航-->
//	             <el-menu-item :index= "item.name"  v-if=" item.pid===1 && item.parentCode==null && item.url != '' " >
//	               <span slot="title">
//	               <router-link :to="item.url"   class="a"   >{{ item.name }}</router-link>
//	               </span>
//	             </el-menu-item>
//	             <!--一级菜单下拉-->
//	           <el-submenu  :index= "item.name"  v-if=" item.pid===1 && item.parentCode===null && item.url === ''"  >  <!--唯一标识-->
//
//	             <template slot="title"  >{{ item.name }}</template>
//
//	             <el-menu-item-group>
//	               <div v-for="(itemSon, indexSon) in menuList" :key="indexSon">
//	               <el-menu-item :index="itemSon.name"  v-if=" item.resourceCode === itemSon.parentCode " >
//	               <router-link :to="itemSon.url"   class="a"   >{{ itemSon.name }}</router-link>
//	               </el-menu-item>
//	               </div>
//	             </el-menu-item-group>
//	           </el-submenu>
//
//	           </div>
		
		
		
		
		User user = (User) httpServletRequest.getAttribute("user");

		// 获取用户权限信息
		List<Permission> ps = permissionService.queryPermissionsByUser(user);
		return ServerResponse.createBySuccess(ps);

	}

}
