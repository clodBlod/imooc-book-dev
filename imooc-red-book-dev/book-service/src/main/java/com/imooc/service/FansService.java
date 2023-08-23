package com.imooc.service;

import com.imooc.utils.PagedGridResult;
import com.imooc.vo.FanVO;
import com.imooc.vo.VlogerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FansService {


    /**
     * @description: 关注
     * @param myId 当前用户的id
     * @param vlogerId 博主的id
     * @return: void
     */
    void follow(String myId, String vlogerId);

    /**
     * @description:取消关注
     * @param myId
     * @param vlogerId
     * @return: void
     */
    void cancel(String myId, String vlogerId);

    /**
     * @description: 查询我是否关注博主
     * @param myId
     * @param vlogerId
     * @return: boolean
     */
    boolean queryDoIFollowVloger(String myId, String vlogerId);

    /**
     * @description: 查询我关注的博主列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.utils.PagedGridResult
     */
    PagedGridResult queryMyFollows(String userId,Integer page,Integer pageSize);

    /**
     * @description: 查询我的粉丝列表
     * @param map
     * @return: java.util.List<com.imooc.vo.FanVO>
     */
    PagedGridResult queryMyFans(String userId,Integer page,Integer pageSize);
}
