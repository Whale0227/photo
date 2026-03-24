package com.photo.module.tag.controller;

import com.photo.common.result.R;
import com.photo.module.tag.entity.Tag;
import com.photo.module.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签接口
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理")
@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取我的标签列表")
    @GetMapping("/list")
    public R<List<Tag>> listMyTags() {
        return R.ok(tagService.listMyTags());
    }

    @Operation(summary = "为图片设置标签")
    @PutMapping("/photo/{photoId}")
    public R<Void> setPhotoTags(@PathVariable Long photoId,
                                 @RequestBody List<String> tagNames) {
        tagService.setPhotoTags(photoId, tagNames);
        return R.ok();
    }

    @Operation(summary = "获取图片的标签")
    @GetMapping("/photo/{photoId}")
    public R<List<String>> getPhotoTags(@PathVariable Long photoId) {
        return R.ok(tagService.getPhotoTags(photoId));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{tagId}")
    public R<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return R.ok();
    }
}
