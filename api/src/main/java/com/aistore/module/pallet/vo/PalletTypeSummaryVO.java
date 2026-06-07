package com.aistore.module.pallet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 托盘类型列表项 VO（对齐 PalletTypeSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletTypeSummaryVO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal maxLoad;
    private Integer maxStack;
    private Integer status;
    private LocalDateTime createdAt;
}
