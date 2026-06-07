package com.aistore.module.department.dto;

import com.aistore.module.department.enums.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门列表查询参数
 * 封装 GET /api/departments 的所有查询参数
 * 严格对齐 OpenAPI 契约 listDepartments 接口的 parameters 定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentQueryParam {

    /**
     * 关键词搜索（模糊匹配部门名称、编码）
     */
    private String keyword;

    /**
     * 部门类型筛选
     */
    private DepartmentType type;

    /**
     * 部门状态筛选（0=禁用，1=启用）
     */
    private Integer status;

    /**
     * 页码，从 1 开始，默认 1
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * 每页条数，默认 20
     */
    @Builder.Default
    private Integer pageSize = 20;
}
