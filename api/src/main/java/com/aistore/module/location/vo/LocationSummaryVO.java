package com.aistore.module.location.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 库位列表项 VO（对齐 LocationSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSummaryVO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private String zoneName;
    private String code;
    private String locType;
    private Integer status;
    private LocalDateTime createdAt;
}
