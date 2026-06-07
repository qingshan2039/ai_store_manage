package com.aistore.module.packagingrelation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 包装关系列表项 VO（对齐 PackagingRelationSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingRelationSummaryVO {
    private Long id;
    private Long parentLevelId;
    private String parentLevelName;
    private Long childLevelId;
    private String childLevelName;
    private BigDecimal childQty;
    private Integer isFixedQty;
    private Integer status;
    private LocalDateTime createdAt;
}
