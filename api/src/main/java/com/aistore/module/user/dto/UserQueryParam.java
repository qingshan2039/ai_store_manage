package com.aistore.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户列表查询参数
 * 封装 GET /api/users 的所有查询参数
 * 严格对齐 OpenAPI 契约 listUsers 接口的 parameters 定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryParam {

    /**
     * 关键词搜索（模糊匹配姓名、工号、手机号）
     */
    private String keyword;

    /**
     * 工号筛选（精确或前缀匹配）
     */
    private String employeeNo;

    /**
     * 姓名筛选（模糊匹配）
     */
    private String name;

    /**
     * 手机号筛选（精确匹配）
     */
    private String phoneNumber;

    /**
     * 账号状态筛选（0=禁用，1=启用）
     */
    private Integer status;

    /**
     * 部门 ID 筛选
     */
    private Long departmentId;

    /**
     * 部门类型筛选（按所属部门 type 过滤，如 TRANSPORT/WAREHOUSE/PRODUCTION）
     */
    private String departmentType;

    /**
     * 职位筛选（模糊匹配）
     */
    private String jobTitle;

    /**
     * 性别筛选（0=未知，1=男，2=女）
     */
    private Integer gender;

    /**
     * 创建时间起始（包含）
     */
    private LocalDateTime createdAtStart;

    /**
     * 创建时间截止（包含）
     */
    private LocalDateTime createdAtEnd;

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
