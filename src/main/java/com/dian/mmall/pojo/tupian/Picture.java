package com.dian.mmall.pojo.tupian;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Picture {
//图片上传中临时存库
	private long id;
	private Integer use_status;  // 1 上传 ，2删除，3使用
	private String user_name;
	private long user_id;
	private String tocken;
	private String create_time;
	private String picture_name;  //图片名称
	private String picture_url;  //图片名称
	@Override
	public String toString() {
		return "Picture [id=" + id + ", use_status=" + use_status + ", user_name=" + user_name + ", user_id=" + user_id
				+ ", tocken=" + tocken + ", create_time=" + create_time + ", picture_name=" + picture_name
				+ ", picture_url=" + picture_url + "]";
	}
	
	
}
