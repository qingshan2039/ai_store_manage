package com.aistore.module.zone.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 库区列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneListResponse {
    private List<ZoneSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
