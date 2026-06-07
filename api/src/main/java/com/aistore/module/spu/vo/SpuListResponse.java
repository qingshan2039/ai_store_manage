package com.aistore.module.spu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** SPU 列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuListResponse {
    private List<SpuSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
