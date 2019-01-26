package com.dian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dian.mmall.service.IUserService;

import java.util.List;
import java.util.Map;

@Controller
public class HelloController {
  //注入jdbc
    @Autowired
    IUserService i;


    @ResponseBody
    @GetMapping("/query")
    public Object map(){
     //   List<Map<String, Object>> list = i.getall();
        return  i.getall();
    }
}
