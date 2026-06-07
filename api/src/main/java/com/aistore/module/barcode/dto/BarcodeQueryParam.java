package com.aistore.module.barcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 条码列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeQueryParam {
    private String keyword;
    private Long levelId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
