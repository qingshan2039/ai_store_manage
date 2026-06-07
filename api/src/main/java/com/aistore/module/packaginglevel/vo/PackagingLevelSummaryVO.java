package com.aistore.module.packaginglevel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 包装层级列表项 VO（对齐 PackagingLevelSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingLevelSummaryVO {
    private Long id;
    private Long skuId;
    private String skuName;
    private String levelName;
    private Integer levelSeq;
    private String unitCode;
    private Integer isBaseUnit;
    private Integer isSellable;
    private Integer status;
    private LocalDateTime createdAt;
}
