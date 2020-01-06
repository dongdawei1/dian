package com.dian.mmall.common;

public enum ResponseMessage {
   ShuRuBuHeFa("输入不合法"),
   ShuRuHeFa("输入合法"),
   CaiDanBuCunZai("菜单不存在"),
   YongHuMingKeYong("用户名可用"),
   YongHuMingBuKeYong("用户名不可用"),
   ZhuCeShiBai("注册失败"),
   gongsimingchengkong("公司/企业/个人名称不能为空"),
   shichangming("市场名称不能为空"),
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
	shimingIDkong("实名ID不能为空"),
	huoqushimingxinxishibai("获取实名信息失败"),
	yonghuidhuoshenpixiangbucunzi("用户或者审批项不存在"),
	shenpishenggong("审批成功"),
	youxiaoqibuhefa("有效期在超过范围"),
	canshuyouwu("操作参数有误"),
	yonghuidbucunzai("用户ID不存在"),
	huoquxinxishibai("获取用户信息失败"),
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
	tupianbunnegkong("图片不能为空"),
	meiyouchaxundaoshimingxinxi("没有查询到实名信息"),
	chaxunshimingshixishibai("查询实名信息失败"),
	benditupianbucunzai("本地图片不存在"),
	chaxunleixingbunnegweikong("查询类型不能为空"),
	chaxunleixingbunnegweicuowo("查询类型不存在"),
	mianjibunnegweikong("面积不能为空"),
	mianjibuhefa("面积为大于1小于1万的整数"),
	mianjicuowu("面积输入错误"),
	caidanIDweikong("菜单id不能为空"),
	chaxunyiyouleixshibai("查询已有类型失败"),
	leixingyicunzai("商品/服务类型已经存在"),
	fabuleixinbixuan("发布类型必须选择"),
	shenhefuwuleixin("服务类型不通过，审核必须也选不通过"),
	shangpinfuwuleixingidnull("商品服务类型id不能为空"),
	shangpinfuwuleixluokushibai("商品服务类型落库失败，刷新后重试"),
	shangpinleixinchaxunshibai("商品类型查询失败,重新选择：服务类型 "),
	fuwuleixinIdcuowu("服务类型id不存在,只能选:新服务类型不通过和发布不通过"),
	zhiyoushiming("只有实名的批发商户才能申请为接单商户"),
	yishijiedian("已是接单商户或在审批中"),
	shenhezhuangt("通知状态必须选择"),
	zhuantaicuowu("通知状态错误,请重新选择"),
	shimingidbunengweikong("实名id不能为空"),
	qianyueshijianhuodidianbunengkong("签约时间或签约地点不能为空"),
	bixuxianxuanzedizhi("请先选择签约城市"),
	tuizhibaojinkaxinx("银行卡或者支付宝必填其一"),
	zhanghuxingmingnull("账户所有人姓名不能为空"),
	zhibaojinbunnegnull("质保金不能为空"),
	zhibaojinxioyue("质保金不能小于300元"),
	yijingshi("已经是接单用户,无法再次加入"),
	gengxinyue("更新余额失败"),
	yonghuzhanghu("用户账户创建/更新失败"),
	chuangjianliushuishibai("创建流水失败"),
	lianxiren("联系人不能为空"),
	dizhixiangq("地址详情不能为空"),
	shangpinmingkong("商品名不能为空"),
	shangpinchaxunshibai("商品名查询失败"),
	baozhuangfangshikong("包装方式不能为空"),
	baozhuangfangshicuowo("包装方式选择错误"),
	danweikong("单位不能为空"),
	danweicuowo("单位不存在"),
	danweiyubaozhuangbupipei("选择的单位与包装不匹配"),
	guigekong("包装规格不能为空"),
	guigecuowo("包装规格输入错误"),
	danjiakong("单价不能为空"),
	danjiacuowo("单价输入错误"),
	shuliangkong("数量不能为空"),
	shuliangcuowo("数量输入错误"),
	chengsshicuowo("城市不能为空"),
	songhuokong("送货方式不能为空"),
	songhuocuowo("送货方式选择错误"),
	zhichiyudingkong("请选择是否支持线上预定"),
	zhichiyudingcuowo("是否支持线上预定选择错误"),
	yunfeikong("运费或满减金额不能为空"),
	yunfeicuowo("运费或满减金额输入错误"),
	jiageyouxiaoqicuowo("价格有效期选择错误"),
	jiageyouxiaoqikong("价格有效期不能为空"),
	jiagekaishicuowo("价格开始时间不能小于当前时间"),
	jiagekaishiyaozai("价格开始时间不能大于"),
	jiagejieshucuowo("价格结束时间不能小于当前时间"),
	jiagejieshuyaozai("价格结束时间不能大于"),
	chaxunleixinkong("查询类型不能为空"),
	chaxunzhuangtcuowo("查询状态错误"),
	shangpinidcuowo("商品ID有误"),
	shangpinchaxunkong("商品查询失败"),
	shangpinyoudingdanbunnegchan("有进行中的订单不允许删除"),
	shangpinyoudingdanbunenggai("有进行中的订单不允许编辑"),
	shangpinleixingheming("商品名和商品类型不能修改"),
	shichangsuozaichengqukong("市场所在城区不能为空"),
	chaxunshangpinshibai("查询商品失败"),
	songhuoriqiyouwu("送货日期输入错误"),
	songhuoriqizhinnegsantiannei("送货日期只能是三天内"),
	songhuoriqibunnegxiaoyujint("送货日期不能小于今天"),
	chuangjiandingdanshibai("创建订单落库失败"),
	dingdanbunnegweikong("采购列表不能为空"),
	dingdanchaxunshibai("订单查询失败"),
	;
   String message;
   ResponseMessage(String message){
	   this.message=message;
   }
   public String getMessage(){
       return message;
   }
}
