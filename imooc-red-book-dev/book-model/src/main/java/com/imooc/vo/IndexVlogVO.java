package com.imooc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IndexVlogVO {
    private String vlogId;
    private String vlogerId;
    private String vlogerFace;
    private String vlogerName;
    private String content;
    private String url;
    private String cover;
    private Integer width;
    private Integer height;
    private Integer likeCounts; // 视频被喜欢数
    private Integer commentsCounts;
    private Integer isPrivate;
    private boolean isPlay = false;     // 视频是否播放
    private boolean doIFollowVloger = false;
    private boolean doILikeThisVlog = false;

}
