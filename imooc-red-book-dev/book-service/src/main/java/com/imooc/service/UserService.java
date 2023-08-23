package com.imooc.service;


import com.imooc.bo.UpdatedUserBo;
import com.imooc.pojo.Users;

public interface UserService {
    // 判断用户是否存在
    Users queryMobileIsExist(String mobile);

    // 创建用户
    Users createUser(String mobile);

    // 查询用户信息
    Users getUserById(String userId);

    Users updateUserInfo(UpdatedUserBo updatedUserBo);

    // 更新前置方法
    Users updateUserInfo(UpdatedUserBo updatedUserBo,Integer type);
}
