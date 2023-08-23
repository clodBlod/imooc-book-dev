package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Vlog;
import com.imooc.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Mapper
public interface VlogMapperCustom {
    // 首页视频+搜索
    List<IndexVlogVO> getIndexVlogList(@Param("paramMap")Map<String, Object> map);

    // 获取视频详细信息
    IndexVlogVO getVlogDetail(String vlogId);

    /**
     * @description: 获取我喜欢的视频列表
     * @param userId
     * @return: com.imooc.vo.IndexVlogVO
     */
    List<IndexVlogVO> getLikeVlogList(String userId);

    /**
     * @description: 获取我关注的博主的视频列表
     * @param userId
     * @return: java.util.List<com.imooc.vo.IndexVlogVO>
     */
    List<IndexVlogVO> getMyFollowVlogList(String userId);

    /**
     * @description: 获取我互关朋友视频列表
     * @param userId
     * @return: java.util.List<com.imooc.vo.IndexVlogVO>
     */
    List<IndexVlogVO> getMyFriendVlogList(String userId);

}