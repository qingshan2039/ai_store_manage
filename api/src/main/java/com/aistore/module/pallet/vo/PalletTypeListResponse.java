package com.aistore.module.pallet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 托盘类型列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletTypeListResponse {
    private List<PalletTypeSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
