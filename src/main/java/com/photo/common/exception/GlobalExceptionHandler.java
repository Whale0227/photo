package com.photo.common.exception;

import com.photo.common.result.R;
import com.photo.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（主动抛出）
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 注解触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return R.fail(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 处理文件大小超出限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("文件大小超出限制: {}", e.getMessage());
        return R.fail(ResultCode.FILE_SIZE_EXCEED);
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return R.fail(ResultCode.ERROR, "服务器内部错误，请联系管理员");
    }
}
