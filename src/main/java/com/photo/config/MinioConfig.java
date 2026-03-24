package com.photo.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置
 * 启动时自动检测 Bucket 是否存在，不存在则创建并设置公开读策略
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        initBucket(client);
        return client;
    }

    /**
     * 初始化 Bucket：不存在则自动创建，并设置公开读策略（图片可直接访问）
     */
    private void initBucket(MinioClient client) {
        String bucketName = minioProperties.getBucketName();
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                // 设置公开读策略，允许直接通过 URL 访问图片
                client.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(buildPublicReadPolicy(bucketName))
                        .build());
                log.info("MinIO Bucket [{}] 创建成功并设置公开读策略", bucketName);
            } else {
                log.info("MinIO Bucket [{}] 已存在", bucketName);
            }
        } catch (Exception e) {
            log.error("MinIO 初始化 Bucket 失败，请检查 MinIO 服务是否启动: {}", e.getMessage());
            // 不抛出异常，避免阻塞项目启动（开发阶段可注释掉 MinIO 相关逻辑）
        }
    }

    /**
     * 构建公开读 Bucket 策略（JSON格式）
     */
    private String buildPublicReadPolicy(String bucketName) {
        return """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {"AWS": ["*"]},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);
    }
}
