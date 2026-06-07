package com.aistore.module.location.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 库位列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationListResponse {
    private List<LocationSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
