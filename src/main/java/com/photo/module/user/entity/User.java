package com.photo.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.photo.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 密码（BCrypt加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像路径 */
    private String avatar;

    /** 角色：0-普通用户 1-管理员 */
    private Integer role;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    /** 已用存储空间(字节) */
    private Long storageUsed;

    /** 存储上限(字节)，默认5GB */
    private Long storageLimit;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
}
