package com.photo.module.share.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享 VO
 */
@Data
public class ShareVO {

    private Long id;
    private Integer shareType;
    private Long targetId;
    /** 完整分享链接 */
    private String shareUrl;
    private String shareCode;
    private String extractCode;
    private LocalDateTime expireTime;
    private Integer viewCount;
    private Integer maxView;
    private Integer status;
    private LocalDateTime createTime;
}
