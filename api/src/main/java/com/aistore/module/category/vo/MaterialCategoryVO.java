package com.aistore.module.category.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 物料品类详情响应 VO（对齐 MaterialCategory Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryVO {
    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
