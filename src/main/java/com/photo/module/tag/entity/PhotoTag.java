package com.photo.module.tag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片标签关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("photo_tag")
public class PhotoTag {

    private Long photoId;

    private Long tagId;
}
