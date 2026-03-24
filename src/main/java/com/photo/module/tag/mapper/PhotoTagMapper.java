package com.photo.module.tag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.module.tag.entity.PhotoTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoTagMapper extends BaseMapper<PhotoTag> {

    @Select("SELECT tag_id FROM photo_tag WHERE photo_id = #{photoId}")
    List<Long> selectTagIdsByPhotoId(Long photoId);

    @Delete("DELETE FROM photo_tag WHERE photo_id = #{photoId}")
    int deleteByPhotoId(Long photoId);

    @Select("SELECT t.name FROM tag t INNER JOIN photo_tag pt ON t.id = pt.tag_id WHERE pt.photo_id = #{photoId}")
    List<String> selectTagNamesByPhotoId(Long photoId);
}
