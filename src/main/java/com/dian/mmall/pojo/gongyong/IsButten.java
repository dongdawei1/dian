package com.dian.mmall.pojo.gongyong;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IsButten {
	private boolean isDisplayEdit = false; // 编辑键
	private boolean isDisplayHide = false; // 隐藏键
	private boolean isDisplayRelease = false; // 发布键
	private boolean isDisplayRefresh = false; // 刷新键
	private boolean isDisplayDelete = false; // 删除键
	private boolean transaction = false; // 在线交易

}
