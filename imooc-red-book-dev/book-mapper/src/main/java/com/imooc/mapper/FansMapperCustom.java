package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Fans;
import com.imooc.vo.FanVO;
import com.imooc.vo.VlogerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface FansMapperCustom {
    /**
     * @description: 查询我的关注列表
     * @param map
     * @return: java.util.List<com.imooc.vo.VlogerVo>
     */
    List<VlogerVo> queryMyFollows(@Param("params")Map<String,Object> map);

    /**
     * @description: 查询我的粉丝列表
     * @param map
     * @return: java.util.List<com.imooc.vo.FanVO>
     */
    List<FanVO> queryMyFans(@Param("params") Map<String,Object> map);

}