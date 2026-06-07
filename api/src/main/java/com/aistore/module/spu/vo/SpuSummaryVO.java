package com.aistore.module.spu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** SPU 列表项 VO（对齐 SpuSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuSummaryVO {
    private Long id;
    private String spuCode;
    private String spuName;
    private String categoryCode;
    private String categoryName;
    private String baseUnit;
    private Integer status;
    private LocalDateTime createdAt;
}
