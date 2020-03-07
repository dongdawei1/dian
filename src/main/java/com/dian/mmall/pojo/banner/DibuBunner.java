package com.dian.mmall.pojo.banner;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DibuBunner {
	private long id;
	
	private Integer permissionid; //在哪个菜单下展示
	
	private Integer bunnerType; //0首页弹窗，1首页轮播，2详情页轮播，3边测独立窗口，4其他
	private Integer tablenameid; //至能配置有查看权限有交集的类型
	
	private long tableId;
	private long createId;
	private String updateTime;
	private String 	url;//路由连接
	
	private String dibuBunnerbiaoti;
	private String introduceList;
	private Integer role;
	private String releaseType;
	
	private String detailed; //城区
	private Integer bunnerStatus; //状态 1进行中，2已结束
	private String startTime; //活动开始时间
	private String endTime; //活动结束时间
	private String examineName; //创建人员
	private String imgUrl; //图片地址
	private String createTime; //活动创建时间
	private long 	userId;//广告发布人id
	private Integer 	fanwei;//范围 0全国优先级最高，1全市，2全区
	private Integer 	moren;//是否是默认 0是1不是，先查1
}
