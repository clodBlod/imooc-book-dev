package com.imooc.mo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
// 文档
@Document("message")    // 映射到mongodb的document
public class MessageMO {
    @Id
    private String id;              // 消息主键id
    @Field("fromUserId")
    private String fromUserId;      // 发送方用户id
    @Field("fromNickName")
    private String fromNickName;    // 发送方的用户昵称
    @Field("fromFace")
    private String fromFace;        // 发送方的用户头像

    @Field("toUserId")
    private String toUserId;        // 接收方用户id

    @Field("msgType")
    private Integer msgType;        // 消息类型 枚举

    @Field("msgContent")
    private Map msgContent;      // 消息具体内容

    @Field("createTime")
    private Date createTime;        // 消息创建时间


}
