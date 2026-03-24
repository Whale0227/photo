package com.photo.module.tag.service;

import com.photo.module.tag.entity.Tag;

import java.util.List;

/**
 * 标签 Service
 */
public interface TagService {

    /** 获取当前用户所有标签 */
    List<Tag> listMyTags();

    /** 为图片设置标签（覆盖更新） */
    void setPhotoTags(Long photoId, List<String> tagNames);

    /** 获取图片的标签名列表 */
    List<String> getPhotoTags(Long photoId);

    /** 删除标签 */
    void deleteTag(Long tagId);
}
