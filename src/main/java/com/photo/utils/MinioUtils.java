package com.photo.utils;

import com.photo.config.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 工具类
 * 封装文件上传、删除、获取访问URL等操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 上传文件
     * @param file      MultipartFile
     * @param directory 存储目录，如 "photos/2024/06"
     * @return 对象路径（objectKey），如 "photos/2024/06/abc123.jpg"
     */
    public String upload(MultipartFile file, String directory) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        // 使用 UUID 重命名，防止文件名冲突
        String objectKey = directory + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        log.info("文件上传成功: {}", objectKey);
        return objectKey;
    }

    /**
     * 上传输入流（用于缩略图等）
     * @param inputStream 输入流
     * @param objectKey   对象路径
     * @param contentType MIME类型
     * @param size        文件大小
     */
    public void uploadStream(InputStream inputStream, String objectKey,
                              String contentType, long size) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build());
    }

    /**
     * 删除文件
     * @param objectKey 对象路径
     */
    public void delete(String objectKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .build());
        log.info("文件删除成功: {}", objectKey);
    }

    /**
     * 获取文件永久访问 URL（Bucket 为公开读时使用）
     * 格式：http://localhost:9000/photos/xxx.jpg
     * @param objectKey 对象路径
     * @return 访问URL
     */
    public String getUrl(String objectKey) {
        return minioProperties.getEndpoint() + "/"
                + minioProperties.getBucketName() + "/"
                + objectKey;
    }

    /**
     * 获取文件临时访问 URL（私有Bucket时使用，有过期时间）
     * @param objectKey 对象路径
     * @param duration  有效时长
     * @param unit      时间单位
     * @return 带签名的临时URL
     */
    public String getPresignedUrl(String objectKey, int duration, TimeUnit unit) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .method(Method.GET)
                .expiry(duration, unit)
                .build());
    }

    /**
     * 获取文件输入流（用于下载）
     * @param objectKey 对象路径
     * @return InputStream
     */
    public InputStream download(String objectKey) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .build());
    }

    /**
     * 判断文件是否存在
     * @param objectKey 对象路径
     */
    public boolean exists(String objectKey) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectKey)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
