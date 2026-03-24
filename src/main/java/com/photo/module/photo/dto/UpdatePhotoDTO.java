package com.photo.module.photo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改图片信息 DTO
 */
@Data
public class UpdatePhotoDTO {

    /** 归属相册ID（null 表示移到未分类） */
    private Long albumId;

    @Size(max = 512, message = "描述最长512个字符")
    private String description;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic;
}
