package com.photo.module.tag.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.module.tag.entity.PhotoTag;
import com.photo.module.tag.entity.Tag;
import com.photo.module.tag.mapper.PhotoTagMapper;
import com.photo.module.tag.mapper.TagMapper;
import com.photo.module.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;
    private final PhotoTagMapper photoTagMapper;

    @Override
    public List<Tag> listMyTags() {
        Long userId = StpUtil.getLoginIdAsLong();
        return list(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getUserId, userId)
                .orderByDesc(Tag::getUseCount));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPhotoTags(Long photoId, List<String> tagNames) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 1. 删除旧关联
        photoTagMapper.deleteByPhotoId(photoId);

        if (tagNames == null || tagNames.isEmpty()) return;

        List<PhotoTag> relations = new ArrayList<>();
        for (String name : tagNames) {
            // 2. 查或创建标签
            Tag tag = getOne(new LambdaQueryWrapper<Tag>()
                    .eq(Tag::getUserId, userId)
                    .eq(Tag::getName, name));
            if (tag == null) {
                tag = new Tag();
                tag.setUserId(userId);
                tag.setName(name);
                tag.setUseCount(1);
                tag.setCreateTime(LocalDateTime.now());
                save(tag);
            } else {
                // 更新使用次数
                update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Tag>()
                        .eq(Tag::getId, tag.getId())
                        .set(Tag::getUseCount, tag.getUseCount() + 1));
            }
            relations.add(new PhotoTag(photoId, tag.getId()));
        }
        // 3. 批量插入新关联
        for (PhotoTag relation : relations) {
            photoTagMapper.insert(relation);
        }
    }

    @Override
    public List<String> getPhotoTags(Long photoId) {
        return photoTagMapper.selectTagNamesByPhotoId(photoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        Long userId = StpUtil.getLoginIdAsLong();
        removeById(tagId);
        // 删除所有关联
        photoTagMapper.delete(new LambdaQueryWrapper<PhotoTag>()
                .eq(PhotoTag::getTagId, tagId));
    }
}
