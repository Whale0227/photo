package com.photo.module.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建/修改相册 DTO
 */
@Data
public class AlbumDTO {

    @NotBlank(message = "相册名称不能为空")
    @Size(max = 64, message = "相册名称最长64个字符")
    private String name;

    @Size(max = 256, message = "描述最长256个字符")
    private String description;

    /** 封面图片ID */
    private Long coverId;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic = 0;

    /** 排序权重 */
    private Integer sort = 0;
}
