package com.aistore.module.barcode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 条码列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeListResponse {
    private List<BarcodeSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
