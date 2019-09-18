package com.controller;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 测试下springMvc
 * @author: Mr.He
 * @date: 2019-09-01 17:02
 **/
@Controller
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    public  String getTest(){
        System.out.println("你好");
        return "success你好 !!";
    }

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

}
