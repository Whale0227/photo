package com.photo.module.user.vo;

import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
public class LoginVO {

    /** Sa-Token 令牌 */
    private String token;

    /** Token 名称（请求头字段名） */
    private String tokenName;

    /** 用户信息 */
    private UserVO userInfo;
}
