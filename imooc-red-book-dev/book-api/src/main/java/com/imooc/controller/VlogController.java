package com.imooc.controller;

import com.imooc.bo.VlogBo;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.service.VlogService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.IndexVlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vlog")
@Slf4j
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;
    @Autowired
    private RedisOperator redis;

    // 视频信息入库
    @PostMapping("/publish")
    public GraceJSONResult publish(@RequestBody VlogBo vlogBo) {
        vlogService.createVlog(vlogBo);
        return GraceJSONResult.ok();
    }


    // 查询视频列表
    @GetMapping("/indexList")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize,
                                     // search 默认为空
                                     @RequestParam(defaultValue = "") String search) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.getIndexVlogList(userId,search, page, pageSize);

        return GraceJSONResult.ok(pagedGridResult);
    }

    // 查询视频详细页
    @GetMapping("/detail")
    public GraceJSONResult detail(@RequestParam String userId,
                                  @RequestParam String vlogId) {

        IndexVlogVO vlogDetail = vlogService.getVlogDetail(vlogId);
        return GraceJSONResult.ok(vlogDetail);
    }

    // 设置视频私密或者公开
    // 私密
    @PostMapping("/changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                  @RequestParam String vlogId) {

        vlogService.changePrivateOrPublic(userId,vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    // 公开
    @PostMapping("/changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                           @RequestParam String vlogId) {

        vlogService.changePrivateOrPublic(userId,vlogId,YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    // 查询个人公开和私密视频列表
    // 公开
    @GetMapping("/myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {
        PagedGridResult list = vlogService.getPrivateAndPublicVlogList(userId, YesOrNo.NO.type, page, pageSize);
        return GraceJSONResult.ok(list);
    }

    // 私密
    @GetMapping("/myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {
        PagedGridResult list = vlogService.getPrivateAndPublicVlogList(userId, YesOrNo.YES.type, page, pageSize);
        return GraceJSONResult.ok(list);
    }

    @PostMapping("/like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {
        // mysql
        vlogService.likeVlog(userId,vlogId);
        // redis
        // 点赞后：视频和视频发布者的点赞都会+1,用户喜欢的视频在redis中保存关联关系
        redis.increment(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1);
        redis.increment(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1);
        // 保存用户喜欢的视频在redis中保存关联关系
        redis.set(REDIS_USER_LIKE_VLOG+":"+userId+":"+vlogId,YesOrNo.YES.value);
        return GraceJSONResult.ok();
    }

    @PostMapping("/unlike")
    public GraceJSONResult unLike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {
        // 判断该视频是否已经被用户喜欢
        String isLiked = redis.get(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);
        if (isLiked == null) {
            return GraceJSONResult.error();
        }
        // mysql
        vlogService.unlikeVlog(userId,vlogId);
        // redis
        // 取消点赞后：视频和视频发布者的点赞都会-1,删除用户喜欢的视频在redis中保存关联关系
        redis.decrement(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1);
        redis.decrement(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1);
        // 删除用户喜欢的视频在redis中保存关联关系
        redis.del(REDIS_USER_LIKE_VLOG+":"+userId+":"+vlogId);
        return GraceJSONResult.ok();
    }

    /*
     * @description: 点赞完视频立马重新查询视频总数
     * @param vlogId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId){
        Integer counts = vlogService.vlogBeLikedCounts(vlogId);
        return GraceJSONResult.ok(counts);
    }

    /**
     * @description: 查询我喜欢的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {
        PagedGridResult result = vlogService.getLikeVlogList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }

    /**
     * @description: 查询我关注的博主的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/followList")
    public GraceJSONResult followList(@RequestParam("myId") String userId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {
        PagedGridResult result = vlogService.getMyFollowVlogList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }

    /**
     * @description: 查询我互关朋友视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/friendList")
    public GraceJSONResult friendList(@RequestParam("myId") String userId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        PagedGridResult result = vlogService.getMyFriendVlogList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }

}
