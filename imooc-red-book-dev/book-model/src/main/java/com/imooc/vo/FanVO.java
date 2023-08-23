package com.imooc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FanVO {
    private String vlogerId;    // 粉丝的id
    private String nickname;
    private String face;
    private boolean isFriend = false;
}
