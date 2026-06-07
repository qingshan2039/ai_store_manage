package com.aistore.module.pallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 托盘类型列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletTypeQueryParam {
    private String keyword;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
