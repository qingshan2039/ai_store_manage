package com.aistore.module.location.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 库位详情响应 VO（对齐 Location Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationVO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long zoneId;
    private String zoneName;
    private String code;
    private String locType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
