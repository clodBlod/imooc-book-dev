package com.imooc.service;

import com.imooc.bo.VlogBo;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Vlog;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface VlogService {

    // 新增视频
    void createVlog(VlogBo vlogBo);

    // 获取视频
    PagedGridResult getIndexVlogList(String userId,String search, Integer page, Integer pageSize);

    // 获取视频详细页面
    IndexVlogVO getVlogDetail(String vlogId);

    // 设置视频公开/私密
    void changePrivateOrPublic(String userId, String vlogId, Integer type);

    // 查询个人公开和私密视频列表
    // 实现分页功能
    PagedGridResult getPrivateAndPublicVlogList(String userId, Integer isPrivate, Integer page, Integer pageSize);

    /**
     * @description: 点赞视频
     * @param userId
     * @param vlogId
     * @return: void
     */
    void likeVlog(String userId,String vlogId);


    /**
     * @description: 取消点赞视频
     * @param userId
     * @param vlogId
     * @return: void
     */
    void unlikeVlog(String userId, String vlogId);

    /**
     * @description: 视频被喜欢的数量
     * @param vlogId
     * @return: java.lang.Integer
     */
    public Integer vlogBeLikedCounts(String vlogId);

    /**
     * @description: 查询我喜欢的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.utils.PagedGridResult
     */
    PagedGridResult getLikeVlogList(String userId,Integer page, Integer pageSize);

    /**
     * @description: 获取我关注的博主的视频列表
     * @param userId
     * @return: PagedGridResult
     */
    PagedGridResult getMyFollowVlogList(String userId,Integer page, Integer pageSize);

    /**
     * @description: 获取我互关朋友的视频列表
     * @param userId
     * @return: PagedGridResult
     */
    PagedGridResult getMyFriendVlogList(String userId,Integer page, Integer pageSize);
}
