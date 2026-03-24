package com.photo.module.photo.dto;

import lombok.Data;

import java.util.List;

/**
 * 图片搜索/分页查询 DTO
 */
@Data
public class PhotoQueryDTO {

    /** 关键词（搜索描述或文件名） */
    private String keyword;

    /** 相册ID（null 表示全部） */
    private Long albumId;

    /** 标签ID */
    private Long tagId;

    /** 是否只查公开：0-全部 1-只看公开 */
    private Integer isPublic;

    /** 页码，默认1 */
    private Integer page = 1;

    /** 每页条数，默认20 */
    private Integer size = 20;
}
