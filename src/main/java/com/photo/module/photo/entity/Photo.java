package com.photo.module.photo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.photo.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("photo")
public class Photo extends BaseEntity {

    /** 上传用户ID */
    private Long userId;

    /** 所属相册ID（null 表示未分类） */
    private Long albumId;

    /** 原始文件名 */
    private String originalName;

    /** 存储文件名（UUID重命名） */
    private String fileName;

    /** MinIO Bucket名称 */
    private String bucketName;

    /** MinIO 对象路径 */
    private String objectKey;

    /** 缩略图对象路径 */
    private String thumbKey;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件MIME类型 */
    private String fileType;

    /** 图片宽度(px) */
    private Integer width;

    /** 图片高度(px) */
    private Integer height;

    /** 图片描述 */
    private String description;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic;

    /** 浏览次数 */
    private Integer viewCount;

    /** 下载次数 */
    private Integer downloadCount;
}
