package com.photo.module.share.controller;

import com.photo.common.result.R;
import com.photo.module.share.dto.CreateShareDTO;
import com.photo.module.share.service.ShareService;
import com.photo.module.share.vo.ShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分享接口
 */
@Tag(name = "图片分享")
@RestController
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @Operation(summary = "创建分享链接")
    @PostMapping("/api/share")
    public R<ShareVO> createShare(@RequestBody CreateShareDTO dto) {
        return R.ok(shareService.createShare(dto));
    }

    @Operation(summary = "访问分享（公开，无需登录）")
    @GetMapping("/s/{shareCode}")
    public R<Object> accessShare(
            @PathVariable String shareCode,
            @RequestParam(required = false) String extractCode) {
        return R.ok(shareService.accessShare(shareCode, extractCode));
    }

    @Operation(summary = "获取我的分享列表")
    @GetMapping("/api/share/list")
    public R<List<ShareVO>> listMyShares() {
        return R.ok(shareService.listMyShares());
    }

    @Operation(summary = "取消分享")
    @DeleteMapping("/api/share/{shareId}")
    public R<Void> cancelShare(@PathVariable Long shareId) {
        shareService.cancelShare(shareId);
        return R.ok();
    }
}
