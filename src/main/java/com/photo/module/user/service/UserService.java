package com.photo.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.module.user.dto.LoginDTO;
import com.photo.module.user.dto.RegisterDTO;
import com.photo.module.user.dto.UpdatePasswordDTO;
import com.photo.module.user.dto.UpdateUserDTO;
import com.photo.module.user.entity.User;
import com.photo.module.user.vo.LoginVO;
import com.photo.module.user.vo.UserVO;

/**
 * 用户 Service 接口
 */
public interface UserService extends IService<User> {

    /** 注册 */
    void register(RegisterDTO dto);

    /** 登录，返回 token + 用户信息 */
    LoginVO login(LoginDTO dto);

    /** 退出登录 */
    void logout();

    /** 获取当前登录用户信息 */
    UserVO getCurrentUser();

    /** 修改个人信息 */
    void updateInfo(UpdateUserDTO dto);

    /** 修改密码 */
    void updatePassword(UpdatePasswordDTO dto);

    /** 更新已用存储空间 */
    void updateStorageUsed(Long userId, Long delta);
}
