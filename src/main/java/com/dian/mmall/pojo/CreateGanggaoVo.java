package com.dian.mmall.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateGanggaoVo {
	private int tablenameid;  //根据这个判断 渲染
	private int permissionid;
	private String permissionName;
	private boolean quxiaoguanggao=false;
	private boolean bianjiguanggao=false;
	private boolean tianjiaguanggao=false;
	private boolean deletefabu=true;
	private Object dataObject;
	private String xiangqiurl;
	private List<CreateGanggaoVo> listvo;
	
	
	
	
	
}
