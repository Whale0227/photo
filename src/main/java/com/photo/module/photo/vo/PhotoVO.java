package com.photo.module.photo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片 VO
 */
@Data
public class PhotoVO {

    private Long id;
    private Long userId;
    private Long albumId;
    private String originalName;
    private String fileSize;        // 格式化后的文件大小，如 "2.3 MB"
    private String fileType;
    private Integer width;
    private Integer height;
    private String description;
    private Integer isPublic;
    private Integer viewCount;
    private Integer downloadCount;
    /** 原图访问URL */
    private String url;
    /** 缩略图访问URL */
    private String thumbUrl;
    /** 图片标签列表 */
    private List<String> tags;
    private LocalDateTime createTime;
}
