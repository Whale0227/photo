package com.photo.module.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.photo.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 相册实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("album")
public class Album extends BaseEntity {

    /** 所属用户ID */
    private Long userId;

    /** 相册名称 */
    private String name;

    /** 相册描述 */
    private String description;

    /** 封面图片ID */
    private Long coverId;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic;

    /** 排序权重 */
    private Integer sort;

    /** 图片数量（冗余统计） */
    private Integer photoCount;
}
