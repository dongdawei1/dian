package com.dian.mmall.common.zhiwei;

public enum Position {
	
	//服务员送餐员厨师/厨师长后厨传菜员配菜/打荷洗碗工面点师茶艺师迎宾/接待
	//大堂经理/领班餐饮管理学徒杂工咖啡师预订员营养师厨工餐厅服务员行政主厨日式厨师西点师厨师助理/学徒烧烤师品酒师裱花师川湘菜厨师小龙虾厨师火锅炒料法国菜厨师意大利菜厨师西班牙菜厨师墨西哥菜厨师东南亚菜厨师京鲁菜厨师东北菜厨师江浙菜厨师西北菜厨师粤港菜厨师云贵菜厨师餐饮其他餐厅领班中餐厨师
	//roleId 2饭店  ，11 求职 ，  5 酒水等 
	fuwuyuan(2,"服务员"),
	shouyin(2,"收银员"),
	houchu(2,"后厨"),
	liancaishi(2,"凉菜师"),
	peicai(2,"配菜/打荷"),
	xiwangong(2,"洗碗工"),
    linglu(2,"京鲁菜厨师"),
	    dongbei(2,"东北菜厨师"),
	    jiangzhe(2,"江浙菜厨师"),
	    xibei(2,"西北菜厨师"),
	    egang(2,"粤港菜厨师"),
	    yungui(2,"云贵菜厨师"),
	    chuanxiang(2,"川湘菜厨师"),
	    xiaolongxia(2,"小龙虾厨师"),
	    shaokaoshi(2,"烧烤师"),
		chenmian(2,"拉面师"),
		xiaomian(2,"削面师"),
		songcanyuan(2,"送餐员"),
		neibai(2,"保安/停车管理"),
		weixiu(2,"店内维修工"),
	    qitai(2,"餐饮其他"),
	miandianshi(2,"面点师"),
	chayishi(2,"茶艺师"),
	yingbin(2,"迎宾/接待"),
	lingban(2,"大堂经理/领班"),
    xituzagong(2,"学徒杂工"),
    kafeishi(2,"咖啡师"),
    chugong(2,"厨工"),
  //大堂经理/领班餐饮管理学徒杂工咖啡师预订员营养师厨工餐厅服务员行政主厨日式厨师
    //西点师厨师助理/学徒烧烤师品酒师裱花师川湘菜厨师小龙虾厨师火锅炒料法国菜厨师
    
    xidian(2,"西点师厨"),
    tiaojiushi(2,"调酒师"),
    facan(2,"法国菜厨师"),
    rishi(2,"日式厨师"),
    //意大利菜厨师西班牙菜厨师墨西哥菜厨师东南亚菜厨师京鲁菜厨师东北菜厨师江浙菜厨师
    //西北菜厨师粤港菜厨师云贵菜厨师餐饮其他餐厅领班中餐厨师
    yidali(2,"意大利菜厨师"),
    xibanya(2,"西班牙菜厨师"),
    moxigec(2,"墨西哥菜厨师"),
    dongnanya(2,"东南亚菜厨师"),
 
	;
	
	
	String positionType;
	 Integer roleId;
	Position( Integer roleId,String positionType){
		   this.positionType=positionType;
		   this.roleId=roleId;
	   }
	   public String getPositionType(){
	       return positionType;
	   }
	public Integer getRoleId() {
		return roleId;
	}
}
