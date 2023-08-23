package com.imooc.service;

import com.imooc.bo.VlogBo;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;

import java.util.Map;

public interface MsgService {

    /**
     * @description: 创建消息
     * @param fromUserId
     * @param toUserId
     * @param msgType
     * @param msgContent
     * @return: void
     */
    public void createMsg(String fromUserId,
                          String toUserId,
                          Integer msgType,
                          Map msgContent);
}
