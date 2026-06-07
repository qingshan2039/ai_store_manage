package com.aistore.module.category.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 物料品类列表项 VO（对齐 MaterialCategorySummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategorySummaryVO {
    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}
