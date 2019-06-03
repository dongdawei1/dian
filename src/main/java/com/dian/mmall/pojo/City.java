package com.dian.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
 private int id;
 private int provinces_id;//省、直辖市市id
 private String provinces_name;//省市名
 
 private int provinces_city_id;//省下边市的id 
 private int city_id; // 市id
 private String city_name; // 市名字,直辖市 这里统一叫“直辖市”
 
 private int city_district_county_id;//市id
 private int district_county_id;//区县id
 private String district_county_name;//区县id
 
// SELECT COUNT(*)  FROM `city` c  JOIN city a
// ON c.provinces_id=1  AND a.`city_id`=3  AND a.`provinces_city_id`=1
// JOIN city b 
// ON b.`district_county_id`=4
// AND b.`city_district_county_id`=3

}
