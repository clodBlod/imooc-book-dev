package com.imooc.service.impl;

import com.imooc.mo.MessageMO;
import com.imooc.pojo.Users;
import com.imooc.repository.MessageRepository;
import com.imooc.service.MsgService;
import com.imooc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;

    /**
     * @param fromUserId
     * @param toUserId
     * @param msgType
     * @param msgContent
     * @description: 创建消息
     * @return: void
     */
    @Override
    public void createMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent) {
        Users user = userService.getUserById(fromUserId);

        MessageMO messageMO = new MessageMO();
        //messageMO.setId(); MongoDB自动生成
        messageMO.setFromUserId(user.getId());
        messageMO.setFromNickName(user.getNickname());
        messageMO.setFromFace(user.getFace());
        messageMO.setToUserId(toUserId);
        if (msgContent != null) {
            messageMO.setMsgContent(msgContent);
        }
        messageMO.setMsgType(msgType);
        messageMO.setCreateTime(new Date());
        // 保存
        MessageMO save = messageRepository.save(messageMO);
        log.info("发送的消息："+save);
    }
}
