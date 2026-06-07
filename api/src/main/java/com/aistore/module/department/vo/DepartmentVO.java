package com.aistore.module.department.vo;

import com.aistore.module.department.enums.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门详情响应 VO
 * 严格对齐 OpenAPI 契约 Department Schema
 * 注意：deleted 永远不出现在响应中
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentVO {

    /**
     * 部门 ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 部门类型
     */
    private DepartmentType type;

    /**
     * 部门状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 显示排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 更新人 ID
     */
    private Long updatedBy;
}
