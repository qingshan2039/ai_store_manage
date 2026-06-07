package com.aistore.module.packagingrelation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 包装关系详情响应 VO（对齐 PackagingRelation Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingRelationVO {
    private Long id;
    private Long parentLevelId;
    private String parentLevelName;
    private Long childLevelId;
    private String childLevelName;
    private BigDecimal childQty;
    private Integer isFixedQty;
    private BigDecimal tareWeight;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
