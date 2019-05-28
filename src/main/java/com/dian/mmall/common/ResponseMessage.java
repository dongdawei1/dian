package com.dian.mmall.common;

public enum ResponseMessage {
   ShuRuBuHeFa("输入不合法"),
   ShuRuHeFa("输入合法"),
   CaiDanBuCunZai("菜单不存在"),
   YongHuMingKeYong("用户名可用"),
   YongHuMingBuKeYong("用户名不可用"),
   ZhuCeShiBai("注册失败");
	
   String message;
   ResponseMessage(String message){
	   this.message=message;
   }
   public String getMessage(){
       return message;
   }
}
