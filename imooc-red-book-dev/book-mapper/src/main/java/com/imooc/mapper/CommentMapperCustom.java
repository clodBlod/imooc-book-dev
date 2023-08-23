package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Comment;
import com.imooc.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CommentMapperCustom {

    /**
     * @description: 获取评论列表
     * @param map
     * @return: java.util.List<com.imooc.vo.CommentVO>
     */
    List<CommentVO> getCommentList(@Param("params") Map<String,Object> map);
}