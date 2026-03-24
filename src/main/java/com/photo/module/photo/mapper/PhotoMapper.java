package com.photo.module.photo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.module.photo.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 图片 Mapper
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    @Update("UPDATE photo SET view_count = view_count + 1 WHERE id = #{photoId} AND deleted = 0")
    int incrViewCount(Long photoId);

    @Update("UPDATE photo SET download_count = download_count + 1 WHERE id = #{photoId} AND deleted = 0")
    int incrDownloadCount(Long photoId);
}
