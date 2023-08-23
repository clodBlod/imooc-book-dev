package com.imooc.service.impl;

import com.imooc.bo.UpdatedUserBo;
import com.imooc.enums.Sex;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.exection.GraceException;
import com.imooc.grace.exection.RegistException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 默认头像和默认背景
    private static final String USER_FACE1 = "https://picx.zhimg.com/v2-5273b40c004da5615cb03c58a90319d9_xl.jpg?source=32738c0c";
    private static final String USER_BGIMG = "https://picx.zhimg.com/v2-7e856a1be55cd910f04716973d8a17ce_1440w.jpg?source=32738c0c";


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    public Users queryMobileIsExist(String mobile) {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile",mobile);
        Users users = usersMapper.selectOneByExample(example);
        return users;
    }

    @Transactional
    @Override
    public Users createUser(String mobile) {
        // 唯一主键这里我本想使用UUID，但是现在有IDworker
        String userId = sid.nextShort();
        Users user = new Users();
        user.setId(userId);
        user.setMobile(mobile);
        // 第一次创建，其他数据自动填充
        // 脱敏工具：对敏感数据进行变形处理
        user.setNickname("用户："+ DesensitizationUtil.commonDisplay(mobile)); // 用户名
        user.setImoocNum("用户："+ DesensitizationUtil.commonDisplay(mobile)); // 慕课号
        user.setFace(USER_FACE1); // 头像
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);
        user.setBgImg(USER_BGIMG);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        // 插入到数据库中
        int result = usersMapper.insert(user);
        if (result <= 0) {
            log.info("用户插入失败");
            // 现学现用，我们把这个错误抛出，返回到前端
            // 用户已经存在
            throw new RegistException(ResponseStatusEnum.USER_HAS_EXIST);
        }
        return user;
    }


    @Override
    public Users getUserById(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        return users;
    }

    @Override
    public Users updateUserInfo(UpdatedUserBo updatedUserBo, Integer type) {
        Example example = new Example(Users.class);
        // 修改类型为用户名，需要判断是否冲突
        if (type == UserInfoModifyType.NICKNAME.type) {
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("nickname",updatedUserBo.getNickname());
            Users users = usersMapper.selectOneByExample(example);
            if (users != null) {
                // 用户名已经存在
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }else if (type == UserInfoModifyType.IMOOCNUM.type) {
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("imoocNum",updatedUserBo.getNickname());
            Users users = usersMapper.selectOneByExample(example);
            if (users != null) {
                // 慕课号已经存在
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            }
            // 判断慕课号是否还能够修改
            users = getUserById(updatedUserBo.getId());
            Integer result = users.getCanImoocNumBeUpdated();
            if (result == YesOrNo.NO.type) {
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            updatedUserBo.setCanImoocNumBeUpdated(YesOrNo.YES.type);
        }
        return updateUserInfo(updatedUserBo);
    }

    @Override
    public Users updateUserInfo(UpdatedUserBo updatedUserBo) {
        Users users = new Users();
        BeanUtils.copyProperties(updatedUserBo,users);
        // updateByPrimaryKeySelective 如果字段为空，不覆盖
        // updateByPrimaryKey 字段为空会直接覆盖
        int result = usersMapper.updateByPrimaryKeySelective(users);
        if (result <= 0 ){
            // 用户更新失败
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
            // 更新失败要返回原本的user信息
            Users user = getUserById(updatedUserBo.getId());
            return user;
        }
        return users;
    }
}
