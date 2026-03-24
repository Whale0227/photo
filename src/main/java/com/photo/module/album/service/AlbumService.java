package com.photo.module.album.service;

import com.photo.module.album.dto.AlbumDTO;
import com.photo.module.album.entity.Album;
import com.photo.module.album.vo.AlbumVO;

import java.util.List;

/**
 * 相册 Service 接口
 */
public interface AlbumService {

    /** 创建相册 */
    Long createAlbum(AlbumDTO dto);

    /** 修改相册 */
    void updateAlbum(Long albumId, AlbumDTO dto);

    /** 删除相册（同时删除相册内图片） */
    void deleteAlbum(Long albumId);

    /** 获取当前用户所有相册 */
    List<AlbumVO> listMyAlbums();

    /** 获取相册详情 */
    AlbumVO getAlbumDetail(Long albumId);

    /** 更新相册图片数量 */
    void updatePhotoCount(Long albumId, int delta);

    /** 校验相册归属（非本人抛异常） */
    Album checkOwner(Long albumId);
}
