package com.imooc.controller;


import com.imooc.grace.result.GraceJSONResult;
import com.imooc.model.Stu;
import com.imooc.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 项目测试api
@RestController
public class HelloController {
    // api模块测试
    @GetMapping("/hello")
    public String hello() {
        return "hello!!";
    }

    // 优雅异常返回测试
    @GetMapping("/ok")
    public GraceJSONResult ok(){
        return GraceJSONResult.ok();
    }

    // lombok测试
    @GetMapping("/lombok")
    public GraceJSONResult lombok() {
        return GraceJSONResult.ok(new Stu("吴盟",18));
    }

    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("/sms")
    public Object sms() throws Exception{
        smsUtils.sendSMS("15531419181","3324");
        return GraceJSONResult.ok();
    }

}
