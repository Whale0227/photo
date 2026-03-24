package com.photo.module.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人信息 DTO
 */
@Data
public class UpdateUserDTO {

    @Size(max = 20, message = "昵称最长20位")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    /** 新头像的 objectKey（上传后返回） */
    private String avatar;
}
