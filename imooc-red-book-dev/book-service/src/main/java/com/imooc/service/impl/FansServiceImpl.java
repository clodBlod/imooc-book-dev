package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.enums.MessageEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.FansMapper;
import com.imooc.mapper.FansMapperCustom;
import com.imooc.pojo.Fans;
import com.imooc.service.FansService;
import com.imooc.service.MsgService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.FanVO;
import com.imooc.vo.VlogerVo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {
    @Autowired
    private FansMapper fansMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private FansMapperCustom fansMapperCustom;
    @Autowired
    private RedisOperator redis;
    @Autowired
    private MsgService msgService;

    /**
     * @param fanId     当前用户的id
     * @param vlogerId 博主的id
     * @description: 关注
     * @return: void
     */
    @Override
    @Transactional
    public void follow(String fanId, String vlogerId) {
        Fans fans = new Fans();
        fans.setId(sid.nextShort());    // 设置主键
        fans.setFanId(fanId);            // 我的id
        fans.setVlogerId(vlogerId);     // 博主id
        // 判断博主是否关注我
        Fans record = IsVlogerFollowMe(fanId, vlogerId);
        if (record != null) {
            // 博主已经关注我
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            // 设置博主关注我这条记录的isFanFriendOfMine = 1
            record.setIsFanFriendOfMine(YesOrNo.YES.type);
            // 修改博主关注我的那条记录
            fansMapper.updateByPrimaryKeySelective(record);
        }else {
            // 博主没有关注我
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        // 插入到粉丝表
        fansMapper.insert(fans);

        // 发送系统消息
        msgService.createMsg(fanId,vlogerId, MessageEnum.FOLLOW_YOU.type,null);
    }

    /**
     * @description: 判断博主是否关注我
     * @param fanId : 粉丝
     * @param vlogerId 博主
     * @return: boolean
     */
    public Fans IsVlogerFollowMe(String fanId, String vlogerId) {
        // 我要关注博主，此时要判断一下，博主是否已经关注我
        // 如果博主已经关注我了，那么，两条fans表记录中的isFanFriendOfMine都要置为1
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        // 此时双方颠倒，fanId变为博主，vlogerId变为我
        criteria.andEqualTo("fanId",vlogerId)
                .andEqualTo("vlogerId",fanId);
        Fans fans = fansMapper.selectOneByExample(example);
        return fans;
    }

    /**
     * @param myId
     * @param vlogerId
     * @description:取消关注
     * @return: void
     */
    @Override
    public void cancel(String myId, String vlogerId) {
        // 判断博主是否关注我，如果关注我，需要取消双方的关系
        Fans record = IsVlogerFollowMe(myId, vlogerId);
        if (record != null) {
            // 博主关注我
            // 设置博主关注我这条记录的isFanFriendOfMine = 0
            record.setIsFanFriendOfMine(YesOrNo.NO.type);
            // 修改博主关注我的那条记录
            fansMapper.updateByPrimaryKeySelective(record);
        }
        // 删除自己的关联记录表
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fanId", myId)
                .andEqualTo("vlogerId",vlogerId);
        int i = fansMapper.deleteByExample(example);

    }


    /**
     * @param myId
     * @param vlogerId
     * @description: 查询我是否关注博主
     * @return: boolean
     */
    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        // 查询我是否关注博主
        // 此时：vloger = my ME = vloger
        Fans fans = IsVlogerFollowMe(vlogerId, myId);
        if (fans != null) {
            return true;
        }
        return false;
    }

    /**
     * @param userId
     * @param page
     * @param pageSize
     * @description: 查询我关注的博主列表
     * @return: com.imooc.utils.PagedGridResult
     */
    @Override
    public PagedGridResult queryMyFollows(String userId, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<VlogerVo> list = fansMapperCustom.queryMyFollows(map);
        PagedGridResult pagedGridResult = setterPageGrid(list, page);
        return pagedGridResult;
    }

    /**
     * @param userId
     * @param page
     * @param pageSize
     * @description: 查询我的粉丝列表
     * @return: java.util.List<com.imooc.vo.FanVO>
     */
    @Override
    public PagedGridResult queryMyFans(String userId, Integer page, Integer pageSize) {
        // 1.查询粉丝表
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<FanVO> list = fansMapperCustom.queryMyFans(map);
        // 2.判断我是否关注粉丝
        // 使用redis：REDIS_FANS_AND_VLOGER_RELATIONSHIP = "redis_fans_and_vloger_relationship";
        // redis.set(REDIS_FANS_AND_VLOGER_RELATIONSHIP+":"+userId+":"+vlogerId,YesOrNo.YES.value);
        // 此时，粉丝 = 我，vloger = fan
        for (FanVO fanVO : list) {
            String result = redis.get(REDIS_FANS_AND_VLOGER_RELATIONSHIP + ":" + userId + ":" + fanVO.getVlogerId());
            if (YesOrNo.YES.value.equals(result)) {
                fanVO.setFriend(true);
            }
        }

        PagedGridResult pagedGridResult = setterPageGrid(list, page);
        return pagedGridResult;
    }
}
