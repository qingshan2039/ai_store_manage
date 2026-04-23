package com.aistore.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一错误响应体
 * 严格对齐 OpenAPI 契约 ErrorResponse Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误码（大写下划线风格，用于前端判断错误类型）
     */
    private String code;

    /**
     * 错误描述（人类可读）
     */
    private String message;

    /**
     * 字段级错误详情（参数校验失败时返回）
     */
    private List<FieldError> details;
}
