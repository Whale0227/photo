package com.photo.module.album.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.common.exception.BusinessException;
import com.photo.common.result.ResultCode;
import com.photo.module.album.dto.AlbumDTO;
import com.photo.module.album.entity.Album;
import com.photo.module.album.mapper.AlbumMapper;
import com.photo.module.album.service.AlbumService;
import com.photo.module.album.vo.AlbumVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 相册 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

    private final AlbumMapper albumMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAlbum(AlbumDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        Album album = new Album();
        BeanUtils.copyProperties(dto, album);
        album.setUserId(userId);
        album.setPhotoCount(0);
        save(album);
        return album.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbum(Long albumId, AlbumDTO dto) {
        Album album = checkOwner(albumId);
        BeanUtils.copyProperties(dto, album);
        updateById(album);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(Long albumId) {
        checkOwner(albumId);
        // 仅逻辑删除相册本身，图片归属改为"未分类"由 PhotoService 处理
        removeById(albumId);
    }

    @Override
    public List<AlbumVO> listMyAlbums() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Album> albums = list(new LambdaQueryWrapper<Album>()
                .eq(Album::getUserId, userId)
                .orderByAsc(Album::getSort)
                .orderByDesc(Album::getCreateTime));
        return albums.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public AlbumVO getAlbumDetail(Long albumId) {
        Album album = checkOwner(albumId);
        return toVO(album);
    }

    @Override
    public void updatePhotoCount(Long albumId, int delta) {
        albumMapper.updatePhotoCount(albumId, delta);
    }

    @Override
    public Album checkOwner(Long albumId) {
        Album album = getById(albumId);
        if (album == null) {
            throw new BusinessException(ResultCode.ALBUM_NOT_EXIST);
        }
        Long userId = StpUtil.getLoginIdAsLong();
        if (!album.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ALBUM_NO_PERMISSION);
        }
        return album;
    }

    private AlbumVO toVO(Album album) {
        AlbumVO vo = new AlbumVO();
        BeanUtils.copyProperties(album, vo);
        return vo;
    }
}
