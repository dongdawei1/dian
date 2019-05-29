package com.dian.mmall.common;

public enum ResponseMessage {
   ShuRuBuHeFa("输入不合法"),
   ShuRuHeFa("输入合法"),
   CaiDanBuCunZai("菜单不存在"),
   YongHuMingKeYong("用户名可用"),
   YongHuMingBuKeYong("用户名不可用"),
   ZhuCeShiBai("注册失败"),
   ChuangJianYongHuShiBai("创建用户失败"),
   YanZhengMaCuoWu("验证码错误或失效"),
   YongHuMingMiMaGeShiCouWu("用户名或密码格式错误"),
   YongHuMingMiMaCouWu("用户名或密码错误"),
   HuoQuDengLuXinXiShiBai("用户未登录,无法获取当前用户的信息"),
   XiTongYiChang("系统异常"),
   YangZhengMaShengChengShiBai("验证码生成错误"),
   DengLuGuoQi("登录过期"),
   YuanShiMiMaCuoWu("原始密码错误"),
   ShouJiHaoBuHeFa("手机号不合法"),
   YongHuMingBuKeXiuGai("用户名不可修改"),
   MiMaBuHeFa("密码长度不合法"),
   	MiMaBuYiZhi("两次密码不一致"),
   	BianJiChengGong("编辑成功"),
   	BianJiChengGongChongXinDengLu("编辑成功重新登陆");
	
   String message;
   ResponseMessage(String message){
	   this.message=message;
   }
   public String getMessage(){
       return message;
   }
}
