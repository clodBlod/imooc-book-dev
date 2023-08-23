package com.imooc.config;

import com.imooc.intercepter.PassportIntercepter;
import com.imooc.intercepter.UserTokenIntercepter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IntercepterConfig implements WebMvcConfigurer {

    @Bean
    public PassportIntercepter getPassportIntercepter() {
        return new PassportIntercepter();
    }

    @Bean
    public UserTokenIntercepter getUserTokenIntercepter() {
        return new UserTokenIntercepter();
    }
    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getPassportIntercepter())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(getUserTokenIntercepter())
                .addPathPatterns("/userInfo/modifyUserInfo")
                .addPathPatterns("/userInfo/modifyImage");
    }
}
