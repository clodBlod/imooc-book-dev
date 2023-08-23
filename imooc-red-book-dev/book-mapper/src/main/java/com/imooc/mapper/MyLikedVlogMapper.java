package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.MyLikedVlog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MyLikedVlogMapper extends MyMapper<MyLikedVlog> {
}