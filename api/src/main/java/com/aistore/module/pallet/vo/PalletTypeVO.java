package com.aistore.module.pallet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 托盘类型详情响应 VO（对齐 PalletType Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletTypeVO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal tareWeight;
    private BigDecimal maxLoad;
    private Integer maxStack;
    private String remark;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
