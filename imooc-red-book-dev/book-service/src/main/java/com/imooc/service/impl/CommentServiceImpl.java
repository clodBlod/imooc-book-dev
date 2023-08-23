package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.bo.CommentBO;
import com.imooc.mapper.CommentMapper;
import com.imooc.mapper.CommentMapperCustom;
import com.imooc.pojo.Comment;
import com.imooc.service.CommentService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.CommentVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private RedisOperator redis;
    @Autowired
    private CommentMapperCustom commentMapperCustom;

    /**
     * @param commentBO
     * @description: 创建评论
     * @return: void
     */
    @Override
    public CommentVO createComment(CommentBO commentBO) {
        String commentId = sid.nextShort();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setVlogerId(commentBO.getVlogerId());
        comment.setVlogId(commentBO.getVlogId());
        comment.setCommentUserId(commentBO.getCommentUserId());
        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setContent(commentBO.getContent());

        comment.setLikeCounts(0);
        comment.setCreateTime(new Date());
        // mysql
        commentMapper.insert(comment);
        //redis 评论总数的累加
        redis.increment(REDIS_VLOG_COMMENT_COUNTS+":"+comment.getVlogId(),1);

        // 把新创建评论返回，放在第一行
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment,commentVO);
        return commentVO;
    }

    /**
     * @param commentId
     * @description: 判断评论是否存在
     * @return: boolean
     */
    @Override
    public boolean isCommentExists(String commentId) {
        return commentMapper.existsWithPrimaryKey(commentId);
    }

    /**
     * @param vlogId
     * @param page
     * @param pageSize
     * @description: 获取评论列表
     * @return: java.util.List<com.imooc.vo.CommentVO>
     */
    @Override
    public PagedGridResult getCommentList(String vlogId, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        PageHelper.startPage(page,pageSize);
        List<CommentVO> commentList = commentMapperCustom.getCommentList(map);
        return setterPageGrid(commentList,page);
    }
}
