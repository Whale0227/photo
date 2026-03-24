package com.photo.module.photo.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.common.constant.CommonConstant;
import com.photo.common.entity.PageResult;
import com.photo.common.exception.BusinessException;
import com.photo.common.result.ResultCode;
import com.photo.module.album.service.AlbumService;
import com.photo.module.photo.dto.PhotoQueryDTO;
import com.photo.module.photo.dto.UpdatePhotoDTO;
import com.photo.module.photo.entity.Photo;
import com.photo.module.photo.mapper.PhotoMapper;
import com.photo.module.photo.service.PhotoService;
import com.photo.module.photo.vo.PhotoVO;
import com.photo.module.user.service.UserService;
import com.photo.utils.MinioUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    private final PhotoMapper photoMapper;
    private final MinioUtils minioUtils;
    private final UserService userService;
    private final AlbumService albumService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoVO upload(MultipartFile file, Long albumId, String description) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 1. 校验文件类型
        String contentType = file.getContentType();
        boolean allowed = Arrays.asList(CommonConstant.ALLOWED_IMAGE_TYPES).contains(contentType);
        if (!allowed) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORT);
        }

        // 2. 校验存储配额
        com.photo.module.user.entity.User user = userService.getById(userId);
        if (user.getStorageUsed() + file.getSize() > user.getStorageLimit()) {
            throw new BusinessException(ResultCode.STORAGE_QUOTA_EXCEED);
        }

        try {
            // 3. 上传原图到 MinIO
            String dir = "photos/" + userId + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String objectKey = minioUtils.upload(file, dir);

            // 4. 读取图片尺寸
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;

            // 5. 生成缩略图并上传
            String thumbKey = null;
            if (bufferedImage != null) {
                ByteArrayOutputStream thumbOut = new ByteArrayOutputStream();
                Thumbnails.of(file.getInputStream())
                        .size(CommonConstant.THUMB_WIDTH, CommonConstant.THUMB_HEIGHT)
                        .keepAspectRatio(true)
                        .toOutputStream(thumbOut);
                thumbKey = CommonConstant.THUMB_PREFIX + objectKey;
                byte[] thumbBytes = thumbOut.toByteArray();
                minioUtils.uploadStream(
                        new ByteArrayInputStream(thumbBytes),
                        thumbKey,
                        contentType,
                        thumbBytes.length
                );
            }

            // 6. 保存图片记录
            Photo photo = new Photo();
            photo.setUserId(userId);
            photo.setAlbumId(albumId);
            photo.setOriginalName(file.getOriginalFilename());
            photo.setFileName(objectKey.substring(objectKey.lastIndexOf("/") + 1));
            photo.setBucketName("photos");
            photo.setObjectKey(objectKey);
            photo.setThumbKey(thumbKey);
            photo.setFileSize(file.getSize());
            photo.setFileType(contentType);
            photo.setWidth(width);
            photo.setHeight(height);
            photo.setDescription(description);
            photo.setIsPublic(CommonConstant.PRIVATE);
            photo.setViewCount(0);
            photo.setDownloadCount(0);
            save(photo);

            // 7. 更新用户存储量、相册图片数
            userService.updateStorageUsed(userId, file.getSize());
            if (albumId != null) {
                albumService.updatePhotoCount(albumId, 1);
            }

            log.info("用户[{}]上传图片成功: {}", userId, objectKey);
            return toVO(photo);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片上传失败: ", e);
            throw new BusinessException(ResultCode.PHOTO_UPLOAD_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePhotos(List<Long> photoIds) {
        Long userId = StpUtil.getLoginIdAsLong();
        for (Long photoId : photoIds) {
            Photo photo = checkOwner(photoId);
            // 删除 MinIO 文件
            try {
                minioUtils.delete(photo.getObjectKey());
                if (photo.getThumbKey() != null) {
                    minioUtils.delete(photo.getThumbKey());
                }
            } catch (Exception e) {
                log.warn("MinIO 文件删除失败: {}", photo.getObjectKey());
            }
            // 逻辑删除记录
            removeById(photoId);
            // 更新用户存储量
            userService.updateStorageUsed(userId, -photo.getFileSize());
            // 更新相册图片数
            if (photo.getAlbumId() != null) {
                albumService.updatePhotoCount(photo.getAlbumId(), -1);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePhoto(Long photoId, UpdatePhotoDTO dto) {
        Photo photo = checkOwner(photoId);
        Long oldAlbumId = photo.getAlbumId();

        photo.setDescription(dto.getDescription());
        photo.setIsPublic(dto.getIsPublic());
        // 处理相册变更
        if (dto.getAlbumId() != null && !dto.getAlbumId().equals(oldAlbumId)) {
            photo.setAlbumId(dto.getAlbumId());
            if (oldAlbumId != null) albumService.updatePhotoCount(oldAlbumId, -1);
            albumService.updatePhotoCount(dto.getAlbumId(), 1);
        } else if (dto.getAlbumId() == null && oldAlbumId != null) {
            photo.setAlbumId(null);
            albumService.updatePhotoCount(oldAlbumId, -1);
        }
        updateById(photo);
    }

    @Override
    public PageResult<PhotoVO> pagePhotos(PhotoQueryDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<Photo> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<Photo>()
                .eq(Photo::getUserId, userId)
                .eq(dto.getAlbumId() != null, Photo::getAlbumId, dto.getAlbumId())
                .eq(dto.getIsPublic() != null, Photo::getIsPublic, dto.getIsPublic())
                .and(StringUtils.hasText(dto.getKeyword()), w -> w
                        .like(Photo::getDescription, dto.getKeyword())
                        .or()
                        .like(Photo::getOriginalName, dto.getKeyword()))
                .orderByDesc(Photo::getCreateTime);
        page(page, wrapper);
        List<PhotoVO> vos = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(vos, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PhotoVO getPhotoDetail(Long photoId) {
        Photo photo = checkOwner(photoId);
        photoMapper.incrViewCount(photoId);
        return toVO(photo);
    }

    @Override
    public void download(Long photoId, HttpServletResponse response) {
        Photo photo = checkOwner(photoId);
        try (InputStream in = minioUtils.download(photo.getObjectKey())) {
            response.setContentType(photo.getFileType());
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + photo.getOriginalName() + "\"");
            in.transferTo(response.getOutputStream());
            photoMapper.incrDownloadCount(photoId);
        } catch (Exception e) {
            log.error("图片下载失败: ", e);
            throw new BusinessException(ResultCode.ERROR, "图片下载失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void movePhotos(List<Long> photoIds, Long albumId) {
        for (Long photoId : photoIds) {
            Photo photo = checkOwner(photoId);
            Long oldAlbumId = photo.getAlbumId();
            if (oldAlbumId != null) albumService.updatePhotoCount(oldAlbumId, -1);
            if (albumId != null) albumService.updatePhotoCount(albumId, 1);
            update(new LambdaUpdateWrapper<Photo>()
                    .eq(Photo::getId, photoId)
                    .set(Photo::getAlbumId, albumId));
        }
    }

    @Override
    public Photo checkOwner(Long photoId) {
        Photo photo = getById(photoId);
        if (photo == null) {
            throw new BusinessException(ResultCode.PHOTO_NOT_EXIST);
        }
        Long userId = StpUtil.getLoginIdAsLong();
        if (!photo.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return photo;
    }

    private PhotoVO toVO(Photo photo) {
        PhotoVO vo = new PhotoVO();
        BeanUtils.copyProperties(photo, vo);
        vo.setUrl(minioUtils.getUrl(photo.getObjectKey()));
        if (photo.getThumbKey() != null) {
            vo.setThumbUrl(minioUtils.getUrl(photo.getThumbKey()));
        }
        vo.setFileSize(formatSize(photo.getFileSize()));
        return vo;
    }

    /** 格式化文件大小 */
    private String formatSize(Long bytes) {
        if (bytes == null) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
