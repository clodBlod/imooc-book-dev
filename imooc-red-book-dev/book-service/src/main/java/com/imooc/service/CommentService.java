package com.imooc.service;

import com.imooc.bo.CommentBO;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommentService {

    /**
     * @description: 创建评论
     * @param commentBO
     * @return: CommentVO
     */
    public CommentVO createComment(CommentBO commentBO);

    /**
     * @description: 判断评论是否存在
     * @param commentId
     * @return: boolean
     */
    public boolean isCommentExists(String commentId);

    /**
     * @description: 获取评论列表
     * @param vlogId
     * @return: java.util.List<com.imooc.vo.CommentVO>
     */
    PagedGridResult getCommentList(String vlogId, Integer page, Integer pageSize);
}
