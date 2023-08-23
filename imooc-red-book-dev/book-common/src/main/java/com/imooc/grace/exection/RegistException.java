package com.imooc.grace.exection;

import com.imooc.grace.result.ResponseStatusEnum;

// 自定义用户注册异常类
public class RegistException extends RuntimeException {

    private ResponseStatusEnum responseStatusEnum;

    public RegistException(ResponseStatusEnum responseStatusEnum) {
        super("异常状态码为：" + responseStatusEnum.status()
                + "；具体异常信息为：" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}
