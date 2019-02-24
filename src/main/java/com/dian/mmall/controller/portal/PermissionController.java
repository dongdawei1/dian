package com.dian.mmall.controller.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.pojo.Permission;
import com.dian.mmall.service.PermissionService;



//@Controller

public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@ResponseBody
	//@RequestMapping(value="/loadData1" )
	public Object loadData1() {
		List<Permission> permissions = new ArrayList<Permission>();
		
//		Permission root = new Permission();
//		root.setName("顶级节点");
//		
//		Permission child = new Permission();
//		child.setName("子节点");
//		
//		root.getChildren().add(child);
//		permissions.add(root);
		
		// 读取树形结构数据
		/*
		Permission root = permissionService.queryRootPermission();		
		
		List<Permission> childPermissions = permissionService.queryChildPermissions(root.getId());
		
		for ( Permission childPermission : childPermissions ) {
			List<Permission> childChildPermissions = permissionService.queryChildPermissions(childPermission.getId());
			childPermission.setChildren(childChildPermissions);
		}
		
		root.setChildren(childPermissions);
		
		permissions.add(root);
		*/
		
		// 递归查询数据
		/*
		Permission parent = new Permission();
		parent.setId(0);
		queryChildPermissions(parent);
		//permissions.
		return parent.getChildren();
		*/
		
		// 查询所有的许可数据
		List<Permission> ps =permissionService.queryAll();
		
		/*
		for ( Permission p : ps ) {
			// 子节点
			Permission child = p;
			if ( p.getPid() == 0 ) {
				permissions.add(p);
			} else {
				for ( Permission innerPermission : ps ) {
					if ( child.getPid().equals(innerPermission.getId()) ) {
						// 父节点
						Permission parent = innerPermission;
						// 组合父子节点的关系
						parent.getChildren().add(child);
						break;
					}
				}
			}
		}
		*/
		
		Map<Integer, Permission> permissionMap = new HashMap<Integer, Permission>();
		for (Permission p : ps) {
			permissionMap.put(p.getId(), p);
		}
		for ( Permission p : ps ) {
			Permission child = p;
			if ( child.getPid() == 0 ) {
				permissions.add(p);
			} else {
				Permission parent = permissionMap.get(child.getPid());
				parent.getChildren().add(child);
			}
		}
		
		return permissions;
	}
	

}
