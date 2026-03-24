package com.photo.common.result;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
public enum ResultCode {

    // -------- 通用 --------
    SUCCESS(200, "操作成功"),
    ERROR(500, "服务器内部错误"),
    PARAM_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // -------- 用户 --------
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    EMAIL_ALREADY_EXIST(1005, "邮箱已被注册"),

    // -------- 图片 --------
    PHOTO_NOT_EXIST(2001, "图片不存在"),
    PHOTO_UPLOAD_FAIL(2002, "图片上传失败"),
    FILE_TYPE_NOT_SUPPORT(2003, "不支持的文件类型"),
    FILE_SIZE_EXCEED(2004, "文件大小超出限制"),
    STORAGE_QUOTA_EXCEED(2005, "存储空间不足"),

    // -------- 相册 --------
    ALBUM_NOT_EXIST(3001, "相册不存在"),
    ALBUM_NO_PERMISSION(3002, "无权操作该相册"),

    // -------- 分享 --------
    SHARE_NOT_EXIST(4001, "分享不存在或已失效"),
    SHARE_CODE_ERROR(4002, "提取码错误"),
    SHARE_EXPIRED(4003, "分享链接已过期");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
