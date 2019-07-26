package com.dian.mmall.common;

public enum ResponseMessage {
   ShuRuBuHeFa("输入不合法"),
   ShuRuHeFa("输入合法"),
   CaiDanBuCunZai("菜单不存在"),
   YongHuMingKeYong("用户名可用"),
   YongHuMingBuKeYong("用户名不可用"),
   ZhuCeShiBai("注册失败"),
   gongsimingchengkong("公司/企业/个人名称不能为空"),
   ChuangJianYongHuShiBai("创建用户失败"),
   YanZhengMaCuoWu("验证码错误或失效"),
   YongHuMingMiMaGeShiCouWu("用户名或密码格式错误"),
   YongHuMingMiMaCouWu("用户名或密码错误"),
   HuoQuDengLuXinXiShiBai("用户未登录,无法获取当前用户的信息"),
   XiTongYiChang("系统异常"),
   YangZhengMaShengChengShiBai("验证码生成错误"),
   DengLuGuoQi("登录过期"),
   YuanShiMiMaCuoWu("原始密码错误"),
   ShouJiHaoBuHeFa("手机号输入不合法"),
   YongHuMingBuKeXiuGai("用户名不可修改"),
   MiMaBuHeFa("密码长度不合法"),
   	MiMaBuYiZhi("两次密码不一致"),
   	BianJiChengGong("编辑成功"),
   	BianJiChengGongChongXinDengLu("编辑成功重新登陆"),
	YongHuLeiXingCuoWu("用户类型错误"),
	ChengShiBuHeFa("城市输入不合法"),
	ChengGong("创建成功"),
	GengXinYongHuXinXiShiBai("更新用户信息失败"),
	LuoKuShiBai("落库失败"), 
	NianLiFanWei("年龄必须在18至60之间"), 
	XinBieYouWu("请正确输入性别"),
	YongHuIdYiJingCunZai("用户ID已经存在"),
	JueSeBuHeFa("角色输入不合法"),
	ShiBaiYuanYinWeiKong("失败原因为空"),
	YongHuIdBuJingCunZai("用户ID不存在"),
	ZhuoCeShouJiCuoWu("注册手机号错误"),
	YongHuMinghuoidbuyizhi("用户名或者id不合法"),
	xingbiebuhefa("性别输入不合法"),
	fabuzongshudayu("发布总数不能大于"),
	yonghuweishiming("用户未实名"),
	meiyouquanxian("没有发布权限"),
	fulibunengweikong("福利不能为空"),
	fulibuhefa("福利输入不合法"),
	zhiweikong("职位不能为空"),
	zhiweibuhefa("职位输入不合法"),
	gongzikong("工资不能为空"),
	gongzibuhefa("工资输入不合法"),
	xuelikong("学历不能为空"),
	xuelibuhefa("学历输入不合法"),
	nianxiankong("工作年限不能为空"),
	nianxianbuhefa("工作年限输入不合法"),
	nianlikong("年龄不能为空"),
	nianlingbuhefa("年龄输入不合法"),
	jianglikong("奖励不能为空"),
	jianglibuhefa("奖励不合法"),
	shimingxinxibuyizhi("实名信息不一致"),
	huoqushimingxinxishibai("获取实名信息失败"),
	yonghuidhuoshenpixiangbucunzi("用户或者审批项不存在"),
	shenpishenggong("审批成功"),
	youxiaoqibuhefa("有效期在超过范围"),
	canshuyouwu("操作参数有误"),
	yonghuidbucunzai("用户ID不存在"),
	caozuoleixincuowu("操作类型错误"),
	caozuoshibai("操作失败"),
	caozuochenggong("操作成功"),
	shifougongkaidianhualeixingcuowu("是否公开电话类型错误"),
	gongkaidianhuahuozheshuruyouxiang("公开电话或者输入邮箱"),
	dengluguoqi("用户登陆已过期"),
	meiyouciquanxian("没有权限"),
	cheshichaxunshibai("城市查询失败，请更换城市"),
	weibaozhengxinxianquan("为保证用户信息安全，每天只能查询20次联系方式"),
	chaxunshibai("查询失败"),
	yifabuguojianli("已发布过简历"),
	weifabuguojianli("未发布过简历"),
	fabuleixingkong("发布类型不能为空"),
	chaoguofabuzongshu("此类型信息每位商户只能发布5条"),
	fuwuchengshicuowu("服务城区输入错误"),
	qibujiagebuhefa("起步价格只能是整数数字"),
	shangchuanshibai("上传失败,请稍后重试"),
	shanchuchenggeng("删除成功"),
	shangchushibai("删除失败"),
	idbucunzai("图片ID不存在"),
	tupianbunnegkong("图片不能为空");
   String message;
   ResponseMessage(String message){
	   this.message=message;
   }
   public String getMessage(){
       return message;
   }
}
