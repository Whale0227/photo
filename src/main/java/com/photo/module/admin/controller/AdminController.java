package com.photo.module.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.common.constant.CommonConstant;
import com.photo.common.entity.PageResult;
import com.photo.common.result.R;
import com.photo.module.user.entity.User;
import com.photo.module.user.service.UserService;
import com.photo.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员后台接口（需 role=1 权限）
 */
@Tag(name = "管理员后台")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/user/page")
    public R<PageResult<UserVO>> pageUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(keyword != null && !keyword.isEmpty(), w -> w
                        .like(User::getUsername, keyword)
                        .or().like(User::getNickname, keyword)
                        .or().like(User::getEmail, keyword))
                .orderByDesc(User::getCreateTime);
        userService.page(pageObj, wrapper);
        List<UserVO> vos = pageObj.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        }).collect(Collectors.toList());
        return R.ok(PageResult.of(vos, pageObj.getTotal(), pageObj.getCurrent(), pageObj.getSize()));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/user/{userId}/status")
    public R<Void> updateUserStatus(@PathVariable Long userId,
                                     @RequestParam Integer status) {
        userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStatus, status));
        return R.ok();
    }

    @Operation(summary = "重置用户密码（重置为 123456）")
    @PutMapping("/user/{userId}/reset-password")
    public R<Void> resetPassword(@PathVariable Long userId) {
        // BCrypt 加密后的 "123456"
        String encoded = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXJcpFiHUDqBDYtB5H3duq3iBJy";
        userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, encoded));
        return R.ok("密码已重置为 123456", null);
    }

    @Operation(summary = "调整用户存储配额")
    @PutMapping("/user/{userId}/quota")
    public R<Void> updateStorageLimit(@PathVariable Long userId,
                                       @RequestParam Long storageLimit) {
        userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStorageLimit, storageLimit));
        return R.ok();
    }
}
