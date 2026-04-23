package com.aistore.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段级错误详情
 * 严格对齐 OpenAPI 契约 FieldError Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {

    /**
     * 出错字段名
     */
    private String field;

    /**
     * 字段错误描述
     */
    private String message;
}
