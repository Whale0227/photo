package com.photo.module.share.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.common.exception.BusinessException;
import com.photo.common.result.ResultCode;
import com.photo.module.share.dto.CreateShareDTO;
import com.photo.module.share.entity.PhotoShare;
import com.photo.module.share.mapper.PhotoShareMapper;
import com.photo.module.share.service.ShareService;
import com.photo.module.share.vo.ShareVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl extends ServiceImpl<PhotoShareMapper, PhotoShare> implements ShareService {

    private static final String SHARE_BASE_URL = "http://localhost:8080/s/";

    @Override
    public ShareVO createShare(CreateShareDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();

        PhotoShare share = new PhotoShare();
        share.setUserId(userId);
        share.setShareType(dto.getShareType());
        share.setTargetId(dto.getTargetId());
        share.setShareCode(RandomUtil.randomString(8)); // 8位随机码
        share.setExtractCode(dto.getExtractCode());
        share.setExpireTime(dto.getExpireDays() != null
                ? LocalDateTime.now().plusDays(dto.getExpireDays()) : null);
        share.setViewCount(0);
        share.setStatus(1);
        share.setCreateTime(LocalDateTime.now());
        save(share);
        return toVO(share);
    }

    @Override
    public Object accessShare(String shareCode, String extractCode) {
        PhotoShare share = getOne(new LambdaQueryWrapper<PhotoShare>()
                .eq(PhotoShare::getShareCode, shareCode)
                .eq(PhotoShare::getStatus, 1));
        if (share == null) {
            throw new BusinessException(ResultCode.SHARE_NOT_EXIST);
        }
        // 校验过期
        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            update(new LambdaUpdateWrapper<PhotoShare>()
                    .eq(PhotoShare::getId, share.getId()).set(PhotoShare::getStatus, 0));
            throw new BusinessException(ResultCode.SHARE_EXPIRED);
        }
        // 校验提取码
        if (share.getExtractCode() != null && !share.getExtractCode().equals(extractCode)) {
            throw new BusinessException(ResultCode.SHARE_CODE_ERROR);
        }
        // 校验访问次数
        if (share.getMaxView() != null && share.getViewCount() >= share.getMaxView()) {
            throw new BusinessException(ResultCode.SHARE_NOT_EXIST);
        }
        // 累加访问次数
        update(new LambdaUpdateWrapper<PhotoShare>()
                .eq(PhotoShare::getId, share.getId())
                .set(PhotoShare::getViewCount, share.getViewCount() + 1));
        // 返回目标内容（此处简化，返回 shareVO，实际可根据 shareType 返回图片/相册详情）
        return toVO(share);
    }

    @Override
    public List<ShareVO> listMyShares() {
        Long userId = StpUtil.getLoginIdAsLong();
        return list(new LambdaQueryWrapper<PhotoShare>()
                .eq(PhotoShare::getUserId, userId)
                .orderByDesc(PhotoShare::getCreateTime))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void cancelShare(Long shareId) {
        Long userId = StpUtil.getLoginIdAsLong();
        update(new LambdaUpdateWrapper<PhotoShare>()
                .eq(PhotoShare::getId, shareId)
                .eq(PhotoShare::getUserId, userId)
                .set(PhotoShare::getStatus, 0));
    }

    private ShareVO toVO(PhotoShare share) {
        ShareVO vo = new ShareVO();
        BeanUtils.copyProperties(share, vo);
        vo.setShareUrl(SHARE_BASE_URL + share.getShareCode());
        return vo;
    }
}
