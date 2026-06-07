package com.aistore.module.packaginglevel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 包装层级详情响应 VO（对齐 PackagingLevel Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingLevelVO {
    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String levelName;
    private Integer levelSeq;
    private String unitCode;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal netWeight;
    private BigDecimal grossWeight;
    private Integer isBaseUnit;
    private Integer isSellable;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
