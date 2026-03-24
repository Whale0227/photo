package com.photo.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通用基础实体（所有表的公共字段）
 * 各业务 Entity 继承此类，避免重复定义
 */
@Data
public abstract class BaseEntity implements Serializable {

    /** 主键ID（自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建时间（插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（插入和更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-正常 1-已删除 */
    @TableLogic
    private Integer deleted;
}
