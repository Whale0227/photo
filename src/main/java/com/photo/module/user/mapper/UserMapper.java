package com.photo.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 更新已用存储空间（增量）
     * @param userId 用户ID
     * @param delta  变化量（正数增加，负数减少）
     */
    @Update("UPDATE `user` SET storage_used = storage_used + #{delta} WHERE id = #{userId} AND deleted = 0")
    int updateStorageUsed(Long userId, Long delta);
}
