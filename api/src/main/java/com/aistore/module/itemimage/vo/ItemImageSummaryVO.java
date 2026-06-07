package com.aistore.module.itemimage.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 物料图片列表项 VO（对齐 ItemImageSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImageSummaryVO {
    private Long id;
    private Long spuId;
    private Long skuId;
    private Long levelId;
    private String imageUrl;
    private String imageType;
    private Integer sortOrder;
    private Integer isPrimary;
    private Integer status;
    private LocalDateTime createdAt;
}
