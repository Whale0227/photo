package com.photo.module.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.module.album.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 相册 Mapper
 */
@Mapper
public interface AlbumMapper extends BaseMapper<Album> {

    /**
     * 更新相册图片数量（增量）
     */
    @Update("UPDATE album SET photo_count = photo_count + #{delta} WHERE id = #{albumId} AND deleted = 0")
    int updatePhotoCount(Long albumId, int delta);
}
