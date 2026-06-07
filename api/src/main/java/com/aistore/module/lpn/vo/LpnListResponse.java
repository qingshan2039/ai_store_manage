package com.aistore.module.lpn.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 托盘实例列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LpnListResponse {
    private List<LpnSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
