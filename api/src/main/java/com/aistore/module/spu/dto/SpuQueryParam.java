package com.aistore.module.spu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SPU 列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuQueryParam {
    private String keyword;
    private String categoryCode;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
