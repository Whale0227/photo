package com.photo.module.user.controller;

import com.photo.common.result.R;
import com.photo.module.user.dto.LoginDTO;
import com.photo.module.user.dto.RegisterDTO;
import com.photo.module.user.dto.UpdatePasswordDTO;
import com.photo.module.user.dto.UpdateUserDTO;
import com.photo.module.user.service.UserService;
import com.photo.module.user.vo.LoginVO;
import com.photo.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        //userService.register(dto);
        return R.ok("注册成功", null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(userService.login(dto));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        userService.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<UserVO> getUserInfo() {
        return R.ok(userService.getCurrentUser());
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/info")
    public R<Void> updateInfo(@Valid @RequestBody UpdateUserDTO dto) {
        userService.updateInfo(dto);
        return R.ok();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public R<Void> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto) {
        userService.updatePassword(dto);
        return R.ok("密码修改成功，请重新登录", null);
    }
}
