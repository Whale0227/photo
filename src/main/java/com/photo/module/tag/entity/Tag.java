package com.photo.module.tag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体（无逻辑删除，直接物理删除）
 */
@Data
@TableName("tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建用户ID */
    private Long userId;

    /** 标签名称 */
    private String name;

    /** 使用次数 */
    private Integer useCount;

    private LocalDateTime createTime;
}
