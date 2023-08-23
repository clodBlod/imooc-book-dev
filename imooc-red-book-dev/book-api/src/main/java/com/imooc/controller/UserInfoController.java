package com.imooc.controller;


import com.imooc.bo.UpdatedUserBo;
import com.imooc.config.MinIOConfig;
import com.imooc.enums.FileTypeEnum;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.model.Stu;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.service.base.BaseInfoProperties;
import com.imooc.utils.MinIOUtils;
import com.imooc.utils.RedisOperator;
import com.imooc.utils.SMSUtils;
import com.imooc.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 用户信息接口模块
@RestController
@RequestMapping("/userInfo")
@Slf4j
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private RedisOperator redis;

    @Autowired
    private UserService userService;

    @GetMapping("/query")
    public GraceJSONResult query(@RequestParam String userId) throws Exception{
        log.info("userId:"+userId);
        Users users = userService.getUserById(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserToken(redis.get(REDIS_USER_TOKEN+":"+userId));

        // 扩展UsersVO 关注，粉丝，获赞 通过redis计数去做
        // 我的关注博主总数
        // 我的关注博主总数量
        String myFollowsCountsStr = redis.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);
        // 我的粉丝总数
        String myFansCountsStr = redis.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        // 用户获赞总数，视频博主（点赞/喜欢）总和
        //String likedVlogCountsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + userId);
        String likedVlogerCountsStr = redis.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);
        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer likedVlogCounts = 0;
        Integer likedVlogerCounts = 0;
        Integer totalLikeMeCounts = 0;

        if (StringUtils.isNotBlank(myFollowsCountsStr)) {
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }
        if (StringUtils.isNotBlank(myFansCountsStr)) {
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
        //if (StringUtils.isNotBlank(likedVlogCountsStr)) {
        //    likedVlogCounts = Integer.valueOf(likedVlogCountsStr);
        //}
        if (StringUtils.isNotBlank(likedVlogerCountsStr)) {
            likedVlogerCounts = Integer.valueOf(likedVlogerCountsStr);
        }

        totalLikeMeCounts = likedVlogCounts + likedVlogerCounts;
        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);

        log.info(usersVO.toString());
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("/modifyUserInfo")
    public GraceJSONResult update(@RequestBody UpdatedUserBo updatedUserBo,
                                  @RequestParam Integer type) throws Exception{
        UserInfoModifyType.checkUserInfoTypeIsRight(type);
        Users users = userService.updateUserInfo(updatedUserBo,type);
        return GraceJSONResult.ok(users);
    }

    @Autowired
    private MinIOConfig minIOConfig;

    // 修改图像和背景
    @PostMapping("/modifyImage")
    public GraceJSONResult upload(MultipartFile file, @RequestParam Integer type, @RequestParam String userId) throws Exception {
        // 判断类型
        if (type != FileTypeEnum.BGIMG.type && type != FileTypeEnum.FACE.type) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();
        MinIOUtils.uploadFile(
                minIOConfig.getBucketName(),filename,file.getInputStream());
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + filename;

        // 修改图片地址到数据库
        UpdatedUserBo updatedUserBo = new UpdatedUserBo();
        updatedUserBo.setId(userId);
        if (type == FileTypeEnum.BGIMG.type) {
            updatedUserBo.setBgImg(imgUrl);
        }else if (type == FileTypeEnum.FACE.type) {
            updatedUserBo.setFace(imgUrl);
        }
        Users users = userService.updateUserInfo(updatedUserBo);
        return GraceJSONResult.ok(users);
    }

}
