package com.dian.mmall.pojo;

import java.util.Map;

import com.dian.mmall.util.checknullandmax.IsEmptyAnnotation;
import com.dian.mmall.util.checknullandmax.MaxSize;
import com.dian.mmall.util.checknullandmax.MinSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TUserRole {
	//用户对应的角色
		private long id;
		private long userid;
		private long roleid;

		
}
