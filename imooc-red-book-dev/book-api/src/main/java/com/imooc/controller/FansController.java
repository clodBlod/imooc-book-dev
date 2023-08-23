package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.service.FansService;
import com.imooc.service.UserService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/fans")
@Slf4j
public class FansController extends BaseInfoProperties {
    @Autowired
    private FansService fansService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redis;


    /**
     * @description: 关注
     * @param userId
     * @param vlogerId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam("myId") String userId,
                                  @RequestParam String vlogerId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId)) {
            // 参数错误
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        // 判断当前用户自己不能关注自己
        if (userId.equalsIgnoreCase(vlogerId)) {
            // 参数错误
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        // 判断两个id的用户是否存在
        Users user = userService.getUserById(userId);
        Users vloger = userService.getUserById(vlogerId);
        if (user == null || vloger == null) {
            // 参数错误
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        // mysql
        fansService.follow(userId,vlogerId);

        // redis计数器
        // 博主的粉丝+1，我的关注+1，我和博主的关联关系
        redis.increment(REDIS_MY_FOLLOWS_COUNTS+":"+userId,1); // delta:步长，每次增加数量
        redis.increment(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        redis.set(REDIS_FANS_AND_VLOGER_RELATIONSHIP+":"+userId+":"+vlogerId,YesOrNo.YES.value);

        return GraceJSONResult.ok();
    }

    /**
     * @description: 取消关注
     * @param userId
     * @param vlogerId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @PostMapping("/cancel")
    public GraceJSONResult cancel(@RequestParam("myId") String userId,
                                  @RequestParam String vlogerId) {
        // mysql
        fansService.cancel(userId,vlogerId);

        // redis计数器
        // 博主的粉丝-1，我的关注-1，我和博主的关联关系
        redis.decrement(REDIS_MY_FOLLOWS_COUNTS+":"+userId,1); // delta:步长，每次增加数量
        redis.decrement(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        redis.del(REDIS_FANS_AND_VLOGER_RELATIONSHIP+":"+userId+":"+vlogerId);

        return GraceJSONResult.ok();
    }


    /**
     * @description: 查询我是否关注博主
     * @param userId
     * @param vlogerId
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam("myId") String userId,
                                  @RequestParam String vlogerId) {
        boolean result = fansService.queryDoIFollowVloger(userId, vlogerId);
        return GraceJSONResult.ok(result);
    }

    /**
     * @description:  查询我关注的博主列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam("myId") String userId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){
        PagedGridResult pagedGridResult = fansService.queryMyFollows(userId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    /**
     * @description: 查询我的粉丝列表
     * @param userId
     * @param page
     * @param pageSize
     * @return: com.imooc.grace.result.GraceJSONResult
     */
    @GetMapping("/queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam("myId") String userId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){
        PagedGridResult pagedGridResult = fansService.queryMyFans(userId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }


}
