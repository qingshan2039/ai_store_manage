package com.aistore.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * 携带错误码和错误消息，用于统一异常响应
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码（大写下划线风格，如 VALIDATION_ERROR）
     */
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
