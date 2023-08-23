package com.imooc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
// 这个注解，当目录中有其他的包路径才会生效
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
// 使用注解@mapper 不加这个扫描也可以
//@MapperScan(basePackages = "com.imooc.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        log.info("抖音短视频项目启动成功");
    }
}