package com.imooc.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegistLoginBo {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11,max = 11,message = "手机号不正确")
    private String mobile;
    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
