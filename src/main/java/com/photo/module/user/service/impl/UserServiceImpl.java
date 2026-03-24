package com.photo.module.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.common.constant.CommonConstant;
import com.photo.common.exception.BusinessException;
import com.photo.common.result.ResultCode;
import com.photo.module.user.dto.LoginDTO;
import com.photo.module.user.dto.RegisterDTO;
import com.photo.module.user.dto.UpdatePasswordDTO;
import com.photo.module.user.dto.UpdateUserDTO;
import com.photo.module.user.entity.User;
import com.photo.module.user.mapper.UserMapper;
import com.photo.module.user.service.UserService;
import com.photo.module.user.vo.LoginVO;
import com.photo.module.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        // 1. 校验两次密码
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次密码输入不一致");
        }

        // 2. 校验用户名唯一
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 3. 校验邮箱唯一
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            long emailCount = count(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, dto.getEmail()));
            if (emailCount > 0) {
                throw new BusinessException(ResultCode.EMAIL_ALREADY_EXIST);
            }
        }

        // 4. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(CommonConstant.ROLE_USER);
        user.setStatus(CommonConstant.STATUS_NORMAL);
        user.setStorageUsed(0L);
        user.setStorageLimit(5L * 1024 * 1024 * 1024); // 默认5GB
        save(user);
        log.info("用户注册成功: {}", dto.getUsername());
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验状态
        if (user.getStatus() == CommonConstant.STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 3. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 4. Sa-Token 登录（rememberMe 时 token 有效期7天，否则跟随浏览器会话）
        if (Boolean.TRUE.equals(dto.getRememberMe())) {
            StpUtil.login(user.getId(), "remember-me");
        } else {
            StpUtil.login(user.getId());
        }

        // 5. 更新最后登录时间
        update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now()));

        // 6. 构建响应
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(tokenInfo.getTokenValue());
        loginVO.setTokenName(tokenInfo.getTokenName());
        loginVO.setUserInfo(toVO(user));
        return loginVO;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(UpdateUserDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 校验邮箱唯一
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            long emailCount = count(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, dto.getEmail())
                    .ne(User::getId, userId));
            if (emailCount > 0) {
                throw new BusinessException(ResultCode.EMAIL_ALREADY_EXIST);
            }
        }
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次密码输入不一致");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "旧密码错误");
        }
        update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(dto.getNewPassword())));
        // 修改密码后强制退出登录
        StpUtil.logout();
    }

    @Override
    public void updateStorageUsed(Long userId, Long delta) {
        userMapper.updateStorageUsed(userId, delta);
    }

    /** Entity → VO */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
