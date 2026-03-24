package com.photo.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 * 用法：
 *   R.ok()              // 成功，无数据
 *   R.ok(data)          // 成功，带数据
 *   R.fail(ResultCode)  // 失败，使用枚举
 *   R.fail("自定义消息")  // 失败，自定义消息
 */
@Data
public class R<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    private R() {}

    // -------- 成功 --------

    public static <T> R<T> ok() {
        return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return result(ResultCode.SUCCESS.getCode(), message, data);
    }

    // -------- 失败 --------

    public static <T> R<T> fail() {
        return result(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), null);
    }

    public static <T> R<T> fail(String message) {
        return result(ResultCode.ERROR.getCode(), message, null);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return result(resultCode.getCode(), message, null);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return result(code, message, null);
    }

    // -------- 内部构造 --------

    private static <T> R<T> result(Integer code, String message, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode() == this.code;
    }
}
