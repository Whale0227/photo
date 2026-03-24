package com.photo.module.photo.controller;

import com.photo.common.entity.PageResult;
import com.photo.common.result.R;
import com.photo.module.photo.dto.PhotoQueryDTO;
import com.photo.module.photo.dto.UpdatePhotoDTO;
import com.photo.module.photo.service.PhotoService;
import com.photo.module.photo.vo.PhotoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片接口
 */
@Tag(name = "图片管理")
@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public R<PhotoVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "description", required = false) String description) {
        return R.ok(photoService.upload(file, albumId, description));
    }

    @Operation(summary = "删除图片（支持批量）")
    @DeleteMapping
    public R<Void> deletePhotos(@RequestBody List<Long> photoIds) {
        photoService.deletePhotos(photoIds);
        return R.ok();
    }

    @Operation(summary = "修改图片信息")
    @PutMapping("/{photoId}")
    public R<Void> updatePhoto(@PathVariable Long photoId,
                                @RequestBody UpdatePhotoDTO dto) {
        photoService.updatePhoto(photoId, dto);
        return R.ok();
    }

    @Operation(summary = "分页查询图片")
    @GetMapping("/page")
    public R<PageResult<PhotoVO>> pagePhotos(PhotoQueryDTO dto) {
        return R.ok(photoService.pagePhotos(dto));
    }

    @Operation(summary = "获取图片详情")
    @GetMapping("/{photoId}")
    public R<PhotoVO> getPhotoDetail(@PathVariable Long photoId) {
        return R.ok(photoService.getPhotoDetail(photoId));
    }

    @Operation(summary = "下载图片")
    @GetMapping("/{photoId}/download")
    public void download(@PathVariable Long photoId, HttpServletResponse response) {
        photoService.download(photoId, response);
    }

    @Operation(summary = "批量移动到相册")
    @PutMapping("/move")
    public R<Void> movePhotos(@RequestBody List<Long> photoIds,
                               @RequestParam(value = "albumId", required = false) Long albumId) {
        photoService.movePhotos(photoIds, albumId);
        return R.ok();
    }
}
