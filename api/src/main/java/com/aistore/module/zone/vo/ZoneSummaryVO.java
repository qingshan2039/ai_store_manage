package com.aistore.module.zone.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 库区列表项 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneSummaryVO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private String code;
    private String name;
    private String type;
    private Integer status;
    private LocalDateTime createdAt;
}
