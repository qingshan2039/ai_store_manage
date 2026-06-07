package com.aistore.module.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 供应商列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierQueryParam {
    private String keyword;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
