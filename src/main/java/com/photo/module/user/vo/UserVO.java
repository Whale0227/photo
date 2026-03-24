package com.photo.module.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO（返回给前端，不含密码）
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;
    private Integer status;
    private Long storageUsed;
    private Long storageLimit;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
