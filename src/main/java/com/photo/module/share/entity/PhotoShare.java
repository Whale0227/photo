package com.photo.module.share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享实体
 */
@Data
@TableName("photo_share")
public class PhotoShare {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分享者用户ID */
    private Long userId;

    /** 分享类型：0-单张图片 1-整个相册 */
    private Integer shareType;

    /** 分享目标ID（图片ID 或 相册ID） */
    private Long targetId;

    /** 分享码（唯一短码） */
    private String shareCode;

    /** 提取码（null 表示无需提取码） */
    private String extractCode;

    /** 过期时间（null 表示永久有效） */
    private LocalDateTime expireTime;

    /** 访问次数 */
    private Integer viewCount;

    /** 最大访问次数（null 不限制） */
    private Integer maxView;

    /** 状态：0-已失效 1-有效 */
    private Integer status;

    private LocalDateTime createTime;
}
