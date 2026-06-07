package com.aistore.module.department.dto;

import com.aistore.module.department.enums.DepartmentType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新部门请求 DTO
 * 严格对齐 OpenAPI 契约 UpdateDepartmentRequest Schema
 * 所有字段均为可选，仅传入需要修改的字段；code 创建后不可修改，状态通过独立接口变更
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentRequest {

    /**
     * 部门名称（全局唯一）
     */
    @Size(min = 2, max = 64, message = "部门名称长度必须在2-64个字符之间")
    private String name;

    /**
     * 部门类型
     */
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
}
