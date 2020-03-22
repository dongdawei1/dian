package com.dian.mmall.common.fabu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.connection.ReactiveSetCommands.SDiffCommand;

public enum QuXian {
	
	beijin(110100, new ArrayList<String>() {{
		this.add("朝阳区");
		this.add("怀柔区");
		this.add("丰台区");
		this.add("平谷区");
		this.add("昌平区");
		this.add("大兴区");
		this.add("东城区");
		this.add("通州区");
		this.add("西城区");
		this.add("顺义区");
		this.add("房山区");
		this.add("门头沟区");
		this.add("石景山区");
		this.add("密云区");
		this.add("海淀区");
		this.add("延庆区");
		this.add("全市");
		this.add("电话确认");
	}}),
	shanghai(310100, new ArrayList<String>() {{
		this.add("崇明区");
		this.add("奉贤区");
		this.add("杨浦区");
		this.add("黄浦区");
		this.add("闵行区");
		this.add("宝山区");
		this.add("嘉定区");
		this.add("徐汇区");
		this.add("浦东新区");
		this.add("长宁区");
		this.add("金山区");
		this.add("静安区");
		this.add("松江区");
		this.add("普陀区");
		this.add("青浦区");
		this.add("虹口区");
		this.add("全市");
		this.add("电话确认");
	}});
	
	QuXian(Integer cityDistrictCountyId,ArrayList<String> districtCountyNames){
		this.districtCountyNames=districtCountyNames;
		this.cityDistrictCountyId=cityDistrictCountyId;
	}
	public ArrayList<String> getDistrictCountyNames() {
		return districtCountyNames;
	}

	public Integer getCityDistrictCountyId() {
		return cityDistrictCountyId;
	}
	
	Integer cityDistrictCountyId;
	ArrayList<String> districtCountyNames;
}
