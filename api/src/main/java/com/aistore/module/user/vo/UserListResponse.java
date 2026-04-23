package com.aistore.module.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户列表分页响应体
 * 严格对齐 OpenAPI 契约 UserListResponse Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    /**
     * 用户列表数据
     */
    private List<UserSummaryVO> items;

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
