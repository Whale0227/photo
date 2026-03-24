package com.photo.module.share.dto;

import lombok.Data;

/**
 * 创建分享 DTO
 */
@Data
public class CreateShareDTO {

    /** 分享类型：0-单张图片 1-整个相册 */
    private Integer shareType = 0;

    /** 分享目标ID */
    private Long targetId;

    /** 提取码（null 表示不设置） */
    private String extractCode;

    /**
     * 有效天数（null 表示永久）
     * 可选值：1、7、30、null
     */
    private Integer expireDays;
}
