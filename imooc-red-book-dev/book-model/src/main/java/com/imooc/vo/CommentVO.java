package com.imooc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentVO {
    private String id;
    private String commentId;               // 评论id
    private String vlogerId;                // 博主id
    private String fatherCommentId;         // 父评论id
    private String vlogId;                  // 视频id
    private String commentUserId;           // 评论者id
    private String commentUserNickname;     // 评论者昵称
    private String commentUserFace;         // 评论者头像
    private String content;                 // 评论内容
    private Integer likeCounts;             // 评论被点赞数量
    private String replyedUserNickname;     // 评论被回复，回复人的昵称
    private Date createTime;                // 评论时间
    private Integer isLike = 0;             // 评论是否被点赞
}