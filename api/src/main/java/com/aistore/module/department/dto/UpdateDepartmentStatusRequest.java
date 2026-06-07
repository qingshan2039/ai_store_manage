package com.aistore.module.department.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变更部门状态请求 DTO
 * 严格对齐 OpenAPI 契约 UpdateDepartmentStatusRequest Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentStatusRequest {

    /**
     * 部门状态：0=禁用，1=启用
     */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效，必须为0或1")
    @Max(value = 1, message = "状态值无效，必须为0或1")
    private Integer status;
}
