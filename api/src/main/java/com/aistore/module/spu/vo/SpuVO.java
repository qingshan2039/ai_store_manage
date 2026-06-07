package com.aistore.module.spu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** SPU 详情响应 VO（对齐 Spu Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuVO {
    private Long id;
    private String spuCode;
    private String spuName;
    private String categoryCode;
    private String categoryName;
    private String brand;
    private String baseUnit;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
