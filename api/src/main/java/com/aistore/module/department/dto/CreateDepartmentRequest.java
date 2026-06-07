package com.aistore.module.department.dto;

import com.aistore.module.department.enums.DepartmentType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建部门请求 DTO
 * 严格对齐 OpenAPI 契约 CreateDepartmentRequest Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentRequest {

    /**
     * 部门名称（全局唯一）
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 2, max = 64, message = "部门名称长度必须在2-64个字符之间")
    private String name;

    /**
     * 部门编码（创建后不可修改，全局唯一）
     */
    @NotBlank(message = "部门编码不能为空")
    @Size(min = 2, max = 32, message = "部门编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "部门编码只能包含字母、数字和连字符")
    private String code;

    /**
     * 部门类型
     */
    @NotNull(message = "部门类型不能为空")
    private DepartmentType type;

    /**
     * 显示排序（升序，越小越靠前）
     */
    private Integer sort;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 部门状态：0=禁用，1=启用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
