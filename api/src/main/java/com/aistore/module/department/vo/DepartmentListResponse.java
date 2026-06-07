package com.aistore.module.department.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门列表分页响应体
 * 严格对齐 OpenAPI 契约 DepartmentListResponse Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentListResponse {

    /**
     * 部门列表数据
     */
    private List<DepartmentSummaryVO> items;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;
}
