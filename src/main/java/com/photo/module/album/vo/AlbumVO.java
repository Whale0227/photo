package com.photo.module.album.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册 VO
 */
@Data
public class AlbumVO {

    private Long id;
    private String name;
    private String description;
    private Long coverId;
    /** 封面图片访问URL */
    private String coverUrl;
    private Integer isPublic;
    private Integer sort;
    private Integer photoCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
