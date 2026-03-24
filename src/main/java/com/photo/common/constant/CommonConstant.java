package com.photo.common.constant;

/**
 * 系统通用常量
 */
public interface CommonConstant {

    // -------- 用户角色 --------
    /** 普通用户 */
    int ROLE_USER = 0;
    /** 管理员 */
    int ROLE_ADMIN = 1;

    // -------- 状态 --------
    /** 正常 */
    int STATUS_NORMAL = 1;
    /** 禁用 */
    int STATUS_DISABLED = 0;

    // -------- 公开/私有 --------
    /** 私有 */
    int PRIVATE = 0;
    /** 公开 */
    int PUBLIC = 1;

    // -------- 分享类型 --------
    /** 分享单张图片 */
    int SHARE_TYPE_PHOTO = 0;
    /** 分享整个相册 */
    int SHARE_TYPE_ALBUM = 1;

    // -------- 文件 --------
    /** 允许上传的图片类型 */
    String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/png", "image/gif",
            "image/webp", "image/bmp", "image/svg+xml"
    };
    /** 缩略图目录前缀 */
    String THUMB_PREFIX = "thumb/";
    /** 缩略图宽度(px) */
    int THUMB_WIDTH = 300;
    /** 缩略图高度(px) */
    int THUMB_HEIGHT = 300;

    // -------- 分页 --------
    /** 默认页码 */
    int DEFAULT_PAGE = 1;
    /** 默认每页条数 */
    int DEFAULT_PAGE_SIZE = 20;
    /** 最大每页条数 */
    int MAX_PAGE_SIZE = 100;

    // -------- Sa-Token --------
    /** 管理员登录 type */
    String LOGIN_TYPE_ADMIN = "admin";
    /** 普通用户登录 type */
    String LOGIN_TYPE_USER = "user";
}
