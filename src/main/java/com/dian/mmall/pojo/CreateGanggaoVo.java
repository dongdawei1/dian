package com.dian.mmall.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGanggaoVo {
	private int tablenameid;  //根据这个判断 渲染
	private int permissionid;
	private String permissionName;
	private boolean quxiaoguanggao=false;
	private boolean bianjiguanggao=false;
	private boolean tianjiaguanggao=false;
	private boolean deletefabu=true;
	private Object dataObject;
	
	private List<CreateGanggaoVo> listvo;
	
	
	
	
	
}
