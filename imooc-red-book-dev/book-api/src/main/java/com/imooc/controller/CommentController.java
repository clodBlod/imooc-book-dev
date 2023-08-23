package com.imooc.controller;

import com.imooc.bo.CommentBO;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.exection.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Users;
import com.imooc.service.CommentService;
import com.imooc.service.UserService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * @description: 评论模块
 * @param
 * @return: json
 */
@RestController
@RequestMapping("/comment")
public class CommentController extends BaseInfoProperties {

    @Autowired
    private CommentService commentService;
    @Autowired
    private RedisOperator redis;
    @Autowired
    private UserService userService;

    /**
     * @description: 创建评论
     * @param commentBO
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid CommentBO commentBO) {

        // 校验fatherCommentId
        String fatherCommentId = commentBO.getFatherCommentId();
        if (StringUtils.isNoneBlank(fatherCommentId) && !fatherCommentId.equals("0")) {
            // 查询父评论是否存在
            if (!commentService.isCommentExists(fatherCommentId)) {
                // 不存在就报错
                return GraceJSONResult.errorMsg("父评论不存在！");
            }
        }

        // 校验vlogerId和commentUserId
        String vlogerId = commentBO.getVlogerId();
        if (StringUtils.isNoneBlank(vlogerId)) {
            // 判断博主是否存在，不存在就报错
            if (userService.getUserById(vlogerId) == null) {
                return GraceJSONResult.errorMsg("博主不存在！");
            }
        }

        String commentUserId = commentBO.getCommentUserId();
        if (StringUtils.isNoneBlank(commentUserId)) {
            if (userService.getUserById(commentUserId) == null) {
                return GraceJSONResult.errorMsg("评论人不存在！");
            }
        }


        CommentVO commentVO = commentService.createComment(commentBO);
        return GraceJSONResult.ok(commentVO);
    }

    /**
     * @description: 获取视频评论的数量
     * @param vlogId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/counts")
    public GraceJSONResult counts(@RequestParam String vlogId) {
        String count = redis.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
        if (count != null ){
            return GraceJSONResult.ok(Integer.parseInt(count));
        }

        return GraceJSONResult.ok(Integer.parseInt("0"));
    }

    /**
     * @description: 获取视频评论列表
     * @param vlogId
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize) {
        PagedGridResult list = commentService.getCommentList(vlogId, page, pageSize);
        return GraceJSONResult.ok(list);

    }

    /**
     * @description: 删除评论
     * @param commentUserId
     * @param commentId
     * @param vlogId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @DeleteMapping("/delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId) {
        commentService.deleteComment(commentUserId,commentId,vlogId);
        return GraceJSONResult.ok();
    }

    /**
     * @description: 喜欢评论
     * @param commentId
     * @param userId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("/like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String commentId) {
        commentService.likeComment(userId,commentId);
        return GraceJSONResult.ok();

    }

    /**
     * @description: 取消喜欢评论
     * @param commentId
     * @param userId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("/unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                  @RequestParam String commentId) {
        commentService.unLikeComment(userId,commentId);
        return GraceJSONResult.ok();

    }
}
