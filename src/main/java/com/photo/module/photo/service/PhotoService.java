package com.photo.module.photo.service;

import com.photo.common.entity.PageResult;
import com.photo.module.photo.dto.PhotoQueryDTO;
import com.photo.module.photo.dto.UpdatePhotoDTO;
import com.photo.module.photo.entity.Photo;
import com.photo.module.photo.vo.PhotoVO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 图片 Service 接口
 */
public interface PhotoService {

    /** 上传图片（自动生成缩略图，记录数据库） */
    PhotoVO upload(MultipartFile file, Long albumId, String description);

    /** 批量删除图片 */
    void deletePhotos(List<Long> photoIds);

    /** 修改图片信息 */
    void updatePhoto(Long photoId, UpdatePhotoDTO dto);

    /** 分页查询图片（当前用户） */
    PageResult<PhotoVO> pagePhotos(PhotoQueryDTO dto);

    /** 获取图片详情 */
    PhotoVO getPhotoDetail(Long photoId);

    /** 下载图片 */
    void download(Long photoId, HttpServletResponse response);

    /** 批量移动图片到指定相册 */
    void movePhotos(List<Long> photoIds, Long albumId);

    /** 校验图片归属 */
    Photo checkOwner(Long photoId);
}
