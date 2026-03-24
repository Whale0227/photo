package com.photo.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 数据列表 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long current;

    /** 每页条数 */
    private long size;

    /** 总页数 */
    private long pages;

    /**
     * 快速构建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        long pages = size == 0 ? 0 : (total + size - 1) / size;
        return new PageResult<>(records, total, current, size, pages);
    }
}
