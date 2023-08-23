package com.imooc.intercepter;

import com.imooc.grace.exection.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class PassportIntercepter extends BaseInfoProperties implements HandlerInterceptor {


    @Autowired
    private RedisOperator redis;

    // 访问controller之前，拦截请求
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 获取用户id
        String UserIp = IPUtil.getRequestIp(request);
        // 判断redis中是否存在
        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE+":"+UserIp);
        log.info("是否存在："+keyIsExist);
        if (keyIsExist) {
            // 优雅异常封装
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        // 请求放行
        return true;
    }

    // 渲染视图之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    // 请求结束后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
