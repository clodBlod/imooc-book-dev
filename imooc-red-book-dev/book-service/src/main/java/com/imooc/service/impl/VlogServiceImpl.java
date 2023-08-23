package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.bo.VlogBo;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.exection.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.MyLikedVlogMapper;
import com.imooc.mapper.VlogMapper;
import com.imooc.mapper.VlogMapperCustom;
import com.imooc.pojo.MyLikedVlog;
import com.imooc.pojo.Vlog;
import com.imooc.service.VlogService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {
    @Autowired
    private VlogMapper vlogMapper;
    @Autowired
    private VlogMapperCustom vlogMapperCustom;
    @Autowired
    private Sid sid;
    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;
    @Autowired
    private RedisOperator redis;

    // 视频信息入库
    @Override
    @Transactional
    public void createVlog(VlogBo vlogBo) {
        String id = sid.nextShort();
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogBo,vlog);
        vlog.setId(id);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        // 默认公开
        vlog.setIsPrivate(YesOrNo.NO.type);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());
        int insert = vlogMapper.insert(vlog);
        if (insert != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }
    }

    // 首页视频查询+分页
    @Override
    public PagedGridResult getIndexVlogList(String userId,String search,Integer page, Integer pageSize) {
        // 使用分页助手, 实际上也是一个切面，PageHelper会把我们的sql语句拿到，帮我们加上一个分页
        PageHelper.startPage(page,pageSize);

        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isNoneBlank(search) && StringUtils.isNotEmpty(search)){
            map.put("search",search);
        }

        // 此时我们返回的indexVlogList是经过分页的
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getIndexVlogList(map);
        // 获取视频列表后,
        indexVlogList = setVlogProperty(indexVlogList, userId);

        PagedGridResult pagedGridResult = setterPageGrid(indexVlogList, page);

        return pagedGridResult;
    }

    /**
     * @description: 判断我是否喜欢视频
     * @param userId
     * @param vlogId
     * @return: boolean
     */
    public boolean doILikeVlog(String userId,String vlogId) {
        String doILike = redis.get(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);
        boolean isLike = false;
        if (StringUtils.isNoneBlank(doILike) && doILike.equals(YesOrNo.YES.value)) {
            isLike = true;
        }
        return isLike;
    }

    /**
     * @description: 判断我是否关注博主
     * @param userId
     * @param vlogerId
     * @return: boolean
     */
    public boolean doIFollowVloger(String userId,String vlogerId) {
        String doIFollow = redis.get(REDIS_FANS_AND_VLOGER_RELATIONSHIP + ":" + userId + ":" + vlogerId);
        boolean isFollow = false;
        if (StringUtils.isNoneBlank(doIFollow) && doIFollow.equals(YesOrNo.YES.value)) {
            isFollow = true;
        }
        return isFollow;
    }

    /**
     * @description: 视频被喜欢的数量
     * @param vlogId
     * @return: java.lang.Integer
     */
    @Override
    public Integer vlogBeLikedCounts(String vlogId) {
        String count = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (count != null) {
            return Integer.valueOf(count);
        }else {
            return Integer.valueOf(YesOrNo.NO.type);
        }
    }

    // 获取视频详细信息
    @Override
    public IndexVlogVO getVlogDetail(String vlogId) {
        IndexVlogVO vlogDetail = vlogMapperCustom.getVlogDetail(vlogId);
        return vlogDetail;
    }

    // 设置视频公开/私密
    @Override
    @Transactional
    public void changePrivateOrPublic(String userId, String vlogId, Integer isPrivate) {
        // 平时我们更新的时候，我们可以采用通用mapper中的主键更新,updateByPrimaryKeySelective 如果字段为空，不覆盖
        // 但是，业务要求用户只能控制自己的视频权限，所以采用ByExample
        /* sql 语句
            update `imooc-red-book-dev`.vlog
            set is_private = #{type}
            where id = #{vlogId}
            and vloger_id = #{userId};
        */
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",userId);
        criteria.andEqualTo("id",vlogId);

        Vlog vlog = new Vlog();
        vlog.setIsPrivate(isPrivate);
        int i = vlogMapper.updateByExampleSelective(vlog, example);
        if (i != 1) {
            // 更新失败
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
    }

    // 查询个人公开和私密视频列表
    // 实现分页功能
    public PagedGridResult getPrivateAndPublicVlogList(String userId,Integer isPrivate,
                                                       Integer page, Integer pageSize){
        PageHelper.startPage(page,pageSize);
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",userId)
                .andEqualTo("isPrivate",isPrivate);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        PagedGridResult pagedGridResult = setterPageGrid(vlogs, page);

        return pagedGridResult;
    }

    /**
     * @param userId
     * @param vlogId
     * @description: 点赞视频
     * @return: void
     */
    @Override
    @Transactional
    public void likeVlog(String userId, String vlogId) {
        String id = sid.nextShort();
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setId(id);
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);
        // MySQL
        myLikedVlogMapper.insert(myLikedVlog);
    }

    /**
     * @param userId
     * @param vlogId
     * @description: 取消点赞视频
     * @return: void
     */
    @Override
    public void unlikeVlog(String userId, String vlogId) {
        // 从MySQL中移除
        Example example = new Example(MyLikedVlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId)
                .andEqualTo("vlogId",vlogId);
        myLikedVlogMapper.deleteByExample(example);
    }

    /**
     * @param userId
     * @param page
     * @param pageSize
     * @description: 查询我喜欢的视频列表
     * @return: com.imooc.utils.PagedGridResult
     */
    @Override
    public PagedGridResult getLikeVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<IndexVlogVO> list = vlogMapperCustom.getLikeVlogList(userId);
        return setterPageGrid(list,page);
    }

    /**
     * @param userId
     * @description: 获取我关注的博主的视频列表
     * @return: PagedGridResult
     */
    @Override
    public PagedGridResult getMyFollowVlogList(String userId,Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getMyFollowVlogList(userId);
        // 获取视频列表后,
        indexVlogList = setVlogProperty(indexVlogList,userId);
        return setterPageGrid(indexVlogList,page);
    }

    /**
     * @param userId
     * @param page
     * @param pageSize
     * @description: 获取我互关朋友的视频列表
     * @return: PagedGridResult
     */
    @Override
    public PagedGridResult getMyFriendVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getMyFriendVlogList(userId);
        // 获取视频列表后,
        indexVlogList = setVlogProperty(indexVlogList,userId);

        return setterPageGrid(indexVlogList,page);
    }


    /**
     * @description: 视频列表属性填充公共方法
     * @param indexVlogList
     * @param userId
     * @return: java.util.List<com.imooc.vo.IndexVlogVO>
     */
    public List<IndexVlogVO> setVlogProperty(List<IndexVlogVO> indexVlogList, String userId) {
        for (IndexVlogVO vlogVO : indexVlogList) {
            //判断用户是否喜欢这个视频
            String vlogId = vlogVO.getVlogId(); // 视频ID
            boolean isLike = doILikeVlog(userId, vlogId);
            vlogVO.setDoILikeThisVlog(isLike);

            //判断用户是否关注这个博主
            String vlogerId = vlogVO.getVlogerId();
            boolean isFollow = doIFollowVloger(userId, vlogerId);
            vlogVO.setDoIFollowVloger(isFollow);

            // 获取视频被喜欢的数量
            Integer count = vlogBeLikedCounts(vlogId);
            vlogVO.setLikeCounts(count);

            // 获取视频评论的数量
            String sum = redis.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
            int commentCounts = 0;
            if (sum != null) {
                commentCounts = Integer.valueOf(sum);
            }
            vlogVO.setCommentsCounts(commentCounts);

        }

        return indexVlogList;
    }
}
