-- ============================================================
-- 图片资源管理系统 - 数据库初始化脚本
-- 数据库: photo_manager
-- ============================================================

CREATE DATABASE IF NOT EXISTS photo_manager
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE photo_manager;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`         VARCHAR(50)  NOT NULL COMMENT '用户名（唯一）',
    `password`         VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    `nickname`         VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `email`            VARCHAR(100) DEFAULT NULL COMMENT '邮箱（唯一）',
    `phone`            VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`           VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role`             TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `status`           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `storage_used`     BIGINT       NOT NULL DEFAULT 0 COMMENT '已用存储空间（字节）',
    `storage_limit`    BIGINT       NOT NULL DEFAULT 5368709120 COMMENT '存储配额（字节），默认5GB',
    `last_login_time`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 默认管理员账号（密码: admin123）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`, `storage_limit`)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 1, 1, 107374182400);


-- ============================================================
-- 2. 相册表
-- ============================================================
DROP TABLE IF EXISTS `album`;
CREATE TABLE `album` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT       NOT NULL COMMENT '所属用户ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '相册名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '相册描述',
    `cover_url`   VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `is_public`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
    `photo_count` INT          NOT NULL DEFAULT 0 COMMENT '图片数量（冗余字段）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '相册表';


-- ============================================================
-- 3. 图片表
-- ============================================================
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT       NOT NULL COMMENT '上传用户ID',
    `album_id`    BIGINT       DEFAULT NULL COMMENT '所属相册ID（NULL表示未分类）',
    `title`       VARCHAR(200) DEFAULT NULL COMMENT '图片标题',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '图片描述',
    `bucket_name` VARCHAR(100) NOT NULL COMMENT 'MinIO Bucket名称',
    `object_key`  VARCHAR(500) NOT NULL COMMENT 'MinIO 原图对象Key',
    `thumb_key`   VARCHAR(500) DEFAULT NULL COMMENT 'MinIO 缩略图对象Key',
    `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
    `file_size`   BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    `mime_type`   VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型，如image/jpeg',
    `width`       INT          DEFAULT NULL COMMENT '图片宽度（px）',
    `height`      INT          DEFAULT NULL COMMENT '图片高度（px）',
    `is_public`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
    `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_album_id` (`album_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '图片表';


-- ============================================================
-- 4. 标签表
-- ============================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT      NOT NULL COMMENT '所属用户ID（用户私有标签）',
    `name`        VARCHAR(50) NOT NULL COMMENT '标签名称',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `name`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '标签表';


-- ============================================================
-- 5. 图片标签关联表
-- ============================================================
DROP TABLE IF EXISTS `photo_tag`;
CREATE TABLE `photo_tag` (
    `id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `photo_id` BIGINT NOT NULL COMMENT '图片ID',
    `tag_id`   BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_photo_tag` (`photo_id`, `tag_id`),
    KEY `idx_photo_id` (`photo_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '图片标签关联表';


-- ============================================================
-- 6. 图片分享表
-- ============================================================
DROP TABLE IF EXISTS `photo_share`;
CREATE TABLE `photo_share` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT      NOT NULL COMMENT '创建分享的用户ID',
    `photo_id`      BIGINT      NOT NULL COMMENT '分享的图片ID',
    `share_code`    VARCHAR(32) NOT NULL COMMENT '分享码（URL唯一标识）',
    `extract_code`  VARCHAR(10) DEFAULT NULL COMMENT '提取码（NULL表示无需提取码）',
    `expire_time`   DATETIME    DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',
    `max_view`      INT         DEFAULT NULL COMMENT '最大访问次数（NULL表示不限制）',
    `view_count`    INT         NOT NULL DEFAULT 0 COMMENT '当前访问次数',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_photo_id` (`photo_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '图片分享表';


-- ============================================================
-- 7. 操作日志表（管理员审计）
-- ============================================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    `username`     VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名（冗余）',
    `module`       VARCHAR(50)  DEFAULT NULL COMMENT '操作模块，如 USER/PHOTO/ALBUM',
    `operation`    VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    `method`       VARCHAR(10)  DEFAULT NULL COMMENT '请求方法 GET/POST/PUT/DELETE',
    `request_url`  VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_ip`   VARCHAR(50)  DEFAULT NULL COMMENT '请求IP',
    `request_body` TEXT         DEFAULT NULL COMMENT '请求参数（JSON）',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '操作结果：0-失败，1-成功',
    `error_msg`    VARCHAR(500) DEFAULT NULL COMMENT '异常信息',
    `cost_time`    BIGINT       DEFAULT NULL COMMENT '耗时（毫秒）',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表';
