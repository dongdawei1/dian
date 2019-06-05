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
 private int provincesId;//省、直辖市市id
 private String provincesName;//省市名
 
 private int provincesCityId;//省下边市的id 
 private int cityId; // 市id
 private String cityName; // 市名字,直辖市 这里统一叫“直辖市”
 
 private int cityDistrictCountyId;//市id
 private int districtCountyId;//区县id
 private String districtCountyName;//区县id

 
// SELECT COUNT(*)  FROM `city` c  JOIN city a
// ON c.provinces_id=1  AND a.`city_id`=3  AND a.`provinces_city_id`=1
// JOIN city b 
// ON b.`district_county_id`=4
// AND b.`city_district_county_id`=3

}
