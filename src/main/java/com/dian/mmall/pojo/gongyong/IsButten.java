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
	private boolean edit = false; // 编辑键
	private boolean hide = false; // 隐藏键
	private boolean release = false; // 发布键
	private boolean refresh = false; // 刷新键
	private boolean delete = false; // 删除键
	private boolean transaction = false; // 在线交易
}
