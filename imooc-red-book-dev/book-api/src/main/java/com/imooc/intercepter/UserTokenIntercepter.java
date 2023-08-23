package com.imooc.intercepter;


import com.imooc.grace.exection.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;

import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserTokenIntercepter extends BaseInfoProperties implements HandlerInterceptor {


    @Autowired
    private RedisOperator redis;

    // 访问controller之前，拦截请求
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 获得用户id和token
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        // 前置判断是否为空
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            // redis中的token为空，就需要重新登录
            if (StringUtils.isBlank(uToken)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }else {
                // redis中的token不为空，判断前端传递和redis中token是否一致
                if (!userToken.equals(uToken)) {
                    // token不相等，会话失效
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }

        }else {
            // userid和token为空，说明未登录
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
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
