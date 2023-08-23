package com.imooc.controller;

import com.imooc.bo.RegistLoginBo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import com.imooc.utils.SMSUtils;
import com.imooc.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private UserService userService;


    @PostMapping("/getSMSCode")
    public GraceJSONResult getPassport(@RequestParam String mobile,
                                       HttpServletRequest request) throws Exception{
        if (StringUtils.isBlank(mobile)){
            return GraceJSONResult.ok();
        }

        String userIp = IPUtil.getRequestIp(request);
        // 根据用户IP限制60秒之内只能获取一次验证码,把用户ip放入redis中，
        // 下次发送请求，如果redis中有，就返回消息，在拦截器中处理
        log.info(userIp);
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);
        // 随机生成验证码
        String code = ((int) ((Math.random() * 9 + 1) * 100000)) + "";
        log.info("验证码："+code);
        smsUtils.sendSMS(mobile,code);

        // 把验证码放入reids中,30分钟有效
        redis.set(MOBILE_SMSCODE+":"+mobile,code,30*60);
        // 返回
        return GraceJSONResult.ok();
    }


    @PostMapping("/login")
    public GraceJSONResult login(@Valid @RequestBody RegistLoginBo registLoginBo,
                                 //BindingResult result, 对代码有侵入性
                                 HttpServletRequest request) {
        String mobile = registLoginBo.getMobile();
        String smsCode = registLoginBo.getSmsCode();

        // 从redis中获取验证码
        String redisSmsCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        Users users = userService.queryMobileIsExist(mobile);
        if (users == null) {
            // 空用户注册
            users = userService.createUser(mobile);
        }
        // 如果不为空，可以保存用户信息和会话信息
        String uToken = UUID.randomUUID().toString();
        if (redis == null) {
            log.info("redis为null");
        }
        redis.set(REDIS_USER_TOKEN+":"+users.getId(),uToken);

        // 用户登录成功后，删除redis中的短信验证码
        redis.del(MOBILE_SMSCODE+":"+mobile);
        // 返回用户信息
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);

    }


    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId) throws Exception {
        // 用户注销，清除redis中的token
        redis.del(REDIS_USER_TOKEN+":"+userId);
        return GraceJSONResult.ok();
    }

}
