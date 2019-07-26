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
	private Integer useStatus;  // 1 上传 ，2删除，3使用
	private String userName;
	private long userId;
	private String tocken;
	private String createTime;
	private String pictureName;  //图片名称
	private String pictureUrl;  //图片名称
	@Override
	public String toString() {
		return "Picture [id=" + id + ", useStatus=" + useStatus + ", userName=" + userName + ", userId=" + userId
				+ ", tocken=" + tocken + ", createTime=" + createTime + ", pictureName=" + pictureName + ", pictureUrl="
				+ pictureUrl + "]";
	}
	
	
	
}
