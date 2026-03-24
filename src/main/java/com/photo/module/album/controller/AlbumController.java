package com.photo.module.album.controller;

import com.photo.common.result.R;
import com.photo.module.album.dto.AlbumDTO;
import com.photo.module.album.service.AlbumService;
import com.photo.module.album.vo.AlbumVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 相册接口
 */
@Tag(name = "相册管理")
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "创建相册")
    @PostMapping
    public R<Long> createAlbum(@Valid @RequestBody AlbumDTO dto) {
        return R.ok(albumService.createAlbum(dto));
    }

    @Operation(summary = "修改相册")
    @PutMapping("/{albumId}")
    public R<Void> updateAlbum(@PathVariable Long albumId,
                                @Valid @RequestBody AlbumDTO dto) {
        albumService.updateAlbum(albumId, dto);
        return R.ok();
    }

    @Operation(summary = "删除相册")
    @DeleteMapping("/{albumId}")
    public R<Void> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return R.ok();
    }

    @Operation(summary = "获取我的相册列表")
    @GetMapping("/list")
    public R<List<AlbumVO>> listMyAlbums() {
        return R.ok(albumService.listMyAlbums());
    }

    @Operation(summary = "获取相册详情")
    @GetMapping("/{albumId}")
    public R<AlbumVO> getAlbumDetail(@PathVariable Long albumId) {
        return R.ok(albumService.getAlbumDetail(albumId));
    }
}
